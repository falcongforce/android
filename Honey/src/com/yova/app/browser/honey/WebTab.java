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
import android.widget.TextView;

public class WebTab extends Fragment {
	MainWebView webView;
	WebTab.OnWebViewCreated onWebViewCreated;
	TextView title;
	private boolean mIsWebViewAvailable;
	String url;
	String savedTitle;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if (b != null){
			savedTitle= b.getString("title");
			url = b.getString(MasterActivity.EXTRA_URL);
		}
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
		webView.title = title;
		mIsWebViewAvailable = true;

		if(onWebViewCreated == null){
			MasterActivity ma = (MasterActivity) getActivity();
			onWebViewCreated = ma.onWebViewCreated;	
		}
		
		if (onWebViewCreated != null) {
			onWebViewCreated.onViewChanged(webView);
		}
		Bundle b = getArguments();
		if(b != null && b.get("history") !=null ){

			webView.restoreState(b);
		}else if(url != null)
			webView.loadUrl(url);
		
		webView.requestFocus();
		return webView;
	}

	@Override
	public void onPause() {
		Bundle b = getArguments();
		if(title!= null){
			b.putString("title", title.getText().toString());
		}
		webView.saveState(b);
		webView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		webView.onResume();
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