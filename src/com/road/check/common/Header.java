package com.road.check.common;

import com.road.check.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 通用 头部
 */
public class Header {
	private Activity activity;
	/**头部左侧按钮*/
	public Button btn_header_left = null;
	/**头部右侧按钮*/
	public Button btn_header_right = null;
	/**头部文字*/
	public TextView txt_header = null;
	public RelativeLayout header = null;
	

	/**
	 * 加载头部元素
	 * @param title：大标题文字
	 * @param headerImageResid：头部背景图片的资源id，默认为0
	 * @param leftText：左侧按钮文字
	 * @param leftImageResid：左侧按钮背景图资源id，默认为0
	 * @param leftClick：左侧按钮点击事件，没事件时为null，同时隐藏
	 * @param rightText：右侧按钮文字
	 * @param rightImageResid：左侧按钮背景图资源id，默认为0
	 * @param rightClick：右侧按钮点击事件，没事件时为null，同时隐藏
	 */
	public Header(Context appContext, String title, int headerImageResid, 
			String leftText, int leftImageResid, View.OnClickListener leftClick, 
			String rightText, int rightImageResid, View.OnClickListener rightClick){
		init(appContext, title, headerImageResid, leftText, leftImageResid, leftClick, rightText, rightImageResid, rightClick);
	}
	/**
	 * 加载头部元素
	 * @param title：大标题文字
	 * @param leftText：左侧按钮文字
	 * @param leftClick：左侧按钮点击事件，没事件时为null，同时隐藏
	 * @param rightText：右侧按钮文字
	 * @param rightClick：右侧按钮点击事件，没事件时为null，同时隐藏
	 */
	public Header(Context appContext, String title, 
			String leftText, View.OnClickListener leftClick, 
			String rightText, View.OnClickListener rightClick){
		init(appContext, title, 0, leftText, 0, leftClick, rightText, 0, rightClick);
	}
	/**
	 * 加载头部元素
	 * @param title：大标题文字
	 */
	public Header(Context appContext, String title){
		init(appContext, title, 0, "", 0, null, "", 0, null);
	}
	
	
	/**
	 * 加载头部元素
	 * @param title：大标题文字
	 * @param headerImageResid：头部背景图片的资源id，默认为0
	 * 
	 * @param leftText：左侧按钮文字
	 * @param leftImageResid：左侧按钮背景图资源id，默认为0
	 * @param leftClick：左侧按钮点击事件，没事件时为null，同时隐藏
	 * 
	 * @param rightText：右侧按钮文字
	 * @param rightImageResid：左侧按钮背景图资源id，默认为0
	 * @param rightClick：右侧按钮点击事件，没事件时为null，同时隐藏
	 */
	private void init(Context appContext, String title, int headerImageResid, 
			String leftText, int leftImageResid, View.OnClickListener leftClick, 
			String rightText, int rightImageResid, View.OnClickListener rightClick){
		activity = (Activity)appContext;
		
		header = (RelativeLayout)activity.findViewById(R.id.header);
		header.setVisibility(View.VISIBLE);
		btn_header_left = (Button)activity.findViewById(R.id.header_button_left);
		btn_header_right = (Button)activity.findViewById(R.id.header_button_right);
		txt_header = (TextView)activity.findViewById(R.id.header_text);
		
		
		btn_header_left.setVisibility((leftClick == null) ? View.GONE : View.VISIBLE);
		btn_header_right.setVisibility((rightClick == null) ? View.GONE : View.VISIBLE);
		txt_header.setVisibility((title.length() == 0) ? View.GONE : View.VISIBLE);
		

		if (headerImageResid != 0){
//			header.setBackgroundResource(headerImageResid);
		}else{
			Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.header_bg);

			BitmapDrawable drawable = new BitmapDrawable(bitmap);
			drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			drawable.setDither(true);
//			header.setBackgroundDrawable(drawable);
		}
		
		if (leftImageResid != 0){
			btn_header_left.setBackgroundResource(leftImageResid);
		}
		if (rightImageResid != 0){
			btn_header_right.setBackgroundResource(rightImageResid);
		}
		
		btn_header_left.setText(leftText);
		btn_header_right.setText(rightText);
		txt_header.setText(title);
		
		
		btn_header_left.setOnClickListener(leftClick);
		btn_header_right.setOnClickListener(rightClick);
	}
	
	/**设置标题*/
	public void setTitle(String title){
		txt_header.setText(title);
	}
	/**设置箭头按钮：左侧*/
	public void setArrowButtonLeft(boolean flag){
		int paddingArrow = dip2px(activity, 20);
		int paddingArrowEnd = dip2px(activity, 10);
		int paddingNumber = dip2px(activity, 15);
		if (flag){
			btn_header_left.setBackgroundResource(R.drawable.selector_header_button_left);
			btn_header_left.setPadding(paddingArrow, 0, paddingArrowEnd, 0);
		}else{
			btn_header_left.setBackgroundResource(R.drawable.selector_header_button_right);
			btn_header_left.setPadding(paddingNumber, 0, paddingNumber, 0);
		}
	}
	/**设置箭头按钮：右侧*/
	public void setArrowButtonRight(boolean flag){
		int paddingArrow = dip2px(activity, 20);
		int paddingArrowEnd = dip2px(activity, 10);
		int paddingNumber = dip2px(activity, 15);
		if (flag){
			btn_header_right.setBackgroundResource(R.drawable.selector_header_button_right);
			btn_header_right.setPadding(paddingArrowEnd, 0, paddingArrow, 0);
		}else{
			btn_header_right.setBackgroundResource(R.drawable.selector_header_button_right);
			btn_header_right.setPadding(paddingNumber, 0, paddingNumber, 0);
		}
	}
	
	
	/**单位dip转为px*/
	public int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	}
	/**单位px转为dip*/
	public int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
}