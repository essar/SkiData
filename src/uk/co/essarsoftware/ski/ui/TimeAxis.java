package uk.co.essarsoftware.ski.ui;

import java.util.Date;

import uk.co.essarsoftware.ski.xyplot.XYAxis;

public class TimeAxis extends XYAxis
{
	public TimeAxis(float min, float max, int mode) {
		super("Time", min, max, mode);
	}
	
	@Override
	public String getLabel(float f) {
		Date d = new Date(Math.round(f) * 1000L);
		return String.format("%tk:%tM", d, d);
	}
}
