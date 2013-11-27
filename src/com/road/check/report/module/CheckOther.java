package com.road.check.report.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
import android.widget.AdapterView.OnItemClickListener;

import com.road.check.R;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.common.ViewCache;
import com.road.check.model.Disease;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.report.module.Bed.SelectedErrorAdapter;
import com.road.check.report.module.Footwalk.SavaDatasTask;
import com.road.check.sqlite.DatabaseService;
/**
 * 其他设施养护状况检查记录表
 * */
public class CheckOther {
	public List<String> imgPathList = new ArrayList<String>();
	public HashMap<String, String> imgLocation = new HashMap<String, String>();
	private View baseview;
	private Activity baseactivity;
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
	
	private Round now_round_item;
	private String directionStr = "";
	
	public CheckOther(View view,Activity activity,DatabaseService dbs){
		baseview = view;
		baseactivity = activity;
		imgFilePathList = new ArrayList<String>();
		this.dbs = dbs;
	}
	/**初始化*/
	public void init(Round roundItem,String direction){
		now_round_item = new Round();
		now_round_item = roundItem;
		directionStr = direction;
		
		txt_round_name = (TextView)baseview.findViewById(R.id.txt_round_name);
		txt_roundid = (TextView)baseview.findViewById(R.id.txt_roundid);
		txt_startend_name = (TextView)baseview.findViewById(R.id.txt_startend_name);
		txt_direction = (TextView)baseview.findViewById(R.id.txt_direction);
		txt_time = (TextView)baseview.findViewById(R.id.txt_time);
		edt_distance = (EditText)baseview.findViewById(R.id.edt_distance);
		sp_structure = (Spinner)baseview.findViewById(R.id.sp_structure);
		sp_facility = (Spinner)baseview.findViewById(R.id.sp_facility);
		btn_error_photo = (Button)baseview.findViewById(R.id.btn_error_photo);
		error_photo_listview = (ListView)baseview.findViewById(R.id.error_photo_listview);
		edt_remark = (EditText)baseview.findViewById(R.id.edt_remark);
		
		txt_round_name.setText(roundItem.name);
		txt_roundid.setText(roundItem.roundId);
		if (direction.equals("上行")) {
			txt_startend_name.setText(roundItem.start_location + " 至 " + roundItem.end_location);
			Log.d("上行起点终点显示", "上行起点终点显示");
		}else{
			txt_startend_name.setText( roundItem.end_location+ " 至 " + roundItem.start_location);
			Log.d("下行起点终点显示", "下行起点终点显示");
		}
		txt_direction.setText(direction);
		txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
		
		//所属结构
		structure_list = new ArrayList<String>();
		structure_list.add("");
		structure_list.add("变形");
		structure_list.add("破损");
		structure_list.add("功能失效");
		structure_adapter = new ArrayAdapter<String>(baseactivity,android.R.layout.simple_spinner_item,structure_list);
		structure_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_structure.setAdapter(structure_adapter);
		
		//所属设施
		facility_list = new ArrayList<String>();
		facility_list.add("");
		facility_list.add("破损");
		facility_list.add("功能失效");
		facility_adapter = new ArrayAdapter<String>(baseactivity,android.R.layout.simple_spinner_item,facility_list);
		facility_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_facility.setAdapter(facility_adapter);
		// 结构设施病害选择设置
		sp_structure.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
					if (sp_structure.getItemAtPosition(arg2).toString().equals("变形")||sp_structure.getItemAtPosition(arg2).toString().equals("破损")||sp_structure.getItemAtPosition(arg2).toString().equals("功能失效")) {
						facility_list.clear();
						facility_list.add("");
						facility_adapter.notifyDataSetChanged();
					}else if(sp_structure.getItemAtPosition(arg2).toString().equals("")){
						facility_list.clear();
						facility_list.add("");
						facility_list.add("破损");
						facility_list.add("功能失效");
						facility_adapter.notifyDataSetChanged();
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		sp_facility.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
					if (sp_facility.getItemAtPosition(arg2).toString().equals("破损")||sp_facility.getItemAtPosition(arg2).toString().equals("功能失效")) {
						structure_list.clear();
						structure_list.add("");
						structure_adapter.notifyDataSetChanged();
					}else if(sp_facility.getItemAtPosition(arg2).toString().equals("")){
						structure_list.clear();
						structure_list.add("");
						structure_list.add("变形");
						structure_list.add("破损");
						structure_list.add("功能失效");
						structure_adapter.notifyDataSetChanged();
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//照相
    	photo_url_list = new ArrayList<String>();
    	btn_error_photo.setOnClickListener(new PhotoImgButtonOnClickListener(direction,roundItem.name));
    	photoAdapter = new PhotoAdapter(baseactivity, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				LayoutInflater inflater = baseactivity.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(baseactivity, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(baseactivity);
						ab.setTitle("温馨提示");
						ab.setMessage("您确定要删除图片：" + photo_url_list.get(position) + "吗？");
						ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								File file = new File(path);
								if(file.exists()){
									file.delete();
								}
								imgFilePathList.remove(position);
								photo_url_list.remove(position);
								imgLocation.remove(imgPathList.get(position));
								imgPathList.remove(position);
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
					}
				});
				
				pw_bigimg = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
				pw_bigimg.setAnimationStyle(R.style.AnimationDiseaseBigImg);
				pw_bigimg.showAtLocation(baseactivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
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
				rowView = baseactivity.getLayoutInflater().inflate(R.layout.report_create_module_roadbed_photo_list_item, null);
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
	       
			if(now_round_item.street.equals("")){
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.roundId + 
				"_" + now_round_item.name + "_" + directionStr + "_" + "病害" + "_"+ year + "" + month + "" + 
				day + "" + hour + "" + minute + "" + sec + ".jpg";
			}else{
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.street + "_" + 
				now_round_item.roundId + "_" + now_round_item.name + "_" + directionStr + "_" + "病害" + "_" + 
				year + "" + month + "" + day + "" + hour + "" + minute + "" + sec + ".jpg";
			}
	        
			Uri uri = null;
		    String savaPath = "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "病害";
			try {
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgfilename, savaPath));
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				baseactivity.startActivityForResult(intent, ROADBED_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    /**获取图片名称*/
    public String getImagPath(String roundName){
    	String imgname = "";
    	if(!imgfilename.equals(imgname)){
    		photo_url_list.add(imgfilename);
    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "病害"+ File.separator +  imgfilename);
    		photoAdapter.notifyDataSetChanged();
    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
    		imgname = imgfilename;
    	}
    	return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "病害" + File.separator +  imgfilename;
    }
    /**数据保存*/
    public void savaDatas(){
    	if (imgFilePathList.size()==0) {
			Toast.makeText(baseactivity, "未拍照，请先拍照并填写数据后再保存", 0).show();
		}else {
	    	savaDatasTask = new SavaDatasTask();
	    	savaDatasTask.execute();
		}
    }
    /**保存任务*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
	    	Road_Check_Data_Table item = new Road_Check_Data_Table();
	    	item.roundName = txt_round_name.getText().toString();
	    	item.roundId = txt_roundid.getText().toString();
	    	item.direction = txt_direction.getText().toString();
	    	item.deteTime = txt_time.getText().toString();
	    	item.longFromStartNum = edt_distance.getText().toString();
	    	item.o_structure = sp_structure.getSelectedItem().toString();
	    	item.o_facility = sp_facility.getSelectedItem().toString();
	    	item.remark = edt_remark.getText().toString();
	    	item.reportName = "其他设施养护状况检查记录表";
	    	if(imgFilePathList.size() > 0){
	    		item.firstImageUrl = imgFilePathList.get(0);
	    	}
	    	int check_table_id = dbs.road_check_table.add(item);
	    	
	    	Image imageItem;
	    	for(int i = 0 ; i < imgFilePathList.size() ; i++){
	    		imageItem = new Image();
	    		imageItem.roadcheckid = check_table_id + "";
	    		imageItem.imagepath = imgFilePathList.get(i);
	    		imageItem.imagename = photo_url_list.get(i);
	    		imageItem.imglocation = imgLocation.get(imgPathList.get(i));
	    		dbs.image.add(imageItem);
	    	}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			edt_distance.setText("");
			edt_remark.setText("");
			imgPathList.clear();
			imgLocation.clear();
			sp_structure.setSelection(0);
			sp_facility.setSelection(0);
			photo_url_list.clear();
			photoAdapter.notifyDataSetChanged();
			imgFilePathList.clear();
			txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
			Toast.makeText(baseactivity, "记录保存成功！！", Toast.LENGTH_LONG).show();
		}
    }
    public String getDiseaseContent(String roundName){
    	String diseaseString = sp_structure.getSelectedItem().toString()+sp_facility.getSelectedItem().toString();
    	
    	
    	return diseaseString;
    }
}
