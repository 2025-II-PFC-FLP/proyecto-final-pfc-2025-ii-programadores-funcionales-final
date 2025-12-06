package taller
import scala.util.Random

class RiegoOptimo {

  type Tablon = ( Int , Int , Int )
  type Finca = Vector [ Tablon ]
  type Distancia = Vector [ Vector [ Int ] ]
  type ProgRiego = Vector [ Int ]
  type TiempoInicioRiego = Vector [ Int ]

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

  def tsup ( f : Finca , i : Int ) : Int = f ( i ) . _1
  def treg ( f : Finca , i : Int ) : Int = f ( i ) . _2
  def prio ( f : Finca , i : Int ) : Int = f ( i ) . _3

  def tIR ( f : Finca , pi : ProgRiego ) : TiempoInicioRiego = {
    val n = f.length
    val (tiempos, _) = pi.foldLeft((Vector.fill(n)(0), 0)) {
      case ((acc, tActual), idx) =>
        val nuevoAcc = acc.updated(idx, tActual)
        val nuevoT = tActual + treg(f, idx)
        (nuevoAcc, nuevoT)
    }
    tiempos
  }

  def costoRiegoTablon ( i : Int , f : Finca , pi : ProgRiego ) : Int = {

    val tiempos = tIR(f, pi)
    val ts = tsup(f, i)
    val tr = treg(f, i)
    val p  = prio(f, i)
    val tInicio = tiempos(i)
    val tFin = tInicio + tr

    if (ts - tr >= tInicio)
      ts - tFin
    else
      p * (tFin - ts)
  }


  def costoMovilidad ( f : Finca , pi : ProgRiego , d: Distancia ) : Int = {
    (0 until pi.length - 1).map { j =>
      val actual = pi(j)
      val siguiente = pi(j + 1)
      d(actual)(siguiente)
    }.sum
  }




}
