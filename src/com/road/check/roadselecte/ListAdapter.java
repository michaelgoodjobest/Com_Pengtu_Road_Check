package com.road.check.roadselecte;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.common.ViewCache;
import com.road.check.model.Round;

public class ListAdapter extends ArrayAdapter<Round>{
	private List<Round> list;
	private Activity activity;
	public ListAdapter(Activity activity,List<Round> list) {
		super(activity, 0, list);
		this.list = list;
		this.activity = activity;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Round item = list.get(position);
		
		View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.roadselecte_list_item, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        TextView item_title = (TextView)viewCache.getitem_title();
        TextView item_start_location = (TextView)viewCache.getitem_start_location();
        TextView item_end_location = (TextView)viewCache.getitem_end_location();
        TextView item_length = (TextView)viewCache.getitem_length();
        TextView item_roundid = (TextView)viewCache.getitem_roundid();
        TextView item_width = (TextView)viewCache.getitem_roadwidth();
        
        item_roundid.setText(item.roundId);
        item_width.setText(item.froadwidth);
        item_title.setText(item.name);
        item_start_location.setText(item.start_location);
        item_end_location.setText(item.end_location);
        item_length.setText(item.flength);
        
		if(item.state == 1){
			rowView.setBackgroundResource(R.drawable.selector_listview_selected_item_bg);     	
		}else{
			rowView.setBackgroundResource(R.drawable.selector_listview_item_bg);
		}
		
        
		return rowView;
	}
}
