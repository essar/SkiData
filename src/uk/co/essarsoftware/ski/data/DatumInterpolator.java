package uk.co.essarsoftware.ski.data;


/**
 * Interpolates between missing points in a data list. 
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (1 Dec 2011)
 */
class DatumInterpolator
{
	static int ct = 0;
	
	// Look at the time of each of the input points
	// If time gap between one point and the next is greater than one, average out values
	
	/**
	 * Linearly interpolate between two floating point numbers.
	 */
	private static float linearInterpolate(float f1, float f2) {
		return f1 + ((f2 - f1) / 2);
	}
	
	/**
	 * Linearly interpolate between two integer numbers, rounded down to the nearest integer value.
	 */
	private static int linearInterpolate(int i1, int i2) {
		return i1 + ((i2 - i1) / 2);
	}
	
	/**
	 * Linearly interpolate between two long numbers, rounded down to the nearest long integer value.
	 */
	private static long linearInterpolate(long l1, long l2) {
		return l1 + ((l2 - l1) / 2);
	}

	/**
	 * Linearly interpolate the x, y, latitude, longitude, altitude, speed and time of two Datum points.
	 * If the x, y and altitude change between the two points is unchanged, speed is forced to 0. 
	 */
	private static Datum linearInterpolate(Datum p1, Datum p2) {
		long t = linearInterpolate(p1.t, p2.t);
		float la = linearInterpolate(p1.la, p2.la);
		float lo = linearInterpolate(p1.lo, p2.lo);
		int x = linearInterpolate(p1.x, p2.x);
		int y = linearInterpolate(p1.y, p2.y);
		int a = linearInterpolate(p1.a, p2.a);
		// Force speed to zero if overall movement is zero
		double d = Math.sqrt(Math.pow(x - p1.x, 2) + Math.pow(y - p1.y, 2));
		float s = (d == 0 ? 0.0f : linearInterpolate(p1.s, p2.s));
		
		return new Datum(t, la, lo, x, y, a, s);
	}
	
	/**
	 * Interpolate across a set of <tt>Datum</tt> points, filling any gaps.
	 * @param firstPoint the first point of the list to interpolate.
	 */
	static int interpolateList(Datum firstPoint) {
		return interpolateList(firstPoint, false);
	}
	
	/**
	 * Interpolate across a set of <tt>Datum</tt> points, filling any gaps and optionally removing duplicate points.
	 * @param firstPoint the first <tt>Datum</tt> point in a linked path.
	 * @param removeDups whether to remove duplicated points (by time stamp).
	 * @return the new length of the datum path.
	 */
	static int interpolateList(Datum firstPoint, boolean removeDups) {
		Datum thisPoint = firstPoint;
		int len = 1;
		while(thisPoint.getNext() != null) {
			int timeDelta = (int) (thisPoint.getNext().t - thisPoint.t);
			if(removeDups && timeDelta <= 0) {
				// Remove the point and re-calculate
				thisPoint.setNext(thisPoint.getNext().getNext());
				len --;
			} else {
				if(timeDelta < 0) {
					System.out.println(" ** WARN! Negative time delta (" + timeDelta + ") at position " + len);
				}
				while(timeDelta > 1) {
					// Create a new interpolated point and insert into list
					Datum newPoint = linearInterpolate(thisPoint, thisPoint.getNext());
					newPoint.setNext(thisPoint.getNext());
					thisPoint.setNext(newPoint);
					// Increase interpolation count
					ct ++;

					// Recalculate delta
					timeDelta = (int) (thisPoint.getNext().t - thisPoint.t);
				}
				
				// Move to next point
				thisPoint = thisPoint.getNext();
				len ++;
			}
		}
		return len;
	}
}
