package com.road.check.common;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.road.check.R;

public class ViewCache {
	private View baseView;
	private TextView item_title;
	private TextView item_name;
	private TextView item_time;
	private TextView item_start_location;
	private TextView item_end_location;
	private TextView item_length;
	private TextView item_index;
	private TextView item_error_title;
	private TextView item_info;
	private TextView item_type;
	private TextView item_roundid;
	private TextView item_roadwidth;
	private TextView item_imaview_name;
	private ImageView item_error_imgview;
	private ImageView item_imgview;
	private CheckBox item_check;
	private LinearLayout layout_delete;
	public ViewCache(View baseView) {
	     this.baseView = baseView;
	}
	public TextView getitem_title(){
		if(item_title == null){
			item_title = (TextView)baseView.findViewById(R.id.item_title);
		}
		return item_title;
	}
	public TextView getitem_name(){
		if(item_name == null){
			item_name = (TextView)baseView.findViewById(R.id.item_name);
		}
		return item_name;
	}
	public TextView getitem_time(){
		if(item_time == null){
			item_time = (TextView)baseView.findViewById(R.id.item_time);
		}
		return item_time;
	}
	public TextView getitem_start_location(){
		if(item_start_location == null){
			item_start_location = (TextView)baseView.findViewById(R.id.item_start_location);
		}
		return item_start_location;
	}
	public TextView getitem_end_location(){
		if(item_end_location == null){
			item_end_location = (TextView)baseView.findViewById(R.id.item_end_location);
		}
		return item_end_location;
	}
	public TextView getitem_length(){
		if(item_length == null){
			item_length = (TextView)baseView.findViewById(R.id.item_length);
		}
		return item_length;
	}
	public TextView getitem_index(){
		if(item_index == null){
			item_index = (TextView)baseView.findViewById(R.id.item_index);
		}
		return item_index;
	}
	public TextView getitem_error_title(){
		if(item_error_title == null){
			item_error_title = (TextView)baseView.findViewById(R.id.item_error_title);
		}
		return item_error_title;
	}
	public ImageView getitem_error_imgview(){
		if(item_error_imgview == null){
			item_error_imgview = (ImageView)baseView.findViewById(R.id.item_error_imgview);
		}
		return item_error_imgview;
	}
	public LinearLayout getlayout_delete(){
		if(layout_delete == null){
			layout_delete = (LinearLayout)baseView.findViewById(R.id.layout_delete);
		}
		return layout_delete;
	}
	public TextView getitem_info(){
		if(item_info == null){
			item_info = (TextView)baseView.findViewById(R.id.item_info);
		}
		return item_info;
	}
	public TextView getitem_type(){
		if(item_type == null){
			item_type = (TextView)baseView.findViewById(R.id.item_type);
		}
		return item_type;
	}
	public CheckBox getitem_check(){
		if(item_check == null){
			item_check = (CheckBox)baseView.findViewById(R.id.item_check);
		}
		return item_check;
	}
	public ImageView getitem_imgview(){
		if(item_imgview == null){
			item_imgview = (ImageView)baseView.findViewById(R.id.item_imgview);
		}
		return item_imgview;
	}
	public TextView getitem_roundid(){
		if(item_roundid == null){
			item_roundid = (TextView)baseView.findViewById(R.id.item_roundid);
		}
		return item_roundid;
	}
	public TextView getitem_roadwidth(){
		if(item_roadwidth == null){
			item_roadwidth = (TextView)baseView.findViewById(R.id.item_roadwidth);
		}
		return item_roadwidth;
	}
	public TextView getitem_imaview_name(){
		if(item_imaview_name == null){
			item_imaview_name = (TextView)baseView.findViewById(R.id.item_imaview_name);
		}
		return item_imaview_name;
	}
}

