package com.road.check.report.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.road.check.R;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.common.ViewCache;
import com.road.check.model.Disease;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.report.module.Bed.PhotoAdapter;
import com.road.check.report.module.Bed.SelectedErrorAdapter;
import com.road.check.sqlite.DatabaseService;
/**
 * ���е�����״������¼��
 * */
public class Footwalk {
	public List<String> imgPathList = new ArrayList<String>();
	public HashMap<String, String> imgLocation = new HashMap<String, String>();
	protected View baseView;
	protected Activity baseactivity;
	
	//�����б�
	private List<Disease> errorlist;
	private ErrorAdapter errorAdapter;
	private Dialog errorselecteDialog;
	//��ѡ�����б�
	private SelectedErrorAdapter selectedAdapter;
	private ListView selected_error_listview_footwalk;
	private List<Disease> selectedContentList;
	private AlertDialog delete_dialog;
	//���༯��
	private ListView error_photo_listview;
	private List<String> photo_url_list;
	private PhotoAdapter photoAdapter;
	
	//�ؼ�����
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
	
	//ͼƬ�Ŵ�
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private AlertDialog deleteImg_dialog;
	
	private static String imgfilename = "";
	protected static final int FOOTWALK_TAKEPHOTO = 5; // ���պ�
	private SavaDatasTask savaDatasTask;
	private DatabaseService dbs;
	private List<String> imgFilePathList;
	
	private Round now_round_item;
	private String directionStr = "";
	
	public Footwalk(View view,Activity activity,DatabaseService dbs){
		baseView = view;
		baseactivity = activity;
		selectedContentList = new ArrayList<Disease>();
		imgFilePathList = new ArrayList<String>();
		this.dbs = dbs;
	}
	/**��ʼ��*/
	public void init(Round roundItem,String direction){
		now_round_item = new Round();
		now_round_item = roundItem;
		directionStr = direction;
		
		txt_round_name = (TextView)baseView.findViewById(R.id.txt_round_name);
    	txt_roundid = (TextView)baseView.findViewById(R.id.txt_roundid);
    	txt_startend_name = (TextView)baseView.findViewById(R.id.txt_startend_name);
    	txt_direction = (TextView)baseView.findViewById(R.id.txt_direction);
    	txt_time = (TextView)baseView.findViewById(R.id.txt_time);
    	edt_distance = (EditText)baseView.findViewById(R.id.edt_distance);
    	edt_remark = (EditText)baseView.findViewById(R.id.edt_remark);
    	btn_error_type = (Button)baseView.findViewById(R.id.btn_error_type);
    	selected_error_listview_footwalk = (ListView)baseView.findViewById(R.id.selected_error_listview);
    	btn_error_photo = (Button)baseView.findViewById(R.id.btn_error_photo);
    	error_photo_listview = (ListView)baseView.findViewById(R.id.error_photo_listview);
    	edt_area = (EditText)baseView.findViewById(R.id.edt_area);
    	edt_footwalk_width = (EditText) baseView.findViewById(R.id.edt_footwalk_width);
    	txt_round_name.setText(roundItem.name);
    	txt_roundid.setText(roundItem.roundId);
    	if (direction.equals("����")) {
			txt_startend_name.setText(roundItem.start_location + " �� " + roundItem.end_location);
			Log.d("��������յ���ʾ", "��������յ���ʾ");
		}else{
			txt_startend_name.setText( roundItem.end_location+ " �� " + roundItem.start_location);
			Log.d("��������յ���ʾ", "��������յ���ʾ");
		}
    	txt_direction.setText(direction);
    	txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
    	
    	
    	//����ѡ��
    	errorlist = new ArrayList<Disease>();
		Disease item;
		
		item = new Disease();
		item.id="001";
		item.content = "�ѷ�";
		errorlist.add(item);
		
		item = new Disease();
		item.id="002";
		item.content = "�Ӷ�";
		errorlist.add(item);
		
		item = new Disease();
		item.id="003";
		item.content = "��̨";
		errorlist.add(item);
		
		item = new Disease();
		item.id="004";
		item.content = "����";
		errorlist.add(item);
		
		item = new Disease();
		item.id="005";
		item.content = "����";
		errorlist.add(item);
		
		item = new Disease();
		item.id="006";
		item.content = "Ԥ�ƿ�ȱʧ";
		errorlist.add(item);
    	btn_error_type.setOnClickListener(new ErrorSelecte());
    	
    	//��ѡ����
    	selectedAdapter = new SelectedErrorAdapter(baseactivity, selectedContentList);
    	selected_error_listview_footwalk.setAdapter(selectedAdapter);
    	selected_error_listview_footwalk.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				AlertDialog.Builder ad = new AlertDialog.Builder(baseactivity);
				ad.setTitle("ɾ����ʾ");
				ad.setMessage("��ȷ��Ҫɾ��������");
				ad.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
				ad.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete_dialog.dismiss();
					}
				});
				delete_dialog = ad.create();
				delete_dialog.show();
			}
		});
    	
    	//����
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
				
				//�رյ�����
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
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫɾ��ͼƬ��" + photo_url_list.get(position) + "��");
						ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
						ab.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
				pw_bigimg.setAnimationStyle(R.style.AnimationBigImg);
				pw_bigimg.showAtLocation(baseactivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
	}
	 /**�����л����³�ʼ����ѡ�����б�*/
	public void initSelectedDiseaseList(){
		//��ѡ����
    	selectedAdapter = new SelectedErrorAdapter(baseactivity, selectedContentList);
    	selected_error_listview_footwalk.setAdapter(selectedAdapter);
    	selected_error_listview_footwalk.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
					long arg3) {
				AlertDialog.Builder ad = new AlertDialog.Builder(baseactivity);
				ad.setTitle("ɾ����ʾ");
				ad.setMessage("��ȷ��Ҫɾ��������");
				ad.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
				ad.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete_dialog.dismiss();
					}
				});
				delete_dialog = ad.create();
				delete_dialog.show();
			}
		});
    	
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
				
				//�رյ�����
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
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫɾ��ͼƬ��" + photo_url_list.get(position) + "��");
						ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
						ab.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
	/**����ѡ��*/
	private AlertDialog error_message_dialog;
	class ErrorSelecte implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(selectedContentList.size() > 0){
				AlertDialog.Builder e_ab = new AlertDialog.Builder(baseactivity);
				e_ab.setTitle("��ܰ��ʾ");
				e_ab.setMessage("���Ѿ�ѡ����һ�������������Ҫ�޸���ɾ��ԭ���������������ݣ�");
				e_ab.setNegativeButton("֪����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						error_message_dialog.dismiss();
					}
				});
				error_message_dialog = e_ab.create();
				error_message_dialog.show();
				return;
			}
			
			View diaView = View.inflate(baseactivity, R.layout.report_create_module_errorselecte_dialog, null);
			
			LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
			layout_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					errorselecteDialog.dismiss();
				}
			});
			ListView error_listview = (ListView)diaView.findViewById(R.id.error_listview);
			errorAdapter = new ErrorAdapter(baseactivity, errorlist);
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
			
			errorselecteDialog = new Dialog(baseactivity,R.style.dialog);
			errorselecteDialog.setContentView(diaView);
			errorselecteDialog.show();
		}
	}
	/**����ѡ��������*/
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
	/**��ѡ����������*/
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
		/**����listView�ĸ߶�*/
	    public void setListViewHeightBasedOnChildren(ListView listView) {  
	        //��ȡListView��Ӧ��Adapter  
	    	ListAdapter listAdapter = listView.getAdapter();  
	        if (listAdapter == null) {  
	            return;  
	        }  
	        int totalHeight = 0;  
	        for (int i=0; i<listAdapter.getCount(); i++) {  
	            View listItem = listAdapter.getView(i, null, listView);  
	            listItem.measure(0, 0); //��������View�Ŀ��  
	            //ͳ������������ܸ߶�  
	            totalHeight += listItem.getMeasuredHeight();      
	        }  
	        if(totalHeight == 0){
	        	listView.setVisibility(View.GONE);
	        	return;
	        }else{
	        	listView.setVisibility(View.VISIBLE);
	        }
	        //listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�  
	        //params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�  
	        ViewGroup.LayoutParams params = listView.getLayoutParams();  
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));       
	        ((MarginLayoutParams)params).setMargins(10, 10, 10, 10);  
	        listView.setLayoutParams(params);  
	    }  
	}
	/**��ȡͼƬ����*/
	public int getImgNum(){
		return photo_url_list.size();
	}
	/**ͼƬ������*/
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
		/**����listView�ĸ߶�*/
	    public void setListViewHeightBasedOnChildren(ListView listView) {  
	        //��ȡListView��Ӧ��Adapter  
	    	ListAdapter listAdapter = listView.getAdapter();  
	        if (listAdapter == null) {  
	            return;  
	        }  
	        int totalHeight = 0;  
	        for (int i=0; i<listAdapter.getCount(); i++) {  
	            View listItem = listAdapter.getView(i, null, listView);  
	            listItem.measure(0, 0); //��������View�Ŀ��  
	            //ͳ������������ܸ߶�  
	            totalHeight += listItem.getMeasuredHeight();      
	        }  
	        if(totalHeight == 0){
	        	listView.setVisibility(View.GONE);
	        	return;
	        }else{
	        	listView.setVisibility(View.VISIBLE);
	        }
	        //listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�  
	        //params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�  
	        ViewGroup.LayoutParams params = listView.getLayoutParams();  
	        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));       
	        ((MarginLayoutParams)params).setMargins(10, 10, 10, 10);  
	        listView.setLayoutParams(params);  
	    }  
		
	}
	
	 /**���ఴť����¼�*/
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
				Toast.makeText(baseactivity, "����ѡ�񲡺����ͣ�", Toast.LENGTH_SHORT).show();
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
	       
			if(now_round_item.street.equals("")){
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.roundId + 
				"_" + now_round_item.name + "_" + directionStr + "_" + "����" + "_"+ year + "" + month + "" + 
				day + "" + hour + "" + minute + "" + sec + ".jpg";
			}else{
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.street + "_" + 
				now_round_item.roundId + "_" + now_round_item.name + "_" + directionStr + "_" + "����" + "_" + 
				year + "" + month + "" + day + "" + hour + "" + minute + "" + sec + ".jpg";
			}
	        
			Uri uri = null;
		    String savaPath = "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "����";
			try {
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgfilename, savaPath));
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				baseactivity.startActivityForResult(intent, FOOTWALK_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    /**��ȡͼƬ����*/
    public String getImagPath(String roundName){
    	String imgname = "";
    	if(!imgfilename.equals(imgname)){
    		photo_url_list.add(imgfilename);
    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "����" + File.separator +  imgfilename);
    		photoAdapter.notifyDataSetChanged();
    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
    		imgname = imgfilename;
    	}
    	return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + "����" + File.separator +  imgfilename;
    }
    
    /**���ݱ���*/
    public void savaDatas(){
    	if (imgFilePathList.size()==0) {
			Toast.makeText(baseactivity, "δ���գ��������ղ���д���ݺ��ٱ���", 0).show();
		}else {
	    	savaDatasTask = new SavaDatasTask();
	    	savaDatasTask.execute();
		}

    }
    /**��������*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
			//��ȡ��ѡ����
	    	String diseaseString = "";
	    	for(int i = 0; i < selectedContentList.size() ; i++){
	    		if(i > 0){
	    			diseaseString += ",";	
	    		}
	    		diseaseString += selectedContentList.get(i).content;
	    	}
	    	
	    	Road_Check_Data_Table item = new Road_Check_Data_Table();
	    	item.roundName = txt_round_name.getText().toString();
	    	item.roundId = txt_roundid.getText().toString();
	    	item.direction = txt_direction.getText().toString();
	    	item.deteTime = txt_time.getText().toString();
	    	item.f_footwalkwidth = edt_footwalk_width.getText().toString();
	    	item.longFromStartNum = edt_distance.getText().toString();
	    	item.remark = edt_remark.getText().toString();
	    	item.f_damagedArea= edt_area.getText().toString();
	    	item.diseaseString = diseaseString;
	    	item.reportName = "���е�����״������¼��";
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
			edt_footwalk_width.setText("");
			edt_distance.setText("");
			edt_area.setText("");
			edt_remark.setText("");
			imgPathList.clear();
			imgLocation.clear();
			selectedContentList.clear();
			selectedAdapter.notifyDataSetChanged();
			photo_url_list.clear();
			photoAdapter.notifyDataSetChanged();
			imgFilePathList.clear();
			selectedAdapter.setListViewHeightBasedOnChildren(selected_error_listview_footwalk);
			txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
			
			for(int i = 0 ; i < errorlist.size() ; i++){
				errorlist.get(i).isSlecte = 0;
			}
			Toast.makeText(baseactivity, "��¼����ɹ�����", Toast.LENGTH_LONG).show();
		}
		
    }
    public String getDiseaseContent(String roundName){
    	String diseaseString = "";
    	for(int i = 0; i < selectedContentList.size() ; i++){
    		if(i > 0){
    			diseaseString += ",";	
    		}
    		diseaseString += selectedContentList.get(i).content;
    	}
    	return diseaseString;
    }
	
}