package uk.co.essarsoftware.ski.ui;

import uk.co.essarsoftware.ski.xyplot.XYAxis;

public class SpeedAxis extends XYAxis
{
	public SpeedAxis(float min, float max, int mode) {
		super("speed (kph)", min, max, mode);
		setTickInterval(15.0f);
	}
	
	@Override
	public String getLabel(float f) {
		return String.format("%.1f", f);
	}
}
