package com.yova.app.browser.honey;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
 

 

public class MasterActivity extends FragmentActivity  implements OnClickListener, TabHost.OnTabChangeListener{
 
    private FragmentTabHost mTabHost;
    private MainWebView webView;
	EditText addressBar;
	ImageView favicon;
	ProgressBar loading;
	String defaultUrl = "https://www.google.com/";
	public static final String EXTRA_URL = "url";
	
	//web navigation
	Button go;
	Button back;
	Button forward;
	Button refresh;
	Button addNewTab;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host);
        //setup buttons
		go = (Button) findViewById(R.id.bgo);
		back = (Button) findViewById(R.id.bback);
		forward = (Button) findViewById(R.id.bforward);
		refresh = (Button) findViewById(R.id.brefresh);
		
        // Initialise the TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, super.getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.setOnTabChangedListener(this);
        
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
 
	@Override
	public void onTabChanged(String tabId) {
		
	}
	public View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		TextView tv = (TextView) view.findViewById(R.id.tab_text);
		tv.setText(text);
		return view;
	}
	@Override
	public void onClick(View view) {
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
		HideKeyboardClearFocus();
	}
	
	@Override
	public void onBackPressed() {
		
		if (webView != null && webView.isCustomView()) { 
			webView.hideCustomView();
			return;
		} else if (webView.canGoBack()) {
			webView.goBack();
			HideKeyboardClearFocus();
			return;
		}
		super.onBackPressed();

	}
	public void HideKeyboardClearFocus() {

		addressBar.clearFocus();
		// hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
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