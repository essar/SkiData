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
 * <p>Interface specifying methods used for processing track elements.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public interface Processor
{
	/**
	 * Process a track element.
	 * @param currentMode the current track mode.
	 * @param elem the element to process.
	 * @param eWin the current element window.
	 * @return the new track mode.
	 */
	public Mode processElement(Mode currentMode, TrackElement elem, ElemWindow eWin);
}
