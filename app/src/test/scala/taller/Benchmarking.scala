package taller

import org.scalameter._

object Benchmarking {

  val r = new RiegoOptimo()
  val rPar = new RiegoOptimoPar()

  /**
   * Genera datos de benchmarking para costoRiegoFinca vs costoRiegoFincaPar
   */
  def benchmarkCostoRiegoFinca(): Unit = {
    println("\n===== BENCHMARK: Costo Riego Finca =====")
    println("Tamaño\tSecuencial (ms)\tParalelo (ms)\tAceleración (%)")

    val tamanos = List(5, 6, 7, 8)

    tamanos.foreach { n =>
      val finca = r.fincaAlAzar(n)
      val pi = (0 until n).toVector

      val timeSeq = measure {
        (1 to 100).foreach(_ => r.costoRiegoFinca(finca, pi))
      }

      val timePar = measure {
        (1 to 100).foreach(_ => rPar.costoRiegoFincaPar(finca, pi))
      }

      val speedup = ((timeSeq.value - timePar.value) / timeSeq.value * 100)
      println(f"$n\t${timeSeq.value}%.2f\t\t${timePar.value}%.2f\t\t${speedup}%.2f")
    }
  }

  /**
   * Genera datos de benchmarking para costoMovilidad vs costoMovilidadPar
   */
  def benchmarkCostoMovilidad(): Unit = {
    println("\n===== BENCHMARK: Costo Movilidad =====")
    println("Tamaño\tSecuencial (ms)\tParalelo (ms)\tAceleración (%)")

    val tamanos = List(5, 6, 7, 8)

    tamanos.foreach { n =>
      val finca = r.fincaAlAzar(n)
      val dist = r.distanciaAlAzar(n)
      val pi = (0 until n).toVector

      val timeSeq = measure {
        (1 to 100).foreach(_ => r.costoMovilidad(finca, pi, dist))
      }

      val timePar = measure {
        (1 to 100).foreach(_ => rPar.costoMovilidadPar(finca, pi, dist))
      }

      val speedup = ((timeSeq.value - timePar.value) / timeSeq.value * 100)
      println(f"$n\t${timeSeq.value}%.2f\t\t${timePar.value}%.2f\t\t${speedup}%.2f")
    }
  }

  /**
   * Genera datos de benchmarking para generarProgramacionesRiego vs generarProgramacionesRiegoPar
   */
  def benchmarkGenerarProgramaciones(): Unit = {
    println("\n===== BENCHMARK: Generar Programaciones =====")
    println("Tamaño\tSecuencial (ms)\tParalelo (ms)\tAceleración (%)")

    val tamanos = List(5, 6, 7, 8)

    tamanos.foreach { n =>
      val finca = r.fincaAlAzar(n)

      val timeSeq = measure {
        r.generarProgramacionesRiego(finca)
      }

      val timePar = measure {
        rPar.generarProgramacionesRiegoPar(finca)
      }

      val speedup = ((timeSeq.value - timePar.value) / timeSeq.value * 100)
      println(f"$n\t${timeSeq.value}%.2f\t\t${timePar.value}%.2f\t\t${speedup}%.2f")
    }
  }

  /**
   * Genera datos de benchmarking para ProgramacionRiegoOptimo vs ProgramacionRiegoOptimoPar
   */
  def benchmarkProgramacionOptima(): Unit = {
    println("\n===== BENCHMARK: Programación Óptima =====")
    println("Tamaño\tSecuencial (ms)\tParalelo (ms)\tAceleración (%)")

    val tamanos = List(5, 6, 7, 8)

    tamanos.foreach { n =>
      val finca = r.fincaAlAzar(n)
      val dist = r.distanciaAlAzar(n)

      val timeSeq = measure {
        r.ProgramacionRiegoOptimo(finca, dist)
      }

      val timePar = measure {
        rPar.ProgramacionRiegoOptimoPar(finca, dist)
      }

      val speedup = ((timeSeq.value - timePar.value) / timeSeq.value * 100)
      println(f"$n\t${timeSeq.value}%.2f\t\t${timePar.value}%.2f\t\t${speedup}%.2f")
    }
  }

  /**
   * Ejecuta todos los benchmarks
   */
  def ejecutarTodos(): Unit = {
    println("=" * 60)
    println("BENCHMARKING - PARALELIZACIÓN DEL PROBLEMA DE RIEGO ÓPTIMO")
    println("=" * 60)

    benchmarkCostoRiegoFinca()
    benchmarkCostoMovilidad()
    benchmarkGenerarProgramaciones()
    benchmarkProgramacionOptima()

    println("\n" + "=" * 60)
    println("Benchmark completado")
    println("=" * 60)
  }

  /**
   * Genera una tabla de análisis detallado
   */
  def analisisDetallado(): Unit = {
    println("\n===== ANÁLISIS DETALLADO =====\n")

    val tamanos = List(4, 5, 6, 7, 8)

    println("Análisis de Escalabilidad:")
    println("-" * 60)

    tamanos.foreach { n =>
      val finca = r.fincaAlAzar(n)
      val dist = r.distanciaAlAzar(n)

      println(s"\nFinca de tamaño: $n tablones")
      println(s"Número de permutaciones: ${(1 to n).product}")

      val timeSeq = measure {
        r.ProgramacionRiegoOptimo(finca, dist)
      }

      val timePar = measure {
        rPar.ProgramacionRiegoOptimoPar(finca, dist)
      }

      val speedup = timeSeq.value / timePar.value
      val efficiency = speedup / Runtime.getRuntime.availableProcessors() * 100

      println(f"  Tiempo secuencial: ${timeSeq.value}%.2f ms")
      println(f"  Tiempo paralelo:   ${timePar.value}%.2f ms")
      println(f"  Speedup:           ${speedup}%.2fx")
      println(f"  Eficiencia:        ${efficiency}%.2f%%")

      if (speedup > 1.0) {
        println(s"  ✓ La paralelización ES beneficiosa")
      } else {
        println(s"  ✗ La paralelización NO es beneficiosa (overhead)")
      }
    }
  }

  /**
   * Método principal para ejecutar desde la aplicación
   */
  def main(args: Array[String]): Unit = {
    ejecutarTodos()
    analisisDetallado()
  }
}