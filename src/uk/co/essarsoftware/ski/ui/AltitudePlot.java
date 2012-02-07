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
import uk.co.essarsoftware.ski.xyplot.XYAxis;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

/**
 * <p>Class for plotting altitude against time data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (7 Feb 2012)
 */
public class AltitudePlot extends XYPlot
{
	/**
	 * Build the data set from a <tt>Track</tt>.
	 * @param track the <tt>Track</tt> to plot.
	 * @return an <tt>XYDataSet</tt> that contains the data.
	 */
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		long st = track.getStartTime();
		for(TrackElement te : track) {
			data.add(new XYTrackElement(te.getTime() - st, te.getAltitude(), te));
		}
		return data;
	}
	
	/**
	 * Create and configure a new altitude plot.
	 * @param track the track to plot.
	 */
	public AltitudePlot(Track track) {
		super(buildData(track));
		showScale(true);
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.xyplot.XYPlot#createXAxis(float, float, int)
	 */
	@Override
	public XYAxis createXAxis(float min, float max, int mode) {
		return new TimeAxis(min, max, mode);
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.xyplot.XYPlot#createYAxis(float, float, int)
	 */
	@Override
	public XYAxis createYAxis(float min, float max, int mode) {
		return new AltitudeAxis(min, max, mode);
	}
}
