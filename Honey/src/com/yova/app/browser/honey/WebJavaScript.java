package com.yova.app.browser.honey;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebJavaScript {
	    Context mContext;

	    /** Instantiate the interface and set the context */
	    WebJavaScript(Context c) {
	        mContext = c;
	    }

	    /** Show a toast from the web page */
	    @JavascriptInterface
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }
}
