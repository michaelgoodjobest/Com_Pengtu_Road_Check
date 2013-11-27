package com.road.check.app;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import as.relistic.image.AsynchronousLocationImage;
import as.relistic.image.AsynchronousLocationImage.ImageCallback;

import com.road.check.R;
import com.road.check.common.ViewCache;
import com.road.check.model.Road_Check_Data_Table;

public class CRecordsAdapter extends ArrayAdapter<Road_Check_Data_Table>{
	private List<Road_Check_Data_Table> list;
	private Activity activity;
	private ListView listview;
	private AsynchronousLocationImage asynchronousImage;
	public CRecordsAdapter(Activity activity,List<Road_Check_Data_Table> list,ListView listview) {
		super(activity, 0, list);
		this.list = list;
		this.activity = activity; 
		this.listview = listview;
		asynchronousImage = new AsynchronousLocationImage();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Road_Check_Data_Table item = list.get(position);
		
		View rowView = convertView;
		ViewCache viewcache;
		if(rowView == null){
			rowView = activity.getLayoutInflater().inflate(R.layout.app_checkhistoricalrecords_list_item, null);
			viewcache = new ViewCache(rowView);
			rowView.setTag(viewcache);
		}else{
			viewcache = (ViewCache)rowView.getTag();
		}
		
		String imageUrl = item.firstImageUrl;
        ImageView item_imgview = viewcache.getitem_imgview();
//        Bitmap image = BitmapFactory.decodeFile(imageUrl);
//        Drawable drawable = new BitmapDrawable(image);
//        item_imgview.setBackgroundDrawable(drawable);
        item_imgview.setTag(imageUrl);
        if(item.firstImageUrl == null || item.firstImageUrl.trim().replace(" ", "").equals("")){
        	item_imgview.setBackgroundResource(R.drawable.list_item_default_icon);
        }else{
	        Drawable cachedImage = asynchronousImage.add(imageUrl, new ImageCallback() {
	            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
	                ImageView imageViewByTag = (ImageView) listview.findViewWithTag(imageUrl);
	                if (imageViewByTag != null) {
	                    imageViewByTag.setBackgroundDrawable(imageDrawable);
	                }
	            }
	        });
	        
	        if(cachedImage != null){
	        	item_imgview.setBackgroundDrawable(cachedImage);
	        }
        }
		
		TextView item_title = viewcache.getitem_title();
		TextView item_name = viewcache.getitem_name();
		TextView item_info = viewcache.getitem_info();
		TextView item_time = viewcache.getitem_time();
		
		item_title.setText(item.roundName);
		item_name.setText("°æ" + item.reportName + "°ø");
		
		if(item.category > 0){
			item_info.setText("√Ë ˆ£∫"+item.discribe);
		}else{
			item_info.setText("√Ë ˆ£∫" + item.diseaseString + item.d_roadbedDiseaseString + item.d_dewateringDiseaseSting);
		}
		
		item_time.setText(item.deteTime);
		
		return rowView;
	}

}
