package com.road.check.report.update;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.road.check.R;
import com.road.check.app.functionmodule.ImageSpin;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.common.ViewCache;
import com.road.check.model.Disease;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.sqlite.DatabaseService;

public class Footwalk extends ActivityBase{
	private Header header;
	//病害列表
	private List<Disease> errorlist;
	private ErrorAdapter errorAdapter;
	private Dialog errorselecteDialog;
	//已选病害列表
	private ListView selected_error_listview_footwalk;
	private SelectedErrorAdapter selectedAdapter;
	private List<Disease> selectedContentList;
	private AlertDialog delete_dialog;
	//照相集合
	private ListView error_photo_listview;
	private List<String> photo_url_list;
	private PhotoAdapter photoAdapter;
	
	//控件定义
	private TextView txt_round_name;
	private TextView txt_roundid;
	private TextView txt_startend_name;
	private TextView txt_direction;
	private TextView txt_time;
	private EditText edt_distance;
	private EditText edt_footwalk_width;
	private EditText edt_remark;
	private Button btn_error_type;
	private Button btn_error_photo;
	private EditText edt_area;
	
	//图片放大
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private AlertDialog deleteImg_dialog;
	
	private static String imgfilename = "";
	protected static final int FOOTWALK_TAKEPHOTO = 5; // 拍照后
	private SavaDatasTask savaDatasTask;
	private DatabaseService dbs;
	private List<String> imgFilePathList;
	private String Id = "";
	private Road_Check_Data_Table now_item;
	private Round round_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_footwalk);
		try{
			Id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		
		//加载头部
		header = new Header(this,"人行道养护状况检查记录表",
				"返回",new HeaderLeftButtonOnClickListener(),
				"保存",new HeaderRightButtonOnClickListener());
		
		selectedContentList = new ArrayList<Disease>();
		imgFilePathList = new ArrayList<String>();
		
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
			Intent intent  = new Intent(Footwalk.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Footwalk.this.startActivity(intent);
			Footwalk.this.finish();
		}
	} 
	/**头部右边按钮点击事件：保存*/
	public class HeaderRightButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
		}
	}
	/**初始化*/
	public void init(){
		txt_round_name = (TextView)Footwalk.this.findViewById(R.id.txt_round_name);
    	txt_roundid = (TextView)Footwalk.this.findViewById(R.id.txt_roundid);
    	txt_startend_name = (TextView)Footwalk.this.findViewById(R.id.txt_startend_name);
    	txt_direction = (TextView)Footwalk.this.findViewById(R.id.txt_direction);
    	txt_time = (TextView)Footwalk.this.findViewById(R.id.txt_time);
    	edt_footwalk_width = (EditText)Footwalk.this.findViewById(R.id.edt_footwalk_width);
    	edt_distance = (EditText)Footwalk.this.findViewById(R.id.edt_distance);
    	edt_remark = (EditText)Footwalk.this.findViewById(R.id.edt_remark);
    	btn_error_type = (Button)Footwalk.this.findViewById(R.id.btn_error_type);
    	selected_error_listview_footwalk = (ListView)Footwalk.this.findViewById(R.id.selected_error_listview);
    	btn_error_photo = (Button)Footwalk.this.findViewById(R.id.btn_error_photo);
    	error_photo_listview = (ListView)Footwalk.this.findViewById(R.id.error_photo_listview);
    	edt_area = (EditText)Footwalk.this.findViewById(R.id.edt_area);
    	
    	txt_round_name.setText(now_item.roundName);
    	txt_roundid.setText(now_item.roundId);
    	txt_startend_name.setText(round_item.start_location + " 至 " + round_item.end_location);
    	txt_direction.setText(now_item.direction);
    	txt_time.setText(now_item.deteTime);
    	edt_footwalk_width.setText(now_item.f_footwalkwidth);
    	edt_distance.setText(now_item.longFromStartNum);
    	edt_remark.setText(now_item.remark);
    	edt_area.setText(now_item.f_damagedArea);
    	
    	//病害选择
    	errorlist = new ArrayList<Disease>();
		Disease item;
		
		item = new Disease();
		item.id="001";
		item.content = "裂缝";
		errorlist.add(item);
		
		item = new Disease();
		item.id="002";
		item.content = "坑洞";
		errorlist.add(item);
		
		item = new Disease();
		item.id="003";
		item.content = "错台";
		errorlist.add(item);
		
		item = new Disease();
		item.id="004";
		item.content = "拱起";
		errorlist.add(item);
		
		item = new Disease();
		item.id="005";
		item.content = "沉陷";
		errorlist.add(item);
		
		item = new Disease();
		item.id="006";
		item.content = "预制块缺失";
		errorlist.add(item);
    	btn_error_type.setOnClickListener(new ErrorSelecte());
    	
    	//已选病害
    	String[] selectedDiseaseArray = now_item.diseaseString.split(",");
    	for(int i = 0; i < selectedDiseaseArray.length ; i++){
    		for(int j =0 ; j < errorlist.size(); j++){
    			if(selectedDiseaseArray[i].equals(errorlist.get(j).content)){
    				selectedContentList.add(errorlist.get(j));
    				break;
    			}
    		}
    	}
    	selectedAdapter = new SelectedErrorAdapter(Footwalk.this, selectedContentList);
    	selected_error_listview_footwalk.setAdapter(selectedAdapter);
    	selected_error_listview_footwalk.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				AlertDialog.Builder ad = new AlertDialog.Builder(Footwalk.this);
				ad.setTitle("删除提示");
				ad.setMessage("您确认要删除该项吗？");
				ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Disease item = selectedContentList.get(position);
						for(int i = 0 ; i < errorlist.size() ; i++){
							if(item.id.equals(errorlist.get(i).id)){
								errorlist.get(i).isSlecte = 0;
								break;
							}
						}
						errorAdapter.notifyDataSetChanged();
						selectedContentList.remove(position);
						selectedAdapter.notifyDataSetChanged();
						selectedAdapter.setListViewHeightBasedOnChildren(selected_error_listview_footwalk);
						delete_dialog.dismiss();
					}
				});
				ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete_dialog.dismiss();
					}
				});
				delete_dialog = ad.create();
				delete_dialog.show();
			}
		});
    	
    	//照相
    	photo_url_list = new ArrayList<String>();
    	btn_error_photo.setOnClickListener(new PhotoImgButtonOnClickListener(now_item.direction,now_item.roundName));
    	photoAdapter = new PhotoAdapter(Footwalk.this, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				LayoutInflater inflater = Footwalk.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(Footwalk.this, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(Footwalk.this);
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
				pw_bigimg.setAnimationStyle(R.style.AnimationBigImg);
				pw_bigimg.showAtLocation(Footwalk.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
    	
    	imgFilePathList.addAll(dbs.image.getPathList(now_item.Id));
    	for(int i = 0 ; i < imgFilePathList.size() ; i++){
    		photo_url_list.add(dbs.image.getItemName(imgFilePathList.get(i)));
    	}
    	photoAdapter.notifyDataSetChanged();
    	photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
	}
	 /**界面切换从新初始化已选病害列表*/
	public void initSelectedDiseaseList(){
		//已选病害
    	selectedAdapter = new SelectedErrorAdapter(Footwalk.this, selectedContentList);
    	selected_error_listview_footwalk.setAdapter(selectedAdapter);
    	selected_error_listview_footwalk.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				AlertDialog.Builder ad = new AlertDialog.Builder(Footwalk.this);
				ad.setTitle("删除提示");
				ad.setMessage("您确认要删除该项吗？");
				ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Disease item = selectedContentList.get(position);
						for(int i = 0 ; i < errorlist.size() ; i++){
							if(item.id.equals(errorlist.get(i).id)){
								errorlist.get(i).isSlecte = 0;
								break;
							}
						}
						errorAdapter.notifyDataSetChanged();
						selectedContentList.remove(position);
						selectedAdapter.notifyDataSetChanged();
						selectedAdapter.setListViewHeightBasedOnChildren(selected_error_listview_footwalk);
						delete_dialog.dismiss();
					}
				});
				ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete_dialog.dismiss();
					}
				});
				delete_dialog = ad.create();
				delete_dialog.show();
			}
		});
    	
    	photoAdapter = new PhotoAdapter(Footwalk.this, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				LayoutInflater inflater = Footwalk.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(Footwalk.this, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(Footwalk.this);
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
					}
				});
				
				pw_bigimg = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
				pw_bigimg.setAnimationStyle(R.style.AnimationDiseaseBigImg);
				pw_bigimg.showAtLocation(Footwalk.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
	}
	/**病害选择*/
	private AlertDialog error_message_dialog;
	class ErrorSelecte implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(selectedContentList.size() > 0){
				AlertDialog.Builder e_ab = new AlertDialog.Builder(Footwalk.this);
				e_ab.setTitle("温馨提示");
				e_ab.setMessage("您已经选择了一条病害，如果需要修改请删除原来的那条病害数据！");
				e_ab.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						error_message_dialog.dismiss();
					}
				});
				error_message_dialog = e_ab.create();
				error_message_dialog.show();
				return;
			}
			
			View diaView = View.inflate(Footwalk.this, R.layout.report_create_module_errorselecte_dialog, null);
			
			LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
			layout_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					errorselecteDialog.dismiss();
				}
			});
			ListView error_listview = (ListView)diaView.findViewById(R.id.error_listview);
			errorAdapter = new ErrorAdapter(Footwalk.this, errorlist);
			error_listview.setAdapter(errorAdapter);
			error_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Disease item = errorlist.get(position);
					selectedContentList.clear();
					selectedContentList.add(item);
					selectedAdapter.notifyDataSetChanged();
					selectedAdapter.setListViewHeightBasedOnChildren(selected_error_listview_footwalk);
					errorselecteDialog.dismiss();
				}
			});
			
			errorselecteDialog = new Dialog(Footwalk.this,R.style.dialog);
			errorselecteDialog.setContentView(diaView);
			errorselecteDialog.show();
		}
	}
	/**病害选择适配器*/
	class ErrorAdapter extends ArrayAdapter<Disease>{
		private Activity activity;
		private List<Disease> list;
		public ErrorAdapter(Activity activity,List<Disease> list) {
			super(activity,0,list);
			this.activity = activity;
			this.list = list;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Disease item = list.get(position);
			
			View roundView = convertView;
			ViewCache viewCache;
			if(roundView == null){
				roundView = activity.getLayoutInflater().inflate(R.layout.report_create_module_errorselecte_dialog_item, null);
				viewCache = new ViewCache(roundView);
				roundView.setTag(viewCache);
			}else{
				viewCache = (ViewCache)roundView.getTag();
			}
			
			TextView item_error_title = (TextView)viewCache.getitem_error_title();
			ImageView item_error_imgview = (ImageView)viewCache.getitem_error_imgview();
			
			item_error_title.setText(item.content);
			if(item.isSlecte == 1){
				item_error_imgview.setImageResource(R.drawable.check_mark);
			}else{
				item_error_imgview.setImageResource(0);
			}
			return roundView;
		}
	}
	/**已选病害适配器*/
	class SelectedErrorAdapter extends ArrayAdapter<Disease>{
		private Activity activity;
		private List<Disease> list;
		public SelectedErrorAdapter(Activity activity,List<Disease> list) {
			super(activity, 0,list);
			this.activity = activity;
			this.list = list;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Disease item = list.get(position);
			
			View roundView = convertView;
			ViewCache viewCache;
			if(roundView == null){
				roundView = activity.getLayoutInflater().inflate(R.layout.report_create_module_roadbed_selected_list_item, null);
				viewCache = new ViewCache(roundView);
				roundView.setTag(viewCache);
			}else{
				viewCache = (ViewCache)roundView.getTag();
			}
			
			TextView item_title = (TextView)viewCache.getitem_title();
			
			item_title.setText(item.content);
			
			return roundView;
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
				rowView = Footwalk.this.getLayoutInflater().inflate(R.layout.report_create_module_roadbed_photo_list_item, null);
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
			if(selectedContentList.size() <= 0){
				Toast.makeText(Footwalk.this, "请先选择病害类型！", Toast.LENGTH_SHORT).show();
				return;
			}
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
				Footwalk.this.startActivityForResult(intent, FOOTWALK_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case FOOTWALK_TAKEPHOTO:
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
			//获取已选病害
	    	String diseaseString = "";
	    	for(int i = 0; i < selectedContentList.size() ; i++){
	    		if(i > 0){
	    			diseaseString += ",";	
	    		}
	    		diseaseString += selectedContentList.get(i).content;
	    	}
	    	ContentValues updateContent = new ContentValues();
	    	updateContent.put("f_footwalkwidth", edt_footwalk_width.getText().toString());
	    	updateContent.put("longFromStartNum", edt_distance.getText().toString());
	    	updateContent.put("diseaseString", diseaseString);
	    	updateContent.put("f_damagedArea", edt_area.getText().toString());
	    	updateContent.put("remark", edt_remark.getText().toString());
	    	if(imgFilePathList.size() > 0){
	    		updateContent.put("firstImageUrl", imgFilePathList.get(0));
	    	}
	    	dbs.road_check_table.updateById(updateContent,Id);
	    	
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
			Toast.makeText(Footwalk.this, "保存成功！", Toast.LENGTH_SHORT).show();
			
			Intent intent  = new Intent(Footwalk.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Footwalk.this.startActivity(intent);
			Footwalk.this.finish();
		}
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  
			Intent intent  = new Intent(Footwalk.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Footwalk.this.startActivity(intent);
			Footwalk.this.finish();
			return true;
		}  
		return false;
	}
}
