package uk.co.essarsoftware.ski.ui;

import uk.co.essarsoftware.ski.xyplot.XYAxis;

public class AltitudeAxis extends XYAxis
{
	public AltitudeAxis(float min, float max, int mode) {
		super("altitude (m)", min, max, mode);
	}
	
	@Override
	public String getLabel(float f) {
		return String.format("%,.0f", f);
	}
}
