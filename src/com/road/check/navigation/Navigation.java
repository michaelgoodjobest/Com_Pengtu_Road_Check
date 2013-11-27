package com.road.check.navigation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.road.check.R;
import com.road.check.app.About;
import com.road.check.app.CheckHistoricalRecords;
import com.road.check.app.Intersection;
import com.road.check.app.RoadPointPhoto;
import com.road.check.app.Uploading_List;
import com.road.check.base.ActivityBase;
import com.road.check.common.CheckApplication;
import com.road.check.common.FileUtil;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.report.Create;
import com.road.check.roadselecte.RoadSelecteList_V2;
import com.road.check.sqlite.DatabaseService;

public class Navigation extends ActivityBase {
	private static final int REQUEST_TIMEOUT = 6*1000;//设置请求超时6秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	private AlertDialog changeDirection_dialog;
	public static final String GET_RESPONSE_TEXT_ERROR = "GET_RESPONSE_TEXT_ERROR";
	public String resString;
	private Button btn_start;// 开始
	private Button btn_stop;// 结束
	private ImageButton img_btn_direction_update;// 行驶方向改变
	private TextView txt_round_name;// 道路名称
	private TextView txt_direction;// 当前行驶方向
	private RelativeLayout layout_up_direction_start_photo;// 起点拍照
	private RelativeLayout layout_guideboard_photo;// 路牌拍照
	private RelativeLayout layout_intersection_photo;// 交叉路口拍照
	private RelativeLayout layout_footwalk_photo;// 人行道拍照
	private RelativeLayout layout_report_create;// 报表创建
	private RelativeLayout layout_up_direction_end_photo;// 终点拍照
	private TextView txt_up_direction_end_photo;
	private TextView txt_up_direction_start_photo;
	private TextView txt_img_num_end_photo;
	private TextView txt_img_num_report_create;
	private TextView txt_img_num_footwalk_photo;
	private TextView txt_img_num_intersection_photo;
	private TextView txt_img_num_guideboard_photo;
	private TextView txt_img_num_start_photo;
	private LinearLayout layout_start;
	private RelativeLayout rlayout_stop;
	private RelativeLayout rlayout_start;
	private TextView txt_cl;
	private TextView txt_cr;
	private TextView txt_fl;
	private TextView txt_fr;
	
	

	// 位置信息
	private LocationManager l_manager;
	private LocationListener locationListener;
	private AlertDialog gpsDialog;
	private AlertDialog gpseDialog;
	private AlertDialog dataDialog;
	private ProgressDialog  locateDialog=null;
	private View pw_inflater;
	
	private PopupWindow pw_gpsstart;
	private LinearLayout layout_know;
	private TextView txt_pw_message;
	private int now_goLong = 0;
	public LocationClient mLocClient;
	public BDLocationListener myListener = new MyLocationListener();

	// 计时器
	private Handler countHandler;
	private Timer countimer;
	private long countTimer_delay = 1000 * 3; // 频率
	private CountTimerTask timerTask;
	private final int EVENT_WT = 0x20034;
	private SendTask sendTask;

	private DatabaseService dbs;
	private HashMap<String, Object> btnMap;
	private Animation btn_start_anim;
	private Animation btn_stop_anim_out;
	private AlertDialog backdialog;
	private Intent intent;


	
	// 数据清理
	private ClearDatasTask clearDatasTask;
	private AlertDialog clearDialog;
	private ProgressDialog clearpd;

	// 数据上传
	private Dialog uploading_dialog;

	// 道路选择
	private PopupWindow pw_selecteRoad;

	// 检测版本
	private AlertDialog newVerDialog = null;
	private ProgressDialog newVerProgressDialog = null;
	private String newVerUrl = "";
	private int newVER = 1;
	private ProgressDialog progressDialog = null;
	String webservice_result = "-1";
	private AlertDialog networkdialog;

	// 结束
	private AlertDialog stopDialog = null;
	// 定时器处理handler
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("DEBUGGGGGGG", "HandleMessage所在线程:"
					+ Thread.currentThread().getName());
			// 处理过程
			switch (msg.what) {
			case 0:
				// Toast.makeText(getApplicationContext(),
				// "我在处理"+now_lat+now_lng+"类型"+locType+"",
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplicationContext(), "服务器返回："+resString,
				// Toast.LENGTH_SHORT).show();
				String serviceName = Context.LOCATION_SERVICE;
				l_manager = (LocationManager) getSystemService(serviceName);
				// !l_manager
				// .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
				if ((cApp.lng == null && cApp.lat == null)
						|| (cApp.lng == "" && cApp.lat == "")) {
					// now_lat = "";
					// now_lng = "";
					if (pw_gpsstart != null && pw_gpsstart.isShowing()) {
						pw_gpsstart.dismiss();
					}

					if (gpsDialog != null && gpsDialog.isShowing()) {
						gpsDialog.dismiss();
					}
					AlertDialog.Builder ad = new AlertDialog.Builder(
							Navigation.this);
					ad.setTitle("温馨提示");
					ad.setMessage("网络错误，无法获得位置坐标，请检查网络设置");
					ad.setPositiveButton("去打开",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivityForResult(intent, 0);
									gpsDialog.dismiss();
								}
							});
					ad.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									gpsDialog.dismiss();
								}
							});
					gpsDialog = ad.create();
					gpsDialog.show();

				} else {
					if (haveNetWorkd(Navigation.this)) {
						sendTask = new SendTask();
						sendTask.execute();

					} else {
						if (dataDialog != null && dataDialog.isShowing()) {
							return;
						}
						AlertDialog.Builder ad = new AlertDialog.Builder(
								Navigation.this);
						ad.setTitle("温馨提示");
						ad.setMessage("网络错误，无法获得位置坐标，请检查网络设置");
						ad.setPositiveButton("去打开",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent intent = new Intent(
												Settings.ACTION_DATA_ROAMING_SETTINGS);
										startActivityForResult(intent, 0);
										dataDialog.dismiss();
									}
								});
						ad.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dataDialog.dismiss();
									}
								});
						dataDialog = ad.create();
						dataDialog.show();

					}
				}
				break;
			case 2:
				txt_pw_message.setText("已通过GPS获取到了位置信息，工作可以开始了！");
				layout_know.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation);
		// 控件定义
		btn_start = (Button) this.findViewById(R.id.btn_start);
		layout_up_direction_start_photo = (RelativeLayout) this
				.findViewById(R.id.layout_up_direction_start_photo);
		layout_guideboard_photo = (RelativeLayout) this
				.findViewById(R.id.layout_guideboard_photo);
		layout_intersection_photo = (RelativeLayout) this
				.findViewById(R.id.layout_intersection_photo);
		layout_footwalk_photo = (RelativeLayout) this
				.findViewById(R.id.layout_footwalk_photo);
		layout_report_create = (RelativeLayout) this
				.findViewById(R.id.layout_report_create);
		layout_up_direction_end_photo = (RelativeLayout) this
				.findViewById(R.id.layout_up_direction_end_photo);
		btn_stop = (Button) this.findViewById(R.id.btn_stop);
		img_btn_direction_update = (ImageButton) this
				.findViewById(R.id.img_btn_direction_update);
		txt_round_name = (TextView) this.findViewById(R.id.txt_round_name);
		txt_direction = (TextView) this.findViewById(R.id.txt_direction);
		txt_up_direction_end_photo = (TextView) this
				.findViewById(R.id.txt_up_direction_end_photo);
		txt_up_direction_start_photo = (TextView) this
				.findViewById(R.id.txt_up_direction_start_photo);
		txt_img_num_end_photo = (TextView) this
				.findViewById(R.id.txt_img_num_end_photo);
		txt_img_num_report_create = (TextView) this
				.findViewById(R.id.txt_img_num_report_create);
		txt_img_num_footwalk_photo = (TextView) this
				.findViewById(R.id.txt_img_num_footwalk_photo);
		txt_img_num_intersection_photo = (TextView) this
				.findViewById(R.id.txt_img_num_intersection_photo);
		txt_img_num_guideboard_photo = (TextView) this
				.findViewById(R.id.txt_img_num_guideboard_photo);
		txt_img_num_start_photo = (TextView) this
				.findViewById(R.id.txt_img_num_start_photo);
		layout_start = (LinearLayout) this.findViewById(R.id.layout_start);
		rlayout_stop = (RelativeLayout) this.findViewById(R.id.rlayout_stop);
		rlayout_start = (RelativeLayout) this.findViewById(R.id.rlayout_start);
		txt_cl = (TextView) this.findViewById(R.id.txt_clength);
		txt_cr = (TextView) this.findViewById(R.id.txt_croadwidth);
		txt_fl = (TextView) this.findViewById(R.id.txt_flength);
		txt_fr = (TextView) this.findViewById(R.id.txt_froadwidth);
		
		dbs = new DatabaseService(this);
		btnMap = new HashMap<String, Object>();
		btnMap.put("btn_1", layout_up_direction_start_photo);
		btnMap.put("btn_2", layout_guideboard_photo);
		btnMap.put("btn_3", layout_intersection_photo);
		btnMap.put("btn_4", layout_footwalk_photo);
		btnMap.put("btn_5", layout_report_create);
		btnMap.put("btn_6", layout_up_direction_end_photo);
		btnMap.put("btn_direction", img_btn_direction_update);
		btnMap.put("btn_start", btn_start);
		btnMap.put("btn_stop", btn_stop);
		btnMap.put("txt_img_num_end_photo", txt_img_num_end_photo);
		btnMap.put("txt_img_num_report_create", txt_img_num_report_create);
		btnMap.put("txt_img_num_footwalk_photo", txt_img_num_footwalk_photo);
		btnMap.put("txt_img_num_intersection_photo",
				txt_img_num_intersection_photo);
		btnMap.put("txt_img_num_guideboard_photo", txt_img_num_guideboard_photo);
		btnMap.put("txt_img_num_start_photo", txt_img_num_start_photo);
		btnMap.put("rlayout_stop", rlayout_stop);
		btnMap.put("rlayout_start", rlayout_start);
		btnMap.put("layout_start", layout_start);
		btnMap.put("txt_up_start_photo", txt_up_direction_start_photo);
		btnMap.put("txt_up_end_photo", txt_up_direction_end_photo);
		btnMap.put("txt_cl", txt_cl);
		btnMap.put("txt_cr", txt_cr);
		btnMap.put("txt_fl", txt_fl);
		btnMap.put("txt_fr", txt_fr);
		//测试新版本检测功能
		Log.d("初始前经纬度数据", cApp.lat+cApp.lng);
		// 开始按钮
		btn_start_anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
		layout_start.startAnimation(btn_start_anim);
		btn_start.setOnClickListener(new StartButtonOnClickListener());
		// 结束按钮点击监听
		btn_stop_anim_out = AnimationUtils.loadAnimation(this,
				R.anim.ficker_out);
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder s_ab = new AlertDialog.Builder(
						Navigation.this);
				s_ab.setTitle("温馨提示");
				s_ab.setMessage("确定要结束对当前道路的巡查吗？");
				s_ab.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								stopDialog.dismiss();

								// 基础数据核实

								// 控件属性设置
								layout_up_direction_start_photo
										.setBackgroundResource(R.drawable.navigation_v2_app5_bg_after);
								layout_guideboard_photo
										.setBackgroundResource(R.drawable.navigation_v2_app2_bg_after);
								layout_intersection_photo
										.setBackgroundResource(R.drawable.navigation_v2_app6_bg_after);
								layout_footwalk_photo
										.setBackgroundResource(R.drawable.navigation_v2_app4_bg_after);
								layout_report_create
										.setBackgroundResource(R.drawable.navigation_v2_app3_bg_after);
								layout_up_direction_end_photo
										.setBackgroundResource(R.drawable.navigation_v2_app1_bg_after);
								img_btn_direction_update
										.setBackgroundResource(R.drawable.direction_up_after);
								layout_up_direction_start_photo
										.setClickable(false);
								layout_guideboard_photo.setClickable(false);
								layout_intersection_photo.setClickable(false);
								layout_footwalk_photo.setClickable(false);
								layout_report_create.setClickable(false);
								layout_up_direction_end_photo
										.setClickable(false);
								img_btn_direction_update.setClickable(false);

								cApp.RoundId = "";
								cApp.Direction = -1;
								cApp.lat = "";
								cApp.lng = "";
								txt_round_name.setText("道路名称");
								txt_direction.setText("【方向】");
								layout_start.startAnimation(btn_start_anim);
								rlayout_start.setVisibility(View.VISIBLE);
								rlayout_stop.setVisibility(View.GONE);
								rlayout_stop.startAnimation(btn_stop_anim_out);

								// 移除位置监听器
								mLocClient
										.unRegisterLocationListener(myListener);
								if (timerTask != null) {
									countimer.cancel();
									timerTask.cancel();
								}

								txt_img_num_report_create.setText("--");
								txt_img_num_footwalk_photo.setText("--");
								txt_img_num_intersection_photo.setText("--");
								txt_img_num_guideboard_photo.setText("--");
								txt_img_num_start_photo.setText("--");
								txt_img_num_end_photo.setText("--");
								txt_cl.setText("");
								txt_cr.setText("");
								txt_fl.setText("");
								txt_fr.setText("");
							}
						});
				s_ab.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								stopDialog.dismiss();
							}
						});

				stopDialog = s_ab.create();
				stopDialog.show();

			}
		});
		// 行驶方向更改按钮点击监听
		img_btn_direction_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder aBuilder = new AlertDialog.Builder(
						Navigation.this);
				aBuilder.setTitle("温馨提示");
				aBuilder.setMessage("是否切换道路方向");
				aBuilder.setCancelable(false);
				aBuilder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								changeDirection_dialog.dismiss();
							}
						});
				aBuilder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (cApp.Direction == 0) {
									txt_direction.setText("【下行】");
									txt_up_direction_start_photo
											.setText("下行起点拍照");
									txt_up_direction_end_photo
											.setText("下行终点拍照");
									img_btn_direction_update
											.setBackgroundResource(R.drawable.direction_down);
									cApp.Direction = 1;

									txt_img_num_start_photo
											.setText(getimg_num(6));
									txt_img_num_end_photo
											.setText(getimg_num(7));
								
										txt_img_num_report_create.setText(getimg_num(0));
										txt_img_num_footwalk_photo.setText(getimg_num(4));
										txt_img_num_intersection_photo.setText(getimg_num(3));
										txt_img_num_guideboard_photo.setText(getimg_num(2));
										txt_img_num_start_photo.setText(getimg_num(6));
										txt_img_num_end_photo.setText(getimg_num(7));
									
								} else if (cApp.Direction == 1) {
									txt_direction.setText("【上行】");
									txt_up_direction_start_photo
											.setText("上行起点拍照");
									txt_up_direction_end_photo
											.setText("上行终点拍照");
									img_btn_direction_update
											.setBackgroundResource(R.drawable.direction_up);
									cApp.Direction = 0;

									txt_img_num_start_photo
											.setText(getimg_num(1));
									txt_img_num_end_photo
											.setText(getimg_num(5));
									
										txt_img_num_report_create.setText(getimg_num(0));
										txt_img_num_footwalk_photo.setText(getimg_num(4));
										txt_img_num_intersection_photo.setText(getimg_num(3));
										txt_img_num_guideboard_photo.setText(getimg_num(2));
										txt_img_num_start_photo.setText(getimg_num(1));
										txt_img_num_end_photo.setText(getimg_num(5));
									
								} else {
									Toast.makeText(Navigation.this,
											"操作无效，尚未选择道路！", Toast.LENGTH_SHORT)
											.show();
								}
							}

						});
				changeDirection_dialog = aBuilder.create();
				changeDirection_dialog.show();
			}
		});

		// 起点拍照
		layout_up_direction_start_photo
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (cApp.Direction == 0) {
							intent = new Intent(Navigation.this,
									RoadPointPhoto.class);
							Bundle bundle = new Bundle();
							bundle.putString("title", "上行起点拍照");
							bundle.putString("photoType", Navigation.this
									.getString(R.string.photo_type_up_origin));
							bundle.putInt("photoTypeValue", 1);
							intent.putExtras(bundle);
							Navigation.this.startActivity(intent);
						} else if (cApp.Direction == 1) {
							intent = new Intent(Navigation.this,
									RoadPointPhoto.class);
							Bundle bundle = new Bundle();
							bundle.putString("title", "下行起点拍照");
							bundle.putString("photoType", Navigation.this
									.getString(R.string.photo_type_down_origin));
							bundle.putInt("photoTypeValue", 6);
							intent.putExtras(bundle);
							Navigation.this.startActivity(intent);
						}
					}
				});
		// 路牌拍照
		layout_guideboard_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, RoadPointPhoto.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "路牌拍照");
				bundle.putString("photoType", Navigation.this
						.getString(R.string.photo_type_guideboard));
				bundle.putInt("photoTypeValue", 2);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// 交叉路口拍照
		layout_intersection_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, Intersection.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "路口拍照");
				bundle.putString("photoType", Navigation.this
						.getString(R.string.photo_type_intersection));
				bundle.putInt("photoTypeValue", 3);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// 人行道拍照
		layout_footwalk_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, RoadPointPhoto.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "人行道拍照");
				bundle.putString("photoType",
						Navigation.this.getString(R.string.photo_type_footwalk));
				bundle.putInt("photoTypeValue", 4);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// 报表填写
		layout_report_create.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, Create.class);
				Navigation.this.startActivity(intent);
			}
		});
		// 终点拍照
		layout_up_direction_end_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cApp.Direction == 0) {
					intent = new Intent(Navigation.this, RoadPointPhoto.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", "上行终点拍照");
					bundle.putString("photoType", Navigation.this
							.getString(R.string.photo_type_up_finish));
					bundle.putInt("photoTypeValue", 5);
					intent.putExtras(bundle);
					Navigation.this.startActivity(intent);
				} else if (cApp.Direction == 1) {
					intent = new Intent(Navigation.this, RoadPointPhoto.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", "下行终点拍照");
					bundle.putString("photoType", Navigation.this
							.getString(R.string.photo_type_down_finish));
					bundle.putInt("photoTypeValue", 7);
					intent.putExtras(bundle);
					Navigation.this.startActivity(intent);
				}
			}
		});

		// 控件属性初始化
		layout_up_direction_start_photo
				.setBackgroundResource(R.drawable.navigation_v2_app5_bg_after);
		layout_guideboard_photo
				.setBackgroundResource(R.drawable.navigation_v2_app2_bg_after);
		layout_intersection_photo
				.setBackgroundResource(R.drawable.navigation_v2_app6_bg_after);
		layout_footwalk_photo
				.setBackgroundResource(R.drawable.navigation_v2_app4_bg_after);
		layout_report_create
				.setBackgroundResource(R.drawable.navigation_v2_app3_bg_after);
		layout_up_direction_end_photo
				.setBackgroundResource(R.drawable.navigation_v2_app1_bg_after);
		img_btn_direction_update
				.setBackgroundResource(R.drawable.direction_up_after);
		layout_up_direction_start_photo.setClickable(false);
		layout_guideboard_photo.setClickable(false);
		layout_intersection_photo.setClickable(false);
		layout_footwalk_photo.setClickable(false);
		layout_report_create.setClickable(false);
		layout_up_direction_end_photo.setClickable(false);
		img_btn_direction_update.setClickable(false);

	}

	/** 获取图片数量 */
	public String getimg_num(int category) {
		Round round_item = new Round();
		round_item = dbs.round.getItem(cApp.RoundId);

		List<Road_Check_Data_Table> i_list = new ArrayList<Road_Check_Data_Table>();
		i_list.addAll(dbs.road_check_table.getList(
				round_item.roundId,
				txt_direction.getText().toString().replace("【", "")
						.replace("】", ""), category));
		int num = 0;
		for (int i = 0; i < i_list.size(); i++) {
			String[] imgArray = i_list.get(i).imagePathString.split(",");
			num += imgArray.length;
		}
		return num + "";
	}

	/**************************** 开始按钮点击事件 ***************************/
	class StartButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// // GPS位置信息获取
			// Criteria criteria = new Criteria();
			// criteria.setAccuracy(Criteria.ACCURACY_FINE);
			// criteria.setAltitudeRequired(false);
			// criteria.setBearingRequired(false);
			// criteria.setCostAllowed(true);
			// criteria.setPowerRequirement(Criteria.POWER_LOW);
			// String serviceName = Context.LOCATION_SERVICE;
			// l_manager = (LocationManager) getSystemService(serviceName);
			// if
			// (!l_manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
			// {
			// if (gpsDialog != null && gpsDialog.isShowing()) {
			// gpsDialog.dismiss();
			// }
			// AlertDialog.Builder ad = new AlertDialog.Builder(
			// Navigation.this);
			// ad.setTitle("温馨提示");
			// ad.setMessage("GPS尚未打开，请先打开GPS再操作！");
			// ad.setPositiveButton("去打开",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// Intent intent = new Intent(
			// Settings.ACTION_SETTINGS);
			// startActivityForResult(intent, 0);
			// gpsDialog.dismiss();
			// }
			// });
			// ad.setNegativeButton("取消",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// gpsDialog.dismiss();
			// }
			// });
			// gpsDialog = ad.create();
			// gpsDialog.show();
			// } else {
			// provider = l_manager.getBestProvider(criteria, true);
			// 监听器
			// locationListener = new LocationListener() {
			// @Override
			// public void onStatusChanged(String provider, int status,
			// Bundle extras) {
			//
			// }
			//
			// @Override
			// public void onProviderEnabled(String provider) {
			//
			// }
			//
			// @Override
			// public void onProviderDisabled(String provider) {
			//
			// }
			//
			// @Override
			// public void onLocationChanged(Location location) {
			// if (location != null) {
			// now_lat = location.getLatitude() + "";
			// now_lng = location.getLongitude() + "";
			// now_goLong += 10;
			//
			// }
			// }
			// };
			// String Provider = l_manager.getBestProvider(criteria, true);
			// Location currentlocation =
			// l_manager.getLastKnownLocation(Provider);
			// if (currentlocation==null) {
			// l_manager
			// .requestLocationUpdates(Provider, 0, 0, locationListener);
			// }
			//
			//
			// l_manager.addGpsStatusListener(gpsListener);
			// 启动定时器
			mLocClient = new LocationClient(getApplicationContext());
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			
			option.setOpenGps(true);
			option.setAddrType("all");// 返回的定位结果包含地址信息
			option.setCoorType("gcj02");// 返回的定位结果是百度经纬度,默认值gcj02,bd02
			option.setPriority(LocationClientOption.GpsFirst);
			option.setScanSpan(1000 * 5);// 设置发起定位请求的间隔时间为5000ms
			option.disableCache(true);// 禁止启用缓存定位
			// option.setPoiNumber(5); // 最多返回POI个数
			// option.setPoiDistance(1000); // poi查询距离
			// option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
			mLocClient.setLocOption(option);
			mLocClient.start();
			Log.d("开启后经纬度数据", cApp.lat+cApp.lng);
			LayoutInflater inflater = Navigation.this.getLayoutInflater();
			 pw_inflater = inflater.inflate(
					R.layout.navigation_pw_gpsstart, null);
			LinearLayout layout_close = (LinearLayout) pw_inflater
					.findViewById(R.id.layout_close);
			layout_know = (LinearLayout) pw_inflater
					.findViewById(R.id.layout_know);
			txt_pw_message = (TextView) pw_inflater
					.findViewById(R.id.txt_pw_message);
			Button btn_know = (Button) pw_inflater.findViewById(R.id.btn_know);
			txt_pw_message.setText("正在获取定位信息，请稍候...");
			layout_know.setVisibility(View.GONE);
			layout_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pw_gpsstart.dismiss();
				}
			});
			btn_know.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(Navigation.this, cApp.lat+"_"+cApp.lng +"_"+cApp.locType, 1).show();
					pw_gpsstart.dismiss();
					LayoutInflater inflater_roadselecte = Navigation.this
							.getLayoutInflater();
					View pw_inflater_roadselecte = inflater_roadselecte
							.inflate(R.layout.roadselecte_list, null);
					pw_selecteRoad = new PopupWindow(pw_inflater_roadselecte,
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
							true);
					pw_selecteRoad.setAnimationStyle(R.style.AnimationPreview);
					pw_selecteRoad.showAtLocation(Navigation.this.getWindow()
							.findViewById(Window.ID_ANDROID_CONTENT),
							Gravity.CENTER, 0, 0);
					new RoadSelecteList_V2(Navigation.this, dbs, cApp)
							.initView(pw_inflater_roadselecte, pw_selecteRoad,
									txt_round_name, txt_direction, btnMap);

				}
			});
//			pw_gpsstart = new PopupWindow(pw_inflater,
//					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
//			pw_gpsstart.showAtLocation(Navigation.this.getWindow()
//					.findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER,
//					0, 0);
			LocateTask locateTask = new LocateTask();
			locateTask.execute();
//			while (true) {
//				Log.d("循环中的经纬度数据", cApp.lat+cApp.lng);
//				locateDialog = ProgressDialog.show(Navigation.this, "请稍候",
//						"正在定位...", true);
//				if (((cApp.lat != null && cApp.lng != null))
//						&& (!cApp.lat.equals("") && !cApp.lng.equals(""))) {
//					txt_pw_message.setText("已通过GPS获取到了位置信息，工作可以开始了！");
//					layout_know.setVisibility(View.VISIBLE);
//					Log.w("得到的经纬度数据", cApp.lat+cApp.lng);
//					locateDialog.dismiss();
//					break;
//				}
			}

			// LayoutInflater inflater_roadselecte = Navigation.this
			// .getLayoutInflater();
			// View pw_inflater_roadselecte = inflater_roadselecte.inflate(
			// R.layout.roadselecte_list, null);
			// pw_selecteRoad = new PopupWindow(pw_inflater_roadselecte,
			// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
			// true);
			// pw_selecteRoad.setAnimationStyle(R.style.AnimationPreview);
			// pw_selecteRoad.showAtLocation(Navigation.this.getWindow()
			// .findViewById(Window.ID_ANDROID_CONTENT),
			// Gravity.CENTER, 0, 0);
			// new RoadSelecteList_V2(Navigation.this, dbs, cApp).initView(
			// pw_inflater_roadselecte, pw_selecteRoad,
			// txt_round_name, txt_direction, btnMap);
			//
			// }

		}
	class LocateTask extends AsyncTask<String, String, String>{

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (result.equals("1")) {
				handler.sendEmptyMessageDelayed(2, 1000*1);
				
			}

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			pw_gpsstart = new PopupWindow(pw_inflater,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			pw_gpsstart.showAtLocation(Navigation.this.getWindow()
					.findViewById(Window.ID_ANDROID_CONTENT), Gravity.CENTER,
					0, 0);
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d("循环中的经纬度数据", "循环中的经纬度数据"+cApp.lat+cApp.lng);
//			if (((cApp.lat != null && cApp.lng != null))
//					&& (!cApp.lat.equals("") && !cApp.lng.equals(""))) {
//				
///				Log.w("得到的经纬度数据", cApp.lat+cApp.lng);
//			
//			return "1";
//		}else {
//			return "2";
//		}
		for(;!(cApp.lat!=null&&cApp.lat!=""&&cApp.lat.length()>0);){
			Log.d("循环中的经纬度数据", "循环中的经纬度数据"+cApp.lat+cApp.lng);
			
		}

		return "1";
		
	}
	}

	/***************************** 开始按钮点击事件 END **************************/

	/******************************* GPS位置相关 ********************************/
	// private GpsStatus gpsstatus;
	// private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
	// // GPS状态发生变化时触发
	// @Override
	// public void onGpsStatusChanged(int event) {
	// // 获取当前状态
	// gpsstatus = l_manager.getGpsStatus(null);
	// switch (event) {
	// // 第一次定位时的事件
	// case GpsStatus.GPS_EVENT_FIRST_FIX:
	// txt_pw_message.setText("已通过GPS获取到了位置信息，工作可以开始了！");
	// layout_know.setVisibility(View.VISIBLE);
	// break;
	// // 开始定位的事件
	// case GpsStatus.GPS_EVENT_STARTED:
	// break;
	// // 发送GPS卫星状态事件
	// case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	// // // Toast.makeText(MainActivity.this,
	// // "GPS_EVENT_SATELLITE_STATUS", Toast.LENGTH_SHORT).show();
	// // Iterable<GpsSatellite> allSatellites =
	// // gpsstatus.getSatellites();
	// // Iterator<GpsSatellite> it=allSatellites.iterator();
	// // int count = 0;
	// // while(it.hasNext()){
	// // count++;
	// // }
	// // // Toast.makeText(MainActivity.this, "Satellite Count:" +
	// // count, Toast.LENGTH_SHORT).show();
	// break;
	// // 停止定位事件
	// case GpsStatus.GPS_EVENT_STOPPED:
	// break;
	// }
	// }
	// };

	/******************************** GPS位置相关 END *******************************/

	/************************************ 计时 *************************************/
	// public void wtRun() {
	// String serviceName = Context.LOCATION_SERVICE;
	// l_manager = (LocationManager) getSystemService(serviceName);
	// if (!l_manager
	// .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
	// now_lat = "";
	// now_lng = "";
	// if (pw_gpsstart != null && pw_gpsstart.isShowing()) {
	// pw_gpsstart.dismiss();
	// }
	//
	// if (gpsDialog != null && gpsDialog.isShowing()) {
	// gpsDialog.dismiss();
	// }
	// AlertDialog.Builder ad = new AlertDialog.Builder(Navigation.this);
	// ad.setTitle("温馨提示");
	// ad.setMessage("GPS尚未打开，请先打开GPS再操作！");
	// ad.setPositiveButton("去打开", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// Intent intent = new Intent(
	// Settings.ACTION_SECURITY_SETTINGS);
	// startActivityForResult(intent, 0);
	// gpsDialog.dismiss();
	// }
	// });
	// ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// gpsDialog.dismiss();
	// }
	// });
	// gpsDialog = ad.create();
	// gpsDialog.show();
	// }
	// sendTask = new SendTask();
	// sendTask.execute();
	//
	// }

	// public void countTimerStart() {
	// if (countimer != null) {
	// if (countTimerTask != null) {
	// countTimerTask.cancel();
	// }
	// countTimerTask = new CountTimerTask();
	// countimer.schedule(countTimerTask, countTimer_delay);
	// }else {
	// countimer = new Timer();
	// if (countTimerTask != null) {
	// countTimerTask.cancel();
	// }
	// countTimerTask = new CountTimerTask();
	// countimer.schedule(countTimerTask, countTimer_delay);
	// }
	// }

	// class CountTimerTask extends TimerTask {
	// @Override
	// public void run() {
	//
	// Message msg = countHandler.obtainMessage(EVENT_WT);
	// msg.sendToTarget();
	// }
	// }
	public void countTimerStart() {
		if (countimer != null) {
			countimer.cancel();
			timerTask.cancel();
		}
		countimer = new Timer();
		timerTask = new CountTimerTask();
		countimer.schedule(timerTask, 1000,60000);
	}
//	 60000 * 3
	class CountTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d("DEBUGGGGGGG", "TimerTask所在线程:"
					+ Thread.currentThread().getName());
			Message message = Message.obtain();
			;
			message.what = 0;
			handler.sendMessage(message);
		}
	};

	// 发送
	class SendTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String resultStr = null;
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String cla = cApp.lat;
			String clg = cApp.lng;
//			Log.d("转换前的坐标数据", cla+"_"+clg);
//			String tresult = GetResponseTextByGet("http://ditujiupian.com/service/api.ashx?key=113da871276d40a5aae604836c2f2823&type=gcj2wgs&lng="+clg+"&lat="+cla);
//			String tcla =  tresult.split("_")[0];
//			String tclg =  tresult.split("_")[1];
//			Log.d("转换后的坐标数据", tcla+"_"+tclg);
//			if (tcla.equals("0.0")) {
//				return "2";
//			}
			param.add(new BasicNameValuePair("tracking.userId", ""));
			param.add(new BasicNameValuePair("tracking.device", cApp.IMEI));
			param.add(new BasicNameValuePair("tracking.roadCode", dbs.round
					.getItem(cApp.RoundId).roundId));
			param.add(new BasicNameValuePair("tracking.roadName", dbs.round
					.getItem(cApp.RoundId).name));
			param.add(new BasicNameValuePair("tracking.upOrDown",
					CheckApplication.Direction == 0 ? "1" : "2"));
			param.add(new BasicNameValuePair("tracking.sendTime",
					getNowSystemTimeToSecondDate()));
			param.add(new BasicNameValuePair("tracking.weather", ""));
			param.add(new BasicNameValuePair("tracking.location",
					""));
			param.add(new BasicNameValuePair("tracking.speed", cApp.speed + ""));
			param.add(new BasicNameValuePair("tracking.geohash", ""));
			param.add(new BasicNameValuePair("tracking.lon", cApp.lat));
			param.add(new BasicNameValuePair("tracking.lat",cApp.lng));
			param.add(new BasicNameValuePair("tracking.remark", ""));
			if (!cApp.lat.equals("") && !cApp.lng.equals("")) {
				Log.d("SENDTASK+++++++++++++++++++", cApp.lat + "_" + cApp.lng);
				// // Toast.makeText(getApplicationContext(), "我在发送"+now_lat,
				// Toast.LENGTH_SHORT).show();
				// // String resultStr =
				// //
				// as.relistic.internet.HttpHelper.GetResponseTextByPost(matApp.ApiUrl_Pr
				// // +
				// //
				// "/inspection/damagedisease!roadDiseaseSave.action",param,matApp.Now_User_item.sessionId);
				//
				/*************************** 暂时屏蔽网络操作 **************************/
				resultStr = Navigation
						.GetResponseTextByPost(
								"http://218.17.162.92:9090/szcj/phone/tracking!save.action",
								param);

				Log.d("收到的服务器响应", resultStr);
			}
			return resultStr;

		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("{'success':true}")) {
				Toast.makeText(Navigation.this, "实时轨迹已上报"+cApp.lat+"_"+cApp.lng+"_"+cApp.locType, Toast.LENGTH_LONG)
						.show();
			}else if (result.equals("2")) {
				Toast.makeText(Navigation.this, "坐标转换失败，实时轨迹上传错误", Toast.LENGTH_LONG)
				.show();
			}
			else {
				Toast.makeText(Navigation.this, "实时轨迹上传错误", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// if(location == null){
			// return;
			// }

			String serviceName = Context.LOCATION_SERVICE;
			l_manager = (LocationManager) getSystemService(serviceName);
			if (!l_manager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				if (gpseDialog != null && gpseDialog.isShowing()) {
					return;
				}

				AlertDialog.Builder ad = new AlertDialog.Builder(
						Navigation.this);
				ad.setTitle("温馨提示");
				ad.setMessage("未打开GPS定位，请检查网络设置");
				ad.setPositiveButton("去打开",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivityForResult(intent, 0);
								gpseDialog.dismiss();
							}
						});
				ad.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								gpseDialog.dismiss();
							}
						});
				gpseDialog = ad.create();
				gpseDialog.show();
				return;
			}

			cApp.lat = location.getLatitude() + "";
			cApp.lng = location.getLongitude() + "";
			cApp.locType = location.getLocType();
			Log.d("定位方式", cApp.locType+"");
			cApp.speed = location.getSpeed();

		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}

	}

	public static String GetResponseTextByPost(DefaultHttpClient client,
			String httpUrl, List<NameValuePair> postParams,
			List<NameValuePair> headerParams, String encoding) {
		if (client == null) {
			client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		}

		String result = "";

		/* 建立HTTP Post联机 */
		HttpPost httpRequest = new HttpPost(httpUrl);
		/*
		 * Post运作传送变量必须用NameValuePair[]数组储存
		 */
		try {
			/* 发出HTTP request */
			httpRequest
					.setEntity(new UrlEncodedFormEntity(postParams, encoding));
			/* 取得HTTP response */

			// if (headerParams != null && headerParams.size() > 0) {
			// for (int i = 0; i < headerParams.size(); i++) {
			// NameValuePair item = headerParams.get(i);
			//
			// httpRequest.addHeader(item.getName(), item.getValue());
			// }
			// }
			HttpResponse httpResponse = client.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				result = EntityUtils.toString(httpResponse.getEntity(),
						encoding);// .toString(httpResponse.getEntity());

			}

		} catch (Exception e) {
			result = GET_RESPONSE_TEXT_ERROR;
		}
		return result;
	}

	public static String GetResponseTextByPost(String httpUrl,
			List<NameValuePair> postParams) {
		return GetResponseTextByPost(httpUrl, postParams, null, "utf-8");
	}

	public static String GetResponseTextByPost(String httpUrl,
			List<NameValuePair> postParams, List<NameValuePair> headerParams,
			String encoding) {
		return GetResponseTextByPost(null, httpUrl, postParams, headerParams,
				"utf-8");
	}
	public static String GetResponseTextByGet(String url){
		String result = "";
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet= new HttpGet(url);
//		HttpParams httpParams = client.getParams();
//		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		try {
			HttpResponse httpResponse = client.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 取出响应字符串 */
				result = EntityUtils.toString(httpResponse.getEntity(),
						"utf-8");// .toString(httpResponse.getEntity());

			}if (result!=null&&!result.equals("")) {
				JSONObject jsonObject = new JSONObject(result);
				Log.d("得到的json字符串", jsonObject.toString());
				result = jsonObject.getString("Lat")+"_"+jsonObject.getString("Lng");
				Log.d("得到的result", result);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		    return	GET_RESPONSE_TEXT_ERROR;
		}
		
		return result;
		
	}

	/************************************************ 计时 END *******************************/

	/********************************************** Menu 设置 *******************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, Menu.FIRST + 1, 0, "数据上传").setIcon(
				R.drawable.menu_datas_uploading);
		menu.add(0, Menu.FIRST + 2, 1, "数据清理").setIcon(
				R.drawable.menu_datas_clear);
		menu.add(0, Menu.FIRST + 3, 2, "历史记录").setIcon(
				R.drawable.menu_history_record);
		menu.add(1, Menu.FIRST + 4, 3, "检测更新").setIcon(
				R.drawable.menu_app_check);
		menu.add(1, Menu.FIRST + 5, 4, "关于软件").setIcon(
				R.drawable.menu_about_software);
		menu.add(1, Menu.FIRST + 6, 5, "退出应用").setIcon(R.drawable.menu_out_app);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			intent = new Intent(Navigation.this, Uploading_List.class);
			Navigation.this.startActivity(intent);
//			View upload_dialog_view = View.inflate(Navigation.this,
//					R.layout.app_uploading_dialog, null);
//
//			LinearLayout layout_close = (LinearLayout) upload_dialog_view
//					.findViewById(R.id.layout_close);
//			layout_close.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					uploading_dialog.dismiss();
//				}
//			});
//
//			// 选择上传
//			Button btn_upload_selecte = (Button) upload_dialog_view
//					.findViewById(R.id.btn_upload_selecte);
//			btn_upload_selecte.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					uploading_dialog.dismiss();
//					intent = new Intent(Navigation.this, Uploading_List.class);
//					Navigation.this.startActivity(intent);
//				}
//			});

			// 一键上传
//			Button btn_upload_onekey = (Button) upload_dialog_view
//					.findViewById(R.id.btn_upload_onekey);
//			btn_upload_onekey.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
////					OneKeyUploadingTask oneKeyUploadingTask = new OneKeyUploadingTask(
////							Navigation.this, "", dbs);
////					oneKeyUploadingTask.execute();
////					uploading_dialog.dismiss();
//					return;
//				}
//			});

//			uploading_dialog = new Dialog(Navigation.this, R.style.dialog);
//			uploading_dialog.setContentView(upload_dialog_view);
//			uploading_dialog.show();
			break;
		case Menu.FIRST + 2:
			AlertDialog.Builder clear_ad = new AlertDialog.Builder(
					Navigation.this);
			clear_ad.setTitle("温馨提示");
			clear_ad.setMessage("数据即将被清理掉，包括已上传的文本数据及图片数据。是否继续？");
			clear_ad.setPositiveButton("继续",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dbs.road_check_table.getunUploadedList().size() > 0) {
								Toast.makeText(Navigation.this,
										"有还未上传的数据存在，无法清理数据，请在所有数据都上传后再清理", 1)
										.show();
								clearDialog.dismiss();
								return;
							}
							clearDatasTask = new ClearDatasTask();
							clearDatasTask.execute();
							clearDialog.dismiss();
						}
					});
			clear_ad.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							clearDialog.dismiss();
						}
					});
			clearDialog = clear_ad.create();
			clearDialog.show();
			break;
		case Menu.FIRST + 4:
			// 当前网络监测
			boolean havanetwork = as.relistic.internet.NetWork
					.haveNetWorkd(Navigation.this);
			if (!havanetwork) {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						Navigation.this);
				ad.setTitle("无网络提示");
				ad.setMessage("当前网络不可用，请监测网络是否已连接！");
				ad.setPositiveButton("知道了",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								networkdialog.dismiss();
							}
						});
				networkdialog = ad.create();
				networkdialog.show();
			} else {
				CheckTask checkTask = new CheckTask();
				checkTask.execute();
			}
			break;
		case Menu.FIRST + 5:
			intent = new Intent(Navigation.this, About.class);
			Navigation.this.startActivity(intent);
			break;
		case Menu.FIRST + 6:
			AlertDialog.Builder ad = new AlertDialog.Builder(Navigation.this);
			ad.setTitle(R.string.navigation_back_dialog_title);
			ad.setMessage(R.string.navigation_back_dialog_message);
			ad.setPositiveButton(R.string.navigation_back_dialog_button_yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 关闭进程
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);
						}
					});
			ad.setNegativeButton(R.string.navigation_back_dialog_button_no,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							backdialog.dismiss();
						}
					});
			backdialog = ad.create();
			backdialog.show();
			break;
		case Menu.FIRST + 3:
			intent = new Intent(Navigation.this, CheckHistoricalRecords.class);
			Navigation.this.startActivity(intent);
			break;

		}
		return true;
	}

	/********************************************** Menu 设置 END ****************************/
	/*********************************** 数据清理 ***********************************/
	class ClearDatasTask extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			clearpd = new ProgressDialog(Navigation.this);
			clearpd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			clearpd.setMessage("请稍候，正在清理数据...");
			clearpd.setCancelable(false);
			clearpd.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (Integer.parseInt(values[4]) == 0) {
				clearpd.setMessage(values[3]);
			} else if (Integer.parseInt(values[2]) == 0
					&& Integer.parseInt(values[4]) == 1) {
				clearpd.setMessage("暂无数据可以清理！");
			} else if (Integer.parseInt(values[2]) > 0
					&& Integer.parseInt(values[4]) == 1) {
				clearpd.setProgress(Integer.parseInt(values[0]));
				clearpd.setMax(Integer.parseInt(values[2]));
				clearpd.setMessage(values[3] + "" + values[1]);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				return "";
			}
			publishProgress("0", "0/0", "0", "正在初始化待清理数据...", "0");
			/***** 异常图片处理 ****/
			List<String> imgPathList = new ArrayList<String>();
			List<String> imgPathArrayList = new ArrayList<String>();
			imgPathArrayList.addAll(dbs.road_check_table.getImagePathList());
			for (int i = 0; i < imgPathArrayList.size(); i++) {
				String item_str = imgPathArrayList.get(i);
				String[] item_path_array = item_str.split(",");
				for (int j = 0; j < item_path_array.length; j++) {
					if (!item_path_array[j].equals("")) {
						imgPathList.add(item_path_array[j]);
					}
				}
			}

			/************* (待改进部分；遍历sd卡指定目录下的所有图片文件) ***************/
			File index_file = null;
			Set allimgPathList = new HashSet();
			for (int i = 0; i < imgPathList.size(); i++) {
				File now_file = new File(imgPathList.get(i));
				File dir = now_file.getParentFile();
				if (dir != index_file) {
					File files[] = dir.listFiles();
					if (files == null) {
						continue;
					}
					for (File f : files) {
						String f_path = f.getPath();
						allimgPathList.add(f_path);
					}
					index_file = dir;
				}
			}
			/************* (待改进部分；遍历sd卡指定目录下的所有图片文件) END ***************/

			publishProgress("0", "0/" + allimgPathList.size(),
					allimgPathList.size() + "", "正在查找并清理异常图片数据", "1");
			for (int i = 0; i < allimgPathList.size(); i++) {
				String imgpath = allimgPathList.toArray()[i].toString();
				boolean isHave = false;
				for (int j = 0; j < imgPathList.size(); j++) {
					if (imgpath.equals(imgPathList.get(j))) {
						isHave = true;
						break;
					}
				}
				publishProgress(i + "", i + "/" + allimgPathList.size(),
						allimgPathList.size() + "", "正在查找并清理异常图片数据", "1");
				if (!isHave) {
					File del_file = new File(imgpath);
					del_file.delete();
				}

			}
			/***** 异常图片处理 END ****/

			List<Road_Check_Data_Table> clearlist = new ArrayList<Road_Check_Data_Table>();
			clearlist.addAll(dbs.road_check_table.getUploadedList());
			publishProgress("0", "0/" + clearlist.size(),
					clearlist.size() + "", "正在清理已上传数据", "1");
			for (int i = 0; i < clearlist.size(); i++) {
				Road_Check_Data_Table item = clearlist.get(i);
				String[] imgPathArray = item.imagePathString.split(",");
				publishProgress(i + "", i + "/" + clearlist.size(),
						clearlist.size() + "", "正在清理已上传数据", "1");
				for (int j = 0; j < imgPathArray.length; j++) {
					String path = imgPathArray[j];
					if (!path.equals("") && path != null) {
						File file = new File(path);
						if (file.exists()) {
							file.delete();
						}
					}
				}

				FileUtil.delAllFile(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + "com_pengtu_road_check_data"+"/");
				dbs.road_check_table.deleteDatas(item.Id + "");
				dbs.image.deleteDatas(item.Id + "");
				dbs.verifydatas.clear();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			clearpd.dismiss();
		}
	}

	/*********************************** 数据清理 END *******************************/
	/***************************** 版本检测及更新 ********************************/
	// 检测
	class CheckTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(Navigation.this, "请稍候",
					"正在获取最新版本……", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// 获取最新版本号
				List<NameValuePair> param = new ArrayList<NameValuePair>();
				String resultStr = as.relistic.internet.HttpHelper
						.GetResponseTextByPost("url", param, "sessionId");
				newVER = Integer.parseInt(resultStr);
				newVerUrl = cApp.DownLoadVer_Url;
			} catch (Exception ex) {
				newVER = 1;
				newVerUrl = "";
				webservice_result = "-1";
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			if (newVER > cApp.versionCode && newVerUrl != "") {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						Navigation.this);
				ad.setTitle(R.string.main_update_dialog_title);
				ad.setMessage(R.string.main_update_dialog_message);
				ad.setNegativeButton(R.string.main_update_dialog_button_no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface newVerDialog,
									int which) {
								// startActivity(new Intent(getApplication(),
								// startAppClass));
								newVerDialog.dismiss();
							}
						});

				ad.setPositiveButton(R.string.main_update_dialog_button_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface newVerDialog,
									int which) {
								newVerDialog.dismiss();
								// 下载
								DownloadTask task = new DownloadTask();
								task.execute(newVerUrl);
							}
						});
				newVerDialog = ad.create();
				newVerDialog.show();
			} else {
				new AlertDialog.Builder(Navigation.this)
						.setMessage("当前已是最新版！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).show();
			}
		}
	}

	// 文件保存
	private boolean saveFile(String tempFileFullPath) {
		FileOutputStream os = null;
		ByteArrayInputStream bais = null;
		// 请求数据
		HttpPost httpRequest = new HttpPost(newVerUrl);
		// 创建参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version", "android"));
		try {
			// 对提交数据进行编码
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			// 获取响应服务器的数据
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 利用字节数组流和包装的绑定数据
				byte[] data = new byte[2048];
				// 先把从服务端来的数据转化成字节数组
				data = EntityUtils.toByteArray((HttpEntity) httpResponse
						.getEntity());
				// 再创建字节数组输入流对象
				bais = new ByteArrayInputStream(data);
				File file = new File(tempFileFullPath);

				if (file.exists()) {
					file.delete();
				}

				os = new FileOutputStream(file, true);

				byte buffer[] = new byte[1024];
				int len1 = 0;

				while ((len1 = bais.read(buffer)) > 0) {
					os.write(buffer, 0, len1);
				}
				os = new FileOutputStream(file, true);
				;
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bais != null) {
					bais.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	// 更新
	private class DownloadTask extends AsyncTask<String, String, String> {
		// 更新图片显示区域,显示图片
		@Override
		protected void onPostExecute(String result) {
			newVerProgressDialog.dismiss();
			this.cancel(false);
			// 打开安装程序
			if (result != "-1" || result != "-2") {
				as.relistic.file.FileHelper.OpenFile(result, Navigation.this);
			}
			Navigation.this.finish();
		}

		@Override
		protected void onPreExecute() {
			final CharSequence strDialogTitle = getString(R.string.main_download_dialog_title);
			final CharSequence strDialogBody = getString(R.string.main_download_dialog_message);
			// 显示Progress对话框
			newVerProgressDialog = ProgressDialog.show(Navigation.this,
					strDialogTitle, strDialogBody, true);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			newVerProgressDialog.setMessage(values[0]);
		}

		@Override
		protected String doInBackground(String... params) {
			// 1.确认下载文件临时存放路径
			String tempFilePath = as.relistic.device.SDCardHelper
					.GetSDCardPath();

			if (tempFilePath == "") {
				return "-1";
			}

			tempFilePath += "/temp/";
			File f = new File(tempFilePath);
			if (!f.exists()) {
				f.mkdir();
			}

			// 2.下载文件到临时目录
			String fileName = Navigation.this.getResources().getString(
					R.string.apk_name);// as.relistic.file.FileHelper.GetFileNameFromUrl(params[0]);
			if (fileName == "") {
				return "-2"; // URL地址不正确
			}

			// 将要下载的文件存放完整路径,下载成功后发回执行安装
			String tempFileFullPath = tempFilePath + fileName;
			boolean uploadResult = saveFile(tempFileFullPath);// as.relistic.internet.UploadHelper.SaveFile(params[0],
																// tempFileFullPath);
			return tempFileFullPath;
		}
	}

	/***************************** 版本检测及更新 END ********************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder ad = new AlertDialog.Builder(Navigation.this);
			ad.setTitle(R.string.navigation_back_dialog_title);
			ad.setMessage(R.string.navigation_back_dialog_message);
			ad.setPositiveButton(R.string.navigation_back_dialog_button_yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 关闭进程
							android.os.Process.killProcess(android.os.Process
									.myPid());
							System.exit(0);
						}
					});
			ad.setNegativeButton(R.string.navigation_back_dialog_button_no,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							backdialog.dismiss();
						}
					});
			backdialog = ad.create();
			backdialog.show();
			return true;
		}
		return false;
	}

	@Override
	protected void onRestart() {
		if (cApp.Direction == 0) {
			txt_img_num_report_create.setText(getimg_num(0));
			txt_img_num_footwalk_photo.setText(getimg_num(4));
			txt_img_num_intersection_photo.setText(getimg_num(3));
			txt_img_num_guideboard_photo.setText(getimg_num(2));
			txt_img_num_start_photo.setText(getimg_num(1));
			txt_img_num_end_photo.setText(getimg_num(5));
		} else if (cApp.Direction == 1) {
			txt_img_num_report_create.setText(getimg_num(0));
			txt_img_num_footwalk_photo.setText(getimg_num(4));
			txt_img_num_intersection_photo.setText(getimg_num(3));
			txt_img_num_guideboard_photo.setText(getimg_num(2));
			txt_img_num_start_photo.setText(getimg_num(6));
			txt_img_num_end_photo.setText(getimg_num(7));
		}
		super.onRestart();
	}

	public static String getNowSystemTimeToSecondDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String nowTime = formatter.format(curDate);

		return nowTime;
	}

	public static boolean haveNetWorkd(Context context) {
		Activity netactivity = (Activity) context;
		ConnectivityManager connManager = (ConnectivityManager) netactivity
				.getSystemService(context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetworkInfo对象
		NetworkInfo NetworkInfo = connManager.getActiveNetworkInfo();
		// 获取当前的网络连接是否可用
		if (NetworkInfo != null && NetworkInfo.isAvailable()
				&& NetworkInfo.isConnected()) {
			Log.w("网络检测", "有网络");
			return true;
		}
		Log.w("网络检测", "无网络");
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 if (mLocClient != null){  
	            mLocClient.stop();  
	        }  
		 if (timerTask != null) {
				countimer.cancel();
				timerTask.cancel();
			}
		super.onDestroy();
	}
//	61 ： GPS定位结果
//	62 ： 扫描整合定位依据失败。此时定位结果无效。
//	63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
//	65 ： 定位缓存的结果。
//	66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
//	67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
//	68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
//	161： 表示网络定位结果
//	162~167： 服务端定位失败
}
