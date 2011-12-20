package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.data.SkiData;
import uk.co.essarsoftware.ski.data.Track;
import uk.co.essarsoftware.ski.xyplot.XYPlot;

class AppData
{
	private static AppData ad = new AppData();
	
	SkiData data;
	Track track;
	XYPlot plot;
	
	public AppData() {
		super();
	}
	
	static AppData getAppData() {
		return ad;
	}
}
