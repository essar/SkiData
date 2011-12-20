package uk.co.essarsoftware.ski.android.ui;

import uk.co.essarsoftware.ski.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SummaryTile extends LinearLayout
{
	public SummaryTile(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);
		setGravity(Gravity.CENTER);
		setWeightSum(1.0f);
		
		// Inflate from XML
		LayoutInflater.from(context).inflate(R.layout.summarytile, this, true);
		
		// Get attributes
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.summarytile, 0, 0);
		
		String lbl = array.getString(R.styleable.summarytile_summarytile_name);
		if(lbl != null) {
			((TextView) findViewById(R.id.summarytile_name)).setText(lbl);
		}
		
		// Free resources
		array.recycle();
	}

	public void setValue(String value) {
		((TextView) findViewById(R.id.summarytile_value)).setText(value);
	}
}
