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
	private static final int REQUEST_TIMEOUT = 6*1000;//��������ʱ6����
	private static final int SO_TIMEOUT = 10*1000;  //���õȴ����ݳ�ʱʱ��10����
	private AlertDialog changeDirection_dialog;
	public static final String GET_RESPONSE_TEXT_ERROR = "GET_RESPONSE_TEXT_ERROR";
	public String resString;
	private Button btn_start;// ��ʼ
	private Button btn_stop;// ����
	private ImageButton img_btn_direction_update;// ��ʻ����ı�
	private TextView txt_round_name;// ��·����
	private TextView txt_direction;// ��ǰ��ʻ����
	private RelativeLayout layout_up_direction_start_photo;// �������
	private RelativeLayout layout_guideboard_photo;// ·������
	private RelativeLayout layout_intersection_photo;// ����·������
	private RelativeLayout layout_footwalk_photo;// ���е�����
	private RelativeLayout layout_report_create;// ������
	private RelativeLayout layout_up_direction_end_photo;// �յ�����
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
	
	

	// λ����Ϣ
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

	// ��ʱ��
	private Handler countHandler;
	private Timer countimer;
	private long countTimer_delay = 1000 * 3; // Ƶ��
	private CountTimerTask timerTask;
	private final int EVENT_WT = 0x20034;
	private SendTask sendTask;

	private DatabaseService dbs;
	private HashMap<String, Object> btnMap;
	private Animation btn_start_anim;
	private Animation btn_stop_anim_out;
	private AlertDialog backdialog;
	private Intent intent;


	
	// ��������
	private ClearDatasTask clearDatasTask;
	private AlertDialog clearDialog;
	private ProgressDialog clearpd;

	// �����ϴ�
	private Dialog uploading_dialog;

	// ��·ѡ��
	private PopupWindow pw_selecteRoad;

	// ���汾
	private AlertDialog newVerDialog = null;
	private ProgressDialog newVerProgressDialog = null;
	private String newVerUrl = "";
	private int newVER = 1;
	private ProgressDialog progressDialog = null;
	String webservice_result = "-1";
	private AlertDialog networkdialog;

	// ����
	private AlertDialog stopDialog = null;
	// ��ʱ������handler
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Log.d("DEBUGGGGGGG", "HandleMessage�����߳�:"
					+ Thread.currentThread().getName());
			// �������
			switch (msg.what) {
			case 0:
				// Toast.makeText(getApplicationContext(),
				// "���ڴ���"+now_lat+now_lng+"����"+locType+"",
				// Toast.LENGTH_SHORT).show();
				// Toast.makeText(getApplicationContext(), "���������أ�"+resString,
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
					ad.setTitle("��ܰ��ʾ");
					ad.setMessage("��������޷����λ�����꣬������������");
					ad.setPositiveButton("ȥ��",
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
					ad.setNegativeButton("ȡ��",
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
						ad.setTitle("��ܰ��ʾ");
						ad.setMessage("��������޷����λ�����꣬������������");
						ad.setPositiveButton("ȥ��",
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
						ad.setNegativeButton("ȡ��",
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
				txt_pw_message.setText("��ͨ��GPS��ȡ����λ����Ϣ���������Կ�ʼ�ˣ�");
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
		// �ؼ�����
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
		//�����°汾��⹦��
		Log.d("��ʼǰ��γ������", cApp.lat+cApp.lng);
		// ��ʼ��ť
		btn_start_anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
		layout_start.startAnimation(btn_start_anim);
		btn_start.setOnClickListener(new StartButtonOnClickListener());
		// ������ť�������
		btn_stop_anim_out = AnimationUtils.loadAnimation(this,
				R.anim.ficker_out);
		btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder s_ab = new AlertDialog.Builder(
						Navigation.this);
				s_ab.setTitle("��ܰ��ʾ");
				s_ab.setMessage("ȷ��Ҫ�����Ե�ǰ��·��Ѳ����");
				s_ab.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								stopDialog.dismiss();

								// �������ݺ�ʵ

								// �ؼ���������
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
								txt_round_name.setText("��·����");
								txt_direction.setText("������");
								layout_start.startAnimation(btn_start_anim);
								rlayout_start.setVisibility(View.VISIBLE);
								rlayout_stop.setVisibility(View.GONE);
								rlayout_stop.startAnimation(btn_stop_anim_out);

								// �Ƴ�λ�ü�����
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
				s_ab.setNegativeButton("ȡ��",
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
		// ��ʻ������İ�ť�������
		img_btn_direction_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder aBuilder = new AlertDialog.Builder(
						Navigation.this);
				aBuilder.setTitle("��ܰ��ʾ");
				aBuilder.setMessage("�Ƿ��л���·����");
				aBuilder.setCancelable(false);
				aBuilder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								changeDirection_dialog.dismiss();
							}
						});
				aBuilder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								if (cApp.Direction == 0) {
									txt_direction.setText("�����С�");
									txt_up_direction_start_photo
											.setText("�����������");
									txt_up_direction_end_photo
											.setText("�����յ�����");
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
									txt_direction.setText("�����С�");
									txt_up_direction_start_photo
											.setText("�����������");
									txt_up_direction_end_photo
											.setText("�����յ�����");
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
											"������Ч����δѡ���·��", Toast.LENGTH_SHORT)
											.show();
								}
							}

						});
				changeDirection_dialog = aBuilder.create();
				changeDirection_dialog.show();
			}
		});

		// �������
		layout_up_direction_start_photo
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (cApp.Direction == 0) {
							intent = new Intent(Navigation.this,
									RoadPointPhoto.class);
							Bundle bundle = new Bundle();
							bundle.putString("title", "�����������");
							bundle.putString("photoType", Navigation.this
									.getString(R.string.photo_type_up_origin));
							bundle.putInt("photoTypeValue", 1);
							intent.putExtras(bundle);
							Navigation.this.startActivity(intent);
						} else if (cApp.Direction == 1) {
							intent = new Intent(Navigation.this,
									RoadPointPhoto.class);
							Bundle bundle = new Bundle();
							bundle.putString("title", "�����������");
							bundle.putString("photoType", Navigation.this
									.getString(R.string.photo_type_down_origin));
							bundle.putInt("photoTypeValue", 6);
							intent.putExtras(bundle);
							Navigation.this.startActivity(intent);
						}
					}
				});
		// ·������
		layout_guideboard_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, RoadPointPhoto.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "·������");
				bundle.putString("photoType", Navigation.this
						.getString(R.string.photo_type_guideboard));
				bundle.putInt("photoTypeValue", 2);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// ����·������
		layout_intersection_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, Intersection.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "·������");
				bundle.putString("photoType", Navigation.this
						.getString(R.string.photo_type_intersection));
				bundle.putInt("photoTypeValue", 3);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// ���е�����
		layout_footwalk_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, RoadPointPhoto.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", "���е�����");
				bundle.putString("photoType",
						Navigation.this.getString(R.string.photo_type_footwalk));
				bundle.putInt("photoTypeValue", 4);
				intent.putExtras(bundle);
				Navigation.this.startActivity(intent);
			}
		});
		// ������д
		layout_report_create.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Navigation.this, Create.class);
				Navigation.this.startActivity(intent);
			}
		});
		// �յ�����
		layout_up_direction_end_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cApp.Direction == 0) {
					intent = new Intent(Navigation.this, RoadPointPhoto.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", "�����յ�����");
					bundle.putString("photoType", Navigation.this
							.getString(R.string.photo_type_up_finish));
					bundle.putInt("photoTypeValue", 5);
					intent.putExtras(bundle);
					Navigation.this.startActivity(intent);
				} else if (cApp.Direction == 1) {
					intent = new Intent(Navigation.this, RoadPointPhoto.class);
					Bundle bundle = new Bundle();
					bundle.putString("title", "�����յ�����");
					bundle.putString("photoType", Navigation.this
							.getString(R.string.photo_type_down_finish));
					bundle.putInt("photoTypeValue", 7);
					intent.putExtras(bundle);
					Navigation.this.startActivity(intent);
				}
			}
		});

		// �ؼ����Գ�ʼ��
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

	/** ��ȡͼƬ���� */
	public String getimg_num(int category) {
		Round round_item = new Round();
		round_item = dbs.round.getItem(cApp.RoundId);

		List<Road_Check_Data_Table> i_list = new ArrayList<Road_Check_Data_Table>();
		i_list.addAll(dbs.road_check_table.getList(
				round_item.roundId,
				txt_direction.getText().toString().replace("��", "")
						.replace("��", ""), category));
		int num = 0;
		for (int i = 0; i < i_list.size(); i++) {
			String[] imgArray = i_list.get(i).imagePathString.split(",");
			num += imgArray.length;
		}
		return num + "";
	}

	/**************************** ��ʼ��ť����¼� ***************************/
	class StartButtonOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// // GPSλ����Ϣ��ȡ
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
			// ad.setTitle("��ܰ��ʾ");
			// ad.setMessage("GPS��δ�򿪣����ȴ�GPS�ٲ�����");
			// ad.setPositiveButton("ȥ��",
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
			// ad.setNegativeButton("ȡ��",
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
			// ������
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
			// ������ʱ��
			mLocClient = new LocationClient(getApplicationContext());
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			
			option.setOpenGps(true);
			option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
			option.setCoorType("gcj02");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02,bd02
			option.setPriority(LocationClientOption.GpsFirst);
			option.setScanSpan(1000 * 5);// ���÷���λ����ļ��ʱ��Ϊ5000ms
			option.disableCache(true);// ��ֹ���û��涨λ
			// option.setPoiNumber(5); // ��෵��POI����
			// option.setPoiDistance(1000); // poi��ѯ����
			// option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
			mLocClient.setLocOption(option);
			mLocClient.start();
			Log.d("������γ������", cApp.lat+cApp.lng);
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
			txt_pw_message.setText("���ڻ�ȡ��λ��Ϣ�����Ժ�...");
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
//				Log.d("ѭ���еľ�γ������", cApp.lat+cApp.lng);
//				locateDialog = ProgressDialog.show(Navigation.this, "���Ժ�",
//						"���ڶ�λ...", true);
//				if (((cApp.lat != null && cApp.lng != null))
//						&& (!cApp.lat.equals("") && !cApp.lng.equals(""))) {
//					txt_pw_message.setText("��ͨ��GPS��ȡ����λ����Ϣ���������Կ�ʼ�ˣ�");
//					layout_know.setVisibility(View.VISIBLE);
//					Log.w("�õ��ľ�γ������", cApp.lat+cApp.lng);
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
			Log.d("ѭ���еľ�γ������", "ѭ���еľ�γ������"+cApp.lat+cApp.lng);
//			if (((cApp.lat != null && cApp.lng != null))
//					&& (!cApp.lat.equals("") && !cApp.lng.equals(""))) {
//				
///				Log.w("�õ��ľ�γ������", cApp.lat+cApp.lng);
//			
//			return "1";
//		}else {
//			return "2";
//		}
		for(;!(cApp.lat!=null&&cApp.lat!=""&&cApp.lat.length()>0);){
			Log.d("ѭ���еľ�γ������", "ѭ���еľ�γ������"+cApp.lat+cApp.lng);
			
		}

		return "1";
		
	}
	}

	/***************************** ��ʼ��ť����¼� END **************************/

	/******************************* GPSλ����� ********************************/
	// private GpsStatus gpsstatus;
	// private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
	// // GPS״̬�����仯ʱ����
	// @Override
	// public void onGpsStatusChanged(int event) {
	// // ��ȡ��ǰ״̬
	// gpsstatus = l_manager.getGpsStatus(null);
	// switch (event) {
	// // ��һ�ζ�λʱ���¼�
	// case GpsStatus.GPS_EVENT_FIRST_FIX:
	// txt_pw_message.setText("��ͨ��GPS��ȡ����λ����Ϣ���������Կ�ʼ�ˣ�");
	// layout_know.setVisibility(View.VISIBLE);
	// break;
	// // ��ʼ��λ���¼�
	// case GpsStatus.GPS_EVENT_STARTED:
	// break;
	// // ����GPS����״̬�¼�
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
	// // ֹͣ��λ�¼�
	// case GpsStatus.GPS_EVENT_STOPPED:
	// break;
	// }
	// }
	// };

	/******************************** GPSλ����� END *******************************/

	/************************************ ��ʱ *************************************/
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
	// ad.setTitle("��ܰ��ʾ");
	// ad.setMessage("GPS��δ�򿪣����ȴ�GPS�ٲ�����");
	// ad.setPositiveButton("ȥ��", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// Intent intent = new Intent(
	// Settings.ACTION_SECURITY_SETTINGS);
	// startActivityForResult(intent, 0);
	// gpsDialog.dismiss();
	// }
	// });
	// ad.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
			Log.d("DEBUGGGGGGG", "TimerTask�����߳�:"
					+ Thread.currentThread().getName());
			Message message = Message.obtain();
			;
			message.what = 0;
			handler.sendMessage(message);
		}
	};

	// ����
	class SendTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String resultStr = null;
			List<NameValuePair> param = new ArrayList<NameValuePair>();
			String cla = cApp.lat;
			String clg = cApp.lng;
//			Log.d("ת��ǰ����������", cla+"_"+clg);
//			String tresult = GetResponseTextByGet("http://ditujiupian.com/service/api.ashx?key=113da871276d40a5aae604836c2f2823&type=gcj2wgs&lng="+clg+"&lat="+cla);
//			String tcla =  tresult.split("_")[0];
//			String tclg =  tresult.split("_")[1];
//			Log.d("ת�������������", tcla+"_"+tclg);
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
				// // Toast.makeText(getApplicationContext(), "���ڷ���"+now_lat,
				// Toast.LENGTH_SHORT).show();
				// // String resultStr =
				// //
				// as.relistic.internet.HttpHelper.GetResponseTextByPost(matApp.ApiUrl_Pr
				// // +
				// //
				// "/inspection/damagedisease!roadDiseaseSave.action",param,matApp.Now_User_item.sessionId);
				//
				/*************************** ��ʱ����������� **************************/
				resultStr = Navigation
						.GetResponseTextByPost(
								"http://218.17.162.92:9090/szcj/phone/tracking!save.action",
								param);

				Log.d("�յ��ķ�������Ӧ", resultStr);
			}
			return resultStr;

		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("{'success':true}")) {
				Toast.makeText(Navigation.this, "ʵʱ�켣���ϱ�"+cApp.lat+"_"+cApp.lng+"_"+cApp.locType, Toast.LENGTH_LONG)
						.show();
			}else if (result.equals("2")) {
				Toast.makeText(Navigation.this, "����ת��ʧ�ܣ�ʵʱ�켣�ϴ�����", Toast.LENGTH_LONG)
				.show();
			}
			else {
				Toast.makeText(Navigation.this, "ʵʱ�켣�ϴ�����", Toast.LENGTH_LONG)
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
				ad.setTitle("��ܰ��ʾ");
				ad.setMessage("δ��GPS��λ��������������");
				ad.setPositiveButton("ȥ��",
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
				ad.setNegativeButton("ȡ��",
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
			Log.d("��λ��ʽ", cApp.locType+"");
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

		/* ����HTTP Post���� */
		HttpPost httpRequest = new HttpPost(httpUrl);
		/*
		 * Post�������ͱ���������NameValuePair[]���鴢��
		 */
		try {
			/* ����HTTP request */
			httpRequest
					.setEntity(new UrlEncodedFormEntity(postParams, encoding));
			/* ȡ��HTTP response */

			// if (headerParams != null && headerParams.size() > 0) {
			// for (int i = 0; i < headerParams.size(); i++) {
			// NameValuePair item = headerParams.get(i);
			//
			// httpRequest.addHeader(item.getName(), item.getValue());
			// }
			// }
			HttpResponse httpResponse = client.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ȡ����Ӧ�ַ��� */
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
				/* ȡ����Ӧ�ַ��� */
				result = EntityUtils.toString(httpResponse.getEntity(),
						"utf-8");// .toString(httpResponse.getEntity());

			}if (result!=null&&!result.equals("")) {
				JSONObject jsonObject = new JSONObject(result);
				Log.d("�õ���json�ַ���", jsonObject.toString());
				result = jsonObject.getString("Lat")+"_"+jsonObject.getString("Lng");
				Log.d("�õ���result", result);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		    return	GET_RESPONSE_TEXT_ERROR;
		}
		
		return result;
		
	}

	/************************************************ ��ʱ END *******************************/

	/********************************************** Menu ���� *******************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, Menu.FIRST + 1, 0, "�����ϴ�").setIcon(
				R.drawable.menu_datas_uploading);
		menu.add(0, Menu.FIRST + 2, 1, "��������").setIcon(
				R.drawable.menu_datas_clear);
		menu.add(0, Menu.FIRST + 3, 2, "��ʷ��¼").setIcon(
				R.drawable.menu_history_record);
		menu.add(1, Menu.FIRST + 4, 3, "������").setIcon(
				R.drawable.menu_app_check);
		menu.add(1, Menu.FIRST + 5, 4, "�������").setIcon(
				R.drawable.menu_about_software);
		menu.add(1, Menu.FIRST + 6, 5, "�˳�Ӧ��").setIcon(R.drawable.menu_out_app);
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
//			// ѡ���ϴ�
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

			// һ���ϴ�
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
			clear_ad.setTitle("��ܰ��ʾ");
			clear_ad.setMessage("���ݼ�������������������ϴ����ı����ݼ�ͼƬ���ݡ��Ƿ������");
			clear_ad.setPositiveButton("����",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (dbs.road_check_table.getunUploadedList().size() > 0) {
								Toast.makeText(Navigation.this,
										"�л�δ�ϴ������ݴ��ڣ��޷��������ݣ������������ݶ��ϴ���������", 1)
										.show();
								clearDialog.dismiss();
								return;
							}
							clearDatasTask = new ClearDatasTask();
							clearDatasTask.execute();
							clearDialog.dismiss();
						}
					});
			clear_ad.setNegativeButton("ȡ��",
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
			// ��ǰ������
			boolean havanetwork = as.relistic.internet.NetWork
					.haveNetWorkd(Navigation.this);
			if (!havanetwork) {
				AlertDialog.Builder ad = new AlertDialog.Builder(
						Navigation.this);
				ad.setTitle("��������ʾ");
				ad.setMessage("��ǰ���粻���ã����������Ƿ������ӣ�");
				ad.setPositiveButton("֪����",
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
							// �رս���
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

	/********************************************** Menu ���� END ****************************/
	/*********************************** �������� ***********************************/
	class ClearDatasTask extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			clearpd = new ProgressDialog(Navigation.this);
			clearpd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			clearpd.setMessage("���Ժ�������������...");
			clearpd.setCancelable(false);
			clearpd.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (Integer.parseInt(values[4]) == 0) {
				clearpd.setMessage(values[3]);
			} else if (Integer.parseInt(values[2]) == 0
					&& Integer.parseInt(values[4]) == 1) {
				clearpd.setMessage("�������ݿ�������");
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
			publishProgress("0", "0/0", "0", "���ڳ�ʼ������������...", "0");
			/***** �쳣ͼƬ���� ****/
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

			/************* (���Ľ����֣�����sd��ָ��Ŀ¼�µ�����ͼƬ�ļ�) ***************/
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
			/************* (���Ľ����֣�����sd��ָ��Ŀ¼�µ�����ͼƬ�ļ�) END ***************/

			publishProgress("0", "0/" + allimgPathList.size(),
					allimgPathList.size() + "", "���ڲ��Ҳ������쳣ͼƬ����", "1");
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
						allimgPathList.size() + "", "���ڲ��Ҳ������쳣ͼƬ����", "1");
				if (!isHave) {
					File del_file = new File(imgpath);
					del_file.delete();
				}

			}
			/***** �쳣ͼƬ���� END ****/

			List<Road_Check_Data_Table> clearlist = new ArrayList<Road_Check_Data_Table>();
			clearlist.addAll(dbs.road_check_table.getUploadedList());
			publishProgress("0", "0/" + clearlist.size(),
					clearlist.size() + "", "�����������ϴ�����", "1");
			for (int i = 0; i < clearlist.size(); i++) {
				Road_Check_Data_Table item = clearlist.get(i);
				String[] imgPathArray = item.imagePathString.split(",");
				publishProgress(i + "", i + "/" + clearlist.size(),
						clearlist.size() + "", "�����������ϴ�����", "1");
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

	/*********************************** �������� END *******************************/
	/***************************** �汾��⼰���� ********************************/
	// ���
	class CheckTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(Navigation.this, "���Ժ�",
					"���ڻ�ȡ���°汾����", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// ��ȡ���°汾��
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
								// ����
								DownloadTask task = new DownloadTask();
								task.execute(newVerUrl);
							}
						});
				newVerDialog = ad.create();
				newVerDialog.show();
			} else {
				new AlertDialog.Builder(Navigation.this)
						.setMessage("��ǰ�������°棡")
						.setPositiveButton("ȷ��",
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

	// �ļ�����
	private boolean saveFile(String tempFileFullPath) {
		FileOutputStream os = null;
		ByteArrayInputStream bais = null;
		// ��������
		HttpPost httpRequest = new HttpPost(newVerUrl);
		// ��������
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("version", "android"));
		try {
			// ���ύ���ݽ��б���
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			// ��ȡ��Ӧ������������
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// �����ֽ��������Ͱ�װ�İ�����
				byte[] data = new byte[2048];
				// �ȰѴӷ������������ת�����ֽ�����
				data = EntityUtils.toByteArray((HttpEntity) httpResponse
						.getEntity());
				// �ٴ����ֽ���������������
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

	// ����
	private class DownloadTask extends AsyncTask<String, String, String> {
		// ����ͼƬ��ʾ����,��ʾͼƬ
		@Override
		protected void onPostExecute(String result) {
			newVerProgressDialog.dismiss();
			this.cancel(false);
			// �򿪰�װ����
			if (result != "-1" || result != "-2") {
				as.relistic.file.FileHelper.OpenFile(result, Navigation.this);
			}
			Navigation.this.finish();
		}

		@Override
		protected void onPreExecute() {
			final CharSequence strDialogTitle = getString(R.string.main_download_dialog_title);
			final CharSequence strDialogBody = getString(R.string.main_download_dialog_message);
			// ��ʾProgress�Ի���
			newVerProgressDialog = ProgressDialog.show(Navigation.this,
					strDialogTitle, strDialogBody, true);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			newVerProgressDialog.setMessage(values[0]);
		}

		@Override
		protected String doInBackground(String... params) {
			// 1.ȷ�������ļ���ʱ���·��
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

			// 2.�����ļ�����ʱĿ¼
			String fileName = Navigation.this.getResources().getString(
					R.string.apk_name);// as.relistic.file.FileHelper.GetFileNameFromUrl(params[0]);
			if (fileName == "") {
				return "-2"; // URL��ַ����ȷ
			}

			// ��Ҫ���ص��ļ��������·��,���سɹ��󷢻�ִ�а�װ
			String tempFileFullPath = tempFilePath + fileName;
			boolean uploadResult = saveFile(tempFileFullPath);// as.relistic.internet.UploadHelper.SaveFile(params[0],
																// tempFileFullPath);
			return tempFileFullPath;
		}
	}

	/***************************** �汾��⼰���� END ********************************/
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
							// �رս���
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
		// ��ȡ��������״̬��NetworkInfo����
		NetworkInfo NetworkInfo = connManager.getActiveNetworkInfo();
		// ��ȡ��ǰ�����������Ƿ����
		if (NetworkInfo != null && NetworkInfo.isAvailable()
				&& NetworkInfo.isConnected()) {
			Log.w("������", "������");
			return true;
		}
		Log.w("������", "������");
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
//	61 �� GPS��λ���
//	62 �� ɨ�����϶�λ����ʧ�ܡ���ʱ��λ�����Ч��
//	63 �� �����쳣��û�гɹ���������������󡣴�ʱ��λ�����Ч��
//	65 �� ��λ����Ľ����
//	66 �� ���߶�λ�����ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
//	67 �� ���߶�λʧ�ܡ�ͨ��requestOfflineLocaiton����ʱ��Ӧ�ķ��ؽ��
//	68 �� ��������ʧ��ʱ�����ұ������߶�λʱ��Ӧ�ķ��ؽ��
//	161�� ��ʾ���綨λ���
//	162~167�� ����˶�λʧ��
}
