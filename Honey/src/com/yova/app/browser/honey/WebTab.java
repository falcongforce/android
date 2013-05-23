package com.yova.app.browser.honey;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class WebTab extends Fragment {
	private MainWebView webView;
	WebTab.OnWebViewCreated onWebViewCreated;
	
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
		ImageView favicon = (ImageView) getActivity().findViewById(R.id.favicon);
		ProgressBar loading = (ProgressBar) getActivity().findViewById(R.id.loading);
		Button refresh = (Button) getActivity().findViewById(R.id.brefresh);
		
		webView.addressBar = addressBar;
		webView.favicon = favicon;
		webView.loading = loading;
		webView.refresh = refresh;
		
		mIsWebViewAvailable = true;

		if (onWebViewCreated != null) {
			onWebViewCreated.onViewChanged(webView);
		}
		if (url != null)
			webView.loadUrl(url);
		
		webView.requestFocus();
		return webView;
	}



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

	public abstract static interface OnWebViewCreated{
		public void onViewChanged(MainWebView mainWebView);
	}

	public void setOnWebViewCreated(WebTab.OnWebViewCreated onWebViewCreated) {
		this.onWebViewCreated = onWebViewCreated;
	}
}