package uk.co.essarsoftware.ski.data;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  30-Nov-11 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */

import uk.co.essarsoftware.ski.data.TrackElement.Mode;

/**
 * <p>Class responsible for processing elements into ski modes.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class SkiDataProcessor implements Processor
{
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.Processor#processElement(uk.co.essarsoftware.ski.data.TrackElement.Mode, uk.co.essarsoftware.ski.data.TrackElement, uk.co.essarsoftware.ski.data.ElemWindow)
	 */
	public Mode processElement(Mode currentMode, TrackElement elem, ElemWindow eWin) {
		switch(currentMode) {
			case STOP:
				// Stopped, but now moving
				if(elem.getDistance() > 0 && eWin.moving() >= 0.5) {
					if(elem.getAltitudeChange() > 0 && eWin.ascent() > 0 && eWin.ascending() > 0.3) {
						// Altitude ascending
						return Mode.LIFT;
					}
					if(elem.getAltitudeChange() < 0 && eWin.ascent() < 0 && eWin.descending() > 0.3 ) {
						// Altitude descending
						return Mode.SKI;
					}
				}
				break;
			case SKI:
				// Skiing, but now not moving
				if(elem.getDistance() == 0 && eWin.stopped() > 0.8) {
					return Mode.STOP;
				}
				// Skiing, but now on a lift
				if(elem.getAltitudeChange() > 5 && eWin.ascent() > 0 && eWin.ascending() > 0.9) {
					return Mode.LIFT;
				}
				break;
			case LIFT:
				// On a lift, but now not moving
				if(elem.getDistance() == 0 && eWin.stopped() > 0.8) {
					return Mode.STOP;
				}
				// On a lift, but now skiing
				if(elem.getAltitudeChange() < 0 && eWin.ascent() < 0 && eWin.descending() > 0.9) {
					return Mode.SKI;
				}
				break;
		}
		// No matches, so stayed in same mode
		return currentMode;
	}
}
