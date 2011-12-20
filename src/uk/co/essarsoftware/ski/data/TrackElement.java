package uk.co.essarsoftware.ski.data;

import java.io.Serializable;


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

/**
 * <p>Class representing single point within a GPS track.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class TrackElement implements Serializable
{
	/**
	 * Unique serializable identifier. 
	 */
	private static final long serialVersionUID = -5861116977819301524L;
	
	// Element mode
	private Mode mode;
	// GPS data
	private final Datum datum;
	// Aggregate data
	private final int da, dx, dy;
	private final float dist;
	
	/**
	 * Create new <tt>TrackElement</tt>.
	 * @param datum Containing GPS data.
	 */
	TrackElement(Datum datum) {
		// Check if point is end of the chain
		if(datum.getNext() == null) {
			this.datum = datum;
			// Set aggregate values to zero
			dx = 0;
			dy = 0;
			dist = 0.0f;
			da = 0;
		} else {
			this.datum = datum.getNext();
			// Calculate aggregate values
			dx = datum.getNext().x - datum.x;
			dy = datum.getNext().y - datum.y;
			dist = (float) Math.sqrt((dx * dx) + (dy * dy));
			da = datum.getNext().a - datum.a;
		}
	}
	
	/**
	 * Set the element mode.
	 * @param mode the element mode.
	 */
	void setMode(Mode mode) {
		this.mode = mode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj instanceof TrackElement) {
			TrackElement elem = (TrackElement) obj;
			return getTime() == elem.getTime();
		}
		return false;
	}
	
	/**
	 * Get the altitude.
	 * @return the altitude in metres.
	 */
	public int getAltitude() {
		return datum.a;
	}
	
	/**
	 * Get altitude delta.
	 * @return the change in altitude in metres.
	 */
	public int getAltitudeChange() {
		return da;
	}
	
	/**
	 * Get distance.
	 * @return distance travelled in metres.
	 */
	public float getDistance() {
		return dist;
	}
	
	/**
	 * Get GPS latitude.
	 * @return GPS latitude.
	 */
	public float getLatitude() {
		return datum.la;
	}
	
	/**
	 * Get GPS longitude.
	 * @return GPS longitude.
	 */
	public float getLongitude() {
		return datum.lo;
	}
	
	/**
	 * Get element mode.
	 * @return element mode.
	 */
	public Mode getMode() {
		return mode;
	}
	
	/**
	 * Get GPS speed.
	 * @return GPS speed in kph.
	 */
	public float getSpeed() {
		return datum.s;
	}
	
	/**
	 * Get GPS time stamp;
	 * @return GPS time stamp in seconds;
	 */
	public long getTime() {
		return datum.t;
	}
	
	/**
	 * Get X coordinate.
	 * @return x coordinate.
	 */
	public int getX() {
		return datum.x;
	}
	
	/**
	 * Get Y coordinate.
	 * @return y coordinate.
	 */
	public int getY() {
		return datum.y;
	}
	
	/**
	 * <p>Enumeration representing element mode, indicating if a point represents Skiing, lift or stationary.</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 */
	public enum Mode {
		LIFT, SKI, STOP;
	}
}
