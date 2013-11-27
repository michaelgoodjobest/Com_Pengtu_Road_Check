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
 * ͨ�� ͷ��
 */
public class Header {
	private Activity activity;
	/**ͷ����ఴť*/
	public Button btn_header_left = null;
	/**ͷ���Ҳఴť*/
	public Button btn_header_right = null;
	/**ͷ������*/
	public TextView txt_header = null;
	public RelativeLayout header = null;
	

	/**
	 * ����ͷ��Ԫ��
	 * @param title�����������
	 * @param headerImageResid��ͷ������ͼƬ����Դid��Ĭ��Ϊ0
	 * @param leftText����ఴť����
	 * @param leftImageResid����ఴť����ͼ��Դid��Ĭ��Ϊ0
	 * @param leftClick����ఴť����¼���û�¼�ʱΪnull��ͬʱ����
	 * @param rightText���Ҳఴť����
	 * @param rightImageResid����ఴť����ͼ��Դid��Ĭ��Ϊ0
	 * @param rightClick���Ҳఴť����¼���û�¼�ʱΪnull��ͬʱ����
	 */
	public Header(Context appContext, String title, int headerImageResid, 
			String leftText, int leftImageResid, View.OnClickListener leftClick, 
			String rightText, int rightImageResid, View.OnClickListener rightClick){
		init(appContext, title, headerImageResid, leftText, leftImageResid, leftClick, rightText, rightImageResid, rightClick);
	}
	/**
	 * ����ͷ��Ԫ��
	 * @param title�����������
	 * @param leftText����ఴť����
	 * @param leftClick����ఴť����¼���û�¼�ʱΪnull��ͬʱ����
	 * @param rightText���Ҳఴť����
	 * @param rightClick���Ҳఴť����¼���û�¼�ʱΪnull��ͬʱ����
	 */
	public Header(Context appContext, String title, 
			String leftText, View.OnClickListener leftClick, 
			String rightText, View.OnClickListener rightClick){
		init(appContext, title, 0, leftText, 0, leftClick, rightText, 0, rightClick);
	}
	/**
	 * ����ͷ��Ԫ��
	 * @param title�����������
	 */
	public Header(Context appContext, String title){
		init(appContext, title, 0, "", 0, null, "", 0, null);
	}
	
	
	/**
	 * ����ͷ��Ԫ��
	 * @param title�����������
	 * @param headerImageResid��ͷ������ͼƬ����Դid��Ĭ��Ϊ0
	 * 
	 * @param leftText����ఴť����
	 * @param leftImageResid����ఴť����ͼ��Դid��Ĭ��Ϊ0
	 * @param leftClick����ఴť����¼���û�¼�ʱΪnull��ͬʱ����
	 * 
	 * @param rightText���Ҳఴť����
	 * @param rightImageResid����ఴť����ͼ��Դid��Ĭ��Ϊ0
	 * @param rightClick���Ҳఴť����¼���û�¼�ʱΪnull��ͬʱ����
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
	
	/**���ñ���*/
	public void setTitle(String title){
		txt_header.setText(title);
	}
	/**���ü�ͷ��ť�����*/
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
	/**���ü�ͷ��ť���Ҳ�*/
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
	
	
	/**��λdipתΪpx*/
	public int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
	}
	/**��λpxתΪdip*/
	public int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
}