package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.TrackElement;
import android.graphics.Paint;

public abstract class LinearRenderer implements Renderer
{
	private float minValue, maxValue;
	
	protected LinearRenderer(float minValue, float maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		System.out.println("Building renderer: " + minValue + "-" + maxValue);
	}
	
	private int getColorValue(float in) {
		// Lowest value should be black
		// Pass through colour rainbow - red-orange-yellow-green-blue
		// Highest value should be white
		
		// Get overall position
		float x = (in - minValue) / (maxValue - minValue);
		final float f = (1.0f / 6.0f);
		
		// Create RGB components
		short r = 0x00;
		short g = 0x00;
		short b = 0x00;
		
		// Calculate RGB components, depending on overall position (x)
		if(x <= f) {
			// Black to red
			r = (short) Math.round(0xFF * (x / f));
		} else if(x <= (2 * f)) {
			// Red to yellow
			r = 0xFF;
			g = (short) Math.round(0xFF * ((x / f) - 1.0f));
		} else if(x <= (3 * f)) {
			// Yellow to green
			r = (short) Math.round(0xFF * (3.0f - (x / f)));
			g = 0xFF;
		} else if(x <= (4 * f)) {
			// Green to cyan
			g = 0xFF;
			b = (short) Math.round(0xFF * ((x / f) - 3.0f));
		} else if(x <= (5 * f)) {
			// Cyan to blue
			g = (short) Math.round(0xFF * (5.0f - (x / f)));
			b = 0xFF;
		} else {
			// Blue to white
			r = (short) Math.round(0xFF * ((x / f) - 5.0f));
			g = (short) Math.round(0xFF * ((x / f) - 5.0f));
			b = 0xFF;
		}

		return (0xFF << 24) | (r & 0x00FF) << 16 | (g & 0x00FF) << 8 | (b & 0x00FF);
	}
	
	public Paint paintValue(float in, Paint p) {
		p.setColor(getColorValue(in));
		return p;
	}
	
	public static void main(String[] args) {
		LinearRenderer lr = new LinearRenderer(1500, 1700) {
			public float getValue(TrackElement te) {
				return 0;
			}
		};
		for(float f = lr.minValue; f <= lr.maxValue; f += 5) {
			System.out.println(String.format("In:%.2f\tCol:#%H", f, lr.getColorValue(f)));
		}
	}
}
