package com.yova.app.browser.honey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebIconDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.yova.app.browser.honey.WebTab.OnWebViewCreated;

public class MasterActivity extends FragmentActivity implements
		OnClickListener, TabHost.OnTabChangeListener {
	final String CLASS_NAME = MasterActivity.class.getSimpleName();
	static boolean checkJavascript;
	static String checkDefaultZoom;
	private FragmentTabHost mTabHost;
	private MainWebView webView;
	EditText addressBar;
	ImageView favicon;
	ProgressBar loading;
	String defaultUrl = "file:///android_asset/newtab.html";
	public static final String EXTRA_URL = "url";
	public static final String TABHOST = "TABHOST";
	// web navigation
	Button go;
	Button back;
	Button forward;
	Button refresh;
	HorizontalScrollView scroll;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("MasterActivity", "OnCreate");
		
		
		setContentView(R.layout.tab_host);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		MasterActivity.checkDefaultZoom = prefs.getString("defaultZoom", "FAR");
		MasterActivity.checkJavascript = prefs.getBoolean("enableJavaScript", true);
		
		// hide keyboard
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		WebIconDatabase.getInstance().open(
				getDir("icons", MODE_PRIVATE).getPath());
		// setup buttons
		setupButtons();

		// Initialise the TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, super.getSupportFragmentManager(),
				android.R.id.tabcontent);
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setOnWebViewCreated(onWebViewCreated);
		scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		
		Button addNewTab = (Button) findViewById(R.id.addNewTab);
		addNewTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = String.valueOf(System.currentTimeMillis() + 1);
				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_URL, defaultUrl);
				addTab(null, id, bundle);
				mTabHost.setCurrentTabByTag(id);
	
			}
		});
		
		Intent intent = getIntent(); 
		if(savedInstanceState == null){
			if(intent == null || intent.getData() == null){
				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_URL, defaultUrl);
				addTab(null, String.valueOf(System.currentTimeMillis() + 1), bundle);
			}
			else{
				onNewIntent(intent);
			}

		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		   // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	
	        	Intent i = new Intent(this, SettingsActivity.class);
	            startActivityForResult(i, 1);
	            
	            Log.e(CLASS_NAME, "action_settings");
	            return true;
	        case R.id.action_bookmarks:
	        	Log.e(CLASS_NAME, "action_bookmarks");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return super.onMenuOpened(featureId, menu);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent != null && intent.getData() != null){
			Uri uri = null;
			uri = intent.getData();
			String id = String.valueOf(System.currentTimeMillis() + 1);
			Bundle bundleIntent = new Bundle();
			bundleIntent.putString(EXTRA_URL, uri.toString());
			addTab(null, id, bundleIntent);

			mTabHost.setCurrentTabByTag(id);
			delayedPositionTabs();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		delayedPositionTabs();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		delayedPositionTabs();
	}
	
	public void delayedPositionTabs(){
		scroll.postDelayed(new Runnable() {            
		    @Override
		    public void run() {
		    	positionTabs();
		    }
		}, 500);
	}
	private void addTab(String tag, String id, Bundle bundle) {
		// Attach a Tab view factory to the spec
		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(id).setIndicator(
				createTabView(tag));

		mTabHost.addTab(tabSpec, WebTab.class, bundle);
	}
	
	@Override
	public void onTabChanged(String tabId) {
		
		
	}
	
	public View createTabView(String text) {

		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		
		

		view.setOnTouchListener(new OnTouchListener() {
			boolean selected = false;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN && v.isSelected()){
					selected = v.isSelected();
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					if(selected){
						mTabHost.removeCurrentTab();
						
					}else{
						selected = false;
					}
				}else if(event.getAction() == MotionEvent.ACTION_CANCEL){
					selected = false;
				}
				return false;
			}
		});
		
		
		if (text == null || "".equals(text)) {
			return view;
		}
		TextView tv = (TextView) view.findViewById(R.id.tab_text);

		tv.setText(text);
		return view;
	}

	@Override
	public void onClick(View view) {
		if (webView == null)
			return;
		switch (view.getId()) {
		case R.id.bgo:

			if (addressBar != null
					&& !addressBar.getText().toString().equals("")) {
				String url = this.addressBar.getText().toString();

				if (url != null && url.equalsIgnoreCase(webView.getUrl())) {
					webView.reload();
				} else if (URLUtil.isValidUrl(url)) {

					webView.loadUrl(url);
				} else {
					url = URLUtil.guessUrl(url);
					webView.loadUrl(url);
				}

			}
			break;
		case R.id.bback:
			if (webView.canGoBack()) {
				webView.goBack();
			}
			break;
		case R.id.bforward:
			if (webView.canGoForward()) {
				webView.goForward();
			}
			break;
		case R.id.brefresh:
			webView.reload();

			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {

		if (webView != null && webView.isCustomView()) {
			webView.hideCustomView();
			return;
		} else if (webView != null && webView.canGoBack()) {
			webView.goBack();
			return;
		}
		super.onBackPressed();

	}

	public void setupButtons() {
		addressBar = (EditText) findViewById(R.id.eturl);

		addressBar.setOnEditorActionListener(onEditListen);
		favicon = (ImageView) findViewById(R.id.favicon);
		loading = (ProgressBar) findViewById(R.id.loading);

		go = (Button) findViewById(R.id.bgo);
		back = (Button) findViewById(R.id.bback);
		forward = (Button) findViewById(R.id.bforward);
		refresh = (Button) findViewById(R.id.brefresh);
		
		go.setOnClickListener(this);
		back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}

	// Listens for WebView change
	OnWebViewCreated onWebViewCreated = new OnWebViewCreated() {

		public void onViewChanged(MainWebView mainWebView) {
			webView = mainWebView;
			registerForContextMenu(mainWebView);
			positionTabs();
		}
	};
	public void positionTabs(){
		Point point = new Point();
		int position = mTabHost.getCurrentTab();
		final TabWidget tabWidget = mTabHost.getTabWidget();
		getWindowManager().getDefaultDisplay().getSize(point);
		
		final int screenWidth = point.x;
		
		View selected = tabWidget.getChildAt(position);
		int newX = 0;
		int leftX = 0;
		if(selected != null){
			leftX = selected.getLeft();
			newX = leftX + (selected.getWidth() / 2)
					- (screenWidth / 2);
		}
		if (newX < 0) {
			newX = 0;
		}
		scroll.smoothScrollTo(newX, 0);
		
	}
	// Address Bar Listener
	OnEditorActionListener onEditListen = new OnEditorActionListener() {

		public boolean onEditorAction(TextView v, int keyCode, KeyEvent ev) {

			if (ev != null && ev.getAction() == KeyEvent.ACTION_DOWN
					&& ev.getKeyCode() == KeyEvent.KEYCODE_ENTER
					&& webView != null) {

				if (addressBar != null
						&& !addressBar.getText().toString().equals("")) {
					String address = addressBar.getText().toString();
					String webUrl = webView.getUrl();
					if (address != null && webUrl != null
							&& address.equalsIgnoreCase(webUrl)) {
						webView.reload();
					} else if (URLUtil.isValidUrl(address)) {

						webView.loadUrl(address);
					} else {
						address = URLUtil.guessUrl(address);
						webView.loadUrl(address);
					}
				}

			}
			return false;
		}
	};

}