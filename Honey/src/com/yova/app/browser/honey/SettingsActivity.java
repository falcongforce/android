package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		MasterActivity.checkDefaultZoom = sharedPreferences.getString("defaultZoom", "FAR");
		MasterActivity.checkJavascript = sharedPreferences.getBoolean("enableJavaScript", true);
		
	}
}
