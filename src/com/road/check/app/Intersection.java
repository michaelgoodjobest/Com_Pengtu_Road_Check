package com.road.check.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.road.check.R;
import com.road.check.app.functionmodule.ImageSpin;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.sqlite.DatabaseService;

public class Intersection extends ActivityBase{
	private Header header;
	private ImageView img_photo;
	private TextView txt_now_page;
	private TextView txt_all_page;
	private TextView txt_imgname;
	private TextView txt_intersection_name;
	private Button btn_photo;
	private Button btn_prev;
	private Button btn_next;
	private Button btn_record;
	private EditText edt_describe;
//	private EditText edt_distance;
	private EditText edt_distance_forstart;
	private EditText edt_distance_forend;
	private EditText edt_intersection_name;
	
	private String title = "";
	private String photoType = "";
	private int photoTypeValue = -1;
	private String directionStr = "";
	private String nowimPath = "";
	private String nowimName = "";
	private Round now_round_item;
	private DatabaseService dbs;
	//ͼƬ��������
	private int now_img_position = 0;
	
	//����
	private List<String> imgPathList;
	private HashMap<String,String> imgNameList;
	private HashMap<String,String> imgDescribe;
	private HashMap<String,String> imgLocation;
	private final int ROADPOINTPHOTO_TAKEPHOTO = 10;
	
	private SavaDatasTask savaDatasTask;
	private AlertDialog backMessageDialog;
	
	//ͼƬ�Ŵ�
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private List<String> deleteImgPathList;
	//ͼƬɾ��
	private AlertDialog deleteImg_dialog;
	
	//��ʷ��¼
	private List<Road_Check_Data_Table> check_list;
	private int controlValue = 0;
	
	//������Ϣ
	private HashMap<String,String> i_name_map;
//	private HashMap<String,String> i_distance_map;
	private HashMap<String,String> i_distance_start_map;
	private HashMap<String,String> i_distance_end_map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_intersection);
		
		try{
			title = this.getIntent().getExtras().getString("title");
			photoType = this.getIntent().getExtras().getString("photoType");
			photoTypeValue = this.getIntent().getExtras().getInt("photoTypeValue");
		}catch(Exception ex){
		}
		
		//ͷ������
		header = new Header(this, title,
				"����",new HeaderLeftOnClickListener(),
				"����",new HeaderRightOnClickListener());
		
		img_photo = (ImageView)this.findViewById(R.id.img_photo);
		txt_now_page = (TextView)this.findViewById(R.id.txt_now_page);
		txt_all_page = (TextView)this.findViewById(R.id.txt_all_page);
		txt_imgname = (TextView)this.findViewById(R.id.txt_imgname);
		txt_intersection_name = (TextView)this.findViewById(R.id.txt_intersection_name);
		btn_photo = (Button)this.findViewById(R.id.btn_photo);
		btn_prev = (Button)this.findViewById(R.id.btn_prev);
		btn_next = (Button)this.findViewById(R.id.btn_next);
		btn_record = (Button)this.findViewById(R.id.btn_record);
		edt_describe = (EditText)this.findViewById(R.id.edt_describe);
//		edt_distance = (EditText)this.findViewById(R.id.edt_distance);
		edt_distance_forstart = (EditText)this.findViewById(R.id.edt_distance_forstart);
		edt_distance_forend = (EditText)this.findViewById(R.id.edt_distance_forend);
		edt_intersection_name = (EditText)this.findViewById(R.id.edt_intersection_name);
		
		dbs = new DatabaseService(this);
		
		//�ж�������
    	if(cApp.Direction == 1){
    		directionStr = "����";
    	}else if(cApp.Direction == 0){
    		directionStr = "����";
    	}
    	
    	//��ȡ��ǰ��/�ŶԶ�������
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);
		
		//ͼƬ���ϳ�ʼ��
		imgPathList = new ArrayList<String>();
		imgNameList = new HashMap<String, String>();
		imgDescribe = new HashMap<String, String>();
		imgLocation = new HashMap<String, String>();
		deleteImgPathList = new ArrayList<String>();
		
		//������Ϣ
		i_name_map = new HashMap<String, String>();
//		i_distance_map = new HashMap<String, String>();
		i_distance_start_map = new HashMap<String, String>();
		i_distance_end_map = new HashMap<String, String>();
		
		//����
		btn_photo.setOnClickListener(new PhotoImgButtonOnClickListener());
		//��һ��
		btn_prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(now_img_position > 0){
					btn_next.setClickable(true);
					now_img_position -- ;
					img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(now_img_position)));
					txt_now_page.setText("��" + (now_img_position + 1)+ "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
					edt_intersection_name.setText(i_name_map.get(imgPathList.get(now_img_position)));
//					edt_distance.setText(i_distance_map.get(imgPathList.get(now_img_position)));
					edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(now_img_position)));
					edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(now_img_position)));
				}else{
					btn_prev.setClickable(false);
					Toast.makeText(Intersection.this, "��ǰ�Ѿ��ǵ�һ��ͼƬ�ˣ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//��һ��
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(now_img_position < imgPathList.size() - 1){
					btn_prev.setClickable(true);
					now_img_position ++ ;
					img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(now_img_position)));
					txt_now_page.setText("��" + (now_img_position + 1)+ "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
					edt_intersection_name.setText(i_name_map.get(imgPathList.get(now_img_position)));
//					edt_distance.setText(i_distance_map.get(imgPathList.get(now_img_position)));
					edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(now_img_position)));
					edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(now_img_position)));
				}else{
					btn_next.setClickable(false);
					Toast.makeText(Intersection.this, "��ǰ�Ѿ������һ��ͼƬ�ˣ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//��ʷ��¼
		btn_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				{
					if(controlValue == 1){
						imgPathList.clear();
						imgNameList.clear();
						imgLocation.clear();
						imgDescribe.clear();
						i_name_map.clear();
//						i_distance_map.clear();
						i_distance_start_map.clear();
						i_distance_end_map.clear();
						edt_describe.setText("");
						edt_intersection_name.setText("");
//						edt_distance.setText("");
						edt_distance_forstart.setText("");
						edt_distance_forend.setText("");
						txt_now_page.setText("��" + now_img_position + "��");
						txt_all_page.setText("��" + imgPathList.size() + "��");
						txt_imgname.setText("--ͼƬ����--");
						btn_prev.setClickable(true);
						btn_next.setClickable(true);
						img_photo.setBackgroundDrawable(null);
						controlValue = 0;
						return;
					}
//						if(imgPathList.size() > 0 || check_list != null){
						//if( check_list != null){
//							AlertDialog.Builder ab = new AlertDialog.Builder(Intersection.this);
//							ab.setTitle("��ܰ��ʾ");
//							ab.setMessage("������δ���棬�Ƿ񱣴棿");
//							ab.setPositiveButton("����", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									backMessageDialog.dismiss();
//									//����
//									if(check_list != null){
//										for(int i = 0 ; i < check_list.size() ; i++){
//											Road_Check_Data_Table d_item = check_list.get(i);
//											dbs.road_check_table.deleteDatas(d_item.Id + "");
//										}
//									}
//									Road_Check_Data_Table item = new Road_Check_Data_Table();
//									item.roundName = now_round_item.name;
//							    	item.roundId = now_round_item.roundId;
//							    	item.category = photoTypeValue;
//							    	item.direction = directionStr;
//							    	item.deteTime = as.relistic.common.Helper.getNowSystemTimeToSecond();
//							    	item.reportName = title;
//							    	if(imgPathList.size() > 0){
//							    		item.firstImageUrl = imgPathList.get(0);
//							    	}
//							    	int check_talbe_id = dbs.road_check_table.add(item);
//							    	
//							    	Image image_item;
//									for(int i =0 ; i < imgPathList.size(); i ++){
//										image_item = new Image();
//										image_item.roadcheckid = check_talbe_id + "";
//										image_item.imagepath = imgPathList.get(i);
//										image_item.imagename = imgNameList.get(imgPathList.get(i));
//										image_item.imagedescribe = imgDescribe.get(imgPathList.get(i));
//										image_item.i_distance = i_distance_map.get(imgPathList.get(i));
//										image_item.i_name = i_name_map.get(imgPathList.get(i));
//										image_item.i_start_from_start = i_distance_start_map.get(imgPathList.get(i));
//										image_item.i_end_from_start = i_distance_end_map.get(imgPathList.get(i));
//										dbs.image.add(image_item);
//									}
//									//ɾ����ȥ����ͼƬ
//									for(int i = 0 ; i < deleteImgPathList.size() ; i++){
//										String deletimgpath = deleteImgPathList.get(i);
//										File file = new File(deletimgpath);
//										if(file.exists()){
//											file.delete();
//										}
//									}
//									txt_intersection_name.setVisibility(View.GONE);
//									now_img_position = 0;
//									txt_imgname.setText("--ͼƬ����--");
//									txt_now_page.setText("��0��");
//									txt_all_page.setText("��0��");
//									img_photo.setBackgroundDrawable(null);
//									edt_describe.setText("");
//									edt_intersection_name.setText("");
//									edt_distance.setText("");
//									edt_distance_forstart.setText("");
//									edt_distance_forend.setText("");
//									controlValue = 0;
//									check_list.clear();
//									imgPathList.clear();
//									imgNameList.clear();
//									imgDescribe.clear();
//									i_name_map.clear();
//									i_distance_map.clear();
//									i_distance_start_map.clear();
//									i_distance_end_map.clear();
//									btn_photo.setEnabled(true);
//								}
//							});
//							ab.setNegativeButton("������", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									backMessageDialog.dismiss();
//									txt_intersection_name.setVisibility(View.GONE);
//									now_img_position = 0;
//									txt_imgname.setText("--ͼƬ����--");
//									txt_now_page.setText("��0��");
//									txt_all_page.setText("��0��");
//									img_photo.setBackgroundDrawable(null);
//									edt_describe.setText("");
//									edt_intersection_name.setText("");
//									edt_distance.setText("");
//									edt_distance_forstart.setText("");
//									edt_distance_forend.setText("");
//									controlValue = 0;
//									check_list.clear();
//									imgPathList.clear();
//									imgNameList.clear();
//									imgDescribe.clear();
//									i_name_map.clear();
//									i_distance_map.clear();
//									i_distance_start_map.clear();
//									i_distance_end_map.clear();
//									btn_photo.setEnabled(true);
//								}
//							});
//							
//							backMessageDialog = ab.create();
//							backMessageDialog.show();
////						}
//						return;

					if(check_list == null){
						check_list = new ArrayList<Road_Check_Data_Table>();
					}else{
						check_list.clear();
					}
					check_list.addAll(dbs.road_check_table.getList(now_round_item.roundId, directionStr, photoTypeValue));
					if(check_list.size() == 0){
						return;
					}
					
					imgPathList.clear();
					imgNameList.clear();
					imgDescribe.clear();
					imgLocation.clear();
					i_name_map.clear();
//					i_distance_map.clear();
					i_distance_start_map.clear();
					i_distance_end_map.clear();
					for(int i = 0 ; i < check_list.size() ; i++){
						Road_Check_Data_Table item = check_list.get(i);
						imgPathList.addAll(dbs.image.getPathList(item.Id));
						for(int j =0 ; j < imgPathList.size() ; j++){
							imgNameList.put(imgPathList.get(j),dbs.image.getItemName(imgPathList.get(j)));
							imgDescribe.put(imgPathList.get(j),dbs.image.getItemDescribe(imgPathList.get(j)));
							imgLocation.put(imgPathList.get(j),dbs.image.getItemLocation(imgPathList.get(j)));
							i_name_map.put(imgPathList.get(j), dbs.image.getItemI_Name(imgPathList.get(j)));
//							i_distance_map.put(imgPathList.get(j),dbs.image.getItemI_Distance(imgPathList.get(j)));
							i_distance_start_map.put(imgPathList.get(j),dbs.image.getItemI_Start_From_Start(imgPathList.get(j)));
							i_distance_end_map.put(imgPathList.get(j),dbs.image.getItemI_End_From_Start(imgPathList.get(j)));
						}
						if(i == 0){
							edt_intersection_name.setText(i_name_map.get(imgPathList.get(0)));
//							edt_distance.setText(i_distance_map.get(imgPathList.get(0)));
							edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(0)));
							edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(0)));
						}
					}
					
					if(imgPathList.size() > 0){
						img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(now_img_position)));
					}
					
					//ͼƬ����
					now_img_position = 0;
					if(imgNameList.size() > 0){
						txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
						txt_now_page.setText("��" + (now_img_position + 1) + "��");
						txt_all_page.setText("��" + imgPathList.size() + "��");
						if(imgDescribe.size() > 0){
							edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));	
						}
					}else{
						txt_imgname.setText("--ͼƬ����--");
						txt_now_page.setText("��0��");
						txt_all_page.setText("��0��");
					}
					
					
					controlValue = 1;
					btn_photo.setEnabled(false);
					txt_intersection_name.setVisibility(View.VISIBLE);
				}
			}
		
		});
		//�����ı�����

		edt_describe.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(imgPathList.size() > 0 && count > 0){
					imgDescribe.put(imgPathList.get(now_img_position), edt_describe.getText().toString());
				}
			}
		});
		//·�����Ƽ���
		edt_intersection_name.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				txt_intersection_name.setText(edt_intersection_name.getText().toString());
				if(imgPathList.size() > 0 && count > 0){
					i_name_map.put(imgPathList.get(now_img_position), edt_intersection_name.getText().toString());
				}
			}
		});
		//����������
//		edt_distance.addTextChangedListener(new TextWatcher(){
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				txt_intersection_name.setText(edt_intersection_name.getText().toString());
//				if(imgPathList.size() > 0 && count > 0){
//					i_distance_map.put(imgPathList.get(now_img_position), edt_distance.getText().toString());
//				}
//			}
//		});
		//·����㵽������
		edt_distance_forstart.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				txt_intersection_name.setText(edt_intersection_name.getText().toString());
				if(imgPathList.size() > 0 && count > 0){
					i_distance_start_map.put(imgPathList.get(now_img_position), edt_distance_forstart.getText().toString());
				}
			}
		});
		//·���յ㵽������
		edt_distance_forend.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				txt_intersection_name.setText(edt_intersection_name.getText().toString());
				if(imgPathList.size() > 0 && count > 0){
					i_distance_end_map.put(imgPathList.get(now_img_position), edt_distance_forend.getText().toString());
				}
			}
		});
		
		//�Ŵ�ͼƬ
		img_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(imgPathList.size() <= 0){
					return;
				}
				LayoutInflater inflater = Intersection.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(Intersection.this, imgPathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(now_img_position);
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						AlertDialog.Builder ab = new AlertDialog.Builder(Intersection.this);
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫɾ��ͼƬ��" + imgNameList.get(imgPathList.get(position)) + "��");
						ab.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
//								File file = new File(imgPathList.get(position));
//								if(file.exists()){
//									file.delete();
//								}
								deleteImgPathList.add(imgPathList.get(position));
								imgLocation.remove(imgPathList.get(position));
								imgDescribe.remove(imgPathList.get(position));
								i_name_map.remove(imgPathList.get(position));
//								i_distance_map.remove(imgPathList.get(position));
								i_distance_start_map.remove(imgPathList.get(position));
								i_distance_end_map.remove(imgPathList.get(position));
								imgNameList.remove(imgPathList.get(position));
								imgPathList.remove(position);
								rBigImgAdapter.notifyDataSetChanged();
								
								if(position < imgPathList.size()){
									now_img_position = position;
									img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(position)));
									txt_now_page.setText("��" + (position + 1)+ "��");
									txt_all_page.setText("��" + imgPathList.size() + "��");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position)));
									edt_intersection_name.setText(i_name_map.get(imgPathList.get(position)));
//									edt_distance.setText(i_distance_map.get(imgPathList.get(position)));
									edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(position)));
									edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(position)));
								}else if(imgPathList.size() > 0){
									now_img_position = position - 1;
									img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(position -1)));
									txt_now_page.setText("��" + (position)+ "��");
									txt_all_page.setText("��" + imgPathList.size() + "��");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position - 1)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position -1)));
									edt_intersection_name.setText(i_name_map.get(imgPathList.get(position -1)));
//									edt_distance.setText(i_distance_map.get(imgPathList.get(position -1)));
									edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(position -1)));
									edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(position -1)));
								}else{
									now_img_position = 0;
									img_photo.setBackgroundDrawable(null);
									txt_now_page.setText("��0��");
									txt_all_page.setText("��0��");
									txt_imgname.setText("--ͼƬ����--");
									edt_describe.setText("");
									pw_bigimg.dismiss();
								}
								if(imgPathList.size() <= 0){
									deleteImg_dialog.dismiss();
//									edt_distance.setText("");
									edt_intersection_name.setText("");
									edt_distance_forstart.setText("");
									edt_distance_forend.setText("");
									
								}
//								for(int i = 0 ; i < check_list.size() ; i++){
//									Road_Check_Data_Table item = check_list.get(i);
//									if(item.imagePathString.equals(imgPathList.get(now_img_position))){
//										edt_intersection_name.setText(i_name_map.get());
//										edt_distance.setText(item.intersection_length_from_start);
//										edt_distance_forstart.setText(item.intersectionstart_length_from_start);
//										edt_distance_forend.setText(item.intersectionend_length_from_start);
//										break;
//									}
//								}
								deleteImg_dialog.dismiss();
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
				pw_bigimg.showAtLocation(Intersection.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
		
		//ͼƬ����
		txt_now_page.setText("��" + now_img_position + "��");
		txt_all_page.setText("��" + imgPathList.size() + "��");
		txt_imgname.setText("--ͼƬ����--");
		
	}
	/**ͼƬ��ʽת��*/
	public static Drawable loadImageFromPath(String imagePath) {
    	try{
    		FileInputStream fis = new FileInputStream(imagePath);
    		Bitmap bitmap = BitmapFactory.decodeStream(fis);
    		BitmapDrawable bd = new BitmapDrawable(bitmap); 
    		fis.close();
    		return bd;
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**���ఴť����¼�*/
    public class PhotoImgButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Time time = new Time("GMT+8");       
	        time.setToNow();      
	        int year = time.year;      
	        int month = time.month+1;      
	        int day = time.monthDay;      
	        int minute = time.minute;      
	        int hour = time.hour;      
	        int sec = time.second;      
	       
			String imgfilename = "";
			if(now_round_item.street.equals("")){
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.roundId + 
				"_" + now_round_item.name + "_" + directionStr + "_" + photoType + "_"+ year + "" + month + "" + 
				day + "" + hour + "" + minute + "" + sec + ".jpg";
			}else{
				imgfilename = now_round_item.area + "_" + now_round_item.type + "_" + now_round_item.street + "_" + 
				now_round_item.roundId + "_" + now_round_item.name + "_" + directionStr + "_" + photoType + "_" + 
				year + "" + month + "" + day + "" + hour + "" + minute + "" + sec + ".jpg";
			}
				
			Uri uri = null;
		    String savaPath = "com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr + "/" + photoType;
			try {
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type);
				new com.road.check.common.FileUtil().createDIR("com_pengtu_road_check_data" + "/" + now_round_item.area + "/" + now_round_item.type + "/" + now_round_item.roundId + now_round_item.name + directionStr);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgfilename, savaPath));
				nowimPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + savaPath + File.separator + imgfilename;
				nowimName = imgfilename;
				
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				Intersection.this.startActivityForResult(intent, ROADPOINTPHOTO_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	/**ͷ����߰�ť����¼�:����*/
	class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {//backMessageDialog
			if(imgPathList.size() > 0 ){
				AlertDialog.Builder ab = new AlertDialog.Builder(Intersection.this);
				ab.setTitle("��ܰ��ʾ");
				ab.setMessage("������δ���棬�Ƿ񱣴棿");
				ab.setPositiveButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backMessageDialog.dismiss();
						savaDatasTask = new SavaDatasTask();
						savaDatasTask.execute();
						Intersection.this.finish();
					}
				});
				ab.setNegativeButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backMessageDialog.dismiss();
						Intersection.this.finish();
					}
				});
				
				backMessageDialog = ab.create();
				backMessageDialog.show();
			}else{
				Intersection.this.finish();
			}
		}
	}
	/**ͷ���ұ߰�ť����¼�:����*/
	class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (imgPathList.size()==0) {
				Toast.makeText(Intersection.this, "δ�������գ������պ���д���ݣ�֮���ٱ���", 0).show();
			}else {
				savaDatasTask = new SavaDatasTask();
				savaDatasTask.execute();
			}

		}
	}
	
	/**����󷵻�*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ROADPOINTPHOTO_TAKEPHOTO:
				if (resultCode == Activity.RESULT_OK) {
					settingImag(nowimPath,nowimName);
					if(imgPathList.size() > 0){
						img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(0)));
						txt_imgname.setText(imgNameList.get(imgPathList.get(0)));
						edt_describe.setText(imgDescribe.get(imgPathList.get(0)));
						edt_intersection_name.setText(i_name_map.get(imgPathList.get(0)));
//						edt_distance.setText(i_distance_map.get(imgPathList.get(0)));
						edt_distance_forstart.setText(i_distance_start_map.get(imgPathList.get(0)));
						edt_distance_forend.setText(i_distance_end_map.get(imgPathList.get(0)));
						
						i_name_map.put(imgPathList.get(imgPathList.size() -1),edt_intersection_name.getText().toString());
//						i_distance_map.put(imgPathList.get(imgPathList.size() -1), edt_distance.getText().toString());
						i_distance_start_map.put(imgPathList.get(imgPathList.size() -1), edt_distance_forstart.getText().toString());
						i_distance_end_map.put(imgPathList.get(imgPathList.size() -1), edt_distance_forend.getText().toString());
					}
					
					if(imgPathList.size() >= 2){
						btn_prev.setClickable(true);
						btn_next.setClickable(true);
					}else{
						btn_prev.setClickable(false);
						btn_next.setClickable(false);
					}
					
					txt_now_page.setText("��1��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					
				}
				break;
			default:
				break;
		}
	}
	
	private void settingImag(String imgPath,String nowimName){
//		int maxWidth=160,maxHeight=160;  //������ͼƬ�Ĵ�С  
//	    BitmapFactory.Options options = new BitmapFactory.Options();    
//	    options.inJustDecodeBounds = true; 
//	    Bitmap image = BitmapFactory.decodeFile(imgPath, options);    
//	    double ratio = 1D;    
//	    if (maxWidth > 0 && maxHeight <= 0) {
//	        // �޶���ȣ��߶Ȳ�������    
//	        ratio = Math.ceil(options.outWidth / maxWidth);    
//	    } else if (maxHeight > 0 && maxWidth <= 0) {
//	        // �޶��߶ȣ������ƿ��    
//	        ratio = Math.ceil(options.outHeight / maxHeight);    
//	    } else if (maxWidth > 0 && maxHeight > 0) { 
//	    	
//	        // �߶ȺͿ�ȶ��������ƣ���ʱ�����Ǽ�������������������ɵ�����ͼƬ�ߴ磬����ʹͼƬ����    
//	        double _widthRatio = Math.ceil(options.outWidth / maxWidth);    
//	        double _heightRatio = (double) Math.ceil(options.outHeight / maxHeight);    
//	        ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;    
//	    }    
//	    if (ratio > 1){
//	        options.inSampleSize = (int) ratio;  
//	    	options.inJustDecodeBounds = false;    
//	    	options.inPreferredConfig = Bitmap.Config.RGB_565;
//	    	image = BitmapFactory.decodeFile(imgPath, options);  
//	    	//������sdCard  
//	    	File file = new File(imgPath);
//	    	
//	    	//��ȡͼƬ����ת�Ƕȣ���Щϵͳ�����յ�ͼƬ��ת�ˣ��е�û����ת 
//	    	int degree = ImageSpin.readPictureDegree(file.getAbsolutePath());
//	    	//��תͼƬ
//	    	image = ImageSpin.rotaingImageView(degree,image);
//	    	
//	    	imgPathList.add(imgPath);
//	    	imgNameList.put(imgPath,nowimName);
//	    	try {    
//	    		FileOutputStream out = new FileOutputStream(file);    
//	    		if(image.compress(Bitmap.CompressFormat.JPEG, 100, out)){ 
//	               out.flush();    
//	               out.close();    
//	    		}  
//	    	} catch (Exception e) {   
//	    		e.printStackTrace();  
//	    	}finally{  
//	    	}
//	    }
		int maxWidth = 800, maxHeight = 600; // ������ͼƬ�Ĵ�С,ԭ��160
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
			double _heightRatio = (double) Math.ceil(options.outHeight
					/ maxHeight);
			ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;
		}
		Log.w("ratio", imgPath + "--" + nowimName + "--" + ratio);
		if (ratio > 1) {
			String[] name;
			options.inSampleSize = (int) ratio;
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			image = BitmapFactory.decodeFile(imgPath, options);
			// ������sdCard
			File file = new File(imgPath);

			// ��ȡͼƬ����ת�Ƕȣ���Щϵͳ�����յ�ͼƬ��ת�ˣ��е�û����ת
			int degree = ImageSpin.readPictureDegree(file.getAbsolutePath());
			// ��תͼƬ
			image = ImageSpin.rotaingImageView(degree, image);
			//��ˮӡ
//			int w = 180, h = 40;
//			Bitmap wm = Bitmap.createBitmap(w, h, Config.RGB_565);
//			Canvas cv = new Canvas(wm);
//			cv.drawColor(Color.TRANSPARENT);
//			Paint p = new Paint();
//			String familyName = "����";
//			p.setTextSize(15);
//			cv.drawText(name[0], 0, 10, p);
//			cv.drawText(name[1], 0, 25, p);
//			cv.save();
//			cv.restore();
//			Time time = new Time("GMT-8");
//			String markname = "";
//			time.setToNow();
//			int year = time.year;
//			int month = time.month +1;
//			int day = time.monthDay;
//			if(photoTypeValue==1||photoTypeValue==5||photoTypeValue==6||photoTypeValue==7){
//			markname =  now_round_item.name + ""
//					+ photoType + ""+ year + "_" + month + "_" + day 
//					;
//			}else{
//			markname =  now_round_item.name + directionStr + "_"
//			+ photoType + ""+ year + "_" + month + "_" + day 
//			;}
			
//			image = createBitmap(image, markname);
			
			Log.d("�����Ժ��+++++", cApp.lat+"_"+cApp.lng);
			imgPathList.add(imgPath);
			imgNameList.put(imgPath, nowimName);
			imgLocation.put(imgPath, cApp.lat+"_"+cApp.lng);//��ʱ����ͼƬλ����Ϣ
			try {
				FileOutputStream out = new FileOutputStream(file);
				if (image.compress(Bitmap.CompressFormat.JPEG, 85, out)) {
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}
    /**��������*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
			if(check_list != null){
				for(int i = 0 ; i < check_list.size() ; i++){
					Road_Check_Data_Table d_item = check_list.get(i);
					dbs.road_check_table.deleteDatas(d_item.Id + "");
				}
			}
			Road_Check_Data_Table item = new Road_Check_Data_Table();
			item.roundName = now_round_item.name;
	    	item.roundId = now_round_item.roundId;
	    	item.category = photoTypeValue;
	    	item.direction = directionStr;
	    	item.deteTime = as.relistic.common.Helper.getNowSystemTimeToSecond();
	    	item.reportName = title;
	    	if(imgPathList.size() > 0){
	    		item.firstImageUrl = imgPathList.get(0);
	    	}
	    	int check_talbe_id = dbs.road_check_table.add(item);
	    	
	    	Image image_item;
			for(int i =0 ; i < imgPathList.size(); i ++){
				image_item = new Image();
				image_item.roadcheckid = check_talbe_id + "";
				image_item.imagepath = imgPathList.get(i);
				image_item.imagename = imgNameList.get(imgPathList.get(i));
				image_item.imagedescribe = imgDescribe.get(imgPathList.get(i));
				image_item.imglocation = imgLocation.get(imgPathList.get(i));
				image_item.i_distance = i_distance_start_map.get(imgPathList.get(i));
				image_item.i_name = i_name_map.get(imgPathList.get(i));
				image_item.i_start_from_start = i_distance_start_map.get(imgPathList.get(i));
				image_item.i_end_from_start = i_distance_end_map.get(imgPathList.get(i));
				dbs.image.add(image_item);
			}
			
			//ɾ����ȥ����ͼƬ
			for(int i = 0 ; i < deleteImgPathList.size() ; i++){
				String deletimgpath = deleteImgPathList.get(i);
				File file = new File(deletimgpath);
				if(file.exists()){
					file.delete();
				}
			}
			
//	    	//��ȡͼƬ·����ͼƬ���ơ�
//	    	String imagePathString = "";
//	    	String fistPathString = "";
//	    	for(int i = 0 ; i < imgPathList.size() ; i++){
//	    		if(i > 0){
//	    			imagePathString += ",";
//	    		}else{
//	    			fistPathString = imgPathList.get(i);
//	    		}
//	    		imagePathString += imgPathList.get(i);
//	    	}
//	    	
//	    	Road_Check_Data_Table item = new Road_Check_Data_Table();
//	    	item.Id = as.relistic.common.Helper.getTimeStampEstimator();
//	    	item.roundName = now_round_item.name;
//	    	item.roundId = now_round_item.id;
//	    	item.category = 1;
//	    	item.direction = directionStr;
//	    	item.imagePathString = imagePathString;
//	    	item.firstImageUrl = fistPathString;
//	    	item.deteTime = as.relistic.common.Helper.getNowSystemTimeToSecond();
//	    	dbs.road_check_table.add(item);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(controlValue == 0){
				now_img_position = 0;
				imgPathList.clear();
				imgNameList.clear();
				imgDescribe.clear();
				i_name_map.clear();
//				i_distance_map.clear();
				i_distance_start_map.clear();
				i_distance_end_map.clear();
				edt_describe.setText("");
				edt_intersection_name.setText("");
//				edt_distance.setText("");
				edt_distance_forstart.setText("");
				edt_distance_forend.setText("");
				txt_now_page.setText("��" + now_img_position + "��");
				txt_all_page.setText("��" + imgPathList.size() + "��");
				txt_imgname.setText("--ͼƬ����--");
				btn_prev.setClickable(true);
				btn_next.setClickable(true);
				img_photo.setBackgroundDrawable(null);
				Intersection.this.finish();
			}
			Toast.makeText(Intersection.this, "����ɹ���", Toast.LENGTH_SHORT).show();
		}
    }
}
