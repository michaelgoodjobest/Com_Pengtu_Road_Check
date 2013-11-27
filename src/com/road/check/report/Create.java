package com.road.check.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.road.check.R;
import com.road.check.app.functionmodule.ImageSpin;
import com.road.check.base.ActivityBase;
import com.road.check.model.Round;
import com.road.check.navigation.PageAdapter;
import com.road.check.report.module.AlongLine;
import com.road.check.report.module.Bed;
import com.road.check.report.module.CheckOther;
import com.road.check.report.module.Dewatering;
import com.road.check.report.module.Evenness;
import com.road.check.report.module.Footwalk;
import com.road.check.report.module.Roadbed;
import com.road.check.sqlite.DatabaseService;

public class Create extends ActivityBase{

	private Button btn_road_selecte;
	private TextView txt_title;
	private Button btn_sava;
	private TextView txt_page_message;
	//ҳǩ����
	private ViewPager mPager;
	private LayoutInflater mInflater;
	private List<View> pageListViews;
	private List<View>HpageListViews;
	private PageAdapter pageAdapter;
	
	private DatabaseService dbs;
	private String directionStr = "";
	private AlertDialog nosava_dialog;
	private AllDatasSavaTask allDatasSavaTask;
	private ProgressDialog p_dialog;
	private AlertDialog as_adialog;
	
	private int pageIndex = 2;
	private Round now_round_item;
	
	protected static final int ROADBED_TAKEPHOTO = 1;
	protected static final int BED_TAKEPHOTO = 2;
	protected static final int DEWATERING_TAKEPHOTO = 3;
	protected static final int ALONGLINE_TAKEPHOTO = 4;
	protected static final int FOOTWALK_TAKEPHOTO = 5;
	protected static final int CHECKOTHER_TAKEPHOTO = 7;
	
	private Roadbed roadbed;
	private Bed bed;
	private Dewatering dewatering;
	private AlongLine alongLine;
	private Footwalk footwalk;
	private Evenness evenness;
	private CheckOther checkOther;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_create);
		btn_road_selecte = (Button)this.findViewById(R.id.btn_road_selecte);//���ص�·ѡ��
		btn_sava = (Button)this.findViewById(R.id.btn_sava);//����
		txt_title = (TextView)this.findViewById(R.id.txt_title);//����
		txt_page_message = (TextView)this.findViewById(R.id.txt_page_message);//ҳǩ��ʾ
		
		dbs = new DatabaseService(this);
		
		//�ж�������
    	if(cApp.Direction == 1){
    		directionStr = "����";
    	}else if(cApp.Direction == 0){
    		directionStr = "����";
    	}
		
		//��ȡ��ǰ��/�Ŷ���
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);
		if (now_round_item.divide.equals("0")) {
			initPageView();
			txt_page_message.setText("1/5");
			txt_title.setText("·������ˮ��ʩ����״������¼�� ��·��׼");
		}
		else {
			pageIndex = 1;
			txt_page_message.setText("1/2");
			txt_title.setText("·������״������� ��·��׼");
			initHighWayPageView();
		}
		//ҳǩ��ʼ��

		//����
		btn_road_selecte.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean havaNoSavaDatas = false;
				if (now_round_item.divide.equals("0")) {
					
					if(roadbed.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
					if(dewatering.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
					if(footwalk.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
					if(checkOther.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
					if(evenness.getDirectionHavaDatas() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
				}else {
					if(bed.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
					
					if(alongLine.getImgNum() > 0){
						havaNoSavaDatas = true;
						initNoDatasDialog();
						return;
					}
				}
				
				Create.this.finish();
			}
		});
		//����
		btn_sava.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(pageIndex){
					case 0:
						roadbed.savaDatas();
						break;
					case 1:
						bed.savaDatas();
						break;
					case 2:
						dewatering.savaDatas();
						break;
					case 3:
						alongLine.savaDatas();
						break;
					case 4:
						footwalk.savaDatas();
						break;
					case 5:
						evenness.savaDatas();
						break;
					case 6:
						checkOther.savaDatas();
						break;
					default:
						break;
				}
			}
		});
		
	}
	private void initHighWayPageView() {
		// TODO Auto-generated method stub
		mInflater = getLayoutInflater();
		HpageListViews = new ArrayList<View>();
		

		//·������״�������
		View report_bussation_facility_mar_page_bed = mInflater.inflate(R.layout.report_create_page_bed, null);

		//������ʩ��״�������
		View report_bussation_facility_mar_page_road_alongline = mInflater.inflate(R.layout.report_create_page_road_alongline, null);

		//��ͼ��ʼ��

		initPageBed(report_bussation_facility_mar_page_bed);

		initPageAlongLine(report_bussation_facility_mar_page_road_alongline);
		
		HpageListViews.add(report_bussation_facility_mar_page_bed);
		HpageListViews.add(report_bussation_facility_mar_page_road_alongline);
		
		pageAdapter = new PageAdapter(HpageListViews);
		mPager = (ViewPager)Create.this.findViewById(R.id.vPager);
		mPager.setAdapter(pageAdapter);
		mPager.setOnPageChangeListener(new MyHOnPageChangeListener());
	}
	/**����������δ����ʱ�ĵ�����*/
	public void initNoDatasDialog(){
		//nosava_dialog
		AlertDialog.Builder ab = new AlertDialog.Builder(Create.this);
		ab.setTitle("��ܰ��ʾ");
		ab.setMessage("����ǰ����һЩ����������δ���棬�Ƿ񱣴棿");
		ab.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				allDatasSavaTask = new AllDatasSavaTask();
				allDatasSavaTask.execute();
				nosava_dialog.dismiss();
			}
		});
		ab.setNegativeButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				nosava_dialog.dismiss();
				Create.this.finish();
			}
		});
		nosava_dialog = ab.create();
		nosava_dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ROADBED_TAKEPHOTO:
				if (resultCode == Activity.RESULT_OK) {
					String imgPath = roadbed.getImagPath(now_round_item.name);
					String disease = roadbed.getDiseaseContent(now_round_item.name);
					settingImag(imgPath,disease);
				}
				break;
			case BED_TAKEPHOTO:
				if (resultCode == Activity.RESULT_OK) {
					String imgPath = bed.getImagPath(now_round_item.name);
					String disease = bed.getDiseaseContent(now_round_item.name);
					settingImag(imgPath, disease);
				}
				break;
			case DEWATERING_TAKEPHOTO:
				if (resultCode == Activity.RESULT_OK) {
					String imgPath = dewatering.getImagPath(now_round_item.name);
					String disease = dewatering.getDiseaseContent(now_round_item.name);
					settingImag(imgPath, disease);
				}
				break;
			case ALONGLINE_TAKEPHOTO:
				if (resultCode == Activity.RESULT_OK) {
					String imgPath = alongLine.getImagPath(now_round_item.name);
					String disease = alongLine.getDiseaseContent(now_round_item.name);
					settingImag(imgPath,disease);
				}
				break;
			case FOOTWALK_TAKEPHOTO:
				if(resultCode == Activity.RESULT_OK){
					String imgPath = footwalk.getImagPath(now_round_item.name);
					String disease = footwalk.getDiseaseContent(now_round_item.name);
					settingImag(imgPath,disease);
				}
				break;
			case CHECKOTHER_TAKEPHOTO:
				if(resultCode == Activity.RESULT_OK){
					String imgPath = checkOther.getImagPath(now_round_item.name);
					String disease = checkOther.getDiseaseContent(now_round_item.name);
					settingImag(imgPath,disease);
				}
				break;
			default:
				break;
		}
	
	}
	
	private void settingImag(String imgPath, String disease){
		int maxWidth=800,maxHeight=600;  //������ͼƬ�Ĵ�С  
	    BitmapFactory.Options options = new BitmapFactory.Options();    
	    options.inJustDecodeBounds = true; 
	    Bitmap image = BitmapFactory.decodeFile(imgPath, options);    
	    double ratio = 1D;    
	    if (maxWidth > 0 && maxHeight <= 0) {    
	        // �޶���ȣ��߶Ȳ�������    
	        ratio = Math.ceil(options.outWidth / maxWidth);    
	    } else if (maxHeight > 0 && maxWidth <= 0) {    
	        // �޶��߶ȣ������ƿ��    
	        ratio = Math.ceil(options.outHeight / maxHeight);    
	    } else if (maxWidth > 0 && maxHeight > 0) {    
	        // �߶ȺͿ�ȶ��������ƣ���ʱ�����Ǽ�������������������ɵ�����ͼƬ�ߴ磬����ʹͼƬ����    
	        double _widthRatio = Math.ceil(options.outWidth / maxWidth);    
	        double _heightRatio = (double) Math.ceil(options.outHeight / maxHeight);    
	        ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;    
	    }    
	    if (ratio > 1){ 
	        options.inSampleSize = (int) ratio;  
	    	options.inJustDecodeBounds = false;    
	    	options.inPreferredConfig = Bitmap.Config.RGB_565;
	    	image = BitmapFactory.decodeFile(imgPath, options);  
	    	//������sdCard  
	    	File file = new File(imgPath);
	    	//��ȡͼƬ����ת�Ƕȣ���Щϵͳ�����յ�ͼƬ��ת�ˣ��е�û����ת 
	    	int degree = ImageSpin.readPictureDegree(file.getAbsolutePath());
	    	//��תͼƬ
	    	image = ImageSpin.rotaingImageView(degree,image);
	    	Time time = new Time("GMT-8");
			String markname = "";
			time.setToNow();
			int year = time.year;
			int month = time.month +1;
			int day = time.monthDay;
			
			
			switch (pageIndex ) {
			case 0:
				
					
				roadbed.imgPathList.add(imgPath);
				roadbed.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 1:
				bed.imgPathList.add(imgPath);
				bed.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 2:
				dewatering.imgPathList.add(imgPath);
				dewatering.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 3:
				alongLine.imgPathList.add(imgPath);
				alongLine.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 4:
				footwalk.imgPathList.add(imgPath);
				footwalk.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 6:
				checkOther.imgPathList.add(imgPath);
				checkOther.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "����" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			default:
				break;
			}
			image = createBitmap(image, markname);
	    	try {    
	    		FileOutputStream out = new FileOutputStream(file);    
	    		if(image.compress(Bitmap.CompressFormat.JPEG, 85, out)){ 
	               out.flush();    
	               out.close();    
	    		}  
	    	} catch (Exception e) {   
	    		e.printStackTrace();  
	    	}finally{  
	    	}
	    }
	}
	private Bitmap createBitmap(Bitmap src,String markname) {
		if (src==null) {
			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();
		System.out.print("ͼƬ���"+w+"ͼƬ����"+h+"");
		Bitmap newb = Bitmap.createBitmap(w,h,Config.RGB_565);
		Canvas cvCanvas = new Canvas(newb);
		Paint paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		Rect srct = new Rect(0,0,w,h);
		Rect dest = new Rect(0,0,w,h);
		cvCanvas.drawBitmap(src, srct, dest, paint);
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//���û���  
	    textPaint.setTextSize(20.0f);//�����С  
	    textPaint.setTypeface(Typeface.DEFAULT_BOLD);//����Ĭ�ϵĿ��  
	    textPaint.setColor(Color.RED);
	    textPaint.setAntiAlias(true);
	    textPaint.setDither(true);//���õ���ɫ  
	    //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//Ӱ��������  
	    cvCanvas.drawText(markname, 85, 25, textPaint);//������ȥ�֣���ʼδ֪x,y������ֻ�ʻ��� 
		cvCanvas.save(Canvas.ALL_SAVE_FLAG);
		cvCanvas.restore();
		return newb;
	}
	/***************************************ҳǩ����*************************************/
	 /**ҳǩ��ʼ��*/
    private void initPageView() {
		mInflater = getLayoutInflater();
		pageListViews = new ArrayList<View>();
		
		
//		//·������״�������
//		View report_bussation_facility_mar_page_bed = mInflater.inflate(R.layout.report_create_page_bed, null);
		//·������ˮ��ʩ����״������¼��
		View report_bussation_facility_mar_page_dewatering = mInflater.inflate(R.layout.report_create_page_dewatering, null);
		//������ʩ����״������¼��
		View report_bussation_facility_mar_page_orther = mInflater.inflate(R.layout.report_create_page_orther, null);
//		//������ʩ��״�������
//		View report_bussation_facility_mar_page_road_alongline = mInflater.inflate(R.layout.report_create_page_road_alongline, null);
		//���е�����״������¼��
		View report_bussation_facility_mar_page_footwalk = mInflater.inflate(R.layout.report_create_page_footwalk, null);
		//ƽ���ȼ��ԭʼ��¼
		View report_bussation_facility_mar_page_evenness = mInflater.inflate(R.layout.report_create_page_evenness, null);
		//·�没���������¼��
		View report_bussation_facility_mar_page_roadbed = mInflater.inflate(R.layout.report_create_page_roadbed,null);
		//��ͼ��ʼ��
		
//		initPageBed(report_bussation_facility_mar_page_bed);
		initPageDewatering(report_bussation_facility_mar_page_dewatering);
		initPageOrther(report_bussation_facility_mar_page_orther);
//		initPageAlongLine(report_bussation_facility_mar_page_road_alongline);
		initPageFootwalk(report_bussation_facility_mar_page_footwalk);
		initPageEvenness(report_bussation_facility_mar_page_evenness);

		initPageRoadbed(report_bussation_facility_mar_page_roadbed);
		
		
//		pageListViews.add(report_bussation_facility_mar_page_bed);
		pageListViews.add(report_bussation_facility_mar_page_dewatering);
		pageListViews.add(report_bussation_facility_mar_page_orther);
//		pageListViews.add(report_bussation_facility_mar_page_road_alongline);
		pageListViews.add(report_bussation_facility_mar_page_footwalk);
		pageListViews.add(report_bussation_facility_mar_page_evenness);

		pageListViews.add(report_bussation_facility_mar_page_roadbed);
		
		pageAdapter = new PageAdapter(pageListViews);
		mPager = (ViewPager)Create.this.findViewById(R.id.vPager);
		mPager.setAdapter(pageAdapter);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
    /**·�没���������¼��*/
    public void initPageRoadbed(View view){
    	roadbed = new Roadbed(view, Create.this,dbs);
    	roadbed.init(now_round_item,directionStr);
    }
    /**·������״�������*/
    public void initPageBed(View view){
    	bed = new Bed(view, Create.this,dbs);
    	bed.init(now_round_item,directionStr);
    }
    /**·������ˮ��ʩ����״������¼��*/
    public void initPageDewatering(View view){
    	dewatering = new Dewatering(view,Create.this,dbs);
    	dewatering.init(now_round_item, directionStr);
    }
    /**������ʩ��״�������*/
    public void initPageAlongLine(View view){
    	alongLine = new AlongLine(view,Create.this,dbs);
    	alongLine.init(now_round_item, directionStr);
    }
    /**���е�����״������¼��*/
	public void initPageFootwalk(View view){
		footwalk = new Footwalk(view,Create.this,dbs);
		footwalk.init(now_round_item, directionStr);
	}
    /**ƽ���ȼ��ԭʼ��¼*/
	public void initPageEvenness(View view){
		evenness = new Evenness(view,Create.this,dbs);
		evenness.init(now_round_item, directionStr);
	}
    /**������ʩ����״������¼��*/
	public void initPageOrther(View view){
		checkOther = new CheckOther(view,Create.this,dbs);
		checkOther.init(now_round_item, directionStr);
	}
    /**ҳǩ�л�����*/
    public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:

				txt_title.setText("·������ˮ��ʩ����״������¼�� ��·��׼");
				txt_page_message.setText("1/5");
				pageIndex = 2;
				dewatering.initImgList();
				
				
				break;

//			case 1:
//				
//					txt_title.setText("·������״������� ��·��׼");
//				
//				
//				txt_page_message.setText("2/7");
//				pageIndex = 1;
//				bed.initSelectedDiseaseList();
//				break;
			case 1:
				txt_title.setText("������ʩ����״������¼�� ��·��׼");
				txt_page_message.setText("2/5");
				pageIndex = 6;
				break;
//			case 3:
//				txt_title.setText("������ʩ��״������� ��·��׼");
//				txt_page_message.setText("4/7");
//				pageIndex = 3;
//				alongLine.initSelectedDiseaseList();
//				break;
			case 2:
				txt_title.setText("���е�����״������¼�� ��·��׼");
				txt_page_message.setText("3/5");
				pageIndex = 4;
				footwalk.initSelectedDiseaseList();
				break;
			case 3:
				txt_title.setText("ƽ���ȼ��ԭʼ��¼ ��·��׼");
				txt_page_message.setText("4/5");
				pageIndex = 5;
				break;
			case 4:
				txt_title.setText("·�没���������¼�� ��·��׼");
				txt_page_message.setText("5/5");
				pageIndex = 0;
				roadbed.initSelectedDiseaseList();
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
    public class MyHOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {

			case 0:
				
					txt_title.setText("·������״������� ��·��׼");
				
				
				txt_page_message.setText("1/2");
				pageIndex = 1;
				bed.initSelectedDiseaseList();
				break;

			case 1:
				txt_title.setText("������ʩ��״������� ��·��׼");
				txt_page_message.setText("2/2");
				pageIndex = 3;
				alongLine.initSelectedDiseaseList();
				break;
			
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
    /**�л�ҳǩ��ť����¼�*/
    private class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}
	/***************************************ҳǩ���� END*************************************/
    /**���б������ݱ���*/
    public class AllDatasSavaTask extends AsyncTask<String, String, String>{
    	private int savaResult = 0;
    	private int startpageindex = -1;
    	@Override
    	protected void onPreExecute() {
    	p_dialog = ProgressDialog.show( Create.this, 
  	          "������ʾ", 
  	          "���ڱ������ݣ�", 
  	          true ); 
    	}
    	@Override
    	protected void onProgressUpdate(String... values) {
    		p_dialog.setMessage(values[0]);
    	}
		@Override
		protected String doInBackground(String... params) {
			if (now_round_item.divide.equals("0")) {
				if(roadbed.getImgNum() > 0){
					publishProgress("���ڱ���·�没���������¼�����ݣ�");
					roadbed.savaDatas();
				}
				if(dewatering.getImgNum() > 0){
					publishProgress("���ڱ���·������ˮ��ʩ����״������¼�����ݣ�");
					dewatering.savaDatas();
				}
				if(footwalk.getImgNum() > 0){
					publishProgress("���ڱ������е�����״������¼�����ݣ�");
					footwalk.savaDatas();
				}
				if(checkOther.getImgNum() > 0){
					publishProgress("���ڱ���������ʩ����״������¼�����ݣ�");
					checkOther.savaDatas();
				}
				if(evenness.getDirectionHavaDatas() > 0){
					publishProgress("���ڱ���ƽ���ȼ��ԭʼ��¼�����ݣ�");
					evenness.savaDatas();
				}
			}else {
				if(bed.getImgNum() > 0){
					publishProgress("���ڱ���·������״����������ݣ�");
					String bedSavaResult = bed.savaDatas();
					savaResult += Integer.parseInt(bedSavaResult);
					
				}
				
				if(alongLine.getImgNum() > 0){
					publishProgress("���ڱ���������ʩ��״����������ݣ�");
					String alongLineSavaResult = alongLine.savaDatas();
					savaResult += Integer.parseInt(alongLineSavaResult);
				}
			}
			
			
			return null;
		}
		@Override
		protected void onPostExecute(String result) {//as_adialog
			String savaresult = "����ɹ���";
			String pBtnText = "֪����";
			switch(savaResult){
				case 1:
					savaresult = "��·������״���������δѡ�񡰲����̶ȡ����ñ����ݱ���ʧ�ܣ���ѡ�񡰲����̶ȡ����ٱ��棡";
					startpageindex = 0;
					pBtnText = "ȥ�޸�";
					break;
				case 2:
					savaresult = "��������ʩ��״���������δѡ�񡰲����̶ȡ����ñ����ݱ���ʧ�ܣ���ѡ�񡰲����̶ȡ����ٱ��棡";
					startpageindex = 1;
					pBtnText = "ȥ�޸�";
					break;
				case 3:
					savaresult = "��·������״������� �� ��������ʩ��״���������δѡ�񡰲����̶ȡ�����ر����ݱ���ʧ�ܣ���ѡ�񡰲����̶ȡ����ٱ��棡";
					startpageindex = 0;
					pBtnText = "ȥ�޸�";
					break;
				case 4:
					savaresult="��·������״����δ���գ����������ύ���ݺ󱣴�";
					startpageindex = 0;
					pBtnText = "ȥ�޸�";
					break;
				case 50:
					savaresult="��������ʩ��δ���գ����������ύ���ݺ󱣴�";
					startpageindex = 1;
					pBtnText = "ȥ�޸�";
				case 51:
					savaresult="��������ʩ��δ���գ����������ύ���ݺ󱣴�,��·������״���������δѡ�񡰲����̶�,��ѡ�񡰲����̶ȡ����ٱ��棡";
					startpageindex = 1;
					pBtnText = "ȥ�޸�";
				default:
					break;
			}
			AlertDialog.Builder allsava_ab = new AlertDialog.Builder(Create.this);
			allsava_ab.setTitle("��ܰ��ʾ");
			allsava_ab.setMessage(savaresult);
			allsava_ab.setPositiveButton(pBtnText, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(startpageindex){
						case -1:
							as_adialog.dismiss();
							Create.this.finish();
							break;
						case 0:
							mPager.setCurrentItem(startpageindex);
							as_adialog.dismiss();
							break;
						case 1:
							mPager.setCurrentItem(startpageindex);
							as_adialog.dismiss();
							break;
					}
				}
			});
			as_adialog = allsava_ab.create();
			as_adialog.show();
				
			p_dialog.dismiss();
		}
    }

}
