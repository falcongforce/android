package com.yova.app.browser.honey;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
public class MasterActivity extends FragmentActivity  {
 
    private FragmentTabHost mTabHost;
    
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;
	String defaultUrl = "https://www.google.com/";
	public static final String EXTRA_URL = "url";
	
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host);
        
        // Initialise the TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, super.getSupportFragmentManager(), android.R.id.tabcontent);
        
        mTabHost.setCurrentTab(-2);
        WebTab tab = new WebTab();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, defaultUrl);
        tab.setArguments(bundle);
        addTab("First Tab", tab);
        
        tab = new WebTab();
        bundle = new Bundle();
        bundle.putString(EXTRA_URL, defaultUrl);
        tab.setArguments(bundle);
        addTab("Second Tab", tab);
        
        tab = new WebTab();
        bundle = new Bundle();
        bundle.putString(EXTRA_URL, defaultUrl);
        tab.setArguments(bundle);
        addTab("Third Tab", tab);
        
        tab = new WebTab();
        bundle = new Bundle();
        bundle.putString(EXTRA_URL, defaultUrl);
        tab.setArguments(bundle);
        addTab("Fourth Tab", tab);


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

    private void addTab(String tag,WebTab newTab) {
        // Attach a Tab view factory to the spec
    	TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag).setIndicator(createTabView(tag));

    	mTabHost.addTab(tabSpec, WebTab.class, newTab.getArguments());
    }
 
    public void onTabChanged(String tag) {
        //TabInfo newTab = this.mapTabInfo.get(tag);
//        int pos = this.mTabHost.getCurrentTab();
//
//        
//		FragmentManager manager = getSupportFragmentManager();
//		FragmentTransaction ft = manager.beginTransaction();
//		currentTab = mPagerAdapter.getItem(pos);
//		ft.replace(android.R.id.tabcontent, currentTab);
//		ft.commit();
    }
	public View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		TextView tv = (TextView) view.findViewById(R.id.tab_text);
		tv.setText(text);
		return view;
	}
	
	public void setupWebTab() {
//		addressBar = (EditText) findViewById(R.id.eturl);
//		favicon = (ImageView) findViewById(R.id.favicon);
//		loading = (ProgressBar) findViewById(R.id.loading);
//		Button go = (Button) findViewById(R.id.bgo);
//		Button back = (Button) findViewById(R.id.bback);
//		Button forward = (Button) findViewById(R.id.bforward);
//		Button refresh = (Button) findViewById(R.id.brefresh);
//
//		webView.addressBar = addressBar;
//		webView.refresh = refresh;
//		webView.loading = loading;
//		webView.favicon = favicon;
//
//		go.setOnClickListener(this);
//		back.setOnClickListener(this);
//		forward.setOnClickListener(this);
//		refresh.setOnClickListener(this);
	}
}