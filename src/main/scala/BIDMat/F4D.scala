//-*-coding:utf-8-*-
package BIDMat
import MatFunctions._
import edu.berkeley.bid.CBLAS._
import java.util.Arrays
import java.util.concurrent.atomic._
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global


case class F4D(val d1:Int, val d2:Int, val d3:Int, val d4:Int, val data:Array[Float]) extends A4D(d1, d2, d3, d4) { 

  def apply(indx:Int):Float = { 
    if (indx < length) { 
      data(indx)
    } else { 
      throw new RuntimeException("F4D index out of range")
    }
  }

  def apply(x:Int, y:Int, z:Int, w:Int):Float = { 
    val indx = x + dim1 * (y + dim2 * (z + dim3 * w))
    if (indx < length) { 
      data(indx)
    } else { 
      throw new RuntimeException("F4D indices out of range")
    }
  }
  
  def apply(ii:IMat, jj:IMat, kk:IMat, mm:IMat):F4D = {
  		val nx = ii match {case aa:MatrixWildcard => dim1; case _ => ii.length}
  		val ny = jj match {case aa:MatrixWildcard => dim2; case _ => jj.length}
  		val nz = kk match {case aa:MatrixWildcard => dim3; case _ => kk.length}
  		val nw = mm match {case aa:MatrixWildcard => dim4; case _ => mm.length}
  		val out = F4D(nx, ny, nz, nw);
  		var m = 0;
  		while (m < nw) {
  			val mval = mm match {
  			case aa:MatrixWildcard => m
  			case _ => mm.data(m)
  			}
  			var k = 0;
  			while (k < nz) {
  				val kval = kk match {
  				case aa:MatrixWildcard => k
  				case _ => kk.data(k)
  				}
  				var j = 0;
  				while (j < ny) {
  					val jval = jj match {
  					case aa:MatrixWildcard => j
  					case _ => jj.data(j)
  					}
  					val base = nx * (j + ny * (k + nz * m));
  					val xbase = dim1 * (jval + dim2 * (kval + dim3 * mval));
  					var i = 0;
  					ii match {
  					case aa:MatrixWildcard => {
  						while (i < nz) {
  							out.data(i + base) = data(i + xbase);
  							i += 1
  						}
  					}
  					case _ => {
  						while (i < nz) {
  							out.data(i + base) = data(ii.data(i) + xbase);
  							i += 1;
  						}
  					}
  					}
  					j += 1
  				}
  				k += 1
  			}
  			m += 1
  		}
  		out
  }


  def update(indx:Int, v:Float) = { 
    if (indx < 0 || indx < length) { 
      data(indx) = v
    } else { 
      throw new RuntimeException("F4D index out of range")
    }
    this
  }

  def update(x:Int, y:Int, z:Int, w:Int, v:Float) = { 
    val indx = x + dim1 * (y + dim2 * (z + dim3 * w))
    if (indx < 0 || indx < length) { 
      data(indx) = v
    } else { 
      throw new RuntimeException("F4D indices out of range")
    }
    this
  }
  
  override def update(ii:IMat, jj:IMat, kk:IMat, mm:IMat, vv:A4D):F4D = {
    vv match {
      case uu:F4D => update(ii, jj, kk, mm, uu);
      case _ => throw new RuntimeException("F4D illegal assignment RHS")
    }
  }

  def update(ii:IMat, jj:IMat, kk:IMat, mm:IMat, vv:F4D):F4D = {
  	val nx = ii match {case aa:MatrixWildcard => dim1; case _ => ii.length}
  	val ny = jj match {case aa:MatrixWildcard => dim2; case _ => jj.length}
  	val nz = kk match {case aa:MatrixWildcard => dim3; case _ => kk.length}
  	val nw = mm match {case aa:MatrixWildcard => dim4; case _ => mm.length}
  	if (nx != vv.dim1 || ny != vv.dim2 || nz != vv.dim3 || nw != vv.dim4) {
  		throw new RuntimeException("F4D update dimensions mismatch")
  	}
  	var m = 0;
  	while (m < nw) {
  		val mval = mm match {
  		case aa:MatrixWildcard => m
  		case _ => mm.data(m)
  		}
  		var k = 0;
  		while (k < nz) {
  			val kval = kk match {
  			case aa:MatrixWildcard => k
  			case _ => kk.data(k)
  			}
  			var j = 0;
  			while (j < ny) {
  				val jval = jj match {
  				case aa:MatrixWildcard => j
  				case _ => jj.data(j)
  				}
  				val base = nx * (j + ny * (k + nz * m));
  				val xbase = dim1 * (jval + dim2 * (kval + dim3 * mval));
  				var i = 0;
  				ii match {
  				case aa:MatrixWildcard => {
  					while (i < nz) {
  						data(i + xbase) = vv.data(i + base)
  								i += 1
  					}
  				}
  				case _ => {
  					while (i < nz) {
  						data(ii.data(i) + xbase) = vv.data(i + base);
  						i += 1;
  					}
  				}
  				}
  				j += 1
  			}
  			k += 1
  		}
  		m += 1
  	}
  	this
  }
  
  def update(ii:IMat, jj:IMat, kk:IMat, mm:IMat, v:Float):F4D = {
  	val nx = ii match {case aa:MatrixWildcard => dim1; case _ => ii.length}
  	val ny = jj match {case aa:MatrixWildcard => dim2; case _ => jj.length}
  	val nz = kk match {case aa:MatrixWildcard => dim3; case _ => kk.length}
  	val nw = mm match {case aa:MatrixWildcard => dim4; case _ => mm.length}
  	var m = 0;
  	while (m < nw) {
  		val mval = mm match {
  		case aa:MatrixWildcard => m
  		case _ => mm.data(m)
  		}
  		var k = 0;
  		while (k < nz) {
  			val kval = kk match {
  			case aa:MatrixWildcard => k
  			case _ => kk.data(k)
  			}
  			var j = 0;
  			while (j < ny) {
  				val jval = jj match {
  				case aa:MatrixWildcard => j
  				case _ => jj.data(j)
  				}
  				val xbase = dim1 * (jval + dim2 * (kval + dim3 * mval));
  				var i = 0;
  				ii match {
  				case aa:MatrixWildcard => {
  					while (i < nz) {
  						data(i + xbase) = v;
  								i += 1
  					}
  				}
  				case _ => {
  					while (i < nz) {
  						data(ii.data(i) + xbase) = v;
  						i += 1;
  					}
  				}
  				}
  				j += 1
  			}
  			k += 1
  		}
  		m += 1
  	}
  	this
  }

  def permute(p1:Int, p2:Int, p3:Int, p4:Int):F4D = permute(irow(p1,p2,p3,p4))

  def permute(perm0:IMat):F4D = { 
    if (perm0.length != 4) { 
      throw new RuntimeException("F4D permute bad permutation ")
    }
    val perm = perm0.copy
    val dims = irow(dim1, dim2, dim3, dim4)
    val iperm = invperm(perm)
    var out = F4D(dims(iperm(0)), dims(iperm(1)), dims(iperm(2)), dims(iperm(3)))
    var out2 = F4D(dims(iperm(0)), dims(iperm(1)), dims(iperm(2)), dims(iperm(3)))
    System.arraycopy(data, 0, out.data, 0, length)
    for (i <- 3 until 0 by -1) { 
      if (perm(i) != i) { 
        val (d1, d2, d3) = A3D.getDims(i, perm, dims)
        if (d1 > 1 && d2 > 1) { 
          spermute(d1, d2, d3, out.data, out2.data)
          val tmp = out2
          out2 = out
          out = tmp
        }
        A3D.rotate(i, perm, dims)
      } 
    }
    out
  }

}

object F4D {
  
  def apply(dim1:Int, dim2:Int, dim3:Int, dim4:Int) = new F4D(dim1, dim2, dim3, dim4, new Array[Float](dim1*dim2*dim3*dim4))

  def apply(dim1:Int, dim2:Int, dim3:Int, dim4:Int, f:FMat):F4D = { 
	  if (dim1*dim2*dim3*dim4 == f.length) { 
		  new F4D(dim1, dim2, dim3, dim4, f.data)
	  } else { 
		  throw new RuntimeException("A4D input matrix size mismatch")
	  }
  } 
}






