package taller
import scala.util.Random

class RiegoOptimo {

  type Tablon = (Int, Int, Int)
  type Finca = Vector[Tablon]
  type Distancia = Vector[Vector[Int]]
  type ProgRiego = Vector[Int]
  type TiempoInicioRiego = Vector[Int]

  val random = new Random()

  def fincaAlAzar(long: Int): Finca = {
    Vector.fill(long)(
      (
        random.nextInt(long * 2) + 1,
        random.nextInt(long) + 1,
        random.nextInt(4) + 1
      )
    )
  }

  def distanciaAlAzar(long: Int): Distancia = {
    val v = Vector.fill(long, long)(random.nextInt(long * 3) + 1)

    Vector.tabulate(long, long) { (i, j) =>
      if (i < j) v(i)(j)
      else if (i == j) 0
      else v(j)(i)
    }
  }

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

  def costoRiegoTablon(i: Int, f: Finca, pi: ProgRiego): Int = {
    val tiempos = tIR(f, pi)
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

  def costoRiegoFinca(f: Finca, pi: ProgRiego): Int = {
    (0 until f.length).foldLeft(0) { (total, i) =>
      total + costoRiegoTablon(i, f, pi)
    }
  }

  def costoMovilidad(f: Finca, pi: ProgRiego, d: Distancia): Int = {
    if (pi.length <= 1) 0
    else {
      pi.sliding(2).foldLeft(0) { (total, par) =>
        val actual = par(0)
        val siguiente = par(1)
        total + d(actual)(siguiente)
      }
    }
  }

  def generarProgramacionesRiego(f: Finca): Vector[ProgRiego] = {
    val n = f.length

    def permutacionesRec(nums: Vector[Int]): Vector[Vector[Int]] = {
      if (nums.isEmpty) Vector(Vector())
      else {
        nums.flatMap { num =>
          val resto = nums.filter(_ != num)
          permutacionesRec(resto).map(perm => num +: perm)
        }
      }
    }

    val indices = (0 until n).toVector
    permutacionesRec(indices)
  }

  def ProgramacionRiegoOptimo(f: Finca, d: Distancia): (ProgRiego, Int) = {
    val todasProgramaciones = generarProgramacionesRiego(f)

    if (todasProgramaciones.isEmpty) (Vector(), 0)
    else {
      def evaluarProgramacion(pi: ProgRiego): Int = {
        val costoRiego = costoRiegoFinca(f, pi)
        val costoMov = costoMovilidad(f, pi, d)
        costoRiego + costoMov
      }

      val (mejorProg, mejorCosto) = todasProgramaciones.tail.foldLeft(
        (todasProgramaciones.head, evaluarProgramacion(todasProgramaciones.head))
      ) { case ((mejorActual, costoActual), progActual) =>
        val costoNuevo = evaluarProgramacion(progActual)
        if (costoNuevo < costoActual) (progActual, costoNuevo)
        else (mejorActual, costoActual)
      }

      (mejorProg, mejorCosto)
    }
  }

  def ProgramacionRiegoOptimoV2(f: Finca, d: Distancia): (ProgRiego, Int) = {
    val todasProgramaciones = generarProgramacionesRiego(f)

    def encontrarOptima(progs: Vector[ProgRiego], mejorActual: (ProgRiego, Int)): (ProgRiego, Int) = {
      progs match {
        case Vector() => mejorActual
        case head +: tail =>
          val costo = costoRiegoFinca(f, head) + costoMovilidad(f, head, d)
          val nuevoMejor = if (costo < mejorActual._2) (head, costo) else mejorActual
          encontrarOptima(tail, nuevoMejor)
      }
    }

    if (todasProgramaciones.isEmpty) (Vector(), 0)
    else {
      val primera = todasProgramaciones.head
      val costoPrimera = costoRiegoFinca(f, primera) + costoMovilidad(f, primera, d)
      encontrarOptima(todasProgramaciones.tail, (primera, costoPrimera))
    }
  }
}