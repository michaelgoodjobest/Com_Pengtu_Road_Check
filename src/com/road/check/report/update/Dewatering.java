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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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

public class Dewatering extends ActivityBase{
	private Header header;
	//�ؼ�����
	private TextView txt_round_name;
	private TextView txt_roundid;
	private TextView txt_startend_name;
	private TextView txt_direction;
	private TextView txt_time;
	private EditText edt_distance;
	private EditText edt_remark;
	
	//·������
	private Spinner sp_roadbed;
	private List<String> error_roadbed_list;
	private ArrayAdapter<String> error_roadbed_adapter;
	
	//��ˮ��ʩ
	private Spinner sp_dewatering;
	private List<String> dewatering_list;
	private ArrayAdapter<String> dewatering_adapter;
	
	//ͼƬ�Ŵ�
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private AlertDialog deleteImg_dialog;
	
	//���༯��
	private ListView error_photo_listview;
	private List<String> photo_url_list;
	private PhotoAdapter photoAdapter;
	private Button btn_error_photo;
	private static String imgfilename= "";
	protected static final int DEWATERING_TAKEPHOTO = 3; // ���պ�
	
	private SavaDatasTask savaDatasTask;
	private DatabaseService dbs;
	private List<String> imgFilePathList;
	private String Id="";
	private Road_Check_Data_Table now_item;
	private Round round_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_dewatering);
		try{
			Id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		//����ͷ��
		header = new Header(this,"·������ˮ��ʩ����״������¼��",
				"����",new HeaderLeftOnClickListener(),
				"����",new HeaderRightOnClickListener());
		
		imgFilePathList = new ArrayList<String>();
		
		//��ȡ��ǰ����
		dbs = new DatabaseService(this);
		now_item = new Road_Check_Data_Table();
		now_item = dbs.road_check_table.getItem(Id);
		//��ȡ��·����
		round_item = new Round();
		round_item = dbs.round.getItemByRoundId(now_item.roundId);
		
		//�ؼ���ʼ��
		init();
	}
	/**ͷ����߰�ť����¼�������*/
	public class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent  = new Intent(Dewatering.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Dewatering.this.startActivity(intent);
			Dewatering.this.finish();
		}
	}
	/**ͷ���ұ߰�ť����¼�������*/
	public class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
		}
	}
	/**��ʼ��*/
	public void init(){
		txt_round_name = (TextView)Dewatering.this.findViewById(R.id.txt_round_name);
		txt_roundid = (TextView)Dewatering.this.findViewById(R.id.txt_roundid);
		txt_startend_name = (TextView)Dewatering.this.findViewById(R.id.txt_startend_name);
		txt_direction = (TextView)Dewatering.this.findViewById(R.id.txt_direction);
		txt_time = (TextView)Dewatering.this.findViewById(R.id.txt_time);
		edt_distance = (EditText)Dewatering.this.findViewById(R.id.edt_distance);
		sp_roadbed = (Spinner)Dewatering.this.findViewById(R.id.sp_roadbed);
		sp_dewatering = (Spinner)Dewatering.this.findViewById(R.id.sp_dewatering);
		edt_remark = (EditText)Dewatering.this.findViewById(R.id.edt_remark);
		btn_error_photo = (Button)Dewatering.this.findViewById(R.id.btn_error_photo);
		error_photo_listview = (ListView)Dewatering.this.findViewById(R.id.error_photo_listview);
		
		txt_round_name.setText(now_item.roundName);
		txt_roundid.setText(now_item.roundId);
		txt_startend_name.setText(round_item.start_location + " �� " + round_item.end_location);
		txt_direction.setText(now_item.direction);
		txt_time.setText(now_item.deteTime);
		edt_distance.setText(now_item.longFromStartNum);
		edt_remark.setText(now_item.remark);
		
		//·������
		error_roadbed_list = new ArrayList<String>();
		error_roadbed_list.add("�����幵");
		error_roadbed_list.add("��������");
		error_roadbed_list.add("��������");
		error_roadbed_adapter = new ArrayAdapter<String>(Dewatering.this,android.R.layout.simple_spinner_item,error_roadbed_list);
		error_roadbed_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_roadbed.setAdapter(error_roadbed_adapter);//d_roadbedDiseaseString
		//��ǰ������ֵ
		for(int i =0 ; i < error_roadbed_list.size() ; i++){
			if(error_roadbed_list.get(i).equals(now_item.d_roadbedDiseaseString)){
				sp_roadbed.setSelection(i);
				break;
			}
		}
		
		//��ˮ��ʩ
		dewatering_list = new ArrayList<String>();
		dewatering_list.add("����");
		dewatering_list.add("����");
		dewatering_adapter = new ArrayAdapter<String>(Dewatering.this,android.R.layout.simple_spinner_item,dewatering_list);
		dewatering_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_dewatering.setAdapter(dewatering_adapter);
		//��ǰ��ˮ��ʩ��ֵ
		for(int i = 0 ; i < dewatering_list.size() ; i++){
			if(dewatering_list.get(i).equals(now_item.d_dewateringDiseaseSting)){
				sp_dewatering.setSelection(i);
				break;
			}
		}
		
    	//����
    	photo_url_list = new ArrayList<String>();
    	btn_error_photo.setOnClickListener(new PhotoImgButtonOnClickListener(now_item.direction,now_item.roundName));
    	photoAdapter = new PhotoAdapter(Dewatering.this, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				LayoutInflater inflater = Dewatering.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(Dewatering.this, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(Dewatering.this);
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
						return false;
					}
				});
				
				pw_bigimg = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,true);
				pw_bigimg.setAnimationStyle(R.style.AnimationBigImg);
				pw_bigimg.showAtLocation(Dewatering.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
    	
    	imgFilePathList.addAll(dbs.image.getPathList(now_item.Id));
    	for(int i = 0 ; i < imgFilePathList.size() ; i++){
    		photo_url_list.add(dbs.image.getItemName(imgFilePathList.get(i)));
    	}
    	photoAdapter.notifyDataSetChanged();
    	photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
	}
	/**��ȡͼƬ����*/
	public int getImgNum(){
		return photo_url_list.size();
	}
	 /**�����л����³�ʼ��ͼƬ�б�*/
	public void initImgList(){
    	photoAdapter = new PhotoAdapter(Dewatering.this, photo_url_list);
    	error_photo_listview.setAdapter(photoAdapter);
    	error_photo_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				LayoutInflater inflater = Dewatering.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(Dewatering.this, imgFilePathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(position);
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						final String path = imgFilePathList.get(position);
						AlertDialog.Builder ab = new AlertDialog.Builder(Dewatering.this);
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫɾ��ͼƬ��" + photo_url_list.get(position) + "��");
						ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
				pw_bigimg.showAtLocation(Dewatering.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
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
				rowView = Dewatering.this.getLayoutInflater().inflate(R.layout.report_create_module_roadbed_photo_list_item, null);
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
	            listItem.measure(0, 0); //��������View�Ŀ���  
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
				"_" + round_item.name + "_" + now_item.direction + "_" + "����" + "_"+ year + "" + month + "" + 
				day + "" + hour + "" + minute + "" + sec + ".jpg";
			}else{
				imgfilename = round_item.area + "_" + round_item.type + "_" + round_item.street + "_" + 
				round_item.roundId + "_" + round_item.name + "_" + now_item.direction + "_" + "����" + "_" + 
				year + "" + month + "" + day + "" + hour + "" + minute + "" + sec + ".jpg";
			}
	        
			Uri uri = null;
		    String savaPath = "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "����";
			try {
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgfilename, savaPath));
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Dewatering.this.startActivityForResult(intent, DEWATERING_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case DEWATERING_TAKEPHOTO:
				if(resultCode == Activity.RESULT_OK){
					photo_url_list.add(imgfilename);
		    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + File.separator + round_item.area + File.separator + round_item.type + File.separator + round_item.roundId + round_item.name + now_item.direction + File.separator + "����" + File.separator +  imgfilename);
		    		photoAdapter.notifyDataSetChanged();
		    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
		    		settingImag(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + File.separator + round_item.area + File.separator + round_item.type + File.separator + round_item.roundId + round_item.name + now_item.direction + File.separator + "����" + File.separator +  imgfilename);
				}
				break;
			default:
				break;
		}
	
	}
    private void settingImag(String imgPath){
		int maxWidth=160,maxHeight=160;  //������ͼƬ�Ĵ�С  
	    BitmapFactory.Options options = new BitmapFactory.Options();    
	    options.inJustDecodeBounds = true; 
	    Bitmap image = BitmapFactory.decodeFile(imgPath, options);    
	    double ratio = 1D;    
	    if (maxWidth > 0 && maxHeight <= 0) {    
	        // �޶����ȣ��߶Ȳ�������    
	        ratio = Math.ceil(options.outWidth / maxWidth);    
	    } else if (maxHeight > 0 && maxWidth <= 0) {    
	        // �޶��߶ȣ������ƿ���    
	        ratio = Math.ceil(options.outHeight / maxHeight);    
	    } else if (maxWidth > 0 && maxHeight > 0) {    
	        // �߶ȺͿ��ȶ��������ƣ���ʱ�����Ǽ�������������������ɵ�����ͼƬ�ߴ磬����ʹͼƬ����    
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
    /**��ȡͼƬ����*/
    public String getImagPath(String roundName){
    	String imgname = "";
    	if(!imgfilename.equals(imgname)){
    		photo_url_list.add(imgfilename);
    		imgFilePathList.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "����" + File.separator +  imgfilename);
    		photoAdapter.notifyDataSetChanged();
    		photoAdapter.setListViewHeightBasedOnChildren(error_photo_listview);
    		imgname = imgfilename;
    	}
    	return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "com_pengtu_road_check_data" + "/" + round_item.area + "/" + round_item.type + "/" + round_item.roundId + round_item.name + now_item.direction + "/" + "����" + File.separator +  imgfilename;
    }
    /**���ݱ���*/
    public void savaDatas(){
    	savaDatasTask = new SavaDatasTask();
    	savaDatasTask.execute();
    }
    /**��������*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
	    	ContentValues contentValues = new ContentValues();
	    	contentValues.put("longFromStartNum", edt_distance.getText().toString());
	    	contentValues.put("d_roadbedDiseaseString", sp_roadbed.getSelectedItem().toString());
	    	contentValues.put("d_dewateringDiseaseSting", sp_dewatering.getSelectedItem().toString());
	    	contentValues.put("remark", edt_remark.getText().toString());
	    	if(imgFilePathList.size() > 0){
	    		contentValues.put("firstImageUrl", imgFilePathList.get(0));
	    	}
	    	dbs.road_check_table.updateById(contentValues,Id);
	    	
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
			Toast.makeText(Dewatering.this, "����ɹ���", Toast.LENGTH_SHORT).show();
			
			Intent intent  = new Intent(Dewatering.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Dewatering.this.startActivity(intent);
			Dewatering.this.finish();
		}
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  
			Intent intent  = new Intent(Dewatering.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Dewatering.this.startActivity(intent);
			Dewatering.this.finish();
			return true;
		}  
		return false;
	}
}