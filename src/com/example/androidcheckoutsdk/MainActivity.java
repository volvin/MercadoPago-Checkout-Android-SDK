package com.example.androidcheckoutsdk;

import com.mercadopago.checkout.android.CheckoutBrowserActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

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
	 * payCheckout
	 * 
	 * This operation launches MercadoPago checkout based on a given init point.
	 */
    public void payCheckout(View view) {
    	
    	/*
    	 * First of all: Create the checkout preference or use one existing. For more info go to: https://github.com/mercadopago/sdk-java
    	 * Then replace the strings below with your actual values:
    	 * -- The preference checkout init point
    	 * -- The success and failure urls used in the preference creation 
    	 */
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
    	 		Bundle b = data.getExtras();
    	 		if (b != null) {
        			String reason = b.getString("reason");
        	    	if (reason == "failure") {
        	    		resultText.setText("Failure !!"); 
        	    	}
        	    	else {
        	    		resultText.setText("Canceled by User !!");     	    		
        	    	}    	 			
    	 		}
    	     }
    	  }
    }    
}
