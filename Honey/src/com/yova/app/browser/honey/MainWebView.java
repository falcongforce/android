package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MainWebView extends WebView{
	
	MainWebChromeClient mainWebChromeClient; 
	
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;
	
	int originalOrientation;
	private FrameLayout fullScreenContainer;
	private Activity activity;

	WebChromeClient.CustomViewCallback customViewCallback;
	public View customView;
	
	public MainWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public MainWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MainWebView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		
		activity = (Activity) context;
		
		
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true); 
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        webSettings.setPluginState(PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        
        mainWebChromeClient = new MainWebChromeClient();
        
        setWebChromeClient(mainWebChromeClient); 
       
        setWebViewClient(new MainWebViewClient());
	}
	
	
	class MainWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String address) {
			addressBar.setText(address);
			view.loadUrl(address);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap icon) {
			addressBar.setText(url);
//			loading.setVisibility(ProgressBar.VISIBLE);
//			refresh.setVisibility(Button.GONE);
			super.onPageStarted(view, url, icon);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
//			loading.setVisibility(ProgressBar.GONE);
//			refresh.setVisibility(Button.VISIBLE);
//			if(view.getFavicon() != null){
//				favicon.setImageBitmap(view.getFavicon());
//			} else{
//				Drawable dw = getResources().getDrawable(R.drawable.honey);
//				favicon.setImageDrawable(dw);
//			}
			super.onPageFinished(view, url);
		}
		
	}
	public boolean isCustomView(){
		return customView != null;
	}
	public void hideCustomView(){
		mainWebChromeClient.onHideCustomView();
	}
	class MainWebChromeClient extends WebChromeClient{
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			super.onShowCustomView(view, callback);
			originalOrientation = activity.getRequestedOrientation();
			FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
			fullScreenContainer = new FrameLayout(activity.getBaseContext());
			fullScreenContainer.setBackgroundColor(activity.getResources().getColor(R.color.black));
			fullScreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
			decor.addView(fullScreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
			customView = view;
			customViewCallback = callback;
			activity.setRequestedOrientation(activity.getRequestedOrientation()); 
			
		}
		@Override
		public void onHideCustomView() {
			super.onHideCustomView();
			FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
			decor.removeView(fullScreenContainer);
			 
			fullScreenContainer = null;
			customView = null;
			customViewCallback.onCustomViewHidden();
			
			activity.setRequestedOrientation(originalOrientation); 
			
		}
		
		
	}
}
