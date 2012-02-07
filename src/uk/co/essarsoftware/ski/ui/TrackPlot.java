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
import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

/**
 * <p>Class for plotting Track data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (7 Feb 2012)
 */
public class TrackPlot extends XYPlot
{
	/**
	 * Build the data set from a <tt>Track</tt>.
	 * @param track the <tt>Track</tt> to plot.
	 * @return an <tt>XYDataSet</tt> that contains the data.
	 */
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		for(TrackElement te : track) {
			data.add(new XYTrackElement(te.getX(), te.getY(), te));
		}
		return data;
	}
	
	/**
	 * Create and configure a new track plot.
	 * @param track the track to plot.
	 */
	public TrackPlot(Track track) {
		super(buildData(track));
		setProportional(true);
	}
}
