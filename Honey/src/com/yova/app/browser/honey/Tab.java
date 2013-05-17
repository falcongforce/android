package com.yova.app.browser.honey;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Tab extends Fragment implements OnClickListener {
	View tabView;
	MainWebView webView;
	EditText addressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		
		ImageView favicon;
		ProgressBar loading;
		
		tabView = inflater.inflate(R.layout.activity_main, null);
		webView = (MainWebView) tabView.findViewById(R.id.webview);

		addressBar = (EditText) tabView.findViewById(R.id.eturl);
		favicon = (ImageView) tabView.findViewById(R.id.favicon);
		loading = (ProgressBar) tabView.findViewById(R.id.loading);
		
		
		Button go = (Button) tabView.findViewById(R.id.bgo);
		Button back = (Button) tabView.findViewById(R.id.bback);
		Button forward = (Button) tabView.findViewById(R.id.bforward);
		Button refresh = (Button) tabView.findViewById(R.id.brefresh);

		webView.addressBar = addressBar;
		webView.refresh = refresh;
		webView.loading = loading;
		webView.favicon = favicon;
		
		go.setOnClickListener(this);
		back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);

		// webView.loadUrl("http://www.google.com");
		return tabView;
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

	public void HideKeyboardClearFocus() {

		addressBar.clearFocus();
		// hide keyboard
		InputMethodManager imm = (InputMethodManager) webView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
	}
}