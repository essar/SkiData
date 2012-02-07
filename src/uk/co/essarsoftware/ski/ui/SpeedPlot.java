package uk.co.essarsoftware.ski.ui;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.xyplot.XYAxis;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

public class SpeedPlot extends XYPlot
{
	private static XYDataSet buildData(Track track) {
		XYDataSet data = new XYDataSet();
		long st = track.getStartTime();
		for(TrackElement te : track) {
			if(te.getTime() > Float.MAX_VALUE) {
				System.out.println("** Exceed float bounds");
			}
			data.add(new XYTrackElement(te.getTime() - st, te.getSpeed(), te));
			//data.add(new XYTrackElement(te.getTime(), te.getSpeed(), te));
		}
		return data;
	}
	
	public SpeedPlot(Track track) {
		super(buildData(track));
		showScale(true);
	}
	
	@Override
	public XYAxis createXAxis(float min, float max, int mode) {
		return new TimeAxis(min, max, mode);
	}
	
	@Override
	public XYAxis createYAxis(float min, float max, int mode) {
		return new SpeedAxis(min, max, mode);
	}
}
