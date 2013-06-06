package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
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
	final int OPEN_LINK = 1;
	final int COPY_LINK_ADDRESS = 2;
	
	MainWebChromeClient mainWebChromeClient; 
	
	EditText addressBar;
	ImageView favicon;
	Button refresh;
	ProgressBar loading;
	private boolean pageFinishedLoading = false;
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
		Log.e(CLASS_NAME, "WebView Init");
		
		addJavascriptInterface(new WebJavaScript(context), "AndroidWeb");
		
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
//        showFindDialog(text, showIme)
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
			pageFinishedLoading = false;
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
			pageFinishedLoading = false;
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
			pageFinishedLoading = true;
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
		final HitTestResult result = getHitTestResult();
		OnMenuItemClickListener handler = new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case OPEN_LINK:
//					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(result.getExtra()));
					
					MasterActivity masterActivity = (MasterActivity) getContext();
					String tabId = System.currentTimeMillis()+ "";
					
					Bundle bundle = new Bundle();
					bundle.putString(MasterActivity.EXTRA_URL, result.getExtra());
					masterActivity.addTab(null, tabId, bundle);
					
					masterActivity.setCurrentTab(tabId);
					return true;
				case COPY_LINK_ADDRESS:
					ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("Url Copied", result.getExtra());
					clipboard.setPrimaryClip(clip);
					return true;
					
				default:
					break;
				}
				return false;
			}
		};
		
		if(result.getType() == HitTestResult.SRC_ANCHOR_TYPE){
			menu.setHeaderTitle(result.getExtra());
			menu.add(0, OPEN_LINK, 0, "Open Link").setOnMenuItemClickListener(handler);
	        menu.add(0, COPY_LINK_ADDRESS, 0, "Copy Link Address").setOnMenuItemClickListener(handler);

		}
		
	
		
	}

	public boolean isPageFinishedLoading() {
		return pageFinishedLoading;
	}

	public void setPageFinishedLoading(boolean pageFinishedLoading) {
		this.pageFinishedLoading = pageFinishedLoading;
	}
}
