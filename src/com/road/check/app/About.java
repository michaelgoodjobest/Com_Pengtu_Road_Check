package com.road.check.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;

public class About extends ActivityBase{
	private Header header;
	
	private TextView txt_vercode;
	private Button btn_mobile;
	private Button btn_explain;
	private TextView txt_devicecode;
	
	private String server_tel_num = "0755-82790964";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_about);
		
		//ͷ������
		header = new Header(this,"�������",
				"����",new HeaderLeftOnClickListener(),
				"",null);
		
		txt_vercode = (TextView)this.findViewById(R.id.txt_vercode);
		btn_mobile = (Button)this.findViewById(R.id.btn_mobile);
		btn_explain = (Button)this.findViewById(R.id.btn_explain);
		txt_devicecode = (TextView) this.findViewById(R.id.txt_device_code);
		
		txt_devicecode.setText(cApp.IMEI);
		txt_vercode.setText(cApp.versionName);
		btn_mobile.setText(server_tel_num);
		btn_explain.setText("����鿴ʹ��˵��");
		
		//����
		btn_mobile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.CALL",
						Uri.parse("tel:" + server_tel_num));
				startActivity(intent);
			}
		});
		//ʹ��˵��
		btn_explain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(About.this,User_Documentation.class);
				startActivity(intent);
			}
		});
	}
	/**ͷ����߰�ť����¼�*/
	class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			About.this.finish();
		}
	}
}
