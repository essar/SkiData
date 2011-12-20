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

/**
 * <p>Interface specifying methods used by a class listening for events from a <tt>DataLoader</tt>.</p>
 * 
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public interface DataLoaderListener
{
	/**
	 * Called when the loading process or data processing is aborted.
	 */
	public void aborted();
	
	/**
	 * Called when the data processing has completed.
	 * @param elementCount the total number of elements loaded and processed.
	 */
	public void completed(int elementCount);
	
	/**
	 * Called if no data has been loaded.
	 */
	public void emptyData();
	
	/**
	 * Called if an exception is thrown during the loading process or data processing.
	 * @param e the <tt>Exception</tt> thrown.
	 */
	public void error(Exception e);
	
	/**
	 * Called when the loading process is complete and data processing is beginning.
	 * @param count the total number of elements loaded.
	 */
	public void loadingComplete(int count);
	
	/**
	 * Called when an element has been processed.
	 * @param count the number of elements processed so far.
	 * @param max the total number of elements to be processed.
	 */
	public void processedElement(int count, int max);
}
