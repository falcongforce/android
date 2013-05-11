package com.yova.app.browser.honey;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity implements OnClickListener{
	
	private WebView webView;
	EditText addressBar;
	ImageView favicon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String defaultUrl = "http://www.google.com";
        setContentView(R.layout.activity_main);
        
        addressBar = (EditText) findViewById(R.id.eturl);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

       
       
        webView.setWebViewClient(new MainWebViewClient());
        
        favicon = (ImageView) findViewById(R.id.favicon);
         
        Button go = (Button) findViewById(R.id.bgo);
        Button back = (Button) findViewById(R.id.bback);
        Button forward = (Button) findViewById(R.id.bforward);
        Button refresh = (Button) findViewById(R.id.brefresh);
		OnEditorActionListener onEditListen = new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int keyCode, KeyEvent ev) {

				if (ev != null && ev.getAction() == KeyEvent.ACTION_DOWN && ev.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

					if (addressBar != null && !addressBar.getText().toString().equals("")) {
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
        
        webView.loadUrl(defaultUrl);
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        
    }
    
    public void HideKeyboardClearFocus(){
    	
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
    		webView.goBack();
    		HideKeyboardClearFocus();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bgo:

			if(addressBar != null && !addressBar.getText().toString().equals("")){
				String url = this.addressBar.getText().toString();
				
				if(url !=null && url.equalsIgnoreCase(webView.getUrl())){
					webView.reload();
				}
				else if(URLUtil.isValidUrl(url)){
					
					webView.loadUrl(url);
				}else{
					url = URLUtil.guessUrl(url);
					webView.loadUrl(url);
				}
				
			}
			break;
		case R.id.bback:
			if(webView.canGoBack()){
				webView.goBack();
			}
			break; 
		case R.id.bforward:
			if(webView.canGoForward()){
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
	public void onConfigurationChanged(Configuration newConfig){        
	    super.onConfigurationChanged(newConfig);
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
//			favicon.setImageBitmap(icon); 
			super.onPageStarted(view, url, icon);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			if(view.getFavicon() != null){
				favicon.setImageBitmap(view.getFavicon());
			} else{
				Drawable dw = getResources().getDrawable(R.drawable.honey);
				favicon.setImageDrawable(dw);
			}
			super.onPageFinished(view, url);
		}
	}
}
