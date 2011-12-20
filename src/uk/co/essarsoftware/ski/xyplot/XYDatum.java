package uk.co.essarsoftware.ski.xyplot;

/**
 * <p>Class representing a point that is plotted on an XY chart or histogram.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (17 Dec 2011)
 *
 */
public class XYDatum
{
	private final float v, x, y;
	
	/**
	 * Create a new datum point with a zero value.
	 * @param x the x-coordinate of the point.
	 * @param y the y-coordinate of the point.
	 */
	public XYDatum(float x, float y) {
		this.v = 0.0f;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Create a new datum point with the specified value.
	 * @param x the x-coordinate of the point.
	 * @param y the y-coordinate of the point.
	 * @param v the value of the point.
	 */
	public XYDatum(float x, float y, float v) {
		this.v = v;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the value of the point.
	 * @return the value of the point.
	 */
	public float getV() {
		return v;
	}
	
	/**
	 * Get the x-coordinate of the point.
	 * @return the x-coordinate of the point.
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Get the y-coordinate of the point.
	 * @return the y-coordinate of the point.
	 */
	public float getY() {
		return y;
	}
}
