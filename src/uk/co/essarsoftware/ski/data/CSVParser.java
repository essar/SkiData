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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import uk.co.essarsoftware.ski.geo.CoordConverter;
import uk.co.essarsoftware.ski.geo.UTMCoordinate;
import uk.co.essarsoftware.ski.geo.WGSCoordinate;


/**
 * <p>Class that processes from CSV data.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class CSVParser implements DataParser
{
	// Reader object holding input data
	private LineNumberReader in;
	
	/** Parser configuration */
	CSVConfig config;
	
	/**
	 * Create new <tt>CSVParser</tt> object reading from the specified input stream using default configuration.
	 * @param is <tt>InputStream</tt> to read data from.
	 * @throws IllegalArgumentException if a null or invalid stream is provided.
	 */
	public CSVParser(InputStream is) {
		if(is == null) {
			throw new IllegalArgumentException("Null input stream");
		}
		// Set up reader using 8K buffer
		in = new LineNumberReader(new InputStreamReader(is), 8192);
		
		// Set up default configuration
		config = new CSVConfig();
	}
	
	/**
	 * Create new <tt>CSVParser</tt> object reading from the specified input stream.
	 * @param is <tt>InputStream</tt> to read data from.
	 * @param config Parser configuration.
	 * @throws IllegalArgumentException if a null or invalid stream is provided.
	 */
	public CSVParser(InputStream is, CSVConfig config) {
		// Call default constructor
		this(is);
		// Set configuration
		this.config = config;
	}
	
	/**
	 * Read a line of data from the CSV stream, ignoring commented lines.
	 * @return a <tt>String</tt> containing a line of data or null if the end of the stream has been reached.
	 * @throws IOException if an error occurs reading the input data.
	 */
	private String readLine() throws IOException {
		return readLine(true);
	}
	
	/**
	 * Read a line of data from the CSV stream.
	 * @param ignoreComments flag indicating whether commented lines should be ignored.
	 * @return a <tt>String</tt> containing a line of data or null if the end of the stream has been reached.
	 * @throws IOException if an error occurs reading the input data.
	 */
	private String readLine(boolean ignoreComments) throws IOException {
		// If stream is null, return null
		if(in == null) {
			return null;
		}
		// Read line from reader
		String line = in.readLine();
			
		if(line == null) {
			// Probably reached end of input stream
			return null;
		}
		if(ignoreComments && line.startsWith("#")) {
			// Line read is a comment line, so get the next one
			return readLine();
		}
		return line;
	}
	
	/**
	 * Parse a float value from a String array of data.
	 * @param fieldName the name of the field to parse. This references the column names defined in the parser configuration.
	 * @param parts an array of <tt>String</tt> values. 
	 * @return the parsed float value.
	 * @throws ParseException if a problem occurs parsing the value.
	 */
	private float parseFloatField(String fieldName, String[] parts) throws ParseException {
		try {
			if(config.colMap.containsKey(fieldName)) {
				return Float.parseFloat(parts[config.colMap.get(fieldName)].trim());
			}
			return 0.0f;
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			throw new ParseException("Invalid field index for " + fieldName, in.getLineNumber());
		} catch(NumberFormatException nfe) {
			throw new ParseException("Invalid field value for " + fieldName, in.getLineNumber());
		}
	}
	
	/**
	 * Parse an integer value from a String array of data.
	 * @param fieldName the name of the field to parse. This references the column names defined in the parser configuration.
	 * @param parts an array of <tt>String</tt> values. 
	 * @return the parsed integer value.
	 * @throws ParseException if a problem occurs parsing the value.
	 */
	private int parseIntField(String fieldName, String[] parts) throws ParseException {
		try {
			if(config.colMap.containsKey(fieldName)) {
				return Integer.parseInt(parts[config.colMap.get(fieldName)].trim());
			}
			return 0;
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			throw new ParseException("Invalid field index for " + fieldName, in.getLineNumber());
		} catch(NumberFormatException nfe) {
			throw new ParseException("Invalid field value for " + fieldName, in.getLineNumber());
		}
	}
	
	/**
	 * Parse a String value from a String array of data.
	 * @param fieldName the name of the field to parse. This references the column names defined in the parser configuration.
	 * @param parts an array of <tt>String</tt> values. 
	 * @return the parsed String value.
	 * @throws ParseException if a problem occurs parsing the value.
	 */
	private String parseStringField(String fieldName, String[] parts) throws ParseException {
		try {
			if(config.colMap.containsKey(fieldName)) {
				return parts[config.colMap.get(fieldName)].trim();
			}
			return null;
		} catch(ArrayIndexOutOfBoundsException aioobe) {
			throw new ParseException("Invalid field index for " + fieldName, in.getLineNumber());
		}
	}
	
	/**
	 * Parse a GPS <tt>Datum</tt> object from a line of CSV data.
	 * @param line a <tt>String</tt> containing the CSV data.
	 * @return a GPS <tt>Datum</tt> object.
	 * @throws ParseException if a problem occurs parsing the line.
	 */
	private Datum parseLine(String line) throws ParseException {
		if(line == null) {
			return null;
		}
		
		String[] parts = line.split(",");
		
		// Latitude & Longitude
		float la = parseFloatField("lat", parts);
		float lo = parseFloatField("long", parts);
		
		// X & Y
		int x = 0;
		int y = 0;
		if(!config.colMap.containsKey("x") || !config.colMap.containsKey("y")) {
			// Config missing X or Y elements, so calculate from lat & long
			UTMCoordinate utm = CoordConverter.WGS2UTM(new WGSCoordinate(la, lo, WGSCoordinate.COORD_MODE_DEG));
			x = utm.getX();
			y = utm.getY();
		} else {
			x = parseIntField("x", parts);
			y = parseIntField("y", parts);
		}
		
		// Altitude & Speed
		int a = parseIntField("alt", parts);
		float s = parseFloatField("speed", parts);
		
		// Date & Time
		String d = parseStringField("date", parts);
		String t = parseStringField("time", parts);
		
		// Build date object
		Date time = config.sdf.parse(d + " " + t);
		
		// Build Datum object
		return new Datum(time, la, lo, x, y, a, s);
	}
	
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataParser#readDatum()
	 */
	public Datum readDatum() throws IOException {
		String line = null;
		try {
			line = readLine();
			return parseLine(line);
		} catch(ParseException pe) {
			// Handle ParseException
			try {
				// Open bad file in append mode
				FileWriter out = new FileWriter(config.badFile, true);
				
				// Output line number
				out.write(pe.getErrorOffset() + ":");
				// Output input line
				out.write(line);
				// Output error message
				out.write("#" + pe.getMessage());
				out.write('\n');
				
				out.close();
			} catch(IOException ioe) {
				// TODO Unable to write bad log
				System.err.println(ioe.getMessage());
			}
			// Try next item
			return readDatum();
		}
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataParser#skipLine()
	 */
	public void skipLine() throws IOException {
		readLine(true);
	}
	
	/**
	 * <p>CSV Configuration data</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 */
	class CSVConfig
	{
		// File containing invalid data
		private File badFile;
		// Date format object
		private SimpleDateFormat sdf;
		// Map of column names
		private HashMap<String, Integer> colMap;
		
		/**
		 * Create default configuration.
		 */
		CSVConfig() {
			badFile = new File("import.bad");
			colMap = new HashMap<String, Integer>();
			sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			
			colMap.put("lat", 6);
			colMap.put("long", 7);
			colMap.put("x", 10);
			colMap.put("y", 11);
			colMap.put("alt", 8);
			colMap.put("speed", 9);
			colMap.put("date", 1);
			colMap.put("time", 2);
		}
		
		/**
		 * Load a configuration from the specified file.
		 * @param configFile a <tt>File</tt> referencing the configuration file.
		 */
		CSVConfig(File configFile) {
			this();
		}
		
		/**
		 * Parses CSV configuration from the header of a CSV file.
		 */
		private void parseCSVHeader() {
		}
	}
}
