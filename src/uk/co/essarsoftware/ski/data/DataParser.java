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
import java.io.IOException;


/**
 * <p>Interface specifying an input parser that parses <tt>Datum</tt> objects.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public interface DataParser
{
	/**
	 * Read the next <tt>Datum</tt> object from the input source.
	 * @return the parsed <tt>Datum</tt> object, or null if end of source has been reached.
	 * @throws IOException if an error occurs when reading the input source.
	 */
	public Datum readDatum() throws IOException;
	
	
	/**
	 * Skip an element in the input source.
	 * @throws IOException if an error occurs when skipping the element.
	 */
	public void skipLine() throws IOException;
}
