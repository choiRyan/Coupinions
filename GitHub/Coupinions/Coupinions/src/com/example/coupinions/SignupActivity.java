package com.example.coupinions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SignupActivity extends Activity {

	//These variables will handle the user's registration info
	//Use for the database interaction
	private String newUserEmail;
	private String newPassword;
	private boolean newUserGender; // male = true;
	private boolean checked;
	private boolean registerDone;

	private RadioButton radioGenderButton;
	/*public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + 
	          "\\@" +							
	          "[a-zA-Z][a-zA-Z\\-]{0,64}" +      
	          "(" +
	          "\\." +                           
	          "[a-zA-Z][a-zA-Z\\-]{0,25}" +      
	          ")+"
	      );*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		//yet again, eclipse forced these to be final
		final EditText textNewEmail = (EditText) findViewById(R.id.textEmailSignup);
		final EditText textNewPassword = (EditText) findViewById(R.id.textPasswordSignup);
		final RadioGroup radioGender = (RadioGroup) findViewById(R.id.radioGrpGender);
		Button btnSignUp = (Button) findViewById(R.id.btnSignMeUp);
		//this is fine; if you want to parse the strings that it contains, set a new string to read off of it?

		//Register the new user
		btnSignUp.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) 
			{
				//get user login info
				setNewUserEmail(textNewEmail.getText().toString());
				setNewPassword(textNewPassword.getText().toString());

				//get gender here too					
				//get selected radio button from radioGroup
				int selectedId = radioGender.getCheckedRadioButtonId();

				//find the radio button by returned id
				radioGenderButton = (RadioButton) findViewById(selectedId);
				if(radioGenderButton == findViewById(R.id.radioBtnMale)){
					setNewUserGender(true);
					checked = true;
				}
				else{
					setNewUserGender(false);
					checked = true;
				}
				//***ADD CODE FOR REGISTERING NEW USER HERE*** email, pass, gender
				//if missed email or pw...
				if(newUserEmail.equals(null) || newPassword.equals(null)){
					System.out.println("You can't leave any of these forms blank!");
				}
				if(newUserEmail.length() < 7){
					System.out.println("Invalid E-mail. USC E-mails only!");
				}
				else if(!newUserEmail.toLowerCase().contains("@usc.edu")
						&& newUserEmail.charAt((newUserEmail.length()-1)) == 'u'){
					//naive check^
					//returning 0 means the strings are the same
					System.out.println("Invalid E-mail. USC E-mails Only!");
				} // if missed password 
				else if(newPassword.length() < 6){
					System.out.println("Password must be 6 or more characters.");
				} // if missed checkmark
				else if(!checked){
					System.out.println("Please select your gender");
				}else{ // else you're good to go, likely
					new SendData().execute();
					// Add information to DataBase if not a repeat user
					// For testing purposes, you go straight to the question page after this
					if(registerDone){
						Intent intent = new Intent(SignupActivity.this, MainMenuActivity.class);
						startActivity(intent); //switch activities
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	public String getNewUserEmail() {
		return newUserEmail;
	}

	public void setNewUserEmail(String newUserEmail) {
		this.newUserEmail = newUserEmail;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean getNewUserGender() {
		return newUserGender;
	}

	public void setNewUserGender(boolean newUserGender) {
		this.newUserGender = newUserGender;
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
			String email = newUserEmail;
			String password = newPassword;
			boolean male = newUserGender;
			JSONObject json = new JSONObject();
			try{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ec2-54-200-180-160.us-west-2.compute.amazonaws.com");
				// JSON data:
				json.put("email", email);
				json.put("password", password);
				json.put("cmd",0);
				if(male) json.put("gender", "M");
				else json.put("gender", "F");		 
				JSONArray postjson=new JSONArray();
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
					if(sb.toString().toLowerCase().contains("duplicate")){
						Toast.makeText(getApplicationContext(), "Looks like you already have an account.", 
								Toast.LENGTH_LONG).show();
						registerDone = false;
					}else{ registerDone = true; }
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
