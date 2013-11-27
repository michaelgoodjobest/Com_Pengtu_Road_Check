package com.road.check.report.update;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.road.check.R;
import com.road.check.app.functionmodule.ImageSpin;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.common.ViewCache;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.sqlite.DatabaseService;

public class CheckOther extends ActivityBase{
	private Header header;
	//定义控件
	private TextView txt_round_name;
	private TextView txt_roundid;
	private TextView txt_startend_name;
	private TextView txt_direction;
	private TextView txt_time;
	private EditText edt_distance;
	private Button btn_error_photo;
	private EditText edt_remark;
	
	//所属结构
	private Spinner sp_structure;
	private List<String> structure_list;
	private ArrayAdapter<String> structure_adapter;
	
	//所属设施
	private Spinner sp_facility;
	private List<String> facility_list;
	private ArrayAdapter<String> facility_adapter;
	
	//照相集合
	private ListView error_photo_listview;
	private List<String> photo_url_list;
	private PhotoAdapter photoAdapter;
	private String imgfilename = "";
	protected static final int ROADBED_TAKEPHOTO = 7; // 拍照后
	
	//图片放大
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private AlertDialog deleteImg_dialog;
	
	private SavaDatasTask savaDatasTask;
	private DatabaseService dbs;
	private List<String> imgFilePathList;
	private String Id = "";
	private Road_Check_Data_Table now_item;
	private Round round_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_orther);
		try{
			Id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		
		//加载头部
		header = new Header(this,"其他设施养护状况检查记录表",
				"返回",new HeaderLeftButtonOnClickListener(),
				"保存",new HeaderrightButtonOnClickListener());
		
		//获取当前对象
		dbs = new DatabaseService(this);
		now_item = new Road_Check_Data_Table();
		now_item = dbs.road_check_table.getItem(Id);
		//获取道路对象
		round_item = new Round();
		round_item = dbs.round.getItemByRoundId(now_item.roundId);
		
		//控件初始化
		init();
	}
	/**头部左边按钮点击事件：返回*/
	public class HeaderLeftButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent  = new Intent(CheckOther.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			CheckOther.this.startActivity(intent);
			CheckOther.this.finish();
		}
	}
	/**头部右边按钮点击事件：保存*/
	public class HeaderrightButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
		}
	}
	/**初始化*/
	public void init(){
		txt_round_name = (TextView)CheckOther.this.findViewById(R.id.txt_round_name);
		txt_roundid = (TextView)CheckOther.this.findViewById(R.id.txt_roundid);
		txt_startend_name = (TextView)CheckOther.this.findViewById(R.id.txt_startend_name);
		txt_direction = (TextView)CheckOther.this.findViewById(R.id.txt_direction);
		txt_time = (TextView)CheckOther.this.findViewById(R.id.txt_time);
		edt_distance = (EditText)CheckOther.this.findViewById(R.id.edt_distance);
		sp_structure = (Spinner)CheckOther.this.findViewById(R.id.sp_structure);
		sp_facility = (Spinner)CheckOther.this.findViewById(R.id.sp_facility);
		btn_error_photo = (Button)CheckOther.this.findViewById(R.id.btn_error_photo);
		error_photo_listview = (ListView)CheckOther.this.findViewById(R.id.error_photo_listview);
		edt_remark = (EditText)CheckOther.this.findViewById(R.id.edt_remark);
		
		txt_round_name.setText(now_item.roundName);
		txt_roundid.setText(now_item.roundId);
		txt_startend_name.setText(round_item.start_location + " 至 " + round_item.end_location);
		txt_direction.setText(now_item.direction);
		txt_time.setText(now_item.deteTime);
		edt_distance.setText(now_item.longFromStartNum);
		edt_remark.setText(now_item.remark);
		
		//所属结构
		structure_list = new ArrayList<String>();
		structure_list.add("变型");
		structure_list.add("破损");
		structure_list.add("功能失效");
		structure_adapter = new ArrayAdapter<String>(CheckOther.this,android.R.layout.simple_spinner_item,structure_list);
		structure_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_structure.setAdapter(structure_adapter);
		for(int i = 0 ; i < structure_list.size(); i++){
			if(structure_list.get(i).equals(now_item.o_structure)){
				sp_structure.setSelection(i);
				break;
			}
		}
		
		//所属设施
		facility_list = new ArrayList<String>();
		facility_list.add("破损");
		facility_list.add("功能失效");
		facility_adapter = new ArrayAdapter<String>(CheckOther.this,android.R.layout.simple_spinner_item,facility_list);
		facility_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_facility.setAdapter(facility_adapter);
		for(int i = 0 ; i < facility_list.size(); i++){
			if(facility_list.get(i).equals(now_item.o_facility)){
				sp_facility.setSelection(i);
				break;
			}
		}
		
		//照相
    	photo_url_list = new ArrayList<String>();
    	btn_error_photo.setOnClickListener(new PhotoImgButtonOnClickListener(now_item.direction,now_item.roundName));
    	photoAdapter = new PhotoAdapter(CheckOther.this, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				LayoutInflater inflater = CheckOther.this.getLayoutInflater();
				View view = inflater.inflate(R.layout.app_roadpointphoto_bigimg, null);
				LinearLayout layout_close = (LinearLayout)view.findViewById(R.id.layout_close);
				Gallery gallery_bigimg = (Gallery)view.findViewById(R.id.gallery_bigimg);
				
				//关闭弹出框
				layout_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pw_bigimg.dismiss();
					}
				});
				
				rBigImgAdapter = new RBigImgAdapter(CheckOther.this, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(CheckOther.this);
						ab.setTitle("温馨提示");
						ab.setMessage("您确定要删除图片：" + photo_url_list.get(position) + "吗？");
						ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								File file = new File(path);
								if(file.exists()){
									file.delete();
								}
								if(imgFilePathList.size() == 1){
									imgFilePathList.clear();
									photo_url_list.clear();
								}else{
									imgFilePathList.remove(position);
									photo_url_list.remove(position);
								}
								rBigImgAdapter.notifyDataSetChanged();
								photoAdapter.notifyDataSetChanged();
								deleteImg_dialog.dismiss();
								if(imgFilePathList.size() == 0){
									pw_bigimg.dismiss();
								}
							}
						});
						ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								deleteImg_dialog.dismiss();
							}
						});
						deleteImg_dialog = ab.create();
						deleteImg_dialog.show();
						
						return false;
					}
				});
				
				pw_bigimg = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
				pw_bigimg.setAnimationStyle(R.style.AnimationDiseaseBigImg);
				pw_bigimg.showAtLocation(CheckOther.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
    	imgFilePathList.addAll(dbs.image.getPathList(now_item.Id));
    	for(int i = 0 ; i < imgFilePathList.size() ; i++){
    		photo_url_list.add(dbs.image.getItemName(imgFilePathList.get(i)));
    	}
    	photoAdapter.notifyDataSetChanged();
    	photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
	}
	/**获取图片数量*/
	public int getImgNum(){
		return photo_url_list.size();
	}
	/**图片适配器*/
	class PhotoAdapter extends ArrayAdapter<String>{
		private List<String> list;
		public PhotoAdapter(Activity activity,List<String> list) {
			super(activity,0,list);
			this.list = list;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String item_str = list.get(position);
			
			View rowView = convertView;
			ViewCache viewCache;
			if(rowView == null){
				rowView = CheckOther.this.getLayoutInflater().inflate(R.layout.report_create_module_roadbed_photo_list_item, null);
				viewCache = new ViewCache(rowView);
				rowView.setTag(viewCache);
			}else{
				viewCache = (ViewCache)rowView.getTag();
			}
			
			TextView item_title = (TextView)viewCache.getitem_title();
			item_title.setText(item_str);
			
			return rowView;
		}
		/**设置listView的高度*/
	    public void setListViewHeightBasedOnChildren(ListView listView) {  
	        //获取ListView对应的Adapter  
	    	ListAdapter listAdapter = listView.getAdapter();  
	        if (listAdapter == null) {  
	            return;  
	        }  
	        int totalHeight = 0;  
	        for (int i=0; i<listAdapter.getCount(); i++) {  
	            View listItem = listAdapter.getView(i, null, listView);  
	            listItem.measure(0, 0); //计算子项View的宽高  
	            //统计所有子项的总高度  
	            totalHeight += listItem.getMeasuredHeight();      
	        }  
	        if(totalHeight == 0){
	        	listView.setVisibility(View.GONE);
	        	return;
	        }else{
	        	listView.setVisibility(View.VISIBLE);
	        }
	        //listView.getDividerHeight()获取子项间分隔符占用的高度  
	        //params.height最后得到整个ListView完整显示需要的高度  
	        ViewGroup.LayoutParams params = listView.getLayoutParams();  
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));       
	        ((MarginLayoutParams)params).setMargins(10, 10, 10, 10);  
	        listView.setLayoutParams(params);  
	    }  
		
	}
	
	 /**照相按钮点击事件*/
    public class PhotoImgButtonOnClickListener implements OnClickListener{
    	private String direction = "";
    	private String roundName = "";
    	public PhotoImgButtonOnClickListener(String direction,String roundName){
    		this.direction = direction;
    		this.roundName = roundName;
    	}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Time time = new Time("GMT+8");       
	        time.setToNow();      
	        int year = time.year;      
	        int month = time.month;      
	        int day = time.monthDay;      
	        int minute = time.minute;      
	        int hour = time.hour;      
	        int sec = time.second;      
	       
	        if(round_item.street.equals("")){
				imgfilename = round_item.area + "_" + round_item.type + "_" + round_item.roundId + 
				"_" + round_item.name + "_" + now_item.direction + "_" + "病害" + "_"+ year + "" + month + "" + 
				day + "" + hour + "" + minute + "" + sec + ".jpg";
			}else{
				imgfilename = round_item.area + "_" + round_item.type + "_" + round_item.street + "_" + 
				round_item.roundId + "_" + round_item.name + "_" + now_item.direction + "_" + "病害" + "_" + 
				year + "" + month + "" + day + "" + hour + "" + minute + "" + sec + ".jpg";
			}
	        
			Uri uri = null;
		    String savaPath = "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "病害";
			try {
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgfilename, savaPath));
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				CheckOther.this.startActivityForResult(intent, ROADBED_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ROADBED_TAKEPHOTO:
				if(resultCode == Activity.RESULT_OK){
					photo_url_list.add(imgfilename);
		    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + File.separator + round_item.area + File.separator + round_item.type + File.separator + round_item.roundId + round_item.name + now_item.direction + File.separator + "病害" + File.separator +  imgfilename);
		    		photoAdapter.notifyDataSetChanged();
		    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
		    		settingImag(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + File.separator + round_item.area + File.separator + round_item.type + File.separator + round_item.roundId + round_item.name + now_item.direction + File.separator + "病害" + File.separator +  imgfilename);
				}
				break;
			default:
				break;
		}
	
	}
    private void settingImag(String imgPath){
		int maxWidth=160,maxHeight=160;  //设置新图片的大小  
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
	    	try {    
	    		FileOutputStream out = new FileOutputStream(file);    
	    		if(image.compress(Bitmap.CompressFormat.JPEG, 100, out)){ 
	               out.flush();    
	               out.close();    
	    		}  
	    	} catch (Exception e) {   
	    		e.printStackTrace();  
	    	}finally{  
	    	}
	    }
	}
    /**获取图片名称*/
    public String getImagPath(String roundName){
    	String imgname = "";
    	if(!imgfilename.equals(imgname)){
    		photo_url_list.add(imgfilename);
    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "病害" + File.separator +  imgfilename);
    		photoAdapter.notifyDataSetChanged();
    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
    		imgname = imgfilename;
    	}
    	return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "病害" + File.separator +  imgfilename;
    }
    /**数据保存*/
    public void savaDatas(){
    	savaDatasTask = new SavaDatasTask();
    	savaDatasTask.execute();
    }
    /**保存任务*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
			ContentValues updatecontent = new ContentValues();
	    	updatecontent.put("longFromStartNum", edt_distance.getText().toString());
	    	updatecontent.put("o_structure", sp_structure.getSelectedItem().toString());
	    	updatecontent.put("o_facility", sp_facility.getSelectedItem().toString());
	    	updatecontent.put("remark", edt_remark.getText().toString());
	    	if(imgFilePathList.size() > 0){
	    		updatecontent.put("firstImageUrl", imgFilePathList.get(0));
	    	}
	    	dbs.road_check_table.updateById(updatecontent,Id);
	    	
	    	Image imageItem;
	    	dbs.image.deleteDatas(now_item.Id + "");
	    	for(int i = 0 ; i < imgFilePathList.size() ; i++){
	    		imageItem = new Image();
	    		imageItem.roadcheckid = now_item.Id + "";
	    		imageItem.imagepath = imgFilePathList.get(i);
	    		imageItem.imagename = photo_url_list.get(i);
	    		dbs.image.add(imageItem);
	    	}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(CheckOther.this, "保存成功！", Toast.LENGTH_SHORT).show();
			
			Intent intent  = new Intent(CheckOther.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			CheckOther.this.startActivity(intent);
			CheckOther.this.finish();
		}
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  
			Intent intent  = new Intent(CheckOther.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			CheckOther.this.startActivity(intent);
			CheckOther.this.finish();
			return true;
		}  
		return false;
	}
}
