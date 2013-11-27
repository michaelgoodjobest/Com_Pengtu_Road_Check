package com.road.check.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.road.check.R;
import com.road.check.app.RegisterActicity;
import com.road.check.base.ActivityBase;
import com.road.check.model.User;
import com.road.check.sqlite.DatabaseService;

public class Login extends ActivityBase{
	private EditText edt_username;
	private EditText edt_pwd;
	private Button btn_sure;
	private Button btn_register;
	private LoginTask loginTask;
	private AlertDialog message_dialog;
	private CheckBox checkbox_psw;
	
	private static final String PREFS_NAME = "userinfo";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			String status=Environment.getExternalStorageState(); 
			if(!status.equals(Environment.MEDIA_MOUNTED)){
				AlertDialog.Builder ab = new AlertDialog.Builder(this);
				ab.setTitle("温馨提示");
				ab.setMessage("无SD卡，请安装SD卡！");
				ab.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						message_dialog.dismiss();
						Login.this.finish();
					}
				});
				message_dialog = ab.create();
				message_dialog.show();
				return;
			} 
		}catch(Exception ex){
		}

		setContentView(R.layout.login);
		
		edt_username = (EditText)this.findViewById(R.id.edt_username);
		edt_pwd = (EditText)this.findViewById(R.id.edt_pwd);
		btn_sure = (Button)this.findViewById(R.id.btn_sure);
		checkbox_psw = (CheckBox)this.findViewById(R.id.checkbox_psw);
		btn_register = (Button) findViewById(R.id.btn_register);
		ImageView imgview = (ImageView) findViewById(R.id.iv_templogin);
		
//		loadUserDate();
		
		//登录
		btn_sure.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loginTask = new LoginTask();
				loginTask.execute();
			}
		});
		imgview.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loginTask = new LoginTask();
				loginTask.execute();
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Login.this, RegisterActicity.class);
				Login.this.startActivity(intent);
			}
		});
	}
	/**保存用户信息*/
//	private void saveUserDate() {
//		// 载入配置文件
//		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
//		if(edt_username.getText().toString().equals("")){
//			return;
//		}
//		
//		// 写入配置文件
//		Editor spEd = sp.edit();
//		spEd.putBoolean("isSave", true);
//		spEd.putString("name", edt_username.getText().toString());
//		if (checkbox_psw.isChecked()) {
//			spEd.putString("psw", edt_pwd.getText().toString());
//		} else {
//			spEd.putString("psw", "");
//		}
//		spEd.commit();
//	}
	/**
	 * 载入已记住的用户信息
	 */
//	private void loadUserDate() {
//		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
//
//		if (sp.getBoolean("isSave", false)) {
//			String username = sp.getString("name", "");
//			String psw = sp.getString("psw", "");
//			if(!"".equals(username)){
//				edt_username.setText(username);
//			}
//			if (!"".equals(psw)) {
//				edt_pwd.setText(psw);
//				checkbox_psw.setChecked(true);
//			}
//		}
//	}
	/**************************登录任务***********************/
	class LoginTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
//			List<NameValuePair> param = new ArrayList<NameValuePair>();
//			param.add(new BasicNameValuePair("username",edt_username.getText().toString() ));
//			param.add(new BasicNameValuePair("pwd",edt_pwd.getText().toString() ));
//			String result = as.relistic.internet.HttpHelper.GetResponseTextByPost("",param,"sessionId");
			String IMEI = GetIMEI(Login.this);
			if (IMEI!=null&&!IMEI.equals("")) {

				cApp.IMEI = IMEI;

				return "0";
			}else {
				return "";
			}

		}
		@Override
		protected void onPostExecute(String result) {
			if(result.equals("0")){
//				saveUserDate();
				Intent intent = new Intent(Login.this,com.road.check.navigation.Navigation.class);
				Login.this.startActivity(intent);
				Login.this.finish();
			}else{
				Toast.makeText(Login.this, "用户验证失败！", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/**************************登录任务 END***********************/
	public static String GetIMEI(Context context)
	{
		try
		{

	    	  TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);              

	    	   return telephonemanage.getDeviceId();
			
		}
		catch(Exception ex)
		{
		}

		return "";
	}
}
