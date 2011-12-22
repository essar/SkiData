package uk.co.essarsoftware.ski.android.ui;

import android.app.Activity;
import android.os.Bundle;

public abstract class SkiDataActivity extends Activity
{
	protected AppData getSkiAppData() {
		return AppData.getAppData();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}