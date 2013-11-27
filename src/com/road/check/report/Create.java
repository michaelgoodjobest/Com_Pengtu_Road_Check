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
	//页签部分
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
		btn_road_selecte = (Button)this.findViewById(R.id.btn_road_selecte);//返回道路选择
		btn_sava = (Button)this.findViewById(R.id.btn_sava);//保存
		txt_title = (TextView)this.findViewById(R.id.txt_title);//标题
		txt_page_message = (TextView)this.findViewById(R.id.txt_page_message);//页签提示
		
		dbs = new DatabaseService(this);
		
		//判断上下行
    	if(cApp.Direction == 1){
    		directionStr = "下行";
    	}else if(cApp.Direction == 0){
    		directionStr = "上行";
    	}
		
		//获取当前道/桥对象
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);
		if (now_round_item.divide.equals("0")) {
			initPageView();
			txt_page_message.setText("1/5");
			txt_title.setText("路基与排水设施养护状况检查记录表 道路标准");
		}
		else {
			pageIndex = 1;
			txt_page_message.setText("1/2");
			txt_title.setText("路基技术状况调查表 公路标准");
			initHighWayPageView();
		}
		//页签初始化

		//返回
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
		//保存
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
		

		//路基技术状况调查表
		View report_bussation_facility_mar_page_bed = mInflater.inflate(R.layout.report_create_page_bed, null);

		//沿线设施损坏状况调查表
		View report_bussation_facility_mar_page_road_alongline = mInflater.inflate(R.layout.report_create_page_road_alongline, null);

		//视图初始化

		initPageBed(report_bussation_facility_mar_page_bed);

		initPageAlongLine(report_bussation_facility_mar_page_road_alongline);
		
		HpageListViews.add(report_bussation_facility_mar_page_bed);
		HpageListViews.add(report_bussation_facility_mar_page_road_alongline);
		
		pageAdapter = new PageAdapter(HpageListViews);
		mPager = (ViewPager)Create.this.findViewById(R.id.vPager);
		mPager.setAdapter(pageAdapter);
		mPager.setOnPageChangeListener(new MyHOnPageChangeListener());
	}
	/**当有数据尚未保存时的弹出框*/
	public void initNoDatasDialog(){
		//nosava_dialog
		AlertDialog.Builder ab = new AlertDialog.Builder(Create.this);
		ab.setTitle("温馨提示");
		ab.setMessage("您当前尚有一些报表数据尚未保存，是否保存？");
		ab.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				allDatasSavaTask = new AllDatasSavaTask();
				allDatasSavaTask.execute();
				nosava_dialog.dismiss();
			}
		});
		ab.setNegativeButton("返回", new DialogInterface.OnClickListener() {
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
		int maxWidth=800,maxHeight=600;  //设置新图片的大小  
	    BitmapFactory.Options options = new BitmapFactory.Options();    
	    options.inJustDecodeBounds = true; 
	    Bitmap image = BitmapFactory.decodeFile(imgPath, options);    
	    double ratio = 1D;    
	    if (maxWidth > 0 && maxHeight <= 0) {    
	        // 限定宽度，高度不做限制    
	        ratio = Math.ceil(options.outWidth / maxWidth);    
	    } else if (maxHeight > 0 && maxWidth <= 0) {    
	        // 限定高度，不限制宽度    
	        ratio = Math.ceil(options.outHeight / maxHeight);    
	    } else if (maxWidth > 0 && maxHeight > 0) {    
	        // 高度和宽度都做了限制，这时候我们计算在这个限制内能容纳的最大的图片尺寸，不会使图片变形    
	        double _widthRatio = Math.ceil(options.outWidth / maxWidth);    
	        double _heightRatio = (double) Math.ceil(options.outHeight / maxHeight);    
	        ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;    
	    }    
	    if (ratio > 1){ 
	        options.inSampleSize = (int) ratio;  
	    	options.inJustDecodeBounds = false;    
	    	options.inPreferredConfig = Bitmap.Config.RGB_565;
	    	image = BitmapFactory.decodeFile(imgPath, options);  
	    	//保存入sdCard  
	    	File file = new File(imgPath);
	    	//获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转 
	    	int degree = ImageSpin.readPictureDegree(file.getAbsolutePath());
	    	//旋转图片
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
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 1:
				bed.imgPathList.add(imgPath);
				bed.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 2:
				dewatering.imgPathList.add(imgPath);
				dewatering.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 3:
				alongLine.imgPathList.add(imgPath);
				alongLine.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 4:
				footwalk.imgPathList.add(imgPath);
				footwalk.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
				;
				break;
			case 6:
				checkOther.imgPathList.add(imgPath);
				checkOther.imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);
				
				markname =  now_round_item.name + ""+directionStr
				+ "报表" + "_"+ disease+ "_"+ year + "_" + month + "_" + day 
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
		System.out.print("图片宽度"+w+"图片长度"+h+"");
		Bitmap newb = Bitmap.createBitmap(w,h,Config.RGB_565);
		Canvas cvCanvas = new Canvas(newb);
		Paint paint = new Paint();
		paint.setDither(true);
		paint.setAntiAlias(true);
		Rect srct = new Rect(0,0,w,h);
		Rect dest = new Rect(0,0,w,h);
		cvCanvas.drawBitmap(src, srct, dest, paint);
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔  
	    textPaint.setTextSize(20.0f);//字体大小  
	    textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度  
	    textPaint.setColor(Color.RED);
	    textPaint.setAntiAlias(true);
	    textPaint.setDither(true);//采用的颜色  
	    //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置  
	    cvCanvas.drawText(markname, 85, 25, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制 
		cvCanvas.save(Canvas.ALL_SAVE_FLAG);
		cvCanvas.restore();
		return newb;
	}
	/***************************************页签部分*************************************/
	 /**页签初始化*/
    private void initPageView() {
		mInflater = getLayoutInflater();
		pageListViews = new ArrayList<View>();
		
		
//		//路基技术状况调查表
//		View report_bussation_facility_mar_page_bed = mInflater.inflate(R.layout.report_create_page_bed, null);
		//路基与排水设施养护状况检查记录表
		View report_bussation_facility_mar_page_dewatering = mInflater.inflate(R.layout.report_create_page_dewatering, null);
		//其他设施养护状况检查记录表
		View report_bussation_facility_mar_page_orther = mInflater.inflate(R.layout.report_create_page_orther, null);
//		//沿线设施损坏状况调查表
//		View report_bussation_facility_mar_page_road_alongline = mInflater.inflate(R.layout.report_create_page_road_alongline, null);
		//人行道养护状况检查记录表
		View report_bussation_facility_mar_page_footwalk = mInflater.inflate(R.layout.report_create_page_footwalk, null);
		//平整度检测原始记录
		View report_bussation_facility_mar_page_evenness = mInflater.inflate(R.layout.report_create_page_evenness, null);
		//路面病害调查检查记录表
		View report_bussation_facility_mar_page_roadbed = mInflater.inflate(R.layout.report_create_page_roadbed,null);
		//视图初始化
		
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
    /**路面病害调查检查记录表*/
    public void initPageRoadbed(View view){
    	roadbed = new Roadbed(view, Create.this,dbs);
    	roadbed.init(now_round_item,directionStr);
    }
    /**路基技术状况调查表*/
    public void initPageBed(View view){
    	bed = new Bed(view, Create.this,dbs);
    	bed.init(now_round_item,directionStr);
    }
    /**路基与排水设施养护状况检查记录表*/
    public void initPageDewatering(View view){
    	dewatering = new Dewatering(view,Create.this,dbs);
    	dewatering.init(now_round_item, directionStr);
    }
    /**沿线设施损坏状况调查表*/
    public void initPageAlongLine(View view){
    	alongLine = new AlongLine(view,Create.this,dbs);
    	alongLine.init(now_round_item, directionStr);
    }
    /**人行道养护状况检查记录表*/
	public void initPageFootwalk(View view){
		footwalk = new Footwalk(view,Create.this,dbs);
		footwalk.init(now_round_item, directionStr);
	}
    /**平整度检测原始记录*/
	public void initPageEvenness(View view){
		evenness = new Evenness(view,Create.this,dbs);
		evenness.init(now_round_item, directionStr);
	}
    /**其他设施养护状况检查记录表*/
	public void initPageOrther(View view){
		checkOther = new CheckOther(view,Create.this,dbs);
		checkOther.init(now_round_item, directionStr);
	}
    /**页签切换监听*/
    public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:

				txt_title.setText("路基与排水设施养护状况检查记录表 道路标准");
				txt_page_message.setText("1/5");
				pageIndex = 2;
				dewatering.initImgList();
				
				
				break;

//			case 1:
//				
//					txt_title.setText("路基技术状况调查表 公路标准");
//				
//				
//				txt_page_message.setText("2/7");
//				pageIndex = 1;
//				bed.initSelectedDiseaseList();
//				break;
			case 1:
				txt_title.setText("其他设施养护状况检查记录表 道路标准");
				txt_page_message.setText("2/5");
				pageIndex = 6;
				break;
//			case 3:
//				txt_title.setText("沿线设施损坏状况调查表 公路标准");
//				txt_page_message.setText("4/7");
//				pageIndex = 3;
//				alongLine.initSelectedDiseaseList();
//				break;
			case 2:
				txt_title.setText("人行道养护状况检查记录表 道路标准");
				txt_page_message.setText("3/5");
				pageIndex = 4;
				footwalk.initSelectedDiseaseList();
				break;
			case 3:
				txt_title.setText("平整度检测原始记录 道路标准");
				txt_page_message.setText("4/5");
				pageIndex = 5;
				break;
			case 4:
				txt_title.setText("路面病害调查检查记录表 道路标准");
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
				
					txt_title.setText("路基技术状况调查表 公路标准");
				
				
				txt_page_message.setText("1/2");
				pageIndex = 1;
				bed.initSelectedDiseaseList();
				break;

			case 1:
				txt_title.setText("沿线设施损坏状况调查表 公路标准");
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
    /**切换页签按钮点击事件*/
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
	/***************************************页签部分 END*************************************/
    /**所有报表数据保存*/
    public class AllDatasSavaTask extends AsyncTask<String, String, String>{
    	private int savaResult = 0;
    	private int startpageindex = -1;
    	@Override
    	protected void onPreExecute() {
    	p_dialog = ProgressDialog.show( Create.this, 
  	          "保存提示", 
  	          "正在保存数据！", 
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
					publishProgress("正在保存路面病害调查检查记录表数据！");
					roadbed.savaDatas();
				}
				if(dewatering.getImgNum() > 0){
					publishProgress("正在保存路基与排水设施养护状况检查记录表数据！");
					dewatering.savaDatas();
				}
				if(footwalk.getImgNum() > 0){
					publishProgress("正在保存人行道养护状况检查记录表数据！");
					footwalk.savaDatas();
				}
				if(checkOther.getImgNum() > 0){
					publishProgress("正在保存其他设施养护状况检查记录表数据！");
					checkOther.savaDatas();
				}
				if(evenness.getDirectionHavaDatas() > 0){
					publishProgress("正在保存平整度检测原始记录表数据！");
					evenness.savaDatas();
				}
			}else {
				if(bed.getImgNum() > 0){
					publishProgress("正在保存路基技术状况调查表数据！");
					String bedSavaResult = bed.savaDatas();
					savaResult += Integer.parseInt(bedSavaResult);
					
				}
				
				if(alongLine.getImgNum() > 0){
					publishProgress("正在保存沿线设施损坏状况调查表数据！");
					String alongLineSavaResult = alongLine.savaDatas();
					savaResult += Integer.parseInt(alongLineSavaResult);
				}
			}
			
			
			return null;
		}
		@Override
		protected void onPostExecute(String result) {//as_adialog
			String savaresult = "保存成功！";
			String pBtnText = "知道了";
			switch(savaResult){
				case 1:
					savaresult = "《路基技术状况调查表》尚未选择“病害程度”，该表数据保存失败，请选择“病害程度”后再保存！";
					startpageindex = 0;
					pBtnText = "去修改";
					break;
				case 2:
					savaresult = "《沿线设施损坏状况调查表》尚未选择“病害程度”，该表数据保存失败，请选择“病害程度”后再保存！";
					startpageindex = 1;
					pBtnText = "去修改";
					break;
				case 3:
					savaresult = "《路基技术状况调查表》 与 《沿线设施损坏状况调查表》尚未选择“病害程度”，相关表数据保存失败，请选择“病害程度”后再保存！";
					startpageindex = 0;
					pBtnText = "去修改";
					break;
				case 4:
					savaresult="《路基技术状况表》未拍照，请先拍照提交数据后保存";
					startpageindex = 0;
					pBtnText = "去修改";
					break;
				case 50:
					savaresult="《沿线设施》未拍照，请先拍照提交数据后保存";
					startpageindex = 1;
					pBtnText = "去修改";
				case 51:
					savaresult="《沿线设施》未拍照，请先拍照提交数据后保存,《路基技术状况调查表》尚未选择“病害程度,请选择“病害程度”后再保存！";
					startpageindex = 1;
					pBtnText = "去修改";
				default:
					break;
			}
			AlertDialog.Builder allsava_ab = new AlertDialog.Builder(Create.this);
			allsava_ab.setTitle("温馨提示");
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
