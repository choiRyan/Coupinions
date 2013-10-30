package com.example.coupinionsc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import android.util.Log;

public class Parser {
	InputStream is;
	public Parser(){}
	public void Parse(String cmd, ArrayList<NameValuePair> nameValuePairs){
		String result = "";
		//the year data to send
		//ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(cmd.equals("AddUser")){
			nameValuePairs.add(new BasicNameValuePair("cmd","AddUser"));
			nameValuePairs.add(new BasicNameValuePair("Email","dbadded@usc.edu"));
			nameValuePairs.add(new BasicNameValuePair("Epassword","woot"));
			nameValuePairs.add(new BasicNameValuePair("Points","1"));
			nameValuePairs.add(new BasicNameValuePair("Gender","F"));
		}else if(cmd.equals("GetAnswers")){
			int id = 1; // hard coded; bad
			nameValuePairs.add(new BasicNameValuePair("cmd","GetAnswers"));
			nameValuePairs.add(new BasicNameValuePair("ID",id+""));
		}
		
		
		try{//network stuff
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://ec2-54-200-84-148.us-west-2.compute.amazonaws.com/dbgetset.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
		//convert response to string
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			if ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			result=sb.toString();
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
		}

		//parse json data
		try{
			JSONArray jArray = new JSONArray(result);
			for(int i=0;i<jArray.length();i++){
				JSONObject json_data = jArray.getJSONObject(i);
				Log.i("log_tag", "response: " + json_data.getString("response"));
				//this is format for getters
				Log.i("log_tag","id: "+json_data.getInt("id")+
						", name: "+json_data.getString("name")+
						", sex: "+json_data.getInt("sex")+
						", birthyear: "+json_data.getInt("birthyear")
						);
				
			}

		}catch(JSONException e){
			Log.e("log_tag", "Error parsing data "+e.toString());
		}
	}
}
