package com.mercadopago.checkout.android;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CheckoutWebViewClient extends WebViewClient {

	private final CheckoutResponseListener mListener;
	private String mSuccessUrl;
	private String mFailureUrl;
	
	public CheckoutWebViewClient(CheckoutResponseListener checkoutResponseListener, String successUrl, String failureUrl) {
		this.mListener = checkoutResponseListener;
		mSuccessUrl = successUrl;
		mFailureUrl = failureUrl;
	}

	@Override
	public void onReceivedError(WebView webView, int paramInt, String paramString1, String paramString2) {
		
		// Return a browser failure
		Bundle params = new Bundle();
		params.putString("reason", CheckoutBrowserResults.BROWSER_FAILURE);
		params.putString("detail_1", paramString1);
		params.putString("detail_2", paramString2);
		this.mListener.onFailure(params);
		
		// Avoid super call
		// super.onReceivedError(webView, paramInt, paramString1, paramString2);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView webView, String url) {

		Boolean overrideUrlLoading = Boolean.FALSE;
		
		// Check if equals success url
		String aux1 = url.substring(0, mSuccessUrl.length());
		if (aux1.equals(mSuccessUrl)) {
			
			// Return success with params
			Bundle params = new Bundle();
			try {
				Uri uri = Uri.parse(url);
				params.putString("collection_id", uri.getQueryParameter("collection_id"));				
				params.putString("collection_status", uri.getQueryParameter("collection_status"));				
				params.putString("external_reference", uri.getQueryParameter("external_reference"));				
				params.putString("preference_id", uri.getQueryParameter("preference_id"));				
			}
			catch (Exception ex) {
				// do nothing
			}
			
			this.mListener.onSuccess(params);
			overrideUrlLoading = Boolean.TRUE;
		}

		// Check if equals failure url
		aux1 = url.substring(0, mFailureUrl.length());
		if (aux1.equals(mFailureUrl)) {
			// Return a checkout failure
			Bundle params = new Bundle();
			params.putString("reason", CheckoutBrowserResults.CHECKOUT_FAILURE);			
			this.mListener.onFailure(params);
			overrideUrlLoading = Boolean.TRUE;
		}
		
		return overrideUrlLoading;
	}

	@Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
       super.onPageStarted(view, url, favicon); 
    }

	public abstract interface CheckoutResponseListener {
		public abstract void onSuccess(Bundle paramBundle);
		public abstract void onFailure(Bundle paramBundle);
	}
}