package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;


public class SpeedRenderer extends LinearRenderer
{
	public SpeedRenderer(Track t) {
		super(0.0f, t.getMaxSpeed());
	}
	
	public float getValue(TrackElement te) {
		return te.getSpeed();
	}
}
