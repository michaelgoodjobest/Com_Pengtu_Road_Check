package com.road.check.roadselecte;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.common.ViewCache;
import com.road.check.model.Round;

public class IndexListAdapter extends ArrayAdapter<String>{
	private List<String> list;
	private Activity activity;
	public IndexListAdapter(Activity activity,List<String> list) {
		super(activity, 0, list);
		this.list = list;
		this.activity = activity;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String item_str = list.get(position);
		
		View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.roadselecte_indexlist_item, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        
        TextView item_index = (TextView)viewCache.getitem_index();
        
        item_index.setText(item_str);
		return rowView;
	}
}
