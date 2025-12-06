package taller

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class RiegoOptimoTest extends AnyFunSuite{

  val r = new RiegoOptimo()


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
    val f = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.tsup(f,0) == 10)
    assert(r.tsup(f,1) == 7)
    assert(r.tsup(f,2) == 12)
  }

  test("treg devuelve el tiempo de riego correcto") {
    val f = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.treg(f,0) == 5)
    assert(r.treg(f,1) == 2)
    assert(r.treg(f,2) == 3)
  }

  test("prio devuelve la prioridad correcta") {
    val f = Vector((10,5,3), (7,2,1), (12,3,4))
    assert(r.prio(f,0) == 3)
    assert(r.prio(f,1) == 1)
    assert(r.prio(f,2) == 4)
  }

  test("tIR calcula correctamente los tiempos de inicio") {
    val f = Vector((10,3,2), (8,2,1), (6,4,3))
    val pi = Vector(2,0,1)

    val res = r.tIR(f, pi)

    assert(res(2) == 0)
    assert(res(0) == 4)
    assert(res(1) == 7)
  }

  test("costoRiegoTablon devuelve costo 0 cuando se riega sin retraso") {
    val f = Vector((10,3,2))
    val pi = Vector(0)
    assert(r.costoRiegoTablon(0, f, pi) == 10 - (0+3))
  }

  test("costoRiegoTablon penaliza cuando llega tarde") {
    val f = Vector((5,4,3))
    val pi = Vector(0)
    assert(r.costoRiegoTablon(0, f, pi) == 1)
  }

  test("CostoMovilidad suma distancia entre tablones según pi") {
    val f = Vector((5,3,2), (6,2,1), (8,1,3))
    val pi = Vector(0,2,1)

    val d = Vector(
      Vector(0, 5, 3),
      Vector(5, 0, 4),
      Vector(3, 4, 0)
    )

    assert(r.costoMovilidad(f, pi, d) == 7)
  }


}
