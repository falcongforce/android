package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainWebView extends WebView {
	final String CLASS_NAME = MainWebView.class.getSimpleName();
	MainWebChromeClient mainWebChromeClient; 
	
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;
	TextView title;
	
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
        webSettings.setJavaScriptEnabled(MasterActivity.checkJavascript);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true); 
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDefaultZoom(getZoomLevel());
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        webSettings.setPluginState(PluginState.ON);
        webSettings.setDomStorageEnabled(true);
        
        mainWebChromeClient = new MainWebChromeClient();
        
        setWebChromeClient(mainWebChromeClient); 
       
        setWebViewClient(new MainWebViewClient());
	}
	public ZoomDensity getZoomLevel(){
		if(MasterActivity.checkDefaultZoom.equals("CLOSE")){
			return ZoomDensity.CLOSE;
		}else if(MasterActivity.checkDefaultZoom.equals("MEDIUM")){
			return ZoomDensity.MEDIUM;
		}
		else
			return ZoomDensity.FAR;
	}
	
	class MainWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String address) {
			if(address.equals("file:///C:/Android/git/androidRepo/Honey/assets/newtab.html")){
				
				addressBar.setText("");
			}else{
				
				addressBar.setText(address);
			}
			view.loadUrl(address);
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap icon) {
			if(url.equals("file:///android_asset/newtab.html")){
				
				addressBar.setText("");
			}else{
				
				addressBar.setText(url);
			}
			loading.setVisibility(ProgressBar.VISIBLE);
			refresh.setVisibility(Button.GONE);
			super.onPageStarted(view, url, icon);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			loading.setVisibility(ProgressBar.GONE);
			refresh.setVisibility(Button.VISIBLE);
			if(view.getFavicon() != null){
				favicon.setImageBitmap(view.getFavicon());
			} else{
				Drawable dw = getResources().getDrawable(R.drawable.honey);
				favicon.setImageDrawable(dw);
			}
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
		
		@Override
		public void onReceivedTitle(WebView view, String _title) {
			super.onReceivedTitle(view, _title);
			if(title != null){
				title.setText(_title);
			}
		}
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);
		HitTestResult result = getHitTestResult();
		
		if(result.getType() == HitTestResult.SRC_ANCHOR_TYPE){
			Log.e(CLASS_NAME, result.getExtra());
			Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(result.getExtra()));
			getContext().startActivity(intent);
		}
	}
}
