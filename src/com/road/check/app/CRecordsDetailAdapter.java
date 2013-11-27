package com.road.check.app;


import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.road.check.R;
import com.road.check.common.ViewCache;

public class CRecordsDetailAdapter extends ArrayAdapter<String>{
	private List<String> list_imgpath;
	private Activity activity;
	public CRecordsDetailAdapter(Activity activity,List<String> list_imgpath) {
		super(activity,0, list_imgpath);
		this.list_imgpath = list_imgpath;
		this.activity = activity;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String item_str = list_imgpath.get(position);
		
		View rowView = convertView;
		ViewCache viewcache;
		if(rowView == null){
			rowView = activity.getLayoutInflater().inflate(R.layout.app_checkhistoricalrecords_detail_item, null);
			viewcache = new ViewCache(rowView);
			rowView.setTag(viewcache);
		}else{
			viewcache = (ViewCache)rowView.getTag();
		}
		
        ImageView item_imgview = viewcache.getitem_imgview();
    	Bitmap image = BitmapFactory.decodeFile(item_str);
        Drawable drawable = new BitmapDrawable(image);
    	item_imgview.setBackgroundDrawable(drawable);
        return rowView;
	}

}
