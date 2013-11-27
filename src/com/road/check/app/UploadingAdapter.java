package com.road.check.app;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import as.relistic.image.AsynchronousLocationImage;
import as.relistic.image.AsynchronousLocationImage.ImageCallback;

import com.road.check.R;
import com.road.check.common.ViewCache;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.sqlite.DatabaseService;

public class UploadingAdapter extends ArrayAdapter<Road_Check_Data_Table>{
	private List<Road_Check_Data_Table> list;
	private Activity activity;
	private ListView listview;
	private AsynchronousLocationImage asynchronousImage;
	private DatabaseService dbs;
	private ImageView img_checked;
	public UploadingAdapter(Activity activity,List<Road_Check_Data_Table> list,ListView listview,DatabaseService dbs,ImageView img_checked) {
		super(activity, 0, list);
		this.list = list;
		this.activity = activity;
		this.listview = listview;
		this.dbs = dbs;
		asynchronousImage = new AsynchronousLocationImage();
		this.img_checked = img_checked;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Road_Check_Data_Table item = list.get(position);
		
		View rowView = convertView;
		ViewCache viewcache;
		if(rowView == null){
			rowView = activity.getLayoutInflater().inflate(R.layout.app_uploading_list_item, null);
			viewcache = new ViewCache(rowView);
			rowView.setTag(viewcache);
		}else{
			viewcache = (ViewCache)rowView.getTag();
		}
		
		String imageUrl = item.firstImageUrl;
        ImageView item_image = viewcache.getitem_imgview();
        item_image.setTag(imageUrl);
        if(item.firstImageUrl == null || item.firstImageUrl.trim().replace(" ", "").equals("")){
        	item_image.setBackgroundResource(R.drawable.list_item_default_icon);
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
	        	item_image.setBackgroundDrawable(cachedImage);
	        }
        }
		
		TextView item_title = viewcache.getitem_title();
		TextView item_info= viewcache.getitem_info();
		TextView item_type = viewcache.getitem_type();
		final CheckBox item_check = viewcache.getitem_check();
		
		item_title.setText(item.roundName);
		item_info.setText(item.direction);
		if(item.category == 0){
			item_type.setText("报表");
		}else{
			item_type.setText("路点");
		}
		
		if(item.isSelected == 1){
			item_check.setChecked(true);
		}else{
			item_check.setChecked(false);
		}
		
		item_check.setTag(item.Id);
		item_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String id = item_check.getTag().toString();
				if(isChecked){
					dbs.road_check_table.updateState(1,id);
				}else{
					dbs.road_check_table.updateState(0,id);
				}
				
				if(dbs.road_check_table.isAllSelected()){
					img_checked.setImageResource(R.drawable.check_mark);
				}else{
					img_checked.setImageResource(0);
				}
			}
		});
		
		return rowView;
	}

}
