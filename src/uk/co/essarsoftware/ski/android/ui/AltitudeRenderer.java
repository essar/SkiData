package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.data.TrackElement;


public class AltitudeRenderer extends LinearRenderer
{
	public AltitudeRenderer(Track t) {
		super(t.getLowAltitude(), t.getHighAltitude());
	}
	
	public float getValue(TrackElement te) {
		return te.getAltitude();
	}
}
