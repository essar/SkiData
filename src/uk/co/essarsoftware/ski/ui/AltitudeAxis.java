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
 * <p>Class representing an axis that plots altitude values.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (7 Feb 2012)
 */
public class AltitudeAxis extends XYAxis
{
	/**
	 * Create a new axis for plotting altitude values.
	 * @param min minimum axis value.
	 * @param max maximum axis value.
	 * @param mode scaling mode for the axis.
	 */
	public AltitudeAxis(float min, float max, int mode) {
		super("altitude (m)", min, max, mode);
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.xyplot.XYAxis#getLabel(float)
	 */
	@Override
	public String getLabel(float f) {
		return String.format("%,.0f", f);
	}
}
