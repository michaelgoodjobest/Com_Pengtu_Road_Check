package com.road.check.common;

import android.app.Application;


public class CheckApplication extends Application {
	/**版本号*/
	public static int versionCode = 0;
	/**版本号（对用户显示）*/
	public static String versionName = "v1.0.1";
	public static String IMEI;
	public String DownLoadVer_Url = "";//应用下载地址
	public String ApiUrl_Pr = "";//Api地址前缀
	public String ImgUrl_pr = "";//图片地址前缀
	public static float  speed;//巡检时速度
	public int locType;//定位种类相关信息
	public static String lat = "";//位置坐标
	public static String lng = "";//位置坐标
	public static int ViewPosition = 0;//0：路点拍照，1：报表填写
	public static String RoundId = "";
	public static int Direction = -1;
}
