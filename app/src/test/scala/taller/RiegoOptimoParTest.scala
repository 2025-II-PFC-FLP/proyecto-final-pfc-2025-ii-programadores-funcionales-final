package taller

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.matchers.should.Matchers

@RunWith(classOf[JUnitRunner])
class RiegoOptimoParTest extends AnyFunSuite with Matchers {

  val r = new RiegoOptimo()
  val rPar = new RiegoOptimoPar()
  import r.{Finca, Distancia, ProgRiego}

  // Datos de prueba del enunciado
  val fincaEjemplo1: Finca = Vector(
    (10, 3, 4),
    (5, 3, 3),
    (2, 2, 1),
    (8, 1, 1),
    (6, 4, 2)
  )

  val distanciaEjemplo1: Distancia = Vector(
    Vector(0, 2, 2, 4, 4),
    Vector(2, 0, 4, 2, 6),
    Vector(2, 4, 0, 2, 2),
    Vector(4, 2, 2, 0, 4),
    Vector(4, 6, 2, 4, 0)
  )

  val fincaEjemplo2: Finca = Vector(
    (9, 3, 4),
    (5, 3, 3),
    (2, 2, 1),
    (8, 1, 1),
    (6, 4, 2)
  )

  val distanciaEjemplo2: Distancia = Vector(
    Vector(0, 2, 2, 4, 4),
    Vector(2, 0, 4, 2, 6),
    Vector(2, 4, 0, 2, 2),
    Vector(4, 2, 2, 0, 4),
    Vector(4, 6, 2, 4, 0)
  )

  // ============================================================
  // TESTS PARA costoRiegoFincaPar
  // ============================================================

  test("costoRiegoFincaPar - debe dar el mismo resultado que costoRiegoFinca (finca pequeña)") {
    val fincaPequeña: Finca = Vector((10, 3, 2), (8, 2, 1), (6, 4, 3))
    val pi: ProgRiego = Vector(0, 1, 2)

    val costoSeq = r.costoRiegoFinca(fincaPequeña, pi)
    val costoPar = rPar.costoRiegoFincaPar(fincaPequeña, pi)

    costoPar shouldBe costoSeq
  }

  test("costoRiegoFincaPar - debe dar el mismo resultado con Ejemplo 1, Π1") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)

    val costoSeq = r.costoRiegoFinca(fincaEjemplo1, pi1)
    val costoPar = rPar.costoRiegoFincaPar(fincaEjemplo1, pi1)

    costoPar shouldBe costoSeq
    costoPar shouldBe 33
  }

  test("costoRiegoFincaPar - debe dar el mismo resultado con Ejemplo 1, Π2") {
    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)

    val costoSeq = r.costoRiegoFinca(fincaEjemplo1, pi2)
    val costoPar = rPar.costoRiegoFincaPar(fincaEjemplo1, pi2)

    costoPar shouldBe costoSeq
    costoPar shouldBe 20
  }

  test("costoRiegoFincaPar - debe funcionar con finca de un solo tablón") {
    val fincaUnica: Finca = Vector((10, 3, 4))
    val progUnica: ProgRiego = Vector(0)

    val costoSeq = r.costoRiegoFinca(fincaUnica, progUnica)
    val costoPar = rPar.costoRiegoFincaPar(fincaUnica, progUnica)

    costoPar shouldBe costoSeq
  }

  test("costoRiegoFincaPar - debe manejar correctamente diferentes programaciones") {
    val finca: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3), (10, 4, 2))
    val programaciones = List(
      Vector(0, 1, 2, 3),
      Vector(3, 2, 1, 0),
      Vector(1, 0, 3, 2),
      Vector(2, 3, 0, 1)
    )

    programaciones.foreach { prog =>
      val costoSeq = r.costoRiegoFinca(finca, prog)
      val costoPar = rPar.costoRiegoFincaPar(finca, prog)
      costoPar shouldBe costoSeq
    }
  }

  // ============================================================
  // TESTS PARA costoMovilidadPar
  // ============================================================

  test("costoMovilidadPar - debe dar el mismo resultado que costoMovilidad (finca pequeña)") {
    val fincaPequeña: Finca = Vector((5, 3, 2), (6, 2, 1), (8, 1, 3))
    val pi: ProgRiego = Vector(0, 2, 1)
    val d: Distancia = Vector(
      Vector(0, 5, 3),
      Vector(5, 0, 4),
      Vector(3, 4, 0)
    )

    val costoSeq = r.costoMovilidad(fincaPequeña, pi, d)
    val costoPar = rPar.costoMovilidadPar(fincaPequeña, pi, d)

    costoPar shouldBe costoSeq
    costoPar shouldBe 7
  }

  test("costoMovilidadPar - debe dar el mismo resultado con Ejemplo 1, Π1") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)

    val costoSeq = r.costoMovilidad(fincaEjemplo1, pi1, distanciaEjemplo1)
    val costoPar = rPar.costoMovilidadPar(fincaEjemplo1, pi1, distanciaEjemplo1)

    costoPar shouldBe costoSeq
    costoPar shouldBe 12
  }

  test("costoMovilidadPar - debe dar el mismo resultado con Ejemplo 1, Π2") {
    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)

    val costoSeq = r.costoMovilidad(fincaEjemplo1, pi2, distanciaEjemplo1)
    val costoPar = rPar.costoMovilidadPar(fincaEjemplo1, pi2, distanciaEjemplo1)

    costoPar shouldBe costoSeq
    costoPar shouldBe 18
  }

  test("costoMovilidadPar - debe manejar programación vacía") {
    val fincaVacia: Finca = Vector()
    val progVacia: ProgRiego = Vector()
    val distVacia: Distancia = Vector()

    val costoPar = rPar.costoMovilidadPar(fincaVacia, progVacia, distVacia)
    costoPar shouldBe 0
  }

  test("costoMovilidadPar - debe manejar programación con un solo tablón") {
    val fincaUnica: Finca = Vector((10, 3, 4))
    val progUnica: ProgRiego = Vector(0)
    val distUnica: Distancia = Vector(Vector(0))

    val costoPar = rPar.costoMovilidadPar(fincaUnica, progUnica, distUnica)
    costoPar shouldBe 0
  }

  // ============================================================
  // TESTS PARA generarProgramacionesRiegoPar
  // ============================================================

  test("generarProgramacionesRiegoPar - debe generar el mismo número de permutaciones (finca vacía)") {
    val fincaVacia: Finca = Vector()

    val progsSeq = r.generarProgramacionesRiego(fincaVacia)
    val progsPar = rPar.generarProgramacionesRiegoPar(fincaVacia)

    progsPar.length shouldBe progsSeq.length
    progsPar should contain theSameElementsAs progsSeq
  }

  test("generarProgramacionesRiegoPar - debe generar el mismo número de permutaciones (1 tablón)") {
    val fincaUnTablon: Finca = Vector((5, 2, 1))

    val progsSeq = r.generarProgramacionesRiego(fincaUnTablon)
    val progsPar = rPar.generarProgramacionesRiegoPar(fincaUnTablon)

    progsPar.length shouldBe progsSeq.length
    progsPar should contain theSameElementsAs progsSeq
  }

  test("generarProgramacionesRiegoPar - debe generar todas las permutaciones (2 tablones)") {
    val fincaDosTablones: Finca = Vector((5, 2, 1), (8, 3, 2))

    val progsSeq = r.generarProgramacionesRiego(fincaDosTablones)
    val progsPar = rPar.generarProgramacionesRiegoPar(fincaDosTablones)

    progsPar.length shouldBe 2
    progsPar should contain theSameElementsAs progsSeq
  }

  test("generarProgramacionesRiegoPar - debe generar 6 permutaciones (3 tablones)") {
    val fincaTresTablones: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3))

    val progsSeq = r.generarProgramacionesRiego(fincaTresTablones)
    val progsPar = rPar.generarProgramacionesRiegoPar(fincaTresTablones)

    progsPar.length shouldBe 6
    progsPar should contain theSameElementsAs progsSeq
  }

  test("generarProgramacionesRiegoPar - todas las programaciones deben ser permutaciones válidas") {
    val finca: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3), (10, 4, 2))
    val progsPar = rPar.generarProgramacionesRiegoPar(finca)

    progsPar.length shouldBe 24 // 4! = 24

    progsPar.foreach { prog =>
      prog.sorted shouldBe Vector(0, 1, 2, 3)
      prog.distinct.length shouldBe 4
    }
  }

  // ============================================================
  // TESTS PARA ProgramacionRiegoOptimoPar
  // ============================================================

  test("ProgramacionRiegoOptimoPar - debe dar resultado consistente con versión secuencial (finca vacía)") {
    val fincaVacia: Finca = Vector()
    val distVacia: Distancia = Vector()

    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(fincaVacia, distVacia)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(fincaVacia, distVacia)

    optimaPar shouldBe optimaSeq
    costoPar shouldBe costoSeq
  }

  test("ProgramacionRiegoOptimoPar - debe dar resultado consistente (1 tablón)") {
    val fincaUnTablon: Finca = Vector((10, 3, 4))
    val distUnica: Distancia = Vector(Vector(0))

    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(fincaUnTablon, distUnica)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(fincaUnTablon, distUnica)

    costoPar shouldBe costoSeq
  }

  test("ProgramacionRiegoOptimoPar - debe encontrar el óptimo (Ejemplo 1)") {
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(fincaEjemplo1, distanciaEjemplo1)

    // Verificar que es una permutación válida
    optimaPar.sorted shouldBe Vector(0, 1, 2, 3, 4)
    optimaPar.distinct.length shouldBe 5

    // Verificar que el costo es el calculado
    val costoCalculado = rPar.costoRiegoFincaPar(fincaEjemplo1, optimaPar) +
      rPar.costoMovilidadPar(fincaEjemplo1, optimaPar, distanciaEjemplo1)
    costoPar shouldBe costoCalculado

    // Verificar que es mejor o igual que las programaciones del enunciado
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costoPi1 = r.costoRiegoFinca(fincaEjemplo1, pi1) +
      r.costoMovilidad(fincaEjemplo1, pi1, distanciaEjemplo1)

    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costoPi2 = r.costoRiegoFinca(fincaEjemplo1, pi2) +
      r.costoMovilidad(fincaEjemplo1, pi2, distanciaEjemplo1)

    costoPar should be <= costoPi1
    costoPar should be <= costoPi2
  }

  test("ProgramacionRiegoOptimoPar - debe dar el mismo costo que versión secuencial (Ejemplo 1)") {
    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(fincaEjemplo1, distanciaEjemplo1)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(fincaEjemplo1, distanciaEjemplo1)

    costoPar shouldBe costoSeq
  }

  test("ProgramacionRiegoOptimoPar - debe dar el mismo costo que versión secuencial (Ejemplo 2)") {
    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(fincaEjemplo2, distanciaEjemplo2)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(fincaEjemplo2, distanciaEjemplo2)

    costoPar shouldBe costoSeq
  }

  test("ProgramacionRiegoOptimoPar - debe encontrar el mínimo global (finca pequeña)") {
    val fincaPequeña: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3))
    val distPequeña: Distancia = Vector(
      Vector(0, 2, 3),
      Vector(2, 0, 4),
      Vector(3, 4, 0)
    )

    val (optimaPar, costoOptimoPar) = rPar.ProgramacionRiegoOptimoPar(fincaPequeña, distPequeña)

    // Verificar que encontró el mínimo global
    val todasProgramaciones = r.generarProgramacionesRiego(fincaPequeña)
    val costosTotales = todasProgramaciones.map { prog =>
      r.costoRiegoFinca(fincaPequeña, prog) + r.costoMovilidad(fincaPequeña, prog, distPequeña)
    }

    costoOptimoPar shouldBe costosTotales.min
  }

  test("ProgramacionRiegoOptimoPar - consistencia entre todas las versiones") {
    val finca: Finca = Vector((7, 2, 2), (9, 3, 1), (5, 1, 4))
    val dist: Distancia = Vector(
      Vector(0, 3, 2),
      Vector(3, 0, 5),
      Vector(2, 5, 0)
    )

    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(finca, dist)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(finca, dist)

    // Los costos deben ser iguales (ambos deben encontrar el óptimo)
    costoPar shouldBe costoSeq

    // Verificar que ambas soluciones son óptimas
    val todasProgramaciones = r.generarProgramacionesRiego(finca)
    val costoMinimo = todasProgramaciones.map { prog =>
      r.costoRiegoFinca(finca, prog) + r.costoMovilidad(finca, prog, dist)
    }.min

    costoPar shouldBe costoMinimo
    costoSeq shouldBe costoMinimo
  }

  // ============================================================
  // TESTS DE INTEGRACIÓN
  // ============================================================

  test("Integración - todas las funciones paralelas dan resultados consistentes") {
    val finca: Finca = r.fincaAlAzar(4)
    val dist: Distancia = r.distanciaAlAzar(4)

    val (optimaSeq, costoSeq) = r.ProgramacionRiegoOptimo(finca, dist)
    val (optimaPar, costoPar) = rPar.ProgramacionRiegoOptimoPar(finca, dist)

    // Los costos óptimos deben ser iguales
    costoPar shouldBe costoSeq

    // Verificar costos parciales
    val costoRiegoSeq = r.costoRiegoFinca(finca, optimaSeq)
    val costoRiegoPar = rPar.costoRiegoFincaPar(finca, optimaPar)
    val costoMovSeq = r.costoMovilidad(finca, optimaSeq, dist)
    val costoMovPar = rPar.costoMovilidadPar(finca, optimaPar, dist)

    costoRiegoPar + costoMovPar shouldBe costoPar
  }
}