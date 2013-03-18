package com.example.androidcheckoutsdk;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CheckoutBrowserActivity extends Activity implements CheckoutWebViewClient.CheckoutResponseListener {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout_browser);		
		start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_checkout_browser, menu);
		return true;
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
    	if(event.getAction() == KeyEvent.ACTION_DOWN) {
            switch(keyCode)
            {
	            case KeyEvent.KEYCODE_BACK:
	            	try {
		                if(mWebView.canGoBack() == true) {
		                    mWebView.goBack();
		                }
		                else {
		                	// Close the Web View with canceled by user
		            		Intent returnIntent = new Intent();
		            		setResult(RESULT_CANCELED, returnIntent);     
		            		returnIntent.putExtra("reason", "canceled_by_user");
		                    this.finish();
		                }
		                return true;
	            	}
	            	catch (Exception ex) {
	            		return false;
	            	}
            }
        }
        return super.onKeyDown(keyCode, event);
    }

	@SuppressLint("SetJavaScriptEnabled")
	private void start() {

		String checkoutInitPointUrl = "";
		String successUrl = "";
		String failureUrl = "";

		// Get activity parameters
		Bundle b = getIntent().getExtras();
		if (b != null) {
			checkoutInitPointUrl = b.getString("checkout_init_point_url");
			successUrl = b.getString("success_url");
			failureUrl = b.getString("failure_url");
		}
		else {
			this.finish();
		}
		
		// Set Web View Client
		CheckoutWebViewClient checkoutWebViewClient = new CheckoutWebViewClient(this, successUrl, failureUrl);
		
		// Set Web View attributes
		this.mWebView = (WebView) findViewById(R.id.dialog_web_view);
		this.mWebView.setVerticalScrollBarEnabled(true);
		this.mWebView.setHorizontalScrollBarEnabled(true);
		this.mWebView.setWebViewClient(checkoutWebViewClient);
		this.mWebView.getSettings().setJavaScriptEnabled(Boolean.TRUE);
		
		// Set browser progress dialog
		final Activity activity = this;
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.progressBar_message_checkout_browser));
		progressDialog.setCancelable(false);
		mWebView.setWebChromeClient(new WebChromeClient() { 
		     public void onProgressChanged(WebView view, int progress) {
		    	 try {
			         progressDialog.show();
			         progressDialog.setProgress(0);
			         activity.setProgress(progress * 1000);
			         progressDialog.incrementProgressBy(progress);
			         if(progress == 100 && progressDialog.isShowing()) {
			             progressDialog.dismiss();
			         }		    		 
		    	 }
		    	 catch (Exception ex) {
		    		 // do nothing
		    	 }
		     } 
		});		

		// Navigate url		
		this.mWebView.loadUrl(checkoutInitPointUrl);		
	}

	public void onSuccess(Bundle params) {
		
		// Close the Web View with success
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);     
		this.finish();
	}

	public void onFailure(Bundle params) {
		
		// Close the Web View with failure
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);     
		returnIntent.putExtra("reason", "failure");
        this.finish();
	}	
}
