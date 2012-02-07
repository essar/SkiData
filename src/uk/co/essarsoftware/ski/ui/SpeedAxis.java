package uk.co.essarsoftware.ski.ui;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  07-Feb-12 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */
import uk.co.essarsoftware.ski.xyplot.XYAxis;

/**
 * <p>Class representing an axis that plots speed values.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (7 Feb 2012)
 */
public class SpeedAxis extends XYAxis
{
	/**
	 * Create a new axis for plotting speed values.
	 * @param min minimum axis value.
	 * @param max maximum axis value.
	 * @param mode scaling mode for the axis.
	 */
	public SpeedAxis(float min, float max, int mode) {
		super("speed (kph)", min, max, mode);
		//setTickInterval(15.0f);
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.xyplot.XYAxis#getLabel(float)
	 */
	@Override
	public String getLabel(float f) {
		return String.format("%.1f", f);
	}
}
