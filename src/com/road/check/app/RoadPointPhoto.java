package com.road.check.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.array;
import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import com.road.check.common.CheckApplication;
import com.road.check.common.Header;
import com.road.check.model.Image;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.model.VerifyDatas;
import com.road.check.navigation.Navigation;
import com.road.check.sqlite.DatabaseService;

public class RoadPointPhoto extends ActivityBase {
	private Header header;
	private ImageView img_photo;
	private TextView txt_now_page;
	private TextView txt_all_page;
	private TextView txt_imgname;
	private Button btn_photo;
	private Button btn_prev;
	private Button btn_next;
	private Button btn_record;
	private EditText edt_describe;
	
	private Dialog verify_dialog;
	private EditText edt_check_length;
	private EditText edt_footwalk_width_1;
	private EditText edt_footwalk_width_2;
	private EditText edt_footwalk_width_3;
	private EditText edt_footwalk_width_4;
	private EditText edt_footwalk_width_5;
	private EditText edt_footwalk_width_6;
	private EditText edt_footwalk_width_7;
	private EditText edt_footwalk_width_8;
	private EditText edt_footwalk_width_9;
	private EditText edt_footwalk_width_10;
	private List<EditText> footwalk_width_list;
	
	private CheckApplication application;
	
	private String title = "";
	private String photoType = "";
	private int photoTypeValue = -1;
	private String directionStr = "";
	private String nowimPath = "";
	private String nowimName = "";
	private Round now_round_item;
	private DatabaseService dbs;
	// ͼƬ��������
	private int now_img_position = 0;

	// ����
	private List<String> imgPathList;
	private HashMap<String, String> imgNameList;
	private HashMap<String, String> imgDescribe;
	private HashMap<String, String> imgLocation;
	private final int ROADPOINTPHOTO_TAKEPHOTO = 10;

	private SavaDatasTask savaDatasTask;
	private AlertDialog backMessageDialog;

	// ͼƬ�Ŵ�
	private PopupWindow pw_bigimg;
	private RBigImgAdapter rBigImgAdapter;
	// ͼƬɾ��
	private AlertDialog deleteImg_dialog;

	// ��ʷ��¼
	private List<Road_Check_Data_Table> check_list;
	private int controlValue = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_roadpointphoto);

		try {
			title = this.getIntent().getExtras().getString("title");
			photoType = this.getIntent().getExtras().getString("photoType");
			photoTypeValue = this.getIntent().getExtras()
					.getInt("photoTypeValue");
		} catch (Exception ex) {
		}

		// ͷ������
		header = new Header(this, title, "����", new HeaderLeftOnClickListener(),
				"����", new HeaderRightOnClickListener());

		img_photo = (ImageView) this.findViewById(R.id.img_photo);
		txt_now_page = (TextView) this.findViewById(R.id.txt_now_page);
		txt_all_page = (TextView) this.findViewById(R.id.txt_all_page);
		txt_imgname = (TextView) this.findViewById(R.id.txt_imgname);
		btn_photo = (Button) this.findViewById(R.id.btn_photo);
		btn_prev = (Button) this.findViewById(R.id.btn_prev);
		btn_next = (Button) this.findViewById(R.id.btn_next);
		btn_record = (Button) this.findViewById(R.id.btn_record);
		edt_describe = (EditText) this.findViewById(R.id.edt_describe);

		dbs = new DatabaseService(this);

		// �ж�������
		if (cApp.Direction == 1) {
			directionStr = "����";
		} else if (cApp.Direction == 0) {
			directionStr = "����";
		}

		// ��ȡ��ǰ��/�ŶԶ�������
		now_round_item = new Round();
		now_round_item = dbs.round.getItem(cApp.RoundId);

		// ͼƬ���ϳ�ʼ��
		imgPathList = new ArrayList<String>();
		imgNameList = new HashMap<String, String>();
		imgDescribe = new HashMap<String, String>();
		imgLocation = new HashMap<String, String>();

		// ����
		btn_photo.setOnClickListener(new PhotoImgButtonOnClickListener());
		// ��һ��
		btn_prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (now_img_position > 0) {
					btn_next.setClickable(true);
					now_img_position--;
					img_photo
							.setBackgroundDrawable(loadImageFromPath(imgPathList
									.get(now_img_position)));
					txt_now_page.setText("��" + (now_img_position + 1) + "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					txt_imgname.setText(imgNameList.get(imgPathList
							.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList
							.get(now_img_position)));
				} else {
					btn_prev.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "��ǰ�Ѿ��ǵ�һ��ͼƬ�ˣ�",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// ��һ��
		btn_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (now_img_position < imgPathList.size() - 1) {
					btn_prev.setClickable(true);
					now_img_position++;
					img_photo
							.setBackgroundDrawable(loadImageFromPath(imgPathList
									.get(now_img_position)));
					txt_now_page.setText("��" + (now_img_position + 1) + "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					txt_imgname.setText(imgNameList.get(imgPathList
							.get(now_img_position)));
					edt_describe.setText(imgDescribe.get(imgPathList
							.get(now_img_position)));
				} else {
					btn_next.setClickable(false);
					Toast.makeText(RoadPointPhoto.this, "��ǰ�Ѿ������һ��ͼƬ�ˣ�",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// ��ʷ��¼
		btn_record.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (controlValue == 1) {
					btn_photo.setEnabled(true);
					now_img_position = 0;
					imgPathList.clear();
					imgNameList.clear();
					imgLocation.clear();
					imgDescribe.clear();
					edt_describe.setText("");
					txt_now_page.setText("��" + now_img_position + "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					txt_imgname.setText("--ͼƬ����--");
					btn_prev.setClickable(true);
					btn_next.setClickable(true);
					img_photo.setBackgroundDrawable(null);
					controlValue = 0;
					return;
				}
				if (check_list == null) {
					check_list = new ArrayList<Road_Check_Data_Table>();
				} else {
					check_list.clear();
				}

				check_list.addAll(dbs.road_check_table.getList(
						now_round_item.roundId, directionStr, photoTypeValue));
				if (check_list.size() == 0) {
					return;
				}
				imgPathList.clear();
				imgNameList.clear();
				imgDescribe.clear();
				imgLocation.clear();
				for (int i = 0; i < check_list.size(); i++) {
					Road_Check_Data_Table item = check_list.get(i);
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					for (int j = 0; j < imgPathList.size(); j++) {
						imgNameList.put(imgPathList.get(j),
								dbs.image.getItemName(imgPathList.get(j)));
						imgDescribe.put(imgPathList.get(j),
								dbs.image.getItemDescribe(imgPathList.get(j)));
						imgLocation.put(imgPathList.get(j),
								dbs.image.getItemLocation(imgPathList.get(j)));
					
					}
				}

				if (imgPathList.size() > 0) {
					img_photo
							.setBackgroundDrawable(loadImageFromPath(imgPathList
									.get(now_img_position)));
				}

				// ͼƬ����
				now_img_position = 0;
				if (imgNameList.size() > 0) {
					txt_imgname.setText(imgNameList.get(imgPathList.get(0)));
					txt_now_page.setText("��" + (now_img_position + 1) + "��");
					txt_all_page.setText("��" + imgPathList.size() + "��");
					if (imgDescribe.size() > 0) {
						edt_describe.setText(imgDescribe.get(imgPathList.get(0)));
						Log.w("fdafdsf", imgDescribe.size() + "");
					}
				} else {
					txt_imgname.setText("--ͼƬ����--");
					txt_now_page.setText("��0��");
					txt_all_page.setText("��0��");
				}

				controlValue = 1;
				btn_photo.setEnabled(false);
			}
		});
		// �����ı�����
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
				if (imgPathList.size() > 0 && count > 0) {
					imgDescribe.put(imgPathList.get(now_img_position),
							edt_describe.getText().toString());
				}
			}
		});
		// �Ŵ�ͼƬ
		img_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgPathList.size() <= 0) {
					return;
				}
				LayoutInflater inflater = RoadPointPhoto.this
						.getLayoutInflater();
				View view = inflater.inflate(
						R.layout.app_roadpointphoto_bigimg, null);
				LinearLayout layout_close = (LinearLayout) view
						.findViewById(R.id.layout_close);
				Gallery gallery_bigimg = (Gallery) view
						.findViewById(R.id.gallery_bigimg);

				// �رյ�����
				layout_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pw_bigimg.dismiss();
					}
				});

				rBigImgAdapter = new RBigImgAdapter(RoadPointPhoto.this,
						imgPathList);
				gallery_bigimg.setAdapter(rBigImgAdapter);
				gallery_bigimg.setSelection(now_img_position);
				gallery_bigimg
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, final int position, long arg3) {
								AlertDialog.Builder ab = new AlertDialog.Builder(
										RoadPointPhoto.this);
								ab.setTitle("��ܰ��ʾ");
								ab.setMessage("��ȷ��Ҫɾ��ͼƬ��"
										+ imgNameList.get(imgPathList.get(position)) + "��");
								ab.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// File file = new
												// File(imgPathList.get(position));
												// if(file.exists()){
												// file.delete();
												// }
												if (imgDescribe!=null) {
													imgDescribe.remove(imgPathList
															.get(position));
												}
												
												imgPathList.remove(position);
												imgNameList.remove(imgPathList.get(position));
												imgLocation.remove(imgPathList.get(position));
												rBigImgAdapter
														.notifyDataSetChanged();

												if (position < imgPathList
														.size()) {
													now_img_position = position;
													img_photo
															.setBackgroundDrawable(loadImageFromPath(imgPathList
																	.get(position)));
													txt_now_page.setText("��"
															+ (position + 1)
															+ "��");
													txt_all_page.setText("��"
															+ imgPathList
																	.size()
															+ "��");
													txt_imgname
															.setText(imgNameList
																	.get(position));
													edt_describe
															.setText(imgDescribe
																	.get(imgPathList
																			.get(position)));
												} else if (imgPathList.size() > 0) {
													now_img_position = position - 1;
													img_photo
															.setBackgroundDrawable(loadImageFromPath(imgPathList
																	.get(position - 1)));
													txt_now_page.setText("��"
															+ (position) + "��");
													txt_all_page.setText("��"
															+ imgPathList
																	.size()
															+ "��");
													txt_imgname
															.setText(imgNameList
																	.get(position - 1));
													edt_describe
															.setText(imgDescribe
																	.get(imgPathList
																			.get(position - 1)));
												} else {
													now_img_position = 0;
													img_photo
															.setBackgroundDrawable(null);
													txt_now_page.setText("��0��");
													txt_all_page.setText("��0��");
													txt_imgname
															.setText("--ͼƬ����--");
													edt_describe.setText("");
													pw_bigimg.dismiss();
												}
												deleteImg_dialog.dismiss();
											}
										});
								ab.setNegativeButton("ȡ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteImg_dialog.dismiss();
											}
										});
								deleteImg_dialog = ab.create();
								deleteImg_dialog.show();
							}
						});

				pw_bigimg = new PopupWindow(view, LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT, true);
				pw_bigimg.setAnimationStyle(R.style.AnimationBigImg);
				pw_bigimg.showAtLocation(RoadPointPhoto.this.getWindow()
						.findViewById(Window.ID_ANDROID_CONTENT),
						Gravity.CENTER, 0, 0);
			}
		});

		// ͼƬ����
		txt_now_page.setText("��" + now_img_position + "��");
		txt_all_page.setText("��" + imgPathList.size() + "��");
		txt_imgname.setText("--ͼƬ����--");

	}

	/** ͼƬ��ʽת�� */
	public static Drawable loadImageFromPath(String imagePath) {
		try {
			FileInputStream fis = new FileInputStream(imagePath);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			fis.close();
			return bd;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/** ���ఴť����¼� */
	public class PhotoImgButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Time time = new Time("GMT+8");
			time.setToNow();
			int year = time.year;
			int month = time.month +1;
			int day = time.monthDay;
			int minute = time.minute;
			int hour = time.hour;
			int sec = time.second;

			String imgfilename = "";
			if (now_round_item.street.equals("")) {
				imgfilename = now_round_item.area + "_" + now_round_item.type
						+ "_" + now_round_item.roundId + "_"
						+ now_round_item.name + "_" + directionStr + "_"
						+ photoType + "_" + year + "" + month + "" + day + ""
						+ hour + "" + minute + "" + sec + ".jpg";
			} else {
				imgfilename = now_round_item.area + "_" + now_round_item.type
						+ "_" + now_round_item.street + "_"
						+ now_round_item.roundId + "_" + now_round_item.name
						+ "_" + directionStr + "_" + photoType + "_" + year
						+ "" + month + "" + day + "" + hour + "" + minute + ""
						+ sec + ".jpg";
			}

			Uri uri = null;
			String savaPath = "com_pengtu_road_check_data" + "/"
					+ now_round_item.area + "/" + now_round_item.type + "/"
					+ now_round_item.roundId + now_round_item.name
					+ directionStr + "/" + photoType;
			try {
				new com.road.check.common.FileUtil()
						.createDIR("com_pengtu_road_check_data");
				new com.road.check.common.FileUtil()
						.createDIR("com_pengtu_road_check_data" + "/"
								+ now_round_item.area);
				new com.road.check.common.FileUtil()
						.createDIR("com_pengtu_road_check_data" + "/"
								+ now_round_item.area + "/"
								+ now_round_item.type);
				new com.road.check.common.FileUtil()
						.createDIR("com_pengtu_road_check_data" + "/"
								+ now_round_item.area + "/"
								+ now_round_item.type + "/"
								+ now_round_item.roundId + now_round_item.name
								+ directionStr);
				new com.road.check.common.FileUtil().createDIR(savaPath);
				uri = Uri.fromFile(new com.road.check.common.FileUtil()
						.createFileInSDCard(imgfilename, savaPath));
				nowimPath = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ File.separator
						+ savaPath
						+ File.separator + imgfilename;
				nowimName = imgfilename;

				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				RoadPointPhoto.this.startActivityForResult(intent,
						ROADPOINTPHOTO_TAKEPHOTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** ͷ����߰�ť����¼�:���� */
	class HeaderLeftOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {// backMessageDialog
			if (imgPathList.size() > 0) {
				AlertDialog.Builder ab = new AlertDialog.Builder(
						RoadPointPhoto.this);
				ab.setTitle("��ܰ��ʾ");
				ab.setMessage("������δ���棬�Ƿ񱣴棿");
				ab.setPositiveButton("����",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								backMessageDialog.dismiss();
								savaDatasTask = new SavaDatasTask();
								savaDatasTask.execute();
								RoadPointPhoto.this.finish();
							}
						});
				ab.setNegativeButton("����",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								backMessageDialog.dismiss();
								RoadPointPhoto.this.finish();
							}
						});

				backMessageDialog = ab.create();
				backMessageDialog.show();
			} else {
				RoadPointPhoto.this.finish();
			}
		}
	}

	/** ͷ���ұ߰�ť����¼�:���� */
	class HeaderRightOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (imgPathList.size()==0) {
				Toast.makeText(RoadPointPhoto.this, "δ�������գ������պ���д���ݣ�֮���ٱ���", 0).show();
			}else{
//			if (photoTypeValue ==4) {
//				View verify_dialog_view = View
//				.inflate(
//						RoadPointPhoto.this,
//						R.layout.navigation_verify_dialog,
//						null);
//		edt_check_length = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_check_length);
//		edt_footwalk_width_1 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_1);
//		edt_footwalk_width_2 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_2);
//		edt_footwalk_width_3 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_3);
//		edt_footwalk_width_4 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_4);
//		edt_footwalk_width_5 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_5);
//		edt_footwalk_width_6 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_6);
//		edt_footwalk_width_7 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_7);
//		edt_footwalk_width_8 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_8);
//		edt_footwalk_width_9 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_9);
//		edt_footwalk_width_10 = (EditText) verify_dialog_view
//				.findViewById(R.id.edt_footwalk_width_10);
//		footwalk_width_list = new ArrayList<EditText>();
//		footwalk_width_list.add(edt_footwalk_width_1);
//		footwalk_width_list.add(edt_footwalk_width_2);
//		footwalk_width_list.add(edt_footwalk_width_3);
//		footwalk_width_list.add(edt_footwalk_width_4);
//		footwalk_width_list.add(edt_footwalk_width_5);
//		footwalk_width_list.add(edt_footwalk_width_6);
//		footwalk_width_list.add(edt_footwalk_width_7);
//		footwalk_width_list.add(edt_footwalk_width_8);
//		footwalk_width_list.add(edt_footwalk_width_9);
//		footwalk_width_list.add(edt_footwalk_width_10);
//		Button btn_sure = (Button) verify_dialog_view
//				.findViewById(R.id.btn_sure);
//		btn_sure.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {	
//				// �������ݺ�ʵ
//				
//				String footwalk_width_array = "";
//				for (int i = 0; i < footwalk_width_list
//						.size(); i++) {
//					String width = footwalk_width_list
//							.get(i).getText()
//							.toString();
//					if (width == null
//							|| width.equals("")) {
//						continue;
//					}
//					footwalk_width_array += width + ",";
//				}
//				if (footwalk_width_array.length() > 0) {
//					footwalk_width_array = footwalk_width_array
//							.substring(
//									0,
//									footwalk_width_array
//											.length() - 1);
//				}
//
//				VerifyDatas item = new VerifyDatas();
//				item.roadId = cApp.RoundId;
//				item.check_road_lenth = edt_check_length
//						.getText().toString();
//				item.footwalk_width = footwalk_width_array;
//				item.addTime = as.relistic.common.Helper
//						.getNowSystemTimeToDay();
//				dbs.verifydatas.add(item);
//				verify_dialog.dismiss();
//				savaDatasTask = new SavaDatasTask();
//				savaDatasTask.execute();
//			}
//		});
//		verify_dialog = new Dialog(RoadPointPhoto.this,
//				R.style.dialog);
//		verify_dialog
//				.setContentView(verify_dialog_view);
//		verify_dialog.show();
//			}else{
				savaDatasTask = new SavaDatasTask();
				savaDatasTask.execute();
//			}
			}
		}
	}

	/** ����󷵻� */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ROADPOINTPHOTO_TAKEPHOTO:
			if (resultCode == Activity.RESULT_OK) {
				settingImag(nowimPath, nowimName);
				if (imgPathList.size() > 0) {
					img_photo
							.setBackgroundDrawable(loadImageFromPath(imgPathList
									.get(0)));
					txt_imgname.setText(imgNameList.get(imgPathList.get(0)));
					//edt_describe.setText(imgDescribe.get(imgPathList.get(0))+imgLocation.get(imgPathList.get(0)));
				}

				if (imgPathList.size() >= 2) {
					btn_prev.setClickable(true);
					btn_next.setClickable(true);
				} else {
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

	private void settingImag(String imgPath, String nowimName) {
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

	/** �������� */
	class SavaDatasTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			if (check_list != null) {
				for (int i = 0; i < check_list.size(); i++) {
					Road_Check_Data_Table d_item = check_list.get(i);
					dbs.road_check_table.deleteDatas(d_item.Id + "");
					dbs.image.deleteDatas(d_item.Id + "");
				}
			}
			Road_Check_Data_Table item = new Road_Check_Data_Table();
			item.roundName = now_round_item.name;
			item.roundId = now_round_item.roundId;
			item.category = photoTypeValue;
			item.direction = directionStr;
			item.deteTime = as.relistic.common.Helper
					.getNowSystemTimeToSecond();
			item.reportName = title;
			if (imgPathList.size() > 0) {
				item.firstImageUrl = imgPathList.get(0);
			}
			String check_table_id = dbs.road_check_table.add(item) + "";

			Image image_item;
			
			for (int i = 0; i < imgPathList.size(); i++) {
				image_item = new Image();
				image_item.roadcheckid = check_table_id;
				image_item.imagepath = imgPathList.get(i);
				image_item.imagename = imgNameList.get(imgPathList.get(i));
				image_item.imagedescribe = imgDescribe.get(imgPathList.get(i));
				image_item.imglocation = imgLocation.get(imgPathList.get(i));
				dbs.image.add(image_item);
			}
			//sendTask ����ͼƬ��Ϣ
			
			// //��ȡͼƬ·����ͼƬ���ơ�
			// String imagePathString = "";
			// String fistPathString = "";
			// for(int i = 0 ; i < imgPathList.size() ; i++){
			// if(i > 0){
			// imagePathString += ",";
			// }else{
			// fistPathString = imgPathList.get(i);
			// }
			// imagePathString += imgPathList.get(i);
			// }
			//
			// Road_Check_Data_Table item = new Road_Check_Data_Table();
			// item.Id = as.relistic.common.Helper.getTimeStampEstimator();
			// item.roundName = now_round_item.name;
			// item.roundId = now_round_item.id;
			// item.category = 1;
			// item.direction = directionStr;
			// item.imagePathString = imagePathString;
			// item.firstImageUrl = fistPathString;
			// item.deteTime =
			// as.relistic.common.Helper.getNowSystemTimeToSecond();
			// dbs.road_check_table.add(item);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			now_img_position = 0;
			imgPathList.clear();
			imgNameList.clear();
			imgDescribe.clear();
			imgLocation.clear();
			txt_now_page.setText("��" + now_img_position + "��");
			txt_all_page.setText("��" + imgPathList.size() + "��");
			txt_imgname.setText("--ͼƬ����--");
			btn_prev.setClickable(true);
			btn_next.setClickable(true);
			img_photo.setBackgroundDrawable(null);
			Toast.makeText(RoadPointPhoto.this, "����ɹ���", Toast.LENGTH_SHORT)
					.show();
			RoadPointPhoto.this.finish();
		}
	}

//private Bitmap createBitmap(Bitmap src,String markname) {
//	if (src==null) {
//		return null;
//	}
//
//	int w = src.getWidth();
//	int h = src.getHeight();
//	System.out.print("ͼƬ���"+w+"ͼƬ����"+h+"");
//	Bitmap newb = Bitmap.createBitmap(w,h,Config.RGB_565);
//	Canvas cvCanvas = new Canvas(newb);
//	Paint paint = new Paint();
//	paint.setDither(true);
//	paint.setAntiAlias(true);
//	Rect srct = new Rect(0,0,w,h);
//	Rect dest = new Rect(0,0,w,h);
//	cvCanvas.drawBitmap(src, srct, dest, paint);
//	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//���û���  
//    textPaint.setTextSize(20.0f);//�����С  
//    textPaint.setTypeface(Typeface.DEFAULT_BOLD);//����Ĭ�ϵĿ��  
//    textPaint.setColor(Color.RED);
//    textPaint.setAntiAlias(true);
//    textPaint.setDither(true);//���õ���ɫ  
//    //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//Ӱ��������  
//    cvCanvas.drawText(markname, 85, 20, textPaint);//������ȥ�֣���ʼδ֪x,y������ֻ�ʻ��� 
//	cvCanvas.save(Canvas.ALL_SAVE_FLAG);
//	cvCanvas.restore();
//	return newb;
//}
}
