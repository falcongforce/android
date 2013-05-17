package com.yova.app.browser.honey;
import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
 

 
/**
 * The <code>TabsViewPagerFragmentActivity</code> class implements the Fragment activity that maintains a TabHost using a ViewPager.
 * @author mwho
 */
public class MasterActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
 
    private TabHost mTabHost;
    private PagerAdapter mPagerAdapter;
    Tab currentTab;
    
    private MainWebView webView;
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout
        setContentView(R.layout.tab_host);
        
        // Initialise the TabHost
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setup();
        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), new Vector<Tab>());
        
        mTabHost.setCurrentTab(-2);
        addTab("First Tag", new Tab());
        addTab("Second Tag", new Tab());
        addTab("Third Tag", new Tab());
        addTab("Fourth Tag", new Tab());


		Button addNewTab = (Button) findViewById(R.id.addNewTab);
		addNewTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Log.i("Button", mTabHost.getCurrentTabTag()) ;
//				removeTab(1);
//				addNewTab("new tab");
			}
		});
        
    }

    private void addTab(String tag,Tab newTab) {
        // Attach a Tab view factory to the spec
    	TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag).setIndicator(createTabView(tag));
    	tabSpec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
//    	TextView tv = (TextView)newTab.tabView.findViewById(R.id.contentTV);
//    	tv.setText(tag);
    	mPagerAdapter.addItem(newTab);
    	mTabHost.addTab(tabSpec);
    }
 
    public void onTabChanged(String tag) {
        //TabInfo newTab = this.mapTabInfo.get(tag);
        int pos = this.mTabHost.getCurrentTab();

        
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		currentTab = mPagerAdapter.getItem(pos);
		ft.replace(android.R.id.tabcontent, currentTab);
		ft.commit();
    }
	public View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		TextView tv = (TextView) view.findViewById(R.id.tab_text);
		tv.setText(text);
		return view;
	}

}