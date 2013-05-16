package com.yova.app.browser.honey;

import java.util.HashMap;
import java.util.logging.Logger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class MasterActivity extends FragmentActivity {

	/* Tab identifiers */
	static String TAB_A = "First Tab";
	static String TAB_B = "Second Tab";

	TabHost mTabHost;

	Tab fragment1;
	Tab fragment2;
	HashMap<String, Tab> tabMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_host);
		tabMap = new HashMap<String, Tab>();
		fragment1 = new Tab();
		fragment2 = new Tab();
		
		tabMap.put(TAB_A, fragment1);
		tabMap.put(TAB_B, fragment2);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setOnTabChangedListener(listener);
		mTabHost.setup();

		initializeTab();
		Button addNewTab = (Button) findViewById(R.id.addNewTab);
		addNewTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("Button", mTabHost.getCurrentTabTag()) ;
				removeTab(1);
//				addNewTab("new tab");
			}
		});
		
		addNewTab("Number 3");
		addNewTab("Number 4");
		addNewTab("Number 5");
		addNewTab("Number 6");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Initialize the tabs and set views and identifiers for the tabs
	 */
	public void initializeTab() {

		TabHost.TabSpec spec = mTabHost.newTabSpec(TAB_A);
		mTabHost.setCurrentTab(-3);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView(TAB_A));
		mTabHost.addTab(spec);

		spec = mTabHost.newTabSpec(TAB_B);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView(TAB_B));
		mTabHost.addTab(spec);
	}

	public void addNewTab(String tabString) {
		TabHost.TabSpec spec = mTabHost.newTabSpec(tabString);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});

		spec.setIndicator(createTabView(tabString));
		mTabHost.addTab(spec);
	}
	
	public void removeTab(int index){
		mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(index));
	}

	/*
	 * TabChangeListener for changing the tab when one of the tabs is pressed
	 */
	TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
		
		public void onTabChanged(String tabId) {
			/* Set current tab.. */
			
//			pushFragments(tabMap.get(tabId));
			/*if (tabId.equals(TAB_A)) {
				pushFragments(fragment1);
			} else if (tabId.equals(TAB_B)) {
				pushFragments(fragment2);
			}*/
		}
	};

	/*
	 * adds the fragment to the FrameLayout
	 */
	public void pushFragments(Fragment fragment) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		ft.replace(android.R.id.tabcontent, fragment);
		ft.commit();
	}

	/*
	 * returns the tab view i.e. the tab icon and text
	 */
	private View createTabView(final String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		((TextView) view.findViewById(R.id.tab_text)).setText(text);
		return view;
	}
}