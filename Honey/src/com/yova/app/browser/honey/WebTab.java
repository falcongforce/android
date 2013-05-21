package com.yova.app.browser.honey;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class WebTab extends Fragment {
	private MainWebView webView;
	private boolean mIsWebViewAvailable;
	String url;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if (b != null)
			url = b.getString(MasterActivity.EXTRA_URL);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (webView != null) {
			webView.destroy();
		}
		webView = new MainWebView(getActivity());
		EditText addressBar = (EditText) getActivity().findViewById(R.id.eturl);
		webView.addressBar = addressBar;
		mIsWebViewAvailable = true;

		if (url != null)
			webView.loadUrl(url);
		return webView;
	}

//	@Override
//	public void onClick(View view) {
//		switch (view.getId()) {
//		case R.id.bgo:
//
//			if (addressBar != null
//					&& !addressBar.getText().toString().equals("")) {
//				String url = this.addressBar.getText().toString();
//
//				if (url != null && url.equalsIgnoreCase(webView.getUrl())) {
//					webView.reload();
//				} else if (URLUtil.isValidUrl(url)) {
//
//					webView.loadUrl(url);
//				} else {
//					url = URLUtil.guessUrl(url);
//					webView.loadUrl(url);
//				}
//
//			}
//			break;
//		case R.id.bback:
//			if (webView.canGoBack()) {
//				webView.goBack();
//			}
//			break;
//		case R.id.bforward:
//			if (webView.canGoForward()) {
//				webView.goForward();
//			}
//			break;
//		case R.id.brefresh:
//			webView.reload();
//
//			break;
//
//		default:
//			break;
//		}
//		HideKeyboardClearFocus();
//	}

	@Override
	public void onPause() {
		super.onPause();
		webView.saveState(getArguments());
		webView.onPause();
	}

	@Override
	public void onResume() {
		webView.restoreState(getArguments());
		webView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		mIsWebViewAvailable = false;
		super.onDestroyView();

	}

	public MainWebView getWebview() {
		return mIsWebViewAvailable ? webView : null;
	}
	@Override
	public void onDestroy() {
		if(webView != null){
			webView.destroy();
			webView = null;
		}
		super.onDestroy();
	}

//	public void HideKeyboardClearFocus() {
//
//		addressBar.clearFocus();
//		// hide keyboard
//		InputMethodManager imm = (InputMethodManager) webView.getContext()
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(addressBar.getWindowToken(), 0);
//	}
}