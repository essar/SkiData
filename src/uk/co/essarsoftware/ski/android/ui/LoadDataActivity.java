package uk.co.essarsoftware.ski.android.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import uk.co.essarsoftware.ski.R;
import uk.co.essarsoftware.ski.data.CSVParser;
import uk.co.essarsoftware.ski.data.DataLoader;
import uk.co.essarsoftware.ski.data.DataLoaderListener;
import uk.co.essarsoftware.ski.data.DataParser;
import uk.co.essarsoftware.ski.data.GSDParser;
import uk.co.essarsoftware.ski.data.Processor;
import uk.co.essarsoftware.ski.data.SkiDataProcessor;
import uk.co.essarsoftware.ski.xyplot.XYDataSet;
import uk.co.essarsoftware.ski.xyplot.XYDatum;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * <p>Android Activity responsible for loading data from a source file.</p>
 *
 * @author Steve Roberts <steve.roberts@essarsoftware.co.uk>
 * @version 1.1 (29 Jan 2012)
 *
 */
public class LoadDataActivity extends SkiDataActivity implements DataLoaderListener
{
	private DataLoader ldr;
	private InputStream rIs;
	private ProgressDialog dlg;
	
	private String srcPath = "/mnt/sdcard/skidata/";
	
	/**
	 * Load data from a data source.
	 */
	private void loadData(DataParser dp, boolean all) throws IOException {
		Processor p = new SkiDataProcessor();
    	
    	// Initiate data loader
    	ldr = new DataLoader(dp, p, 0, (all ? -1 : 3600), this);
    	
    	// Display progress dialog
    	dlg = ProgressDialog.show(this, "", "Loading data...", true, true, new DialogInterface.OnCancelListener() {
			//FIXME
    		public void onCancel(DialogInterface dialog) {
    			Log.d(getLocalClassName(), "User pressed cancel");
				ldr.cancel();
			}
		});
    	
    	//TODO More advanced progress bar?
    	ldr.loadDataInBackground();
	}
	
	private void loadCSVData(boolean all) {
		String filename = null;
		try {
			// Get filename
			filename = (String) ((Spinner) findViewById(R.id.loaddata_csv_files_sp)).getSelectedItem();
			if(filename == null) {
				throw new FileNotFoundException("No CSV files found");
			}
			Log.i(getLocalClassName(), "Starting data load from " + filename);
			
			// Open file from SD card
        	File root = new File(getExternalFilesDir(null), srcPath);
        	rIs = new FileInputStream(new File(root, filename));
        	
        	// Create data parser and element processor
        	DataParser dp = new CSVParser(rIs);
        	Log.d(getLocalClassName(), "CSV parser created");
        	
        	loadData(dp, all);
        } catch(IOException ioe) {
        	Log.e(getLocalClassName(), "Unable to read data from file from " + filename, ioe);
        	Toast.makeText(this, "Error: " + ioe.getMessage(), Toast.LENGTH_LONG).show();
        	
        	// Create synthetic data
        	XYDataSet testdata = new XYDataSet();
	        for(float i = 0; i < 100; i ++) {
	        	testdata.add(new XYDatum(i, i));
	        }
	        for(float i = 0; i < 100; i ++) {
	        	testdata.add(new XYDatum(i, (99 - i)));
	        }
	        
	        rIs = null;
        }
	}
	
	private void loadGSDData(boolean all) {
		String filename = null;
		try {
			// Get filename
			filename = (String) ((Spinner) findViewById(R.id.loaddata_gsd_files_sp)).getSelectedItem();
			if(filename == null) {
				throw new FileNotFoundException("No GSD files found");
			}
			Log.i(getLocalClassName(), "Starting data load from " + filename);
			
			// Open file from SD card
        	File root = new File(getExternalFilesDir(null), srcPath);
        	File f = new File(root, filename);
        	rIs = new FileInputStream(f);
        	Log.d(getLocalClassName(), "GSD data source: " + f.getAbsolutePath());
        	
        	// Create data parser and element processor
        	DataParser dp = new GSDParser(rIs, true);
        	Log.d(getLocalClassName(), "GSD parser created");
        	
        	loadData(dp, all);
        } catch(IOException ioe) {
        	Log.e(getLocalClassName(), "Unable to read data from file from " + filename, ioe);
        	Toast.makeText(this, "Error: " + ioe.getMessage(), Toast.LENGTH_LONG).show();
        	
        	// Create synthetic data
        	XYDataSet testdata = new XYDataSet();
	        for(float i = 0; i < 100; i ++) {
	        	testdata.add(new XYDatum(i, i));
	        }
	        for(float i = 0; i < 100; i ++) {
	        	testdata.add(new XYDatum(i, (99 - i)));
	        }
	        
	        rIs = null;
        }
	}
	
    /* (non-Javadoc)
     * @see uk.co.essarsoftware.ski.ui.SkiDataActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load activity content from XML
        setContentView(R.layout.loaddata);
        
        File src = new File(getExternalFilesDir(null), srcPath);
        
        // GSD input
        {
	        String[] fNames = src.list(new FilenameFilter() {
	        	public boolean accept(File dir, String filename) {
	        		return (filename.endsWith(".gsd") || filename.endsWith(".GSD"));
	        	}
	        });
	        Log.d(getLocalClassName(), fNames.length + " GSD file(s) found in directory");
	
	        // Set spinner content
	        ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fNames);
	        ((Spinner) findViewById(R.id.loaddata_gsd_files_sp)).setAdapter(adap);
	        
	        // Add button listener
	        Button btn = (Button) findViewById(R.id.loaddata_gsd_btn);
	        btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					boolean loadAll = ((CheckBox) findViewById(R.id.loaddata_gsd_all_chk)).isChecked();
					loadGSDData(loadAll);
				}
	        });
        }
        
        // CSV input
        {
	        String[] fNames = src.list(new FilenameFilter() {
	        	public boolean accept(File dir, String filename) {
	        		return (filename.endsWith(".csv") || filename.endsWith(".CSV"));
	        	}
	        });
	        Log.d(getLocalClassName(), fNames.length + " CSV file(s) found in directory");
	
	        // Set spinner content
	        ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fNames);
	        ((Spinner) findViewById(R.id.loaddata_csv_files_sp)).setAdapter(adap);
	        
	        // Add button listener
	        Button btn = (Button) findViewById(R.id.loaddata_csv_btn);
	        btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					boolean loadAll = ((CheckBox) findViewById(R.id.loaddata_csv_all_chk)).isChecked();
					loadCSVData(loadAll);
				}
	        });
        }
        
        Log.i(getLocalClassName(), "LoadDataActivity created");
    }
    
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#aborted()
	 */
	public void aborted() {
		// Close progress dialog if open
		if(dlg != null) {
			dlg.dismiss();
			dlg = null;
		}
		
		// Close input stream
        if(rIs != null) {
	        try {
	        	rIs.close();
	        	Log.d(getLocalClassName(), "Input stream closed");
	        } catch(IOException ioe) {
	        	Log.w(getLocalClassName(), String.format("Error when closing input stream"));
	        }
	        rIs = null;
        }
        
		Log.w(getLocalClassName(), "Data load cancelled by user");
		//FIXME Toast.makeText(this, "Data load aborted.", Toast.LENGTH_SHORT).show();
	}

	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#completed(int)
	 */
	public void completed(int elementCount) {
		//Store data in singleton object
        AppData.getAppData().data = ldr.getData();

        // Log loaded data size
        int points = (AppData.getAppData().data == null ? 0 : AppData.getAppData().data.size());
        Log.i(getLocalClassName(), String.format("%d point(s) loaded from file.", points));
        
        // Close progress dialog
        if(dlg != null) {
        	dlg.dismiss();
        	dlg = null;
        }
        
        if(ldr.getData().size() == 0) {
        	// No data loaded
        	//FIXME Toast.makeText(this, "No data loaded.", Toast.LENGTH_SHORT).show();
        	return;
        }
        // Open summary screen
        startActivity(new Intent(this, DataSummaryActivity.class));
	}
	
	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#emptyData()
	 */
	public void emptyData() {
		Log.i(getLocalClassName(), "No data loaded");
	}

	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#loadingError(java.lang.Exception)
	 */
	public void error(Exception e) {
		// Close progress dialog if open
		if(dlg != null) {
			dlg.dismiss();
			dlg = null;
		}
		
		// Close input stream
        if(rIs != null) {
	        try {
	        	rIs.close();
	        	Log.d(getLocalClassName(), "Input stream closed");
	        } catch(IOException ioe) {
	        	Log.w(getLocalClassName(), String.format("Error when closing input stream"));
	        }
	        rIs = null;
        }
        
		Log.e(getLocalClassName(), "Error during data load: " + e.getMessage() + ".");
		//FIXME Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
	}

	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#loadingComplete(int)
	 */
	public void loadingComplete(int count) {
		// Close input stream
        if(rIs != null) {
	        try {
	        	rIs.close();
	        	Log.d(getLocalClassName(), "Input stream closed");
	        } catch(IOException ioe) {
	        	Log.w(getLocalClassName(), String.format("Error when closing input stream"));
	        }
	        rIs = null;
        }
        
		Log.i(getLocalClassName(), "Data loading completed, " + count + " records loaded");
	}

	/* (non-Javadoc)
	 * @see uk.co.essarsoftware.ski.data.DataLoaderListener#processedElement(int, int)
	 */
	public void processedElement(int count, int max) {
		// Update progress dialog (if not indeterminable)
		if(dlg != null) {
			int v = (int) (((float) count / (float) max) * 100.0f);
			dlg.setProgress(v);
		}
		if(count % 2000 == 0) {
			Log.i(getLocalClassName(), count + "/" + max + " records processed.");
		}
	}
}