package uk.co.essarsoftware.ski.ui;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

public class TrackPlot extends XYPlot
{
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		for(TrackElement te : track) {
			data.add(new XYTrackElement(te.getX(), te.getY(), te));
		}
		return data;
	}
	
	public TrackPlot(Track track) {
		super(buildData(track));
		setProportional(true);
	}
}
