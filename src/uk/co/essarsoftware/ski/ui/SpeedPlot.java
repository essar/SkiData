package uk.co.essarsoftware.ski.ui;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

public class SpeedPlot extends XYPlot
{
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		long st = track.getStartTime();
		for(TrackElement te : track) {
			data.add(new XYTrackElement(te.getTime() - st, te.getSpeed(), te));
		}
		return data;
	}
	
	public SpeedPlot(Track track) {
		super(buildData(track));
	}
}
