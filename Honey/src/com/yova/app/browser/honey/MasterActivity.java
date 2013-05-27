package com.yova.app.browser.honey;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

	private FragmentTabHost mTabHost;
	private MainWebView webView;
	EditText addressBar;
	ImageView favicon;
	ProgressBar loading;
	String defaultUrl = "https://www.google.com/";
	public static final String EXTRA_URL = "url";
	public static final String REDIRECT = "REDIRECT";
	// web navigation
	Button go;
	Button back;
	Button forward;
	Button refresh;
	Button addNewTab;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_host);
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
		// mTabHost.setCurrentTab(-2);
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_URL, defaultUrl);
		addTab("First Tab", String.valueOf(System.currentTimeMillis() + 1),
				bundle);

		bundle = new Bundle();
		bundle.putString(EXTRA_URL, defaultUrl);
		addTab("Second Tab", String.valueOf(System.currentTimeMillis() + 1),
				bundle);

		Button addNewTab = (Button) findViewById(R.id.addNewTab);
		addNewTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = String.valueOf(System.currentTimeMillis() + 1);
				Bundle bundle = new Bundle();
				bundle.putString(EXTRA_URL, defaultUrl);
				addTab(null, id, bundle);
				mTabHost.setCurrentTabByTag(id);
				
				int position = mTabHost.getCurrentTab();
				final TabWidget tabWidget = mTabHost.getTabWidget();
				final int screenWidth = getWindowManager().getDefaultDisplay()
						.getWidth();
				final int leftX = tabWidget.getChildAt(position).getLeft();
				int newX = 0;

				newX = leftX + (tabWidget.getChildAt(position).getWidth() / 2)
						- (screenWidth / 2);
				if (newX < 0) {
					newX = 0;
				}
				HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
				scroll.smoothScrollTo(newX, 0);
			}
		});

		Intent intent = getIntent();
		if (intent != null && intent.getData() != null) {
			Uri uri = null;
			uri = intent.getData();

			bundle = new Bundle();
			bundle.putString(EXTRA_URL, uri.toString());
			addTab(REDIRECT, String.valueOf(System.currentTimeMillis() + 1),
					bundle);

			mTabHost.setCurrentTabByTag(REDIRECT);

		}

	}

	private void addTab(String tag, String id, Bundle bundle) {
		// Attach a Tab view factory to the spec
		TabHost.TabSpec tabSpec = mTabHost.newTabSpec(id).setIndicator(
				createTabView(tag));

		mTabHost.addTab(tabSpec, WebTab.class, bundle, onWebViewCreated);
	}

	@Override
	public void onTabChanged(String tabId) {

		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		TextView v = (TextView) mTabHost.getCurrentTabView().findViewById(
				R.id.tab_text);

		int[] pos = { 0, 0 };
		v.getLocationInWindow(pos);
		Log.e("currentTag", pos[0] + " | " + pos[1] + " | " + size + "");
		if (pos[0] < 0) {
			pos[0] = 0;
		}

		HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
//		scroll.smoothScrollTo(pos[0], 0);
	}

	public View createTabView(String text) {

		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
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

		}
	};
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
	// protected void onResume() {
	// super.onResume();
	// if(mTabHost.getCurrentTabTag().equals(REDIRECT)){
	// mTabHost.setCurrentTab(-2);
	// }
	// }
}