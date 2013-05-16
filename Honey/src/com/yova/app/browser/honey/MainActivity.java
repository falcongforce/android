package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements OnClickListener {

	private MainWebView webView;
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;

	int originalOrientation;
	FrameLayout fullScreenContainer;
	WebChromeClient.CustomViewCallback customViewCallback;
	View customView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String defaultUrl = "http://www.google.com";
		setContentView(R.layout.activity_main);

		addressBar = (EditText) findViewById(R.id.eturl);
		webView = (MainWebView) findViewById(R.id.webview);

		favicon = (ImageView) findViewById(R.id.favicon);

		Button go = (Button) findViewById(R.id.bgo);
		Button back = (Button) findViewById(R.id.bback);
		Button forward = (Button) findViewById(R.id.bforward);
		refresh = (Button) findViewById(R.id.brefresh);
		loading = (ProgressBar) findViewById(R.id.loading);

		webView.addressBar = addressBar;
		webView.refresh = refresh;
		webView.loading = loading;
		webView.favicon = favicon;

		OnEditorActionListener onEditListen = new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int keyCode, KeyEvent ev) {

				if (ev != null && ev.getAction() == KeyEvent.ACTION_DOWN
						&& ev.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (addressBar != null
							&& !addressBar.getText().toString().equals("")) {
						String address = addressBar.getText().toString();

						if (address != null
								&& address.equalsIgnoreCase(webView.getUrl())) {
							webView.reload();
						} else if (URLUtil.isValidUrl(address)) {

							webView.loadUrl(address);
						} else {
							address = URLUtil.guessUrl(address);
							webView.loadUrl(address);
						}
						HideKeyboardClearFocus();
					}

				}
				return false;
			}
		};
		addressBar.setOnEditorActionListener(onEditListen);

		go.setOnClickListener(this);
		back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);

		WebIconDatabase.getInstance().open(
				getDir("icons", MODE_PRIVATE).getPath());
		Intent intent = getIntent();
		if (intent != null && intent.getData() != null) {
			Uri uri = null;
			uri = intent.getData();
			webView.loadUrl(uri.toString());

		} else {
			webView.loadUrl(defaultUrl);
		}

	}

	public void HideKeyboardClearFocus() {

		addressBar.clearFocus();
		// hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public void onBackPressed() {
		
		if (webView.isCustomView()) { 
			webView.hideCustomView();
			return;
		} else if (webView.canGoBack()) {
			webView.goBack();
			HideKeyboardClearFocus();
			return;
		}
		super.onBackPressed();

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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		webView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

	@Override
	protected void onDestroy() {
		webView.stopLoading();
		webView.destroy();
		super.onDestroy();
	}

}
