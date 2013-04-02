package com.mercadopago.checkout.android;

import android.graphics.Bitmap;
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
		super.onReceivedError(webView, paramInt, paramString1, paramString2);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView webView, String url) {

		Boolean overrideUrlLoading = Boolean.FALSE;
		
		// Check if equals success url
		String aux1 = url.substring(0, mSuccessUrl.length());
		if (aux1.equals(mSuccessUrl)) {
			this.mListener.onSuccess(null);
			overrideUrlLoading = Boolean.TRUE;
		}

		// Check if equals failure url
		aux1 = url.substring(0, mFailureUrl.length());
		if (aux1.equals(mFailureUrl)) {
			this.mListener.onFailure(null);
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