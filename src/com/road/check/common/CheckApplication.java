package com.road.check.common;

import android.app.Application;


public class CheckApplication extends Application {
	/**�汾��*/
	public static int versionCode = 0;
	/**�汾�ţ����û���ʾ��*/
	public static String versionName = "v1.0.1";
	public static String IMEI;
	public String DownLoadVer_Url = "";//Ӧ�����ص�ַ
	public String ApiUrl_Pr = "";//Api��ַǰ׺
	public String ImgUrl_pr = "";//ͼƬ��ַǰ׺
	public static float  speed;//Ѳ��ʱ�ٶ�
	public int locType;//��λ���������Ϣ
	public static String lat = "";//λ������
	public static String lng = "";//λ������
	public static int ViewPosition = 0;//0��·�����գ�1��������д
	public static String RoundId = "";
	public static int Direction = -1;
}
