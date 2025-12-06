package taller
import scala.util.Random
import common._

class RiegoOptimoPar {

  type Tablon = (Int, Int, Int)
  type Finca = Vector[Tablon]
  type Distancia = Vector[Vector[Int]]
  type ProgRiego = Vector[Int]
  type TiempoInicioRiego = Vector[Int]

  val random = new Random()

  // ============================================================
  // FUNCIONES AUXILIARES (Copiadas de RiegoOptimo)
  // ============================================================

  def tsup(f: Finca, i: Int): Int = f(i)._1
  def treg(f: Finca, i: Int): Int = f(i)._2
  def prio(f: Finca, i: Int): Int = f(i)._3

  def tIR(f: Finca, pi: ProgRiego): TiempoInicioRiego = {
    val n = f.length
    val (tiempos, _) = pi.foldLeft((Vector.fill(n)(0), 0)) {
      case ((acc, tActual), idx) =>
        val nuevoAcc = acc.updated(idx, tActual)
        val nuevoT = tActual + treg(f, idx)
        (nuevoAcc, nuevoT)
    }
    tiempos
  }

  // ============================================================
  // FUNCIONES PARALELAS - PUNTO 3
  // ============================================================

  /**
   * 3.1 Paralelización del cálculo de costo de riego de finca
   * Calcula el costo total de riego usando paralelismo de datos
   */
  def costoRiegoFincaPar(f: Finca, pi: ProgRiego): Int = {
    val tiempos = tIR(f, pi)
    val umbral = 4

    def costoRiegoTablonLocal(i: Int): Int = {
      val tInicio = tiempos(i)
      val tr = treg(f, i)
      val ts = tsup(f, i)
      val p = prio(f, i)

      if (ts - tr >= tInicio) {
        ts - (tInicio + tr)
      } else {
        p * ((tInicio + tr) - ts)
      }
    }

    def sumarCostosPar(indices: Vector[Int]): Int = {
      if (indices.length <= umbral) {
        indices.foldLeft(0)((acc, i) => acc + costoRiegoTablonLocal(i))
      } else {
        val mitad = indices.length / 2
        val (izq, der) = indices.splitAt(mitad)
        val (costoIzq, costoDer) = parallel(
          sumarCostosPar(izq),
          sumarCostosPar(der)
        )
        costoIzq + costoDer
      }
    }

    val indices = (0 until f.length).toVector
    sumarCostosPar(indices)
  }

  /**
   * 3.1 Paralelización del cálculo de costo de movilidad
   * Calcula el costo de movilidad usando paralelismo de datos
   */
  def costoMovilidadPar(f: Finca, pi: ProgRiego, d: Distancia): Int = {
    if (pi.length <= 1) 0
    else {
      val pares = pi.sliding(2).toVector
      val umbral = 4

      def sumarDistanciasPar(parejas: Vector[Vector[Int]]): Int = {
        if (parejas.length <= umbral) {
          parejas.foldLeft(0) { (acc, par) =>
            acc + d(par(0))(par(1))
          }
        } else {
          val mitad = parejas.length / 2
          val (izq, der) = parejas.splitAt(mitad)
          val (costoIzq, costoDer) = parallel(
            sumarDistanciasPar(izq),
            sumarDistanciasPar(der)
          )
          costoIzq + costoDer
        }
      }

      sumarDistanciasPar(pares)
    }
  }

  /**
   * 3.2 Paralelización de la generación de programaciones de riego
   * Genera todas las permutaciones usando paralelismo de tareas
   */
  def generarProgramacionesRiegoPar(f: Finca): Vector[ProgRiego] = {
    val n = f.length
    val umbral = 5

    def permutacionesRecPar(nums: Vector[Int]): Vector[Vector[Int]] = {
      if (nums.isEmpty) {
        Vector(Vector())
      } else if (nums.length <= umbral) {
        // Caso base: usar versión secuencial
        nums.flatMap { num =>
          val resto = nums.filter(_ != num)
          permutacionesRecPar(resto).map(perm => num +: perm)
        }
      } else {
        // Caso recursivo: paralelizar
        val mitad = nums.length / 2
        val (izq, der) = nums.splitAt(mitad)

        val resultados = (izq ++ der).map { num =>
          task {
            val resto = nums.filter(_ != num)
            val perms = permutacionesRecPar(resto)
            perms.map(perm => num +: perm)
          }
        }

        resultados.flatMap(_.join())
      }
    }

    val indices = (0 until n).toVector
    permutacionesRecPar(indices)
  }

  /**
   * 3.3 Paralelización del cálculo de la programación óptima
   * Encuentra la programación óptima usando paralelismo de datos
   */
  def ProgramacionRiegoOptimoPar(f: Finca, d: Distancia): (ProgRiego, Int) = {
    val todasProgramaciones = generarProgramacionesRiegoPar(f)

    if (todasProgramaciones.isEmpty) {
      (Vector(), 0)
    } else {
      val umbral = 10

      def evaluarProgramacion(pi: ProgRiego): Int = {
        val costoRiego = costoRiegoFincaPar(f, pi)
        val costoMov = costoMovilidadPar(f, pi, d)
        costoRiego + costoMov
      }

      def encontrarMinimoPar(progs: Vector[ProgRiego]): (ProgRiego, Int) = {
        if (progs.length <= umbral) {
          // Caso base: evaluación secuencial
          progs.map(p => (p, evaluarProgramacion(p)))
            .minBy(_._2)
        } else {
          // Caso recursivo: dividir y conquistar en paralelo
          val mitad = progs.length / 2
          val (izq, der) = progs.splitAt(mitad)

          val (minimoIzq, minimoDer) = parallel(
            encontrarMinimoPar(izq),
            encontrarMinimoPar(der)
          )

          if (minimoIzq._2 <= minimoDer._2) minimoIzq else minimoDer
        }
      }

      encontrarMinimoPar(todasProgramaciones)
    }
  }
}