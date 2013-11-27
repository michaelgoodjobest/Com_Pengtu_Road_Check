package com.road.check.app;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class User_Documentation extends ActivityBase {
	private WebView webview;
	final Activity context = this;  
	private ProgressDialog dialog = null; 
	private boolean isOpen = false;
	private String url = "";
	private AlertDialog networkdialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_user_documentation);
		
		new Header(this,"ʹ��˵��",
				"����",new HeaderLeftClickEvent(),
				"",null);
		
		url = cApp.ImgUrl_pr + "/user_documentation.html";
		
		if(!as.relistic.internet.NetWork.haveNetWorkd(User_Documentation.this)){
			noNetWorkMessage();
			return;
		}
		
		/*��������*/
  		webview = (WebView)this.findViewById(R.id.documentationview);
  		webview.getSettings().setJavaScriptEnabled(true);
  		webview.setBackgroundColor(0);
  		webview.setWebViewClient(new WebViewClient() {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            // �������µ�ҳ���ʱ����webview�����д����������ϵͳ�Դ������������
            view.loadUrl(url);                                        
            return true;
            }
  		});
  		
  		webview.setWebChromeClient(new WebChromeClient() {   
            public void onProgressChanged(WebView view, int progress) {   
              //Activity��Webview���ݼ��س̶Ⱦ����������Ľ��ȴ�С   
             //�����ص�100%��ʱ�� �������Զ���ʧ   
            	//bar.setVisibility(view.VISIBLE);
            	if (isOpen == false)
            	{
            	dialog = ProgressDialog.show 
	    	      ( User_Documentation.this, 
	    	         "��������", 
	    	          "���ڼ�������,���Ժ�", 
	    	          true ); 
            	isOpen = true;
            	}
              context.setProgress(progress * 100);  
              if( progress == 100)  
              {
            	  dialog.dismiss();
            	  isOpen = false;
              }
            }   
  		});   

	
	  	webview.loadUrl(url);
	  	webview.requestFocusFromTouch();
    	
  		/*��������END*/
	  
	}
	/**��������ʾ*/
	public void noNetWorkMessage(){
		AlertDialog.Builder ad = new AlertDialog.Builder(User_Documentation.this);
		ad.setTitle("��������ʾ");
		ad.setMessage("��ǰ���粻���ã����������Ƿ������ӣ�");
		ad.setPositiveButton("֪����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				networkdialog.dismiss();
			}
		});
		networkdialog = ad.create();
		networkdialog.show();
	}
	/*��෵���¼�*/
	public class HeaderLeftClickEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			User_Documentation.this.finish();
		}
	}
}
