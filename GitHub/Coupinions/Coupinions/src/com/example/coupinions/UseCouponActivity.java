package com.example.coupinions;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class UseCouponActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_use_coupon);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.use_coupon, menu);
		return true;
	}

}
