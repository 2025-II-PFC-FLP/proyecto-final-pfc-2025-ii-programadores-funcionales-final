package taller

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.matchers.should.Matchers

@RunWith(classOf[JUnitRunner])
class RiegoOptimoTest extends AnyFunSuite with Matchers {

  val r = new RiegoOptimo()

  // Importar los tipos type alias de r
  import r.{Finca, Distancia, ProgRiego, TiempoInicioRiego, Tablon}

  test("fincaAlAzar: tamaño correcto") {
    val f = r.fincaAlAzar(5)
    assert(f.length == 5)
  }

  test("fincaAlAzar: ts dentro del rango") {
    val f = r.fincaAlAzar(4)
    assert(f.forall(t => t._1 >= 1 && t._1 <= 8))
  }

  test("fincaAlAzar: tr dentro del rango") {
    val f = r.fincaAlAzar(4)
    assert(f.forall(t => t._2 >= 1 && t._2 <= 4))
  }

  test("fincaAlAzar: prioridad dentro del rango 1-4") {
    val f = r.fincaAlAzar(6)
    assert(f.forall(t => t._3 >= 1 && t._3 <= 4))
  }

  test("fincaAlAzar: todas las tuplas tienen 3 valores") {
    val f = r.fincaAlAzar(5)
    assert(f.forall(t => t.productArity == 3))
  }

  test("distanciaAlAzar: matriz cuadrada") {
    val d = r.distanciaAlAzar(4)
    assert(d.length == 4)
    assert(d.forall(_.length == 4))
  }

  test("distanciaAlAzar: diagonal es cero") {
    val d = r.distanciaAlAzar(5)
    assert((0 until 5).forall(i => d(i)(i) == 0))
  }

  test("distanciaAlAzar: simétrica") {
    val d = r.distanciaAlAzar(5)
    assert((0 until 5).forall(i =>
      (0 until 5).forall(j => d(i)(j) == d(j)(i))
    ))
  }

  test("distanciaAlAzar: valores positivos fuera de la diagonal") {
    val d = r.distanciaAlAzar(4)
    assert((0 until 4).forall(i =>
      (0 until 4).forall(j => if (i != j) d(i)(j) > 0 else true)
    ))
  }

  test("distanciaAlAzar: no genera negativos") {
    val d = r.distanciaAlAzar(6)
    assert(d.flatten.forall(_ >= 0))
  }

  test("tsup devuelve el tiempo de supervivencia correcto") {
    val f: Finca = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.tsup(f,0) == 10)
    assert(r.tsup(f,1) == 7)
    assert(r.tsup(f,2) == 12)
  }

  test("treg devuelve el tiempo de riego correcto") {
    val f: Finca = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.treg(f,0) == 5)
    assert(r.treg(f,1) == 2)
    assert(r.treg(f,2) == 3)
  }

  test("prio devuelve la prioridad correcta") {
    val f: Finca = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.prio(f,0) == 3)
    assert(r.prio(f,1) == 1)
    assert(r.prio(f,2) == 4)
  }

  test("tIR calcula correctamente los tiempos de inicio") {
    val f: Finca = Vector((10,3,2), (8,2,1), (6,4,3))
    val pi: ProgRiego = Vector(2,0,1)
    val res = r.tIR(f, pi)
    assert(res(2) == 0)
    assert(res(0) == 4)
    assert(res(1) == 7)
  }

  test("costoRiegoTablon devuelve costo 0 cuando se riega sin retraso") {
    val f: Finca = Vector((10,3,2))
    val pi: ProgRiego = Vector(0)
    assert(r.costoRiegoTablon(0, f, pi) == 10 - (0+3))
  }

  test("costoRiegoTablon penaliza cuando llega tarde") {
    val f: Finca = Vector((5,4,3))
    val pi: ProgRiego = Vector(0)
    assert(r.costoRiegoTablon(0, f, pi) == 1)
  }

  test("CostoMovilidad suma distancia entre tablones según pi") {
    val f: Finca = Vector((5,3,2), (6,2,1), (8,1,3))
    val pi: ProgRiego = Vector(0,2,1)
    val d: Distancia = Vector(
      Vector(0, 5, 3),
      Vector(5, 0, 4),
      Vector(3, 4, 0)
    )
    assert(r.costoMovilidad(f, pi, d) == 7)
  }

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

  test("costoRiegoTablon - Ejemplo 1, tablón 0, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoTablon(0, fincaEjemplo1, pi1)
    costo shouldBe 7
  }

  test("costoRiegoTablon - Ejemplo 1, tablón 1, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoTablon(1, fincaEjemplo1, pi1)
    costo shouldBe 3
  }

  test("costoRiegoTablon - Ejemplo 1, tablón 2, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoTablon(2, fincaEjemplo1, pi1)
    costo shouldBe 12
  }

  test("costoRiegoTablon - Ejemplo 1, tablón 3, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoTablon(3, fincaEjemplo1, pi1)
    costo shouldBe 1
  }

  test("costoRiegoTablon - Ejemplo 1, tablón 4, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoTablon(4, fincaEjemplo1, pi1)
    costo shouldBe 16
  }

  test("costoRiegoFinca - Ejemplo 1, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoRiegoFinca(fincaEjemplo1, pi1)
    costo shouldBe 39
  }

  test("costoRiegoFinca - Ejemplo 1, programación Π2 = ⟨2,1,4,3,0⟩") {
    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costo = r.costoRiegoFinca(fincaEjemplo1, pi2)
    costo shouldBe 20
  }

  test("costoRiegoFinca - Ejemplo 2, programación Π1 = ⟨2,1,4,3,0⟩") {
    val pi1: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costo = r.costoRiegoFinca(fincaEjemplo2, pi1)
    costo shouldBe 24
  }

  test("costoMovilidad - Ejemplo 1, programación Π1 = ⟨0,1,4,2,3⟩") {
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costo = r.costoMovilidad(fincaEjemplo1, pi1, distanciaEjemplo1)
    costo shouldBe 12
  }

  test("costoMovilidad - Ejemplo 1, programación Π2 = ⟨2,1,4,3,0⟩") {
    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costo = r.costoMovilidad(fincaEjemplo1, pi2, distanciaEjemplo1)
    costo shouldBe 18
  }

  test("costoMovilidad - Ejemplo 2, programación Π1 = ⟨2,1,4,3,0⟩") {
    val pi1: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costo = r.costoMovilidad(fincaEjemplo2, pi1, distanciaEjemplo2)
    costo shouldBe 18
  }

  test("costoMovilidad - programación vacía") {
    val fincaVacia: Finca = Vector()
    val progVacia: ProgRiego = Vector()
    val distVacia: Distancia = Vector()
    val costo = r.costoMovilidad(fincaVacia, progVacia, distVacia)
    costo shouldBe 0
  }

  test("costoMovilidad - programación con un solo tablón") {
    val fincaUnica: Finca = Vector((10, 3, 4))
    val progUnica: ProgRiego = Vector(0)
    val distUnica: Distancia = Vector(Vector(0))
    val costo = r.costoMovilidad(fincaUnica, progUnica, distUnica)
    costo shouldBe 0
  }

  test("generarProgramacionesRiego - finca vacía") {
    val fincaVacia: Finca = Vector()
    val programaciones = r.generarProgramacionesRiego(fincaVacia)
    programaciones shouldBe Vector(Vector())
  }

  test("generarProgramacionesRiego - finca con 1 tablón") {
    val fincaUnTablon: Finca = Vector((5, 2, 1))
    val programaciones = r.generarProgramacionesRiego(fincaUnTablon)
    programaciones should contain theSameElementsAs Vector(Vector(0))
  }

  test("generarProgramacionesRiego - finca con 2 tablones") {
    val fincaDosTablones: Finca = Vector((5, 2, 1), (8, 3, 2))
    val programaciones = r.generarProgramacionesRiego(fincaDosTablones)
    val esperadas = Vector(Vector(0, 1), Vector(1, 0))
    programaciones should contain theSameElementsAs esperadas
  }

  test("generarProgramacionesRiego - finca con 3 tablones, número de permutaciones") {
    val fincaTresTablones: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3))
    val programaciones = r.generarProgramacionesRiego(fincaTresTablones)
    programaciones.length shouldBe 6
  }

  test("generarProgramacionesRiego - todas las programaciones son permutaciones válidas") {
    val finca: Finca = Vector((5, 2, 1), (8, 3, 2), (6, 1, 3))
    val programaciones = r.generarProgramacionesRiego(finca)
    programaciones.foreach { prog =>
      prog.sorted shouldBe Vector(0, 1, 2)
      prog.distinct.length shouldBe 3
    }
  }

  test("ProgramacionRiegoOptimo - finca vacía") {
    val fincaVacia: Finca = Vector()
    val distVacia: Distancia = Vector()
    val (optima, costo) = r.ProgramacionRiegoOptimo(fincaVacia, distVacia)
    optima shouldBe Vector()
    costo shouldBe 0
  }

  test("ProgramacionRiegoOptimo - finca con 1 tablón") {
    val fincaUnTablon: Finca = Vector((10, 3, 4))
    val distUnica: Distancia = Vector(Vector(0))
    val (optima, costo) = r.ProgramacionRiegoOptimo(fincaUnTablon, distUnica)
    optima shouldBe Vector(0)
    costo shouldBe 7
  }

  test("ProgramacionRiegoOptimo - Ejemplo 1 del enunciado (busca mejor que Π1 y Π2)") {
    val (optima, costo) = r.ProgramacionRiegoOptimo(fincaEjemplo1, distanciaEjemplo1)
    optima.sorted shouldBe Vector(0, 1, 2, 3, 4)
    optima.distinct.length shouldBe 5
    val costoCalculado = r.costoRiegoFinca(fincaEjemplo1, optima) +
      r.costoMovilidad(fincaEjemplo1, optima, distanciaEjemplo1)
    costo shouldBe costoCalculado
    val pi1: ProgRiego = Vector(0, 1, 4, 2, 3)
    val costoPi1 = r.costoRiegoFinca(fincaEjemplo1, pi1) +
      r.costoMovilidad(fincaEjemplo1, pi1, distanciaEjemplo1)
    val pi2: ProgRiego = Vector(2, 1, 4, 3, 0)
    val costoPi2 = r.costoRiegoFinca(fincaEjemplo1, pi2) +
      r.costoMovilidad(fincaEjemplo1, pi2, distanciaEjemplo1)
    costo should be <= costoPi1
    costo should be <= costoPi2
    costo should be <= 38
  }

  test("ProgramacionRiegoOptimoV2 - debe dar mismo resultado que ProgramacionRiegoOptimo") {
    val (optima1, costo1) = r.ProgramacionRiegoOptimo(fincaEjemplo1, distanciaEjemplo1)
    val (optima2, costo2) = r.ProgramacionRiegoOptimoV2(fincaEjemplo1, distanciaEjemplo1)
    val costoOptima1 = r.costoRiegoFinca(fincaEjemplo1, optima1) +
      r.costoMovilidad(fincaEjemplo1, optima1, distanciaEjemplo1)
    val costoOptima2 = r.costoRiegoFinca(fincaEjemplo1, optima2) +
      r.costoMovilidad(fincaEjemplo1, optima2, distanciaEjemplo1)
    costoOptima1 shouldBe costoOptima2
    costo1 shouldBe costo2
  }

  test("ProgramacionRiegoOptimo - propiedades de optimalidad") {
    val fincaPequeña: Finca = Vector((5, 2, 1), (8, 3, 2))
    val distPequeña: Distancia = Vector(
      Vector(0, 2),
      Vector(2, 0)
    )
    val (optima, costoOptimo) = r.ProgramacionRiegoOptimo(fincaPequeña, distPequeña)
    val todasProgramaciones = r.generarProgramacionesRiego(fincaPequeña)
    val costosTotales = todasProgramaciones.map { prog =>
      r.costoRiegoFinca(fincaPequeña, prog) + r.costoMovilidad(fincaPequeña, prog, distPequeña)
    }
    costoOptimo shouldBe costosTotales.min
    val costoVerificado = r.costoRiegoFinca(fincaPequeña, optima) +
      r.costoMovilidad(fincaPequeña, optima, distPequeña)
    costoVerificado shouldBe costoOptimo
  }

  test("tIR - cálculo correcto de tiempos de inicio") {
    val finca: Finca = Vector((10, 3, 4), (5, 2, 2))
    val prog: ProgRiego = Vector(0, 1)
    val tiempos = r.tIR(finca, prog)
    tiempos shouldBe Vector(0, 3)
  }

  test("tIR - programación invertida") {
    val finca: Finca = Vector((10, 3, 4), (5, 2, 2))
    val prog: ProgRiego = Vector(1, 0)
    val tiempos = r.tIR(finca, prog)
    tiempos shouldBe Vector(2, 0)
  }
}