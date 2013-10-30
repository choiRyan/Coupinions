package com.example.coupinionsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SignUpPage extends Activity {
	boolean m;
	EditText inputEmail;
	EditText inputPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_page);
		// Show the Up button in the action bar.
		setupActionBar();

		//important strings for sending
		inputEmail = (EditText) findViewById(R.id.datEmail);
		inputPassword = (EditText) findViewById(R.id.datepassword);
	}


	public void onRadioButtonClicked(View view) {

		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.bMale:
			if (checked)
				m = true;
			break;
		case R.id.bFemale:
			if (checked)
				m = false;
			break;
		}
	}

	/** What happens when you click the Submit button */
	public void SignedUp(View page3) {
		// You're Now signed up for Coupinions
		//background addition of user stuff!
		new SendData().execute();
		// Add information to DataBase
		// For testing purposes, you go straight to the question page after this
		Intent intent = new Intent(this, QuestionActivity.class);
		startActivity(intent);
	}
	private class SendData extends AsyncTask<String, Integer, Void> {

		protected void onProgressUpdate() {
			//called when the background task makes any progress
		}

		protected void onPreExecute() {
			//called before doInBackground() is started
		}
		protected void onPostExecute() {
			//called after doInBackground() has finished 
		}

		@Override
		protected Void doInBackground(String... params) {
			String email = inputEmail.getText().toString();
			String password = inputPassword.getText().toString();
			boolean male = m;
			JSONObject json = new JSONObject();
			InputStream is;
			try{
				HttpClient client = new DefaultHttpClient();
				String postURL = ("http://ec2-54-200-84-148.us-west-2.compute.amazonaws.com/dbgetset.php");
				HttpPost post = new HttpPost(postURL);

				json.put("cmd", "AddUser");
				json.put("Email", email);
				json.put("Epassword", password);
				json.put("Points", "3");
				if(male) json.put("Gender", "M");
				else {
					json.put("Gender", "F");
				}
				
				//turn json to string form
				StringEntity se = new StringEntity(json.toString());  
				
				//send json straight to php
				ArrayList<NameValuePair> nvp = new ArrayList<NameValuePair>();    
		        nvp.add(new BasicNameValuePair("json", json.toString()));
		        post.setEntity(new UrlEncodedFormEntity(nvp));

				// HTTP Post request (PHP does its thing)
				HttpResponse response = client.execute(post);
				// PHP did its thing. So we turn response to string
				String _response=EntityUtils.toString(se);
				HttpEntity entity = response.getEntity();
			    _response = EntityUtils.toString(entity);
			    Log.i("RESPONSE_REAL", _response);
				if(response != null) {
		            Log.i("RESPONSE", entity.getContent().toString()); // success or failure msg
		        }
			} catch (UnsupportedEncodingException uee) {
				System.out.println("wj");
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				System.out.println("wjw");
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("wjio");
				ioe.printStackTrace();
			} catch (JSONException je) {
				System.out.println("wjson");
				je.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
