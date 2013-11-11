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

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * To change the question and the answer choices
 * make changes to the strings: string_optionA, string_optionB, string_optionC, string_optionD
 * and also the actual question: string_question
 * 
 * located in res>values>strings.xml
 * 
 * @author Izzy
 * @authorServerSide Ryan
 */
public class QuestionActivity extends Activity 
{

	//declare variables
		private static final char CHOICE_A = 'A';
		private static final char CHOICE_B = 'B';
		private static final char CHOICE_C = 'C';
		private static final char CHOICE_D = 'D'; 
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//we access onCreate so that we can set up the page instantly upon loading.
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		 
		new SendData().execute();
		
		Button btnOptionA = (Button) findViewById(R.id.imgBtnA); 
		Button btnOptionB = (Button) findViewById(R.id.imgBtnB); 
		Button btnOptionC = (Button) findViewById(R.id.imgBtnC); 
		Button btnOptionD = (Button) findViewById(R.id.imgBtnD); 
		
		//*The Click Listeners*
		//pass choice a to the answer activity
		btnOptionA.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				//a toast to tell the user what they chose
				Toast.makeText(getApplicationContext(), "You have chosen option " + CHOICE_A, Toast.LENGTH_LONG).show();
				//moves onto the next activity and passes the choice on
				Intent intent = new Intent(QuestionActivity.this, ResultsActivity.class);

				//**Send CHOICE_A to database
				
				startActivity(intent); //switch activities
			   }
			});
				
		//View coupons
		btnOptionB.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				//a toast to tell the user what they chose
				Toast.makeText(getApplicationContext(), "You have chosen option " + CHOICE_B, Toast.LENGTH_LONG).show();
				//moves onto the next activity and passes the choice on
				Intent intent = new Intent(QuestionActivity.this, ResultsActivity.class);

				//**Send CHOICE_B to database
				
				startActivity(intent); //switch activities
			   }
			});
				
		btnOptionC.setOnClickListener(new View.OnClickListener() {	
			@Override
			  public void onClick(View v) 
			  {
				//a toast to tell the user what they chose
				Toast.makeText(getApplicationContext(), "You have chosen option " + CHOICE_C, Toast.LENGTH_LONG).show();
				//moves onto the next activity and passes the choice on
				Intent intent = new Intent(QuestionActivity.this, ResultsActivity.class);

				//**Send CHOICE_C to database
				
				startActivity(intent); //switch activities
			   }
			});
				
		btnOptionD.setOnClickListener(new View.OnClickListener() {	
			@Override
			   public void onClick(View v) 
			   {
				//a toast to tell the user what they chose
				Toast.makeText(getApplicationContext(), "You have chosen option " + CHOICE_D, Toast.LENGTH_LONG).show();
				//moves onto the next activity and passes the choice on
				Intent intent = new Intent(QuestionActivity.this, ResultsActivity.class);

				//**Send CHOICE_D to database
				
				startActivity(intent); //switch activities
			   }
			});	
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
			JSONObject json = new JSONObject();
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ec2-54-200-180-160.us-west-2.compute.amazonaws.com");
				JSONArray postjson = new JSONArray();
				// JSON data:
					json.put("cmd",3);// 3: get whether or not user has answered latest Q.	 
					postjson.put(json);
					// Post the data:
					httppost.setHeader("json",json.toString());
					httppost.getParams().setParameter("jsonpost",postjson);

				// Execute HTTP Post Request
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
					Log.i("RESPONSE", sb.toString());
					//sb.toString() analysis
				}
			} catch (UnsupportedEncodingException uee) {
				System.out.println("unsupported encoding error (uh oh)");
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				System.out.println("Client-Protocol exception error (ports?)");
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("IO error (ugh)");
				ioe.printStackTrace();
			} catch (JSONException je) {
				System.out.println("Json error(how???)");
				je.printStackTrace();
			}
			return null;
		}
	}
	
	
}
