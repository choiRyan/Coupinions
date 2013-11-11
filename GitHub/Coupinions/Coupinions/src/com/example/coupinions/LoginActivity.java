package com.example.coupinions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// This is the first screen anyone see when cold booting this program.
public class LoginActivity extends Activity
{
	//These variables will handle the user's info
	//Use for the database interaction
	private String userEmail;
	private String password;
	private boolean blnUserVerified; //default false
    public static final String PREFS_NAME = "session";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// if we're getting back to the logged in page, see if the user is already logged in; 
		// send directly to main menu instead of login
		SharedPreferences session = getSharedPreferences(PREFS_NAME, 0);
		if(session.getBoolean("loggedin", false)){ // if not found, is false
			Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
			startActivity(intent); //switch activities
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button buttonLogin = (Button) findViewById(R.id.btnLogin);
		TextView textClickToSignUp = (TextView) findViewById(R.id.textClickToSignup);

		//eclipse forced these to be final
		final EditText textEmailLogin = (EditText) findViewById(R.id.emailLogin);
		final EditText textPasswordLogin = (EditText) findViewById(R.id.passwordLogin);

		buttonLogin.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) 
			{
				//get user login info
				setUserEmail(textEmailLogin.getText().toString());
				setPassword(textPasswordLogin.getText().toString());

				//***ADD CODE FOR VERIFYING USER IDENTITY HEREEEE***
				new SendData().execute(); // done
			}
		});

		//Go to signup page
		textClickToSignUp.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent); //switch activities
			}
		});
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private class SendData extends AsyncTask<String, Integer, Void> {

		protected void onProgressUpdate() {
			//called when the background task makes any progress
		}

		protected void onPreExecute() {
			//called before doInBackground() is started
		}
		protected void onPostExecute() {
			System.out.println("wlekfj");
		}

		@Override
		protected Void doInBackground(String... params) {
			//use strings userEmail and password
			JSONObject json = new JSONObject();
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ec2-54-200-180-160.us-west-2.compute.amazonaws.com");
				// JSON data:
				json.put("email", userEmail);
				json.put("password", password);
				json.put("cmd",1); // 1 = sign in with user and pw
				JSONArray postjson = new JSONArray();
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
					//display in log
					Log.i("RESPONSE", sb.toString());
					//				int points;
					System.out.println(sb.toString());
					if(sb.toString().contains("INCORRECT")){ // no match; don't do anything
						blnUserVerified = false; // don't do anything
						//prompt a message, or just toast
						
					} // if you think this is parsed weirdly... i had php send it that way
					else if(sb.toString().toUpperCase().contains("FOUND")){
						//need to get user_id, points, whether answered all questions
						blnUserVerified = true;
						if(blnUserVerified) //if the user gets verified with email and pass
						{
							StringTokenizer s = new StringTokenizer(sb.toString());
							s.nextToken(); // returns whitespace; throw this away
							s.nextToken(); // returns "FOUND"; throw this away (HARDCODED no spaces)
							
							//save user data locally. loggedin for services, points, and user_id for db.
							SharedPreferences session = getSharedPreferences(PREFS_NAME, 0);
						    SharedPreferences.Editor editor = session.edit();
						    editor.putBoolean("loggedin", true);
							editor.putInt("user_id", Integer.parseInt(s.nextToken()));//user id (INT no spaces)
							editor.putInt("points", Integer.parseInt(s.nextToken()));//number of points (INT no spaces)
							editor.commit();
							Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
							startActivity(intent); //switch activities
							//moves onto the next activity and passes the choice on
							
						}
					}
					//now do something with the output.
				}
			} catch (UnsupportedEncodingException uee) {
				System.out.println("unsupported encoding error");
				uee.printStackTrace();
			} catch (ClientProtocolException cpe) {
				System.out.println("client protocol error");
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("io error");
				ioe.printStackTrace();
			} catch (JSONException je) {
				System.out.println("json error");
				je.printStackTrace();
			}
			return null;
		}
	}

}
