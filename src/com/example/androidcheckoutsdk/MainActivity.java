package com.example.androidcheckoutsdk;

import mercadopago.MP;

import org.codehaus.jettison.json.JSONObject;

import com.mercadopago.checkout.android.CheckoutBrowserActivity;
import com.mercadopago.checkout.android.CheckoutBrowserResults;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Handler mHandler = new Handler();
	private String mCheckoutInitPointUrl = "";
	private String mSuccessUrl = "";
	private String mFailureUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/*
	 * payExistingCheckout
	 * 
	 * This operation runs a fixed preference Checkout sample.
	 */
    public void payExistingCheckout(View view) {
    	
    	navigateFixedCheckoutSample();
    }

    /*
	 * payNewCheckout
	 * 
	 * This operation creates and runs a new Checkout sample.
	 */
    public void payNewCheckout(View view) {
    	
    	createAndNavigateCheckoutSample();
    }

    /*
     * navigateFixedCheckoutSample
     * 
     * Uses a previously created checkout preference and navigates checkout flow.
     */        
    private void navigateFixedCheckoutSample() {

    	String checkout_init_point_url = "https://www.mercadopago.com/mla/checkout/pay?pref_id=85958951-5d0b1625-0788-471d-9d10-3490dfdd2314";
    	String success_url = "https://www.success.com";
    	String failure_url = "https://www.failure.com";
    	
    	// Navigate checkout
		Intent intent = new Intent(getApplicationContext(), CheckoutBrowserActivity.class);
		intent.putExtra("checkout_init_point_url", checkout_init_point_url);
		intent.putExtra("success_url", success_url);
		intent.putExtra("failure_url", failure_url);
		startActivityForResult(intent, 1);		
    }
    
    /*
     * createAndNavigateCheckoutSample
     * 
     * Creates a new checkout preference and navigates checkout flow.
     */    
    private void createAndNavigateCheckoutSample() {
    	
		final String clientId = "1982";
		final String clientSecret = "020Gc1hFJYJQ6ttYqwsl1rs5yIimcHkX";
		final String currencyId = "BRL";
		final String failureUrl = "http://www.failure.com";
		final String quantity = "1";
		final String successUrl = "http://www.success.com";
		final String title = "Donation test";
		final String unitPrice = "3";

    	try {
    		// Start progress dialog
    		final Activity activity = this;
    		final ProgressDialog progressDialog = new ProgressDialog(activity);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		progressDialog.setMessage("Initializing MercadoPago Payments. Please wait ...");
    		progressDialog.show();
    		
	        // Create the checkout preference in a new thread
	        new Thread(new Runnable() {
	        	public void run() {
	        		
        			try {
        	        	// Set preference 
        	    		String preference = "{'items':[{'title':'" + title + "','quantity':" + quantity + ",'currency_id':'" + currencyId + "',";
        	    		preference += "'unit_price':" + unitPrice + "}],'back_urls':{'success':'" + successUrl + "', 'failure':'" + failureUrl + "'}}";

    	        		// Call MercadoPago API
        	    		JSONObject createPreferenceResult = null;
        	    		try {
        	    			MP mp = new MP (clientId, clientSecret);
        	    			createPreferenceResult = mp.createPreference(preference);
        	    		}
    	    			catch (Exception ex) {
    	    				throw ex;
    	    			}        	        	
        	        	
        	        	// Prepare the parameters needed to call checkout     	
        	        	mCheckoutInitPointUrl = createPreferenceResult.getJSONObject("response").getString("init_point");
        	    		mSuccessUrl = createPreferenceResult.getJSONObject("response").getJSONObject("back_urls").getString("success");
        	    		mFailureUrl = createPreferenceResult.getJSONObject("response").getJSONObject("back_urls").getString("failure");                		 	
	            	 }
	            	 catch (Exception ex) {
	            		 // Do something if api call fails
	            	 }	    			
        			
        		 	// After checkout preference creation, do    	    	            		 	
        	        mHandler.post(new Runnable() {
        	            @Override
        	            public void run() {
        	            	try {
        	            		// Stop progress dialog
        	            		progressDialog.dismiss();
        	            		
        	                	// Navigate checkout
        	            		Intent intent = new Intent(getApplicationContext(), CheckoutBrowserActivity.class);
        	            		intent.putExtra("checkout_init_point_url", mCheckoutInitPointUrl);
        	            		intent.putExtra("success_url", mSuccessUrl);
        	            		intent.putExtra("failure_url", mFailureUrl);
        	            		startActivityForResult(intent, 1);        	            		
        	            	}
        	            	catch (Exception ex) {
        	            		// show nothing
        	            	}
        	            }
        	        });	            		             			
	             }
	         }).start();	         
		}
		catch (Exception ex) {
			// Skip			
		} 
    }
    
    /*
     * onActivityResult
     * 
     * Here your code to manage the checkout return.
     * OK for a successful payment creation, CANCELED for failure or "back-back-back" abortion
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	  if (requestCode == 1) {
    		 
    		  TextView resultText = (TextView)findViewById(R.id.resultText);	    	            	            		
          	          
    	     if(resultCode == RESULT_OK) {
    	    	 /*
    	    	  * Replace with your code: Success
    	    	  */
    	    	 resultText.setText("Listo!!");          
    	     }
    	     if (resultCode == RESULT_CANCELED) {
    	    	 /*
    	    	  * Replace with your code: Failure
    	    	  */
    	 		Bundle result = data.getExtras().getBundle("result");
    	 		if (result != null) {
        			String reason = result.getString("reason");
        	    	if (reason.equals(CheckoutBrowserResults.CHECKOUT_FAILURE)) {
        	    		resultText.setText("Checkout Failure !!"); 
        	    	}
        	    	else if (reason.equals(CheckoutBrowserResults.BROWSER_FAILURE)) {        	  
        	    		resultText.setText("Failure !!: " + result.getString("detail_1") + "; " + result.getString("detail_2")); 
        	    	}
        	    	else if (reason.equals(CheckoutBrowserResults.CANCELED_BY_USER)) {
        	    		resultText.setText("Canceled by User !!"); 
        	    	}
        	    	else {
        	    		resultText.setText("Error !!");     	    		
        	    	}    	 			
    	 		}
    	     }
    	  }
    }    
}
