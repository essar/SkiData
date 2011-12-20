package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.TrackElement;
import uk.co.essarsoftware.ski.data.TrackElement.Mode;
import android.graphics.Paint;

public class ModeRenderer implements Renderer
{
	private static final float LIFT = 1.0f;
	private static final float SKI = 2.0f;
	
	private int getColorValue(int in) {
		switch(in) {
			case (int) SKI: // Skiing
				return 0xFFFF0000; // Red
			case (int) LIFT: // Lift
				return 0xFF0000FF; // Blue
			default:   // Stopped
				return 0xFFFFFFFF; // White
		}
	}
	
	public float getValue(TrackElement te) {
		return (te.getMode() == Mode.SKI ? SKI : te.getMode() == Mode.LIFT ? LIFT : 0.0f);
	}
	
	public Paint paintValue(float in, Paint p) {
		p.setColor(getColorValue((int) in));
		return p;
	}
}
