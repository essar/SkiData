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

import uk.co.essarsoftware.ski.data.TrackElement.Mode;

/**
 * <p>Class responsible for loading data from a parsable source, file or stream.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.0 (30 Nov 2011)
 */
public class DataLoader implements Runnable
{
	/** Constant indicating process completed in error. */
	public static final int STATE_ERROR = 0xFF;
	/** Constant indicating process has been cancelled. */
	public static final int STATE_CANCELLED = 0xFE;
	/** Constant indicating process is loading data.*/
	public static final int STATE_LOADING = 0x01;
	/** Constant indicating data is being processed. */
	public static final int STATE_PROCESSING = 0x02;
	/** Constant indicating process completed normally. */
	public static final int STATE_COMPLETE = 0x08;
	
	// Current state
	private int state;
	// Data store
	private SkiData data;

	// Indicating if thread is currently running
	private transient boolean running;
	// Dedicated thread
	private transient Thread thd;
	
	// Parsable source data
	private final DataParser parser;
	// Starting point and maximum points to load
	private final int max, start;
	// Loader Listener
	private final DataLoaderListener l;
	// Data processor
	private final Processor proc;
	
	
	/**
	 * Create a new <tt>DataLoader</tt> starting at the first point, loading all points and using a default listener.
	 * @param parser data parser containing input source.
	 * @param proc data processor.
	 */
	public DataLoader(DataParser parser, Processor proc) {
		this(parser, proc, 0, -1);
	}
	
	private void handleProcessingException(Exception e) {
		// Set loader state
		state = STATE_ERROR;
		
		// Set running flag
		running = false;
		
		// Notify listener
		if(l != null) {
			l.error(e);
		}
	}
	
	/**
	 * Create a new <tt>DataLoader</tt> using a default listener. 
	 * @param parser data parser containing input source.
	 * @param proc data processor.
	 * @param start starting point.
	 * @param max maximum points to load, or -1 to load all available points.
	 */
	public DataLoader(DataParser parser, Processor proc, int start, int max) {
		this(parser, proc, start, max, new DefaultDataLoaderListener());
	}
	
	/**
	 * Create a new <tt>DataLoader</tt>.
	 * @param parser data parser containing input source.
	 * @param proc data processor.
	 * @param start starting point.
	 * @param max maximum points to load, or -1 to load all available points.
	 * @param l loader listener.
	 */
	public DataLoader(DataParser parser, Processor proc, int start, int max, DataLoaderListener l) {
		// Validate parser input
		if(parser == null) {
			throw new IllegalArgumentException("Parser cannot be null");
		}
		this.parser = parser;
		// Validate processor input
		if(proc == null) {
			throw new IllegalArgumentException("Processor cannot be null");
		}
		this.proc = proc;
		// Validate start input
		if(start < 0) {
			throw new IllegalArgumentException("Start cannot be less than zero");
		}
		this.start = start;
		this.max = max;
		this.l = l;
	}

	/**
	 * Cancel the running loader process.
	 */
	public void cancel() {
		// Set loader state
		state = STATE_CANCELLED;
		
		// Set running flag
		running = false;
		
		// Mark thread as interrupted
		if(thd != null) {
			thd.interrupt();
		}
	}
	
	/**
	 * Get the loaded data. Data will only be returned once the loader has completed successfully; no data
	 * will be returned if the process is running, was cancelled or completed in error.
	 * @return a <tt>SkiData</tt> object containing the loaded data, or null if no data has been fully loaded.
	 */
	public SkiData getData() {
		if(state == STATE_COMPLETE) {
			synchronized(data) {
				return data;
			}
		}
		// Not yet loaded data
		return null;
	}
	
	/**
	 * Get the current loader state.
	 * @return integer indicating the current state.
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Start the loading process in a new thread and return.
	 */
	public void loadDataInBackground() {
		// Create and start new thread
		thd = new Thread(this, "DataLoader");
		thd.start();
	}
	
	/**
	 * Start the loading process and wait for process to complete. 
	 * @return a <tt>SkiData</tt> object containing the loaded data, or null if no data was loaded.
	 */
	public SkiData loadData() {
		try {
			loadDataInBackground();
			thd.join();
		} catch(InterruptedException ie) {
			return null;
		}
		return getData();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// Set initial state
		state = STATE_LOADING;
		
		// Set running flag
		running = true;

		// Initialise counter 
		int len = 0;
		
		// Initialise first element
		Datum first = null;
		
		try {
			// Skip rows
			for(int i = 0; i < start; i ++) {
				parser.skipLine();
			}
			
			// Create Datum variables
			Datum prev = null;
			
			// Loop through data
			do {
				// Read next point
				Datum d = parser.readDatum();
				if(first == null) {
					// Record first point
					first = d;
				} else {
					// Add point to chain
					prev.setNext(d);
				}
				
				// Increment counter
				len ++;
				
				// Set previous element to this element
				prev = d;
			} while(running && prev != null && (max < 0 || max > len));
			
			if(state == STATE_CANCELLED) {
				// Notify listener
				if(l != null) {
					l.aborted();
				}
			}
		} catch(IOException ioe) {
			// Unrecoverable IOException during data load
			
			// Handle exception
			handleProcessingException(ioe);
			
			// End run process
			return;
		}
		
		if(len == 0) {
			// Notify listener
			if(l != null) {
				l.emptyData();
			}
		}

		// Notify listener
		if(l != null) {
			l.loadingComplete(len);
		}

		// Time to stop if not in LOADING state.
		if(state != STATE_LOADING) {
			return;
		}
		
		// Start processing data
		// Set loader state
		state = STATE_PROCESSING;

		// Interpolate data
		len = DatumInterpolator.interpolateList(first);
		System.out.println("Interpolated " + DatumInterpolator.ct + " points.");
		
		// Create SkiData constructs
		Mode currentMode = Mode.STOP;
		data = new SkiData();
		
		// Create window of 20 elements long
		ElemWindow eWin = new ElemWindow(first, 20);
		
		synchronized(data) {
			try {
				// Get next data element
				TrackElement elem = eWin.next();
				
				// Loop through all data
				while(running && elem != null) {
					// Process element
					currentMode = proc.processElement(currentMode, elem, eWin);
						
					// Set element mode
					elem.setMode(currentMode);
						
					// Add the element to data set
					data.addElement(elem);
						
					// Update listener
					if(l != null) {
						l.processedElement(data.size(), len);
					}
						
					// Get next data element
					elem = eWin.next();
						
					// Sleep for 10ms
					//Thread.sleep(10);
				}
				if(state == STATE_CANCELLED) {
					// Notify listener
					if(l != null) {
						l.aborted();
					}
				}
			} catch(RuntimeException re) {
				handleProcessingException(re);
			} finally {
				// Close off open data sets
				data.closeAll();
			}
		}
		
		// Time to stop if not in PROCESSING state
		if(state != STATE_PROCESSING) {
			return;
		}
		
		// Set loader state
		state = STATE_COMPLETE;
		
		// Notify listener
		if(l != null) {
			l.completed(data.size());
		}
	}
	
	/**
	 * <p>Default loader listener class. No action is taken on any method call.</p>
	 *
	 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
	 * @version 1.0 (30 Nov 2011)
	 */
	private static class DefaultDataLoaderListener implements DataLoaderListener
	{
		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#aborted()
		 */
		public void aborted() {}

		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#completed(int)
		 */
		public void completed(int elementCount) {}

		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#emptyData()
		 */
		public void emptyData() {}

		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#loadingError(java.lang.Exception)
		 */
		public void error(Exception e) {}

		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#loadingComplete(int)
		 */
		public void loadingComplete(int count) {}

		/* (non-Javadoc)
		 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#processedElement(int, int)
		 */
		public void processedElement(int count, int max) {}
	}
}
