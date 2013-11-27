package com.road.check.common;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.road.check.R;

public class KeyBoard {
	private TextView txt_keyboard_title;
	private TextView txt_num_show;
	private LinearLayout layout_keyboard_close;
	private Button btn_keyboard_num_0;
	private Button btn_keyboard_num_1;
	private Button btn_keyboard_num_2;
	private Button btn_keyboard_num_3;
	private Button btn_keyboard_num_4;
	private Button btn_keyboard_num_5;
	private Button btn_keyboard_num_6;
	private Button btn_keyboard_num_7;
	private Button btn_keyboard_num_8;
	private Button btn_keyboard_num_9;
	private Button btn_keyboard_num_sure;
	private Button btn_keyboard_num_update;
	
	private String numStr = "";
	public KeyBoard(View view,String title,OnClickListener ColseOnClickListener,OnClickListener SureOnClickListener){
		initView(view,title,ColseOnClickListener,SureOnClickListener);
	}
	public void initView(View view,String title,OnClickListener ColseOnClickListener,OnClickListener SureOnClickListener){
		txt_keyboard_title = (TextView)view.findViewById(R.id.txt_keyboard_title);
		txt_num_show = (TextView)view.findViewById(R.id.txt_num_show);
		layout_keyboard_close = (LinearLayout)view.findViewById(R.id.layout_keyboard_close);
		btn_keyboard_num_0 = (Button)view.findViewById(R.id.btn_keyboard_num_0);
		btn_keyboard_num_1 = (Button)view.findViewById(R.id.btn_keyboard_num_1);
		btn_keyboard_num_2 = (Button)view.findViewById(R.id.btn_keyboard_num_2);
		btn_keyboard_num_3 = (Button)view.findViewById(R.id.btn_keyboard_num_3);
		btn_keyboard_num_4 = (Button)view.findViewById(R.id.btn_keyboard_num_4);
		btn_keyboard_num_5 = (Button)view.findViewById(R.id.btn_keyboard_num_5);
		btn_keyboard_num_6 = (Button)view.findViewById(R.id.btn_keyboard_num_6);
		btn_keyboard_num_7 = (Button)view.findViewById(R.id.btn_keyboard_num_7);
		btn_keyboard_num_8 = (Button)view.findViewById(R.id.btn_keyboard_num_8);
		btn_keyboard_num_9 = (Button)view.findViewById(R.id.btn_keyboard_num_9);
		btn_keyboard_num_sure = (Button)view.findViewById(R.id.btn_keyboard_num_sure);
		btn_keyboard_num_update = (Button)view.findViewById(R.id.btn_keyboard_num_update);
		
		
		txt_keyboard_title.setText(title);
		layout_keyboard_close.setOnClickListener(ColseOnClickListener);
		btn_keyboard_num_sure.setOnClickListener(SureOnClickListener);
		
		btn_keyboard_num_0.setOnClickListener(new NumberButtonOnClickListener("0"));
		btn_keyboard_num_1.setOnClickListener(new NumberButtonOnClickListener("1"));
		btn_keyboard_num_2.setOnClickListener(new NumberButtonOnClickListener("2"));
		btn_keyboard_num_3.setOnClickListener(new NumberButtonOnClickListener("3"));
		btn_keyboard_num_4.setOnClickListener(new NumberButtonOnClickListener("4"));
		btn_keyboard_num_5.setOnClickListener(new NumberButtonOnClickListener("5"));
		btn_keyboard_num_6.setOnClickListener(new NumberButtonOnClickListener("6"));
		btn_keyboard_num_7.setOnClickListener(new NumberButtonOnClickListener("7"));
		btn_keyboard_num_8.setOnClickListener(new NumberButtonOnClickListener("8"));
		btn_keyboard_num_9.setOnClickListener(new NumberButtonOnClickListener("9"));
		
		//更改
		btn_keyboard_num_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numStr = "";
				txt_num_show.setText(numStr);
			}
		});
	}
	/**数字按钮点击事件*/
	class NumberButtonOnClickListener implements OnClickListener{
		String num = "";
		public NumberButtonOnClickListener(String num){
			this.num = num;
		}
		@Override
		public void onClick(View v) {
			numStr += num;
			txt_num_show.setText(numStr);
		}
	}
	/**获取序号*/
	public String getNum(){
		return numStr;
	}
}
