package com.road.check.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.R.id;
import android.R.integer;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.CheckApplication;
import com.road.check.common.Header;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.navigation.Navigation;
import com.road.check.sqlite.DatabaseService;

public class Uploading_List extends ActivityBase {
	private Header header;
	private RadioGroup rg_slelect_type;
	private RadioButton rb_all;
	private RadioButton rb_road_point;
	private RadioButton rb_report;
	private LinearLayout layout_check;
	private ImageView img_checked;

	private DatabaseService dbs;
	private ProgressDialog progressDialog = null;
	private AddDatasTask addDatasTask;
	private int selecetAll = 1;
	private int slelect_type_index = -1;

	// �б�
	private ListView upload_listview;
	private UploadingAdapter uploadingAdapter;
	private List<Road_Check_Data_Table> road_check_list;

	// �����ϴ�
	private UploadingTask uploadingTask;
	private ProgressDialog uploadProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_uploading_list);

		// ͷ������
		header = new Header(this, "�����ϴ�", "����",
				new HeaderLeftOnClickListener(), "�ϴ�",
				new HeaderRightOnClickListener());

		rg_slelect_type = (RadioGroup) this.findViewById(R.id.rg_slelect_type);
		rb_all = (RadioButton) this.findViewById(R.id.rb_all);
		rb_road_point = (RadioButton) this.findViewById(R.id.rb_road_point);
		rb_report = (RadioButton) this.findViewById(R.id.rb_report);
		layout_check = (LinearLayout) this.findViewById(R.id.layout_check);
		img_checked = (ImageView) this.findViewById(R.id.img_checked);

		dbs = new DatabaseService(this);

		// ����ѡ��
		rb_all.setChecked(true);
		rg_slelect_type
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (rb_all.getId() == checkedId) {
							slelect_type_index = -1;
						} else if (rb_road_point.getId() == checkedId) {
							slelect_type_index = 1;
						} else if (rb_report.getId() == checkedId) {
							slelect_type_index = 0;
						}

						addDatasTask = new AddDatasTask();
						addDatasTask.execute(slelect_type_index);
					}
				});

		// ȫѡ�¼�
		layout_check.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				road_check_list.clear();
				if (selecetAll == 1) {
					selecetAll = 0;
					img_checked.setImageResource(R.drawable.check_mark);
					dbs.road_check_table.updateState(1, slelect_type_index);
					road_check_list.addAll(dbs.road_check_table
							.getList(slelect_type_index));
					uploadingAdapter.notifyDataSetChanged();
				} else {
					selecetAll = 1;
					img_checked.setImageResource(0);
					dbs.road_check_table.updateState(0, slelect_type_index);
					road_check_list.addAll(dbs.road_check_table
							.getList(slelect_type_index));
					uploadingAdapter.notifyDataSetChanged();
				}
				return false;
			}
		});

		// �б�
		upload_listview = (ListView) this.findViewById(R.id.upload_listview);
		road_check_list = new ArrayList<Road_Check_Data_Table>();
		uploadingAdapter = new UploadingAdapter(this, road_check_list,
				upload_listview, dbs, img_checked);
		upload_listview.setAdapter(uploadingAdapter);

		// ִ���б����ݼ�������
		dbs.road_check_table.updateState(0);
		addDatasTask = new AddDatasTask();
		addDatasTask.execute(slelect_type_index);
	}

	/** ͷ����߰�ť����¼�:���� */
	class HeaderLeftOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Uploading_List.this.finish();
		}
	}

	/** ͷ���ұ߰�ť����¼����ϴ� */
	class HeaderRightOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			uploadingTask = new UploadingTask();
			uploadingTask.execute();
		}
	}

	/** �����ϴ����� */
	class UploadingTask extends AsyncTask<String, String, String> {
		int err_count=0;
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			uploadProgressDialog.setProgress(Integer.parseInt(values[0]));
			uploadProgressDialog.setMax(Integer.parseInt(values[1]));
			uploadProgressDialog.setMessage("�����ϴ�"+values[0]+"/"+values[1]);
		}
		
		@Override
		protected void onPreExecute() {
			uploadProgressDialog = new ProgressDialog(Uploading_List.this);
			uploadProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			uploadProgressDialog.setMessage("���Ժ������ϴ�����");
			uploadProgressDialog.setCancelable(false);
			uploadProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			int count=0;
			String rs = "";
			String resultStr = "";
			List<String> imgPathList = new ArrayList<String>();
			HashMap<String, String> imgLocation = new HashMap<String, String>();
			HashMap<String, String> imgName = new HashMap<String, String>();
			HashMap<String, String> imgDescribe = new HashMap<String, String>();
			HashMap<String, String> iName = new HashMap<String, String>();
			HashMap<String, String> iDistence = new HashMap<String, String>();
			HashMap<String, String> iStartFrom = new HashMap<String, String>();
			HashMap<String, String> iEndFrom = new HashMap<String, String>();
			List<Road_Check_Data_Table> selectedList = new ArrayList<Road_Check_Data_Table>();
			selectedList.addAll(dbs.road_check_table.getSelecteList(1));
			err_count = selectedList.size();
			if (selectedList.size()==0) {
				return "�ޱ�ѡ";
			}
			for (int i = 0; i < selectedList.size(); i++) {

				Road_Check_Data_Table item = selectedList.get(i);
				if (item.reportName.equals("·�没���������¼��")) {
					String filePathList="";
					String imgdatalist="";
					int pavementType = 20;
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));

					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					if (item.r_roundTypeNum.equals("����·��")) {
						pavementType = 10;
					} else if (item.r_roundTypeNum.equals("ˮ��·��")) {
						pavementType = 20;
					} else if (item.r_roundTypeNum.equals("ɳʯ·��")) {
						pavementType = 30;
					} else {
						pavementType = 99;
					}
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();

					param.add(new BasicNameValuePair("entity.photo",
							filePathList));
					try {
						param.add(new BasicNameValuePair("photoData",
								imgdatalist));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum",
							item.longFromStartNum));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					param.add(new BasicNameValuePair("entity.pavementType",
							pavementType + ""));
					param.add(new BasicNameValuePair("entity.diseaseType", ""));
					param.add(new BasicNameValuePair("entity.diseaseName",
							item.diseaseString));
					param.add(new BasicNameValuePair("entity.detectWidth",
							"12.34"));
					param.add(new BasicNameValuePair("entity.degree", "�̶�"));
					param.add(new BasicNameValuePair("entity.unit", "��λ"));
					param.add(new BasicNameValuePair("entity.measure", "12.1"));
					param.add(new BasicNameValuePair("entity.degreeWidth",
							"1.5"));
					param.add(new BasicNameValuePair("entity.degreeHight",
							"2.1"));
					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));

					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/urbanpcioriginal!save.action",
									param);
					Log.d("UPLOADING·�没���������¼��",
							resultStr
									+ imgLocation.get(imgPathList.get(0))
											.split("_")[0]
									+ imgLocation.get(imgPathList.get(0))
											.split("_")[1]);
				} else if (item.reportName.equals("·������״�������")) {
					String filePathList="";
					String imgdatalist="";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();

					param.add(new BasicNameValuePair("entity.photo",
							filePathList));
					try {
						param.add(new BasicNameValuePair("photoData",
								imgdatalist));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum", item.mark));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					String diseaseType = "0";
					if (item.diseaseString.equals("·��߹�����")) {
						diseaseType = "01";
					} else if (item.diseaseString.equals("·����")) {
						diseaseType = "02";
					} else if (item.diseaseString.equals("����̮��")) {
						diseaseType = "03";
					} else if (item.diseaseString.equals("ˮ�ٳ幵")) {
						diseaseType = "04";
					} else if (item.diseaseString.equals("·����������")) {
						diseaseType = "05";
					} else if (item.diseaseString.equals("·Եʯȱ��")) {
						diseaseType = "06";
					} else if (item.diseaseString.equals("·������")) {
						diseaseType = "07";
					} else if (item.diseaseString.equals("��ˮϵͳ����")) {
						diseaseType = "08";
					}
					param.add(new BasicNameValuePair("entity.diseaseType",
							diseaseType));
					param.add(new BasicNameValuePair("entity.diseaseName",
							item.diseaseString));
					String degree = "";
					if (item.b_diseaseDegree.equals("��")) {
						degree = "1";
					} else if (item.b_diseaseDegree.equals("��")) {
						degree = "2";
					} else if (item.b_diseaseDegree.equals("��")) {
						degree = "3";
					} else if (item.b_diseaseDegree.equals("")) {
						degree = "";
					}
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.degree", degree));
					param.add(new BasicNameValuePair("entity.unit", "��λ"));
					param.add(new BasicNameValuePair("entity.measure", "12.1"));
					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/roadscioriginal!save.action",
									param);
					Log.d("UPLOADING·������״�������", resultStr);
				} else if (item.reportName.equals("������ʩ��״�������")) {
					String filePathList="";
					String imgdatalist="";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					param.add(new BasicNameValuePair("entity.photo",
							filePathList));
					try {
						param.add(new BasicNameValuePair("photoData",
								imgdatalist));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum", item.mark));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					String diseaseType = "0";
					if (item.diseaseString.equals("������ʩȱ��")) {
						diseaseType = "01";
					} else if (item.diseaseString.equals("����դ��")) {
						diseaseType = "02";
					} else if (item.diseaseString.equals("��ʾȱ��")) {
						diseaseType = "03";
					} else if (item.diseaseString.equals("����ȱ��")) {
						diseaseType = "04";
					} else if (item.diseaseString.equals("�̻��ܻ�����")) {
						diseaseType = "05";
					}
					param.add(new BasicNameValuePair("entity.diseaseType",
							diseaseType));
					param.add(new BasicNameValuePair("entity.diseaseName",
							item.diseaseString));
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					String degree = "";
					if (item.b_diseaseDegree.equals("��")) {
						degree = "1";
					} else if (item.b_diseaseDegree.equals("��")) {
						degree = "2";
					} else if (item.b_diseaseDegree.equals("��")) {
						degree = "3";
					}else if(item.b_diseaseDegree.equals("")){
						degree = "3";
					}
					param.add(new BasicNameValuePair("entity.degree",degree));
					param.add(new BasicNameValuePair("entity.unit", "��λ"));
					param.add(new BasicNameValuePair("entity.measure", "12.1"));
					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/roadtcioriginal!save.action",
									param);
					Log.d("UPLOADING������ʩ��״�������", resultStr);
				} else if (item.reportName.equals("·������ˮ��ʩ����״������¼��")) {
					String filePathList = "";
					String imgdatalist = "";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();

					param.add(new BasicNameValuePair("entity.photo",filePathList));

					param.add(new BasicNameValuePair("photoData", imgdatalist));

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum",
							item.longFromStartNum));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					String roadDiseaseType1 = "0";
					String roadDiseaseType2 = "0";
					String roadDiseaseType3 = "0";
					String roadDiseaseType4 = "0";
					String roadDiseaseType5 = "0";

					if (item.d_roadbedDiseaseString.equals("�����幵")) {
						roadDiseaseType1 = "1";

					} else if (item.d_roadbedDiseaseString.equals("��������")) {
						roadDiseaseType2 = "1";
					} else if (item.d_roadbedDiseaseString.equals("��������")) {
						roadDiseaseType3 = "1";
					} else if (item.d_dewateringDiseaseSting.equals("����")) {
						roadDiseaseType4 = "1";
					} else if (item.d_dewateringDiseaseSting.equals("����")) {
						roadDiseaseType5 = "1";
					}
					param.add(new BasicNameValuePair("entity.disease1",
							roadDiseaseType1));
					param.add(new BasicNameValuePair("entity.disease2",
							roadDiseaseType2));
					param.add(new BasicNameValuePair("entity.disease3",
							roadDiseaseType3));
					param.add(new BasicNameValuePair("entity.disease4",
							roadDiseaseType4));
					param.add(new BasicNameValuePair("entity.disease5",
							roadDiseaseType5));

					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/urbansdloriginal!save.action",
									param);
					Log.d("UPLOADING·������ˮ��ʩ����״������¼��", resultStr);
				} else if (item.reportName.equals("���е�����״������¼��")) {
					String filePathList="";
					String imgdatalist="";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();

					param.add(new BasicNameValuePair("entity.photo",
							filePathList));
					try {
						param.add(new BasicNameValuePair("photoData",
								imgdatalist));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum",
							item.longFromStartNum));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					String diseaseType1 = "0";
					String diseaseType2 = "0";
					String diseaseType3 = "0";
					String diseaseType4 = "0";
					String diseaseType5 = "0";
					String diseaseType6 = "0";

					if (item.diseaseString.equals("�ѷ�")) {
						diseaseType1 = "1";
					} else if (item.diseaseString.equals("�Ӷ�")) {
						diseaseType2 = "1";
					} else if (item.diseaseString.equals("��̨")) {
						diseaseType3 = "1";
					} else if (item.diseaseString.equals("����")) {
						diseaseType4 = "1";
					} else if (item.diseaseString.equals("����")) {
						diseaseType5 = "1";
					} else if (item.diseaseString.equals("Ԥ�ư�ȱʧ")) {
						diseaseType6 = "1";
					}
					param.add(new BasicNameValuePair("entity.disease1",
							diseaseType1));
					param.add(new BasicNameValuePair("entity.disease2",
							diseaseType2));
					param.add(new BasicNameValuePair("entity.disease3",
							diseaseType3));
					param.add(new BasicNameValuePair("entity.disease4",
							diseaseType4));
					param.add(new BasicNameValuePair("entity.disease5",
							diseaseType5));
					param.add(new BasicNameValuePair("entity.disease6",
							diseaseType6));
					param.add(new BasicNameValuePair("entity.damaged",
							item.f_damagedArea));
					param.add(new BasicNameValuePair("entity.sidewalkWidth",
							item.f_footwalkwidth));
					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/urbanploriginal!save.action",
									param);
					Log.d("UPLOADING���е�����״������¼��", resultStr);
				} else if (item.reportName.equals("ƽ���ȼ��ԭʼ��¼")) {
					int direction = 1;
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum",
							item.longFromStartNum));
					String[] evnum = item.e_evennessNum.split(",");
					for (int j = 0; j < evnum.length; j++) {
						param.add(new BasicNameValuePair("entity.tally"
								+ (j + 1), evnum[j]));
					}

					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/urbanirioriginal!save.action",
									param);
					Log.d("UPLOADINGƽ���ȼ��ԭʼ��¼", resultStr);
				} else if (item.reportName.equals("������ʩ����״������¼��")) {
					String filePathList="";
					String imgdatalist="";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					for (int j = 0; j < imgPathList.size(); j++) {
						if (j > 0) {
							filePathList += "#";
							imgdatalist += "#";
						}
						filePathList += imgPathList.get(j);
						try {
							imgdatalist += encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();

					param.add(new BasicNameValuePair("entity.photo",
							filePathList));
					try {
						param.add(new BasicNameValuePair("photoData",
								imgdatalist));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.surveyDate",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.pegNum",
							item.longFromStartNum));
					param.add(new BasicNameValuePair("entity.pegOffset",
							"123.45"));
					String diseaseType1 = "0";
					String diseaseType2 = "0";
					String diseaseType3 = "0";
					String diseaseType4 = "0";
					String diseaseType5 = "0";

					if (item.o_structure.equals("����")) {
						diseaseType1 = "1";
					} else if (item.o_structure.equals("����")) {
						diseaseType2 = "1";
					} else if (item.o_structure.equals("����ʧЧ")) {
						diseaseType3 = "1";
					} else if (item.o_facility.equals("����")) {
						diseaseType4 = "1";
					} else if (item.diseaseString.equals("����ʧЧ")) {
						diseaseType5 = "1";
					}
					param.add(new BasicNameValuePair("entity.disease1",
							diseaseType1));
					param.add(new BasicNameValuePair("entity.disease2",
							diseaseType2));
					param.add(new BasicNameValuePair("entity.disease3",
							diseaseType3));
					param.add(new BasicNameValuePair("entity.disease4",
							diseaseType4));
					param.add(new BasicNameValuePair("entity.disease5",
							diseaseType5));
					param.add(new BasicNameValuePair("entity.remark",
							item.remark));
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.x", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.y", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/urbanqloriginal!save.action",
									param);
					Log.d("UPLOADING������ʩ����״������¼��", resultStr);
				} else if (item.category == 1 || item.category == 2
						|| item.category == 4 || item.category == 6
						|| item.category == 5 || item.category == 7
						|| item.category == 3) {
					String filePathList="";
					String dataList="";
					String nameList="";
					int direction = 1;
					imgPathList.clear();
					imgPathList.addAll(dbs.image.getPathList(item.Id));
					imgLocation.put(imgPathList.get(0),
							dbs.image.getItemLocation(imgPathList.get(0)));
					imgName.put(imgPathList.get(0),
							dbs.image.getItemName(imgPathList.get(0)));
					imgDescribe.put(imgPathList.get(0),
							dbs.image.getItemDescribe(imgPathList.get(0)));
					iName.put(imgPathList.get(0),
							dbs.image.getItemI_Name(imgPathList.get(0)));
					iDistence.put(imgPathList.get(0),
							dbs.image.getItemI_Distance(imgPathList.get(0)));
					iStartFrom.put(imgPathList.get(0), dbs.image
							.getItemI_Start_From_Start(imgPathList.get(0)));
					iEndFrom.put(imgPathList.get(0), dbs.image
							.getItemI_End_From_Start(imgPathList.get(0)));
					if (item.direction.equals("����")) {
						direction = 1;
					} else {
						direction = 2;
					}
					for(int j=0;j<imgPathList.size();j++){
						if (j>0) {
							filePathList +="#";
							dataList+="#";
							nameList+="#";
						}
						filePathList +=imgPathList.get(j);
						nameList+=imgName.get(imgPathList.get(j));
						try {
							dataList+=encodeBase64File(imgPathList.get(j));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					List<NameValuePair> param = new ArrayList<NameValuePair>();
					param.add(new BasicNameValuePair("device",
							cApp.IMEI));
					param.add(new BasicNameValuePair("entity.photoPath",
							filePathList));
					param.add(new BasicNameValuePair("entity.photoName",
							nameList));
					param.add(new BasicNameValuePair("entity.photoDescribe",
							imgDescribe.get(imgPathList.get(0))));
					try {
						param.add(new BasicNameValuePair("photoData",
								dataList));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					param.add(new BasicNameValuePair("entity.userId", "�û�001"));
					param.add(new BasicNameValuePair("entity.device", cApp.IMEI));
					param.add(new BasicNameValuePair("entity.roadCode",
							item.roundId));
					param.add(new BasicNameValuePair("entity.roadName",
							item.roundName));
					param.add(new BasicNameValuePair("entity.upOrDown",
							direction + ""));
					param.add(new BasicNameValuePair("entity.sendTime",
							item.deteTime));
					param.add(new BasicNameValuePair("entity.weather", "��"));
					param.add(new BasicNameValuePair("entity.location",
							"location�ص�λ��"));
					param.add(new BasicNameValuePair("entity.speed", cApp.speed
							+ ""));
					param.add(new BasicNameValuePair("entity.geohash",
							"geohash"));
					if (item.category == 1 || item.category == 6) {
						param.add(new BasicNameValuePair("entity.photoType",
								1 + ""));
					} else if (item.category == 2) {
						param.add(new BasicNameValuePair("entity.photoType",
								2 + ""));
					} else if (item.category == 4) {
						param.add(new BasicNameValuePair("entity.photoType",
								3 + ""));
					} else if (item.category == 3) {
						param.add(new BasicNameValuePair("entity.photoType",
								4 + ""));
						// int k = 0;
						// while(iName.get(imgPathList.get(0))==null){
						// k++;
						// }
						param.add(new BasicNameValuePair("entity.crossingName",
								iName.get(imgPathList.get(0))));
						param.add(new BasicNameValuePair("entity.pegNum",
								iDistence.get(imgPathList.get(0))));
						param.add(new BasicNameValuePair(
								"entity.crossingStartPegNum", iStartFrom
										.get(imgPathList.get(0))));
						param.add(new BasicNameValuePair(
								"entity.crossingEndPegNum", iEndFrom
										.get(imgPathList.get(0))));
					} else {
						param.add(new BasicNameValuePair("entity.photoType",
								5 + ""));
					}
					param.add(new BasicNameValuePair("entity.lat", imgLocation
							.get(imgPathList.get(0)).split("_")[0]));
					param.add(new BasicNameValuePair("entity.lon", imgLocation
							.get(imgPathList.get(0)).split("_")[1]));
					param.add(new BasicNameValuePair("entity.remark", "��ע"));
					Log.i("cc", param.toString());
					resultStr = Navigation
							.GetResponseTextByPost(
									"http://218.17.162.92:9090/szcj/phone/trackingphoto!save.action",
									param);
					Log.d("��������", resultStr);
				}
				publishProgress(i+"",err_count+"");
				if (resultStr.equals("{'success':true}")) {
					count++;
					continue;
				}else{
					if (rs!="") {
						rs+="_";
					}
					dbs.road_check_table.updateErrorUploadingState(item.Id+"");
					rs +=count+"";
					count++;
					continue;
				}
				
			}
			Log.w("�������б�", rs);
			return rs;

		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("")) {
				uploadProgressDialog.dismiss();
				selecetAll = 1;
				img_checked.setImageResource(0);
				dbs.road_check_table.updateUploadingState();
				road_check_list.clear();
				road_check_list.addAll(dbs.road_check_table
						.getList(slelect_type_index));
				uploadingAdapter.notifyDataSetChanged();
				Toast.makeText(Uploading_List.this, "�ϴ��ɹ�" + result,
						Toast.LENGTH_SHORT).show();
			} else if (result.equals("�ޱ�ѡ")) {
				uploadProgressDialog.dismiss();
				Toast.makeText(Uploading_List.this, "δѡ��Ҫ�ϴ��ļ�¼���빴ѡ���ϴ�", 0).show();
			}else {
				String[] error =result.split("_");
				uploadProgressDialog.dismiss();
				selecetAll = 1;
				dbs.road_check_table.updateUploadingState();
				img_checked.setImageResource(0);
				if (error.length>0) {
//					ArrayList<Road_Check_Data_Table> temp = new ArrayList<Road_Check_Data_Table>();
//					for(int i=0;i<error.length;i++){
//						temp.add(road_check_list.get(Integer.parseInt(error[i])));
//					}
//					dbs.road_check_table.updateState(0, slelect_type_index);
					
					road_check_list.clear();
					road_check_list.addAll(dbs.road_check_table
							.getList(slelect_type_index));
					uploadingAdapter.notifyDataSetChanged();
					if (error.length==err_count) {
						Toast.makeText(Uploading_List.this, "��������ϴ�ʧ��",
								Toast.LENGTH_SHORT).show();
						
					}else {
						Toast.makeText(Uploading_List.this, "������󣬲���δ�ϴ����",
								Toast.LENGTH_SHORT).show();
					}
					
				}else {
					Toast.makeText(Uploading_List.this, "������������ϴ�ʧ��",
							Toast.LENGTH_SHORT).show();
				}
				
			}

		}
	}

	/** ���ݼ��� */
	class AddDatasTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected void onPreExecute() {
			road_check_list.clear();
			progressDialog = ProgressDialog.show(Uploading_List.this, "���Ժ�",
					"���ڼ����б�����...", true);
		}

		@Override
		protected String doInBackground(Integer... params) {
			road_check_list.addAll(dbs.road_check_table.getList(params[0]));
			return "0";
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();

			uploadingAdapter.notifyDataSetChanged();
		}
	}

	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		// return new android.util.Base64;
		return android.util.Base64.encodeToString(buffer, Base64.DEFAULT);

	}

}
