package com.road.check.report.update;

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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.road.check.R;
import com.road.check.app.functionmodule.ImageSpin;
import com.road.check.app.functionmodule.RBigImgAdapter;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.sqlite.DatabaseService;

public class RoadPointPhoto extends ActivityBase{
	private Header header;
	private ImageView img_photo;
	private TextView txt_now_page;
	private TextView txt_all_page;
	private TextView txt_imgname;
	private Button btn_prev;
	private Button btn_next;
	private EditText edt_describe;
	
	private String id = "";
	private String title = "";
	private String nowimPath = "";
	private String nowimName = "";
	private Road_Check_Data_Table now_check_item;
	private Round now_round_item;
	private DatabaseService dbs;
	//ͼƬ��������
	private int now_img_position = 0;
	
	//����
	private List<String> imgPathList;
	private HashMap<String,String> imgNameList;
	private HashMap<String,String> imgDescribe;
	private final int ROADPOINTPHOTO_TAKEPHOTO = 10;
	
	private SavaDatasTask savaDatasTask;
	private AlertDialog backMessageDialog;
	
	//ͼƬ�Ŵ�
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private List<String> deleteImgPathList;
	//ͼƬɾ��
	private AlertDialog deleteImg_dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_roadpointphoto);
		
		try{
			id = this.getIntent().getExtras().getString("id");
			title = this.getIntent().getExtras().getString("title");
		}catch(Exception ex){
		}
		
		//��ȡ��ǰ����
		dbs = new DatabaseService(this);
		now_check_item = dbs.road_check_table.getItem(id);
		
		//ͷ������
		header = new Header(this, title,
				"����",new HeaderLeftOnClickListener(),
				"����",new HeaderRightOnClickListener());
		
		img_photo = (ImageView)this.findViewById(R.id.img_photo);
		txt_now_page = (TextView)this.findViewById(R.id.txt_now_page);
		txt_all_page = (TextView)this.findViewById(R.id.txt_all_page);
		txt_imgname = (TextView)this.findViewById(R.id.txt_imgname);
		btn_prev = (Button)this.findViewById(R.id.btn_prev);
		btn_next = (Button)this.findViewById(R.id.btn_next);
		edt_describe = (EditText)this.findViewById(R.id.edt_describe);
    	
    	//��ȡ��ǰ��/�ŶԶ�������
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);
		
		//ͼƬ���ϳ�ʼ��
		imgPathList = new ArrayList<String>();
		imgNameList = new HashMap<String, String>();
		imgDescribe = new HashMap<String, String>();
		deleteImgPathList = new ArrayList<String>();
		
		imgPathList.addAll(dbs.image.getPathList(now_check_item.Id));
		for(int i = 0 ; i < imgPathList.size() ; i++){
			imgNameList.put(imgPathList.get(i),dbs.image.getItemName(imgPathList.get(i)));
			imgDescribe.put(imgPathList.get(i),dbs.image.getItemDescribe(imgPathList.get(i)));
		}
		img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(0)));
		txt_now_page.setText("��" + (now_img_position + 1)+ "��");
		txt_all_page.setText("��" + imgPathList.size() + "��");
		txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
		edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
		
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
				}else{
					btn_prev.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "��ǰ�Ѿ��ǵ�һ��ͼƬ�ˣ�", Toast.LENGTH_SHORT).show();
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
				}else{
					btn_next.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "��ǰ�Ѿ������һ��ͼƬ�ˣ�", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//�����ı�����
		edt_describe.addTextChangedListener(new TextWatcher(){

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
		//�Ŵ�ͼƬ
		img_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(imgPathList.size() <= 0){
					return;
				}
				LayoutInflater inflater = RoadPointPhoto.this.getLayoutInflater();
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
				
				rBigImgAdapter = new RBigImgAdapter(RoadPointPhoto.this, imgPathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(now_img_position);
				gallery_bigimg.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						AlertDialog.Builder ab = new AlertDialog.Builder(RoadPointPhoto.this);
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫɾ��ͼƬ��" + imgNameList.get(imgPathList.get(position)) + "��");
						ab.setPositiveButton("ɾ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								deleteImgPathList.add(imgPathList.get(position));
								imgPathList.remove(position);
								imgDescribe.remove(imgPathList.get(position));
								imgNameList.remove(imgPathList.get(position));
								rBigImgAdapter.notifyDataSetChanged();
								
								if(position < imgPathList.size()){
									now_img_position = position;
									img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(position)));
									txt_now_page.setText("��" + (position + 1)+ "��");
									txt_all_page.setText("��" + imgPathList.size() + "��");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position)));
								}else if(imgPathList.size() > 0){
									now_img_position = position - 1;
									img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(position - 1)));
									txt_now_page.setText("��" + (position)+ "��");
									txt_all_page.setText("��" + imgPathList.size() + "��");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position - 1)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position - 1)));
								}else{
									now_img_position = 0;
									img_photo.setBackgroundDrawable(null);
									txt_now_page.setText("��0��");
									txt_all_page.setText("��0��");
									txt_imgname.setText("--ͼƬ����--");
									edt_describe.setText("");
									pw_bigimg.dismiss();
								}
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
						return false;
					}
				});
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						AlertDialog.Builder ab = new AlertDialog.Builder(RoadPointPhoto.this);
						ab.setTitle("��ܰ��ʾ");
						ab.setMessage("��ȷ��Ҫ�޸�ͼƬ��" + imgNameList.get(imgPathList.get(position)) + "��");
						ab.setPositiveButton("�޸�", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								File file = new File(imgPathList.get(position));
								if(file.exists()){
									file.delete();
								}
								
								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
								Uri uri = null;
								try {
									uri = Uri.fromFile(new com.road.check.common.FileUtil().createFileInSDCard(imgPathList.get(position)));
									nowimPath = imgPathList.get(position);
									nowimName = imgNameList.get(imgPathList.get(position));
									imgPathList.remove(position);
									imgNameList.remove(imgPathList.get(position));
									rBigImgAdapter.notifyDataSetChanged();
									intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
									RoadPointPhoto.this.startActivityForResult(intent, ROADPOINTPHOTO_TAKEPHOTO);
								} catch (Exception e) {
									e.printStackTrace();
									Log.w("path00:", e.getMessage());
								}
								deleteImg_dialog.dismiss();
								pw_bigimg.dismiss();
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
				pw_bigimg.showAtLocation(RoadPointPhoto.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
		
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
	/**ͷ����߰�ť����¼�:����*/
	class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {//backMessageDialog
			if(imgPathList.size() > 0){
				AlertDialog.Builder ab = new AlertDialog.Builder(RoadPointPhoto.this);
				ab.setTitle("��ܰ��ʾ");
				ab.setMessage("������δ���棬�Ƿ񱣴棿");
				ab.setPositiveButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backMessageDialog.dismiss();
						savaDatasTask = new SavaDatasTask();
						savaDatasTask.execute();
						Intent intent  = new Intent(RoadPointPhoto.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
						Bundle bundle  = new Bundle();
						bundle.putString("id", id);
						intent.putExtras(bundle);
						RoadPointPhoto.this.startActivity(intent);
						RoadPointPhoto.this.finish();
					}
				});
				ab.setNegativeButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backMessageDialog.dismiss();
						Intent intent  = new Intent(RoadPointPhoto.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
						Bundle bundle  = new Bundle();
						bundle.putString("id", id);
						intent.putExtras(bundle);
						RoadPointPhoto.this.startActivity(intent);
						RoadPointPhoto.this.finish();
					}
				});
				
				backMessageDialog = ab.create();
				backMessageDialog.show();
			}else{
				RoadPointPhoto.this.finish();
			}
		}
	}
	/**ͷ���ұ߰�ť����¼�:����*/
	class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
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
		int maxWidth=160,maxHeight=160;  //������ͼƬ�Ĵ�С  
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
	    	
	    	imgPathList.add(imgPath);
	    	imgNameList.put(imgPath,nowimName);
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
    /**��������*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
			Image image_item;
			dbs.image.deleteDatas(now_check_item.Id + "");
	    	for(int i = 0 ; i < imgPathList.size() ; i++){
	    		image_item = new Image();
	    		image_item.roadcheckid = now_check_item.Id + "";
	    		image_item.imagepath = imgPathList.get(i);
	    		image_item.imagename = imgNameList.get(imgPathList.get(i));
	    		image_item.imagedescribe = imgDescribe.get(imgPathList.get(i));
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
			
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(RoadPointPhoto.this, "����ɹ���", Toast.LENGTH_SHORT).show();
			
			Intent intent  = new Intent(RoadPointPhoto.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", id);
			intent.putExtras(bundle);
			RoadPointPhoto.this.startActivity(intent);
			RoadPointPhoto.this.finish();
		}
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  
			Intent intent  = new Intent(RoadPointPhoto.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", id);
			intent.putExtras(bundle);
			RoadPointPhoto.this.startActivity(intent);
			RoadPointPhoto.this.finish();
			return true;
		}  
		return false;
	}
}
