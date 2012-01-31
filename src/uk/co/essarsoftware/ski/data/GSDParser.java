package uk.co.essarsoftware.ski.data;
/*
 * Essar Software Ski Data
 * http://github.com/essar/skidata
 * 
 * -----------+----------+-----------------------------------------------------
 *  Date      | Version  | Comments
 * -----------+----------+-----------------------------------------------------
 *  29-Jan-12 | 1.0      | Initial version
 * -----------+----------+-----------------------------------------------------
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.essarsoftware.ski.geo.CoordConverter;
import uk.co.essarsoftware.ski.geo.DMSCoordinate;
import uk.co.essarsoftware.ski.geo.UTMCoordinate;
import uk.co.essarsoftware.ski.geo.WGSCoordinate;


/**
 * <p>Class that processes from GSD data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.1 (29 Jan 2012)
 */
public class GSDParser implements DataParser
{
	// Reader object holding input data
	private LineNumberReader in;
	
	/** Parser configuration */
	static SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy HHmmss");
	
	/**
	 * Create new <tt>GSDParser</tt> object reading from the specified input stream using default configuration.
	 * @param is <tt>InputStream</tt> to read data from.
	 * @param bypassHeaders flag to set whether header data should be bypassed
	 * @throws IllegalArgumentException if a null or invalid stream is provided.
	 */
	public GSDParser(InputStream is, boolean bypassHeaders) {
		if(is == null) {
			throw new IllegalArgumentException("Null input stream");
		}
		// Set up reader using 8K buffer
		in = new LineNumberReader(new InputStreamReader(is), 8192);
		
		if(bypassHeaders) {
			try {
				// Skip forward to [TP] section
				while(! "[TP]".equalsIgnoreCase(readHeaderLine()));
				
				// Read first data block header
				String b1 = readDataLine();
				if(b1 != null) {
					int ix = b1.indexOf('=');
					String h = "[" + b1.substring(ix + 1) + "]";
					
					// Look for first data block
					while(! h.equalsIgnoreCase(readHeaderLine()));
				}
			} catch(IOException ioe) {
				// TODO handle IOException
				System.err.println("**IOE whilst bypassing headers: " + ioe);
			}
		}
	}
	
	private boolean testHeaderLine(String line) {
		if(line == null) {
			// Probably reached end of input stream
			return true;
		}
		if(line.length() == 0) {
			// Blank line, read the next one
			return false;
		}
		if(! line.startsWith("[")) {
			// Line read is not a header line, so get the next one
			return false;
		}
		return true;
	}
	
	/**
	 * Read a header from the GSD stream.
	 * @return a <tt>String</tt> containing a header or null if the end of the stream has been reached.
	 * @throws IOException if an error occurs reading the header.
	 */
	private String readHeaderLine() throws IOException {
		// If stream is null, return null
		if(in == null) {
			return null;
		}
		
		String line = null;
		do {
			// Read line from reader
			line = in.readLine();
		} while(! testHeaderLine(line));
		
		return line;
	}
	
	private boolean testDataLine(String line) {
		if(line == null) {
			// Probably reached end of input stream
			return true;
		}
		if(line.length() == 0) {
			// Blank line, read the next one
			return false;
		}
		if(line.startsWith("[")) {
			// Line read is a header line, so get the next one
			return false;
		}
		return true;
	}
	
	/**
	 * Read a line of data from the GSD stream.
	 * @return a <tt>String</tt> containing a line of data or null if the end of the stream has been reached.
	 * @throws IOException if an error occurs reading the input data.
	 */
	private String readDataLine() throws IOException {
		// If stream is null, return null
		if(in == null) {
			return null;
		}

		String line = null;
		do {
			// Read line from reader
			line = in.readLine();
		} while(! testDataLine(line));
		
		return line;
	}
	
	/**
	 * Parse a GPS <tt>Datum</tt> object from a line of GSD data.
	 * @param line a <tt>String</tt> containing the GSD data.
	 * @return a GPS <tt>Datum</tt> object.
	 * @throws ParseException if a problem occurs parsing the line.
	 */
	private Datum parseLine(String line) throws ParseException {
		if(line == null) {
			return null;
		}
		
		// Look for allocation operator
		int ix = line.indexOf('=');
		if(ix < 0) {
			return null;
		}
		
		// Get data parts
		String[] parts = line.substring(ix + 1).split(",");
		
		String latStr = String.format("%08d", Integer.parseInt(parts[0]));
		String lonStr = String.format("%08d", Integer.parseInt(parts[1]));
		
		int latD = Integer.parseInt(latStr.substring(0, 2));
		float latM = Integer.parseInt(latStr.substring(2)) / 10000.0f;
		
		int lonD = Integer.parseInt(lonStr.substring(0, 2));
		float lonM = Integer.parseInt(lonStr.substring(2)) / 10000.0f;
		
		//System.out.println(String.format("Latitude: in=%s, D=%d, M=%.4f", latStr, latD, latM));
		//System.out.println(String.format("Longitude: in=%s, D=%d, M=%.4f", lonStr, lonD, lonM));

		// Calculate coordinates
		WGSCoordinate wgs = CoordConverter.DMS2WGS(new DMSCoordinate(latD, latM, lonD, lonM));
		//System.out.println("WGS: " + wgs);
		UTMCoordinate utm = CoordConverter.WGS2UTM(wgs);
		//System.out.println("UTM: " + utm);
		
		// Latitude & Longitude
		float la = wgs.getLatitudeDegrees();
		float lo = wgs.getLongitudeDegrees();
		
		// X & Y
		int x = utm.getX();
		int y = utm.getY();
		
		// Date & Time
		String t = String.format("%6d", Integer.parseInt(parts[2]));
		String d = String.format("%6d", Integer.parseInt(parts[3]));
		
		// Altitude & Speed
		float s = (float) Integer.parseInt(parts[4]) / 100.0f;
		int a = Integer.parseInt(parts[5]) / 10000;
		
		// Build date object
		Date time = sdf.parse(d + " " + t);
		
		// Build Datum object
		return new Datum(time, la, lo, x, y, a, s);
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataParser#readDatum()
	 */
	public Datum readDatum() throws IOException {
		String line = null;
		try {
			line = readDataLine();
			return parseLine(line);
		} catch(ParseException pe) {
			// Handle ParseException
			System.err.println(pe.getMessage());
			// Try next item
			return readDatum();
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataParser#skipLine()
	 */
	public void skipLine() throws IOException {
		readDataLine();
	}
}
