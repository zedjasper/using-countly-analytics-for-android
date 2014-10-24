/**
 * @file ProductAdapter.java
 * @author Zed Jasper Onono
 * @date Oct 17, 2014 
 * Copyright (c) 2014 Kola Studios. All Rights Reserved 
 */

package com.ks.gdgmuk;

import java.util.List;

import ly.count.android.api.Countly;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

public class ProductAdapter extends ArrayAdapter<Product>{
	Context cxt;
	public ProductAdapter(Context context, int resource, List<Product> objects) {
		super(context, resource, objects);
		cxt = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		final Product p = getItem(position);
		JSONObject settings = getSettings();
		
		if(row == null){
			LayoutInflater inflator = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(R.layout.item_row, null);
		}
		
		AQuery aq = new AQuery(row);
		aq.id(R.id.txt_title).text(p.title);
		
		ImageOptions options = new ImageOptions();
		options.targetWidth = 100;
		options.animation = AQuery.FADE_IN;
		
		aq.id(R.id.img_thumbnail).image(p.thumbnailUrl, options);
		aq.id(R.id.rtn_rating).rating(p.rating);
		aq.id(R.id.txt_price).text(String.format("%,d", p.price));
		
		if(settings != null){
			Utils.log("Settings: " + settings);
			try{
				aq.id(R.id.btn_purchase).text(settings.getString("button_text"));
				aq.id(R.id.txt_price).textColor(Color.parseColor(settings.getString("price_label_color")));
				aq.id(R.id.btn_purchase).backgroundColor(Color.parseColor(settings.getString("button_bg_color")));
				aq.id(R.id.btn_purchase).textColor(Color.parseColor(settings.getString("button_text_color")));
			}catch(Exception ex){
				Utils.log("Error handling settings");
			}
		}
		
		aq.id(R.id.btn_purchase).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(cxt, "You have purchased " + p.title, Toast.LENGTH_LONG).show();
				Countly.sharedInstance().recordEvent("PURCHASE", p.price);
			}
		});
		return row;
	}
	
	private JSONObject getSettings(){
		SharedPreferences prefs = cxt.getSharedPreferences("settings", 1);
		JSONObject settings = null;
		try{
			String s = prefs.getString("settings", null);
			if(s != null){
				settings = new JSONObject(s);
			}
		}catch(Exception ex){
			Utils.log("Error opening settings");
		}
		return settings;
	}
}


