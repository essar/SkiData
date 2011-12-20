package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.TrackElement;
import android.graphics.Paint;

public interface Renderer
{
	public abstract Paint paintValue(float in, Paint p);
	
	public abstract float getValue(TrackElement te);
}
