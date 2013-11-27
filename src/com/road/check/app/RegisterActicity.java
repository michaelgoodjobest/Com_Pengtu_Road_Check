package com.road.check.app;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.road.check.R;
import com.road.check.common.Header;
import com.road.check.login.Login;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

public class RegisterActicity extends Activity {
	private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟

	private EditText edt_priclepal;
	private EditText edt_member;
	private EditText edt_phoneNumber;
	private EditText edt_area;
	private EditText edt_remark;
	private Button btn_register;
	private ProgressDialog progressDialog = null;
	public static final String GET_RESPONSE_TEXT_ERROR = "GET_RESPONSE_TEXT_ERROR";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		edt_priclepal = (EditText) findViewById(R.id.edt_priclepal);
		edt_member = (EditText) findViewById(R.id.edt_member);
		edt_phoneNumber = (EditText) findViewById(R.id.edt_phoneNumber);
		edt_area = (EditText) findViewById(R.id.edt_duty_area);
		edt_remark = (EditText) findViewById(R.id.edt_remark);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (haveNetWorkd(RegisterActicity.this)) {
					RegisterTask regTask = new RegisterTask();
					regTask.execute();
				}else {
					Toast.makeText(RegisterActicity.this, "网络未打开，请打开后再试",1).show();
				}
			}
		});
	}

	

	class RegisterTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			if (result.equals("0")) {
				Toast.makeText(RegisterActicity.this, "负责人不能为空", 1).show();
			}else if (result.equals("1")) {
				Toast.makeText(RegisterActicity.this, "组员不能为空", 1).show();
			}
			else if (result.equals("2")) {
				Toast.makeText(RegisterActicity.this, "手机号码不能为空", 1).show();
			}
			else if (result.equals("{'success':true}")) {
				Toast.makeText(RegisterActicity.this, "注册成功", 0).show();
				RegisterActicity.this.finish();
			}
			else if (result.equals("{'success':false}")) {
				Toast.makeText(RegisterActicity.this, "手机已登记过！，请不要重复登记", 1).show();
				RegisterActicity.this.finish();
			}
			else if(result.equals("GET_RESPONSE_TEXT_ERROR")) {
				Toast.makeText(RegisterActicity.this, "网络错误，请确保网络正常后重试！", 1).show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String priclepal = edt_priclepal.getText().toString();
			String member = edt_member.getText().toString();
			String phoneNumber = edt_phoneNumber.getText().toString();

			if (priclepal == null || priclepal.equals("")) {
				
				return "0";
			}
			if (member == null ||member.equals("")) {
				
				return "1";
			}
			if (phoneNumber == null || phoneNumber.equals("")) {
				
				return "2";
			}
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("entity.phoneNo",
					GetIMEI(RegisterActicity.this)));
			param.add(new BasicNameValuePair("entity.teamMember", member));
			param.add(new BasicNameValuePair("entity.principal", priclepal));
			param.add(new BasicNameValuePair("entity.mobileNo", phoneNumber));
			if (edt_area.getText() != null&& !edt_area.equals("")) {
				String area = edt_area.getText().toString();
				param.add(new BasicNameValuePair("entity.dutyArea", area));
			}
			if (edt_remark.getText() != null&& !edt_remark.equals("")) {
				String remark = edt_remark.getText().toString();
				param.add(new BasicNameValuePair("entity.remark", remark));
			}
			String result = getResultbyPost("http://192.168.0.137:9090/szcj/phone/registerphone!save.action",
					param);
			return result;
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(RegisterActicity.this, "请稍候",
					"正在上传数据...", true);
		}
	}

	public static String GetIMEI(Context context) {
		try {

			TelephonyManager telephonemanage = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			return telephonemanage.getDeviceId();

		} catch (Exception ex) {
		}

		return "";
	}

	public static String getResultbyPost(String url,
			List<NameValuePair> dataParam) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpPost post = new HttpPost(url);
		String result = "";
		try {
			post.setEntity(new UrlEncodedFormEntity(dataParam, "utf-8"));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = GET_RESPONSE_TEXT_ERROR;
		}

		return result;

	}
	public static boolean haveNetWorkd(Context context){
		Activity netactivity = (Activity)context;	
		ConnectivityManager connManager = (ConnectivityManager)netactivity.getSystemService(context.CONNECTIVITY_SERVICE);  
		// 获取代表联网状态的NetworkInfo对象   
		NetworkInfo NetworkInfo = connManager.getActiveNetworkInfo();  
		// 获取当前的网络连接是否可用  
		if (NetworkInfo != null && NetworkInfo.isAvailable() && NetworkInfo.isConnected()){
		Log.w("网络检测", "有网络");
			return true;
		}
		Log.w("网络检测", "无网络");
		return false;
	}

}
