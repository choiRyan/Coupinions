package com.example.coupinions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends Activity {
   private String email;
   private int user_id;
   private int points;
   private int up2date;
   // 1 = yes;first button goes to answers. -1 = no;first button goes to questions. 0=not determined
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//before doing  anything when you go back to the main menu before loading, refresh all the above 4 things
		//find whether answered Qs. 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		new SendData().execute(); // check on creation for q/a option
		Button imgBtn1 = (Button) findViewById(R.id.imgBtnMenu1); //Go to question/answer
		Button imgBtn2 = (Button) findViewById(R.id.imgBtnMenu2); //View coupons
		Button imgBtn3 = (Button) findViewById(R.id.imgBtnMenu3); //Go to main site
		Button imgBtn4 = (Button) findViewById(R.id.imgBtnMenu4); //for testing, go back to login

		
		
		//Go to question
		imgBtn1.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				Intent intent = new Intent(MainMenuActivity.this, QuestionActivity.class);
				startActivity(intent); //switch activities
			   }
			});
		
		//View coupons
		imgBtn2.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				Intent intent = new Intent(MainMenuActivity.this, ViewCouponsActivity.class);
				startActivity(intent); //switch activities
			   }
			});
		
		//Go to main site
		imgBtn3.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				//if clicked, open the Coupinions web site
				Uri uri = Uri.parse("http://www.coupinions.us");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);	
			   }
			});
		
		//for testing, go back to login
		imgBtn4.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   { // 
				SharedPreferences session = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
			    SharedPreferences.Editor editor = session.edit();
			    editor.putBoolean("loggedin", false);
				editor.commit();
				Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
				startActivity(intent); //switch activities
			   }
			});
	}
	
	protected void onStart(){ // this happens every time you go back to Main Menu.
		super.onStart();
		new SendData().execute();
	}
	
	private class SendData extends AsyncTask<String, Integer, Void> {
		@Override
		protected void onPreExecute() {
			//called before doInBackground() is started
		}
			
		@Override
		protected void onPostExecute(Void result) {
			//super.onPostExecute(result);
			Button imgBtn1 = (Button) findViewById(R.id.imgBtnMenu1); //Go to question/answer
			if(up2date == 1) imgBtn1.setText("View last answer");
			else if(up2date == -1) imgBtn1.setText("Answer a question");
		}

		@Override
		protected Void doInBackground(String... params) {
			SharedPreferences session = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
			email = session.getString("email", "E-mail could not be found. Please log in again!");
			user_id = session.getInt("user_id", -1); // returns -1 if not found
			points = session.getInt("points", -1);
			JSONObject json = new JSONObject();
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ec2-54-200-180-160.us-west-2.compute.amazonaws.com");
				json.put("user_id", user_id);
				json.put("cmd", 3); // 3 = see if user has answered latest Q or not.
				JSONArray postjson = new JSONArray();
				postjson.put(json);
				httppost.setHeader("json",json.toString());
				httppost.getParams().setParameter("jsonpost",postjson);
				HttpResponse response = httpclient.execute(httppost);
				// for reading response (json):
				if(response != null)
				{
					InputStream is = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					StringBuilder sb = new StringBuilder();
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							sb.append(line + "\n");
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//display in log
					Log.i("RESPONSE", sb.toString());
					//not analyze output from php
					if(sb.toString().contains("false")){
						up2date = -1;
					}else if(sb.toString().contains("true")){
						up2date = 1;
					}
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

}
