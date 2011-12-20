package uk.co.essarsoftware.ski.xyplot;

import uk.co.essarsoftware.ski.data.TrackElement;

public class XYTrackElement extends XYDatum
{
	private final TrackElement e;
	
	public XYTrackElement(float x, float y, TrackElement e) {
		super(x, y);
		this.e = e;
	}
	
	public TrackElement getE() {
		return e;
	}
}
