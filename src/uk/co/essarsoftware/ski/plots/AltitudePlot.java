package uk.co.essarsoftware.ski.plots;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;
import uk.co.essarsoftware.ski.xyplot.XYTrackElement;

public class AltitudePlot extends XYPlot
{
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		long st = track.getStartTime();
		for(TrackElement te : track) {
			data.add(new XYTrackElement(te.getTime() - st, te.getAltitude(), te));
		}
		return data;
	}
	
	public AltitudePlot(Track track) {
		super(buildData(track));
	}
}
