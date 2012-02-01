package uk.co.essarsoftware.ski.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;


public class DatumTest
{
	public static void main(String[] args) throws Exception {
		long sTime = System.currentTimeMillis();
		
		// Load input points
		//File f = new File("../data/ski_20110222.csv");
		File f = new File("../data/20110222.gsd");
		//CSVDatumLoader ldr = new CSVDatumLoader(f);
		//CSVParser p = new CSVParser(new FileInputStream(f));
		GSDParser p = new GSDParser(new FileInputStream(f), true);
		SkiDataProcessor ep = new SkiDataProcessor();
		
		int start = Integer.parseInt(args[0]);
		int size = Integer.parseInt(args[1]);
		
		/*
		for(int i = 0; i < start; i ++) {
			ldr.readDatum();
		}
		
		Datum[] ds = new Datum[size];
		for(int i = 0; i < ds.length; i ++) {
			ds[i] = ldr.readDatum();
		}
		for(int i = 0; i < ds.length - 1; i ++) {
			ds[i].setNext(ds[i + 1]);
		}
		
		Datum d = ds[0];
		//DatumInterpolator.interpolateList(d);
		*/
		
		//DatumProcessor pr = new DatumProcessor(p);
		DataLoader dl = new DataLoader(p, ep, start, size, new DataLoaderListener() {
			public void aborted() {
				System.out.println("Data loading aborted");
			}

			public void completed(int elementCount) {
				System.out.println(elementCount + " element(s) loaded and processed");
			}
			
			public void emptyData() {
				System.out.println("Empty data");
			}

			public void error(Exception e) {
				e.printStackTrace(System.err);
			}

			public void loadingComplete(int count) {
				System.out.println(count + " element(s) loaded");
			}

			public void processedElement(int count, int max) {
				if(count % 200 == 0) {
					System.out.println("Element " + count + " of " + max + " processed");
				}
			}
		});
		SkiData data = dl.loadData();
		
		/*
		dl.loadDataInBackground();
		Thread.sleep(2000);
		dl.cancel();
		SkiData data = dl.getData();
		*/
		
		long fTime = System.currentTimeMillis();
		
		System.out.println(String.format("Loaded %d points in %d miliseconds.", data.size(), (fTime - sTime)));
		
		// Output data to file
		try {
			File outFile = new File(f.getParentFile(), "output.csv");
			FileWriter fw = new FileWriter(outFile);

			try {
				for(TrackElement e : data.getAllElements()) {
					fw.write(String.format("%d,%.6f,%.6f,%d,%d,%d,%d,%.3f,%.3f,%s\n", e.getTime(), e.getLatitude(), e.getLongitude(), e.getX(), e.getY(), e.getAltitude(), e.getAltitudeChange(), e.getSpeed(), e.getDistance(), e.getMode()));
				}
			} finally {
				fw.close();
				System.out.println("Data written to: " + outFile.getCanonicalPath());
			}

		} catch(IOException ioe) {
			System.err.println("** Unable to write data file: " + ioe.getMessage());
		}

		{
			Track t = data.getAllElements();
			long st = (t.getStartTime() * 1000L);
			long et = (t.getEndTime() * 1000L);
			System.out.println("==== Ski Data ====");
			System.out.println("File: " + f.getCanonicalPath());
			System.out.println("------------------");
			System.out.println("Total records: " + t.size());
			System.out.println(String.format("Time: %tF %tT - %tF %tT", st, st, et, et));
			System.out.println("------------------");
			System.out.println(String.format("Total distance: %.2fkm", (t.getDistance() / 1000)));
			System.out.println(String.format("Altitude: %,dm-%,dm", t.getLowAltitude(), t.getHighAltitude()));
			System.out.println(String.format("Average speed: %.2fkph", t.getAverageSpeed()));
			System.out.println(String.format("Maximum speed: %.2fkph", t.getMaxSpeed()));
		}
	}
}
