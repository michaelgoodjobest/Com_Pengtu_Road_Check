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
	//图片张数索引
	private int now_img_position = 0;
	
	//照相
	private List<String> imgPathList;
	private HashMap<String,String> imgNameList;
	private HashMap<String,String> imgDescribe;
	private final int ROADPOINTPHOTO_TAKEPHOTO = 10;
	
	private SavaDatasTask savaDatasTask;
	private AlertDialog backMessageDialog;
	
	//图片放大
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	private List<String> deleteImgPathList;
	//图片删除
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
		
		//获取当前对象
		dbs = new DatabaseService(this);
		now_check_item = dbs.road_check_table.getItem(id);
		
		//头部加载
		header = new Header(this, title,
				"返回",new HeaderLeftOnClickListener(),
				"保存",new HeaderRightOnClickListener());
		
		img_photo = (ImageView)this.findViewById(R.id.img_photo);
		txt_now_page = (TextView)this.findViewById(R.id.txt_now_page);
		txt_all_page = (TextView)this.findViewById(R.id.txt_all_page);
		txt_imgname = (TextView)this.findViewById(R.id.txt_imgname);
		btn_prev = (Button)this.findViewById(R.id.btn_prev);
		btn_next = (Button)this.findViewById(R.id.btn_next);
		edt_describe = (EditText)this.findViewById(R.id.edt_describe);
    	
    	//获取当前道/桥对对象及名称
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);
		
		//图片集合初始化
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
		txt_now_page.setText("第" + (now_img_position + 1)+ "张");
		txt_all_page.setText("共" + imgPathList.size() + "张");
		txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
		edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
		
		//上一张
		btn_prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(now_img_position > 0){
					btn_next.setClickable(true);
					now_img_position -- ;
					img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(now_img_position)));
					txt_now_page.setText("第" + (now_img_position + 1)+ "张");
					txt_all_page.setText("共" + imgPathList.size() + "张");
					txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
				}else{
					btn_prev.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "当前已经是第一张图片了！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//下一张
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(now_img_position < imgPathList.size() - 1){
					btn_prev.setClickable(true);
					now_img_position ++ ;
					img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(now_img_position)));
					txt_now_page.setText("第" + (now_img_position + 1)+ "张");
					txt_all_page.setText("共" + imgPathList.size() + "张");
					txt_imgname.setText(imgNameList.get(imgPathList.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList.get(now_img_position)));
				}else{
					btn_next.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "当前已经是最后一张图片了！", Toast.LENGTH_SHORT).show();
				}
			}
		});
		//描述文本监听
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
		//放大图片
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
				
				//关闭弹出框
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
						ab.setTitle("温馨提示");
						ab.setMessage("您确定要删除图片：" + imgNameList.get(imgPathList.get(position)) + "吗？");
						ab.setPositiveButton("删除", new DialogInterface.OnClickListener() {
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
									txt_now_page.setText("第" + (position + 1)+ "张");
									txt_all_page.setText("共" + imgPathList.size() + "张");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position)));
								}else if(imgPathList.size() > 0){
									now_img_position = position - 1;
									img_photo.setBackgroundDrawable(loadImageFromPath(imgPathList.get(position - 1)));
									txt_now_page.setText("第" + (position)+ "张");
									txt_all_page.setText("共" + imgPathList.size() + "张");
									txt_imgname.setText(imgNameList.get(imgPathList.get(position - 1)));
									edt_describe.setText(imgDescribe.get(imgPathList.get(position - 1)));
								}else{
									now_img_position = 0;
									img_photo.setBackgroundDrawable(null);
									txt_now_page.setText("第0张");
									txt_all_page.setText("共0张");
									txt_imgname.setText("--图片名称--");
									edt_describe.setText("");
									pw_bigimg.dismiss();
								}
								deleteImg_dialog.dismiss();
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
				gallery_bigimg.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int position, long arg3) {
						AlertDialog.Builder ab = new AlertDialog.Builder(RoadPointPhoto.this);
						ab.setTitle("温馨提示");
						ab.setMessage("您确定要修改图片：" + imgNameList.get(imgPathList.get(position)) + "吗？");
						ab.setPositiveButton("修改", new DialogInterface.OnClickListener() {
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
				pw_bigimg.setAnimationStyle(R.style.AnimationBigImg);
				pw_bigimg.showAtLocation(RoadPointPhoto.this.getWindow().findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER, 0, 0);
			}
		});
		
	}
	/**图片格式转换*/
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
	/**头部左边按钮点击事件:返回*/
	class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {//backMessageDialog
			if(imgPathList.size() > 0){
				AlertDialog.Builder ab = new AlertDialog.Builder(RoadPointPhoto.this);
				ab.setTitle("温馨提示");
				ab.setMessage("数据尚未保存，是否保存？");
				ab.setPositiveButton("保存", new DialogInterface.OnClickListener() {
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
				ab.setNegativeButton("返回", new DialogInterface.OnClickListener() {
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
	/**头部右边按钮点击事件:保存*/
	class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
		}
	}
	
	/**照相后返回*/
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
					
					txt_now_page.setText("第1张");
					txt_all_page.setText("共" + imgPathList.size() + "张");
					
				}
				break;
			default:
				break;
		}
	}
	
	private void settingImag(String imgPath,String nowimName){
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
    /**保存任务*/
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
			
			//删掉已去掉的图片
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
			Toast.makeText(RoadPointPhoto.this, "保存成功！", Toast.LENGTH_SHORT).show();
			
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
