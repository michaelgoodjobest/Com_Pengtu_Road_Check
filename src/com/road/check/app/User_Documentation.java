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
		
		new Header(this,"使用说明",
				"返回",new HeaderLeftClickEvent(),
				"",null);
		
		url = cApp.ImgUrl_pr + "/user_documentation.html";
		
		if(!as.relistic.internet.NetWork.haveNetWorkd(User_Documentation.this)){
			noNetWorkMessage();
			return;
		}
		
		/*浏览器相关*/
  		webview = (WebView)this.findViewById(R.id.documentationview);
  		webview.getSettings().setJavaScriptEnabled(true);
  		webview.setBackgroundColor(0);
  		webview.setWebViewClient(new WebViewClient() {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            // 当开启新的页面的时候用webview来进行处理而不是用系统自带的浏览器处理
            view.loadUrl(url);                                        
            return true;
            }
  		});
  		
  		webview.setWebChromeClient(new WebChromeClient() {   
            public void onProgressChanged(WebView view, int progress) {   
              //Activity和Webview根据加载程度决定进度条的进度大小   
             //当加载到100%的时候 进度条自动消失   
            	//bar.setVisibility(view.VISIBLE);
            	if (isOpen == false)
            	{
            	dialog = ProgressDialog.show 
	    	      ( User_Documentation.this, 
	    	         "加载数据", 
	    	          "正在加载数据,请稍候", 
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
    	
  		/*浏览器相关END*/
	  
	}
	/**无网络提示*/
	public void noNetWorkMessage(){
		AlertDialog.Builder ad = new AlertDialog.Builder(User_Documentation.this);
		ad.setTitle("无网络提示");
		ad.setMessage("当前网络不可用，请监测网络是否已连接！");
		ad.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				networkdialog.dismiss();
			}
		});
		networkdialog = ad.create();
		networkdialog.show();
	}
	/*左侧返回事件*/
	public class HeaderLeftClickEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			User_Documentation.this.finish();
		}
	}
}
