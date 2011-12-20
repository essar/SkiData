package uk.co.essarsoftware.ski.data;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  30-Nov-11 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Class representing a single item of ski-GPS data.</p>
 * <p>Holds latitude and longitude, Cartesian X and Y, altitude and speed. <tt>Datum</tt> objects can
 * also be chained together in a linked-list to represent data paths.</p>
 * 
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30-Nov-11)
 */
class Datum implements Serializable
{
	/**
	 * Unique serializable identifier.
	 */
	private static final long serialVersionUID = 8133578513511037408L;
	
	// Holds the next chained datum object
	private Datum next;
	/** Long holding GPS time in seconds. */
	final long t;
	
	/** Float holding GPS latitude. */
	final float la;
	/** Float holding GPS longitude. */
	final float lo;
	
	/** Float holding GPS speed. */
	final float s;
	
	/** Integer holding GPS altitude. */
	final int a;
	
	/** Integer holding Cartesian X coordinate. */
	final int x;
	
	/** Integer holding Cartesian Y coordinate. */
	final int y;
	
	/**
	 * Create a new <tt>Datum</tt> object, holding data from a GPS point, using a <tt>Date</tt> object.
	 * @param t GPS Date.
	 * @param la GPS latitude.
	 * @param lo GPS longitude.
	 * @param x Cartesian X coordinate.
	 * @param y Cartesian Y coordinate
	 * @param a GPS altitude.
	 * @param s GPS speed.
	 */
	Datum(Date t, float la, float lo, int x, int y, int a, float s) {
		this(t.getTime() / 1000, la, lo, x, y, a, s);
	}
	
	/*Datum(long t, float la, float lo, int a, float s) {
		// Perform coordinate conversion
		UTMCoordinate utm = CoordConverter.WGS2UTM(new WGSCoordinate(la, lo, WGSCoordinate.COORD_MODE_DEG));
		this(t, la, lo, utm.getX(), utm.getY(), a, s);
	}*/
	
	/**
	 * Create a new <tt>Datum</tt> object, holding data from a GPS point, using a GPS time stamp.
	 * @param t GPS time in seconds.
	 * @param la GPS latitude.
	 * @param lo GPS longitude.
	 * @param x Cartesian X coordinate.
	 * @param y Cartesian Y coordinate
	 * @param a GPS altitude.
	 * @param s GPS speed.
	 */
	Datum(long t, float la, float lo, int x, int y, int a, float s) {
		this.t = t;
		this.la = la;
		this.lo = lo;
		this.x = x;
		this.y = y;
		this.a = a;
		this.s = s;
	}
	
	/**
	 * Retrieve the next GPS datum point in the list.
	 * @return the <tt>Datum</tt> object that is after this in the path.
	 */
	Datum getNext() {
		return next;
	}
	
	/**
	 * Set the next GPS datum point in the list. 
	 * @param next a <tt>Datum</tt> object that is after this in the path.
	 */
	void setNext(Datum next) {
		this.next = next;
	}
	
	/**
	 * Get the GPS time as a Date object.
	 * @return GPS time.
	 */
	public Date getTimeAsDate() {
		return (new Date(t * 1000L));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("[%d]:(%2.5f,%2.5f):(%d,%d):%d:%2.2f", t, la, lo, x, y, a, s);
	}
}
