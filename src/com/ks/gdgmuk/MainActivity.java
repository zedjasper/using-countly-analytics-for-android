package com.ks.gdgmuk;

import java.util.ArrayList;

import ly.count.android.api.Countly;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

public class MainActivity extends ActionBarActivity {
	AQuery aq;
	ArrayList<Product> products;
	/*
	 * Please makes sure you change these to your own values - I will take all these down on 15th Nov - 2014
	 */
	String url = "http://kolastudios.com/gdgmuk/get_products.php"; //Our Products as a JSONArray
	String settingsUrl = "http://kolastudios.com/gdgmuk/get_settings.php"; //Our Settings as a JSONObject. You can't change these settings unless you host your own
	
	String APP_KEY = "4e15048fc410b7e35b3721392cb80175c9e12e40";//Count.ly APP Key
	String C_SERVER_URL = "https://cloud.count.ly"; //Countly server url
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Countly.sharedInstance().init(this, C_SERVER_URL, APP_KEY);
		setContentView(R.layout.activity_main);
		
		aq = new AQuery(this); //Look it up https://code.google.com/p/android-query/
		
		aq.id(R.id.img_retry).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getProducts();
			}
		});
		
		downloadSettings(); //download settings from our server - to be used later in #ProductAdapter
		getProducts();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Countly.sharedInstance().onStart();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		Countly.sharedInstance().onStop();
	}
	
	private void getProducts(){
		Utils.log("getProducts()");
		aq.id(R.id.rlt_error_container).gone();
		aq.id(R.id.prog).visible();
		aq.id(R.id.lst_products).gone();
		aq.ajax(url, JSONArray.class, this, "ajaxCallback");
	}
	
	public void ajaxCallback(String url, JSONArray json, AjaxStatus status){
		Utils.log("Got results");
		if(json != null){
			populateProducts(json);
		}else{
			aq.id(R.id.rlt_error_container).visible();
			aq.id(R.id.prog).gone();
			aq.id(R.id.lst_products).gone();
		}
	}
	
	private void populateProducts(JSONArray json){
		try{
			aq.id(R.id.rlt_error_container).gone();
			aq.id(R.id.prog).gone();
			aq.id(R.id.lst_products).visible();
			
			products = new ArrayList<Product>();
			JSONObject obj;
			Product p;
			
			for(int i = 0; i < json.length(); i++){
				p = new Product();
				obj = json.getJSONObject(i);
				p.title = obj.getString("title");
				p.thumbnailUrl = obj.getString("thumbnail");
				p.price = obj.getInt("price");
				p.rating = obj.getInt("rating");
				
				products.add(p);
			}
			
			ProductAdapter adapter = new ProductAdapter(this, R.layout.item_row, products);
			aq.id(R.id.lst_products).adapter(adapter);
		}catch(Exception ex){
			Utils.log("Error loading products: " + ex.getMessage());
		}
	}
	
	private void downloadSettings(){
		aq.ajax(settingsUrl, JSONObject.class, this, "ajaxSettingsCallback");
	}
	
	public void ajaxSettingsCallback(String u, JSONObject json, AjaxStatus status){
		Utils.log("Got settings");
		if(json != null){
			saveSettings(json);
		}
	}
	
	private void saveSettings(JSONObject json){
		SharedPreferences prefs = getSharedPreferences("settings", 1);
		Editor e = prefs.edit();
		e.putString("settings", json.toString());
		e.commit();
	}
}
