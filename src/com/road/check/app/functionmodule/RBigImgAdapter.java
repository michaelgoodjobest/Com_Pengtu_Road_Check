package com.road.check.app.functionmodule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.common.ViewCache;

public class RBigImgAdapter extends ArrayAdapter<String>{
	private List<String> list;
	private Activity activity;
	public RBigImgAdapter(Activity activity,List<String> list) {
		super(activity,0,list);
		this.list = list;
		this.activity = activity;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String imgPath = list.get(position);
		
		View rowView = convertView;
		ViewCache viewCache;
		if(rowView == null){
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.app_roadpointphoto_bigimg_item, null);
			viewCache = new ViewCache(rowView);
			rowView.setTag(viewCache);
		}else{
			viewCache = (ViewCache)rowView.getTag();
		}
		
		ImageView item_imgview = (ImageView)viewCache.getitem_imgview();
		TextView item_imaview_name = (TextView)viewCache.getitem_imaview_name();
		
		item_imgview.setBackgroundDrawable(loadImageFromPath(imgPath));
		item_imaview_name.setText(getImageName(imgPath));
		return rowView;
	}
	/**获取图片名称*/
	public String getImageName(String path){
		int index = path.lastIndexOf("/");
		return path.substring(index + 1, path.length()-1);
	}
	/**单位dip转为px*/
	public int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	}
	public static Drawable loadImageFromPath(String imagePath) {
    	try{
    		FileInputStream fis = new FileInputStream(imagePath);
    		Bitmap bitmap = BitmapFactory.decodeStream(fis);
    		BitmapDrawable bd = new BitmapDrawable(bitmap); 
    		fis.close();
    		return bd;
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
