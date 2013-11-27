package com.road.check.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.sqlite.DatabaseService;

public class CheckHistoricalRecordsDetail extends ActivityBase{
	private Header header;
	private TextView txt_roadid;
	private TextView txt_roadname;
	private TextView txt_direction;
	private TextView txt_reportname;
	private TextView txt_detetime;
	private TextView txt_detailcontent;
	private Gallery gallery_img_view;
	private CRecordsDetailAdapter detail_adapter;
	private List<String> list_imgpath;
	//���ݿ�
	private DatabaseService dbs;
	//����
	private String table_data_id = "";
	//��ǰ���ݶ���
	private Road_Check_Data_Table now_item;
	
	private Intent intent;
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_checkhistoricalrecords_detail);
		//����ͷ��
		header = new Header(this,"��ϸ",
				"����",new HeaderLeftOnClickListener(),
				"",null);
		
		try{
			table_data_id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		
		//��ȡ��ǰ���ݶ���
		dbs = new DatabaseService(this);
		now_item = new Road_Check_Data_Table();
		now_item = dbs.road_check_table.getItem(table_data_id);
		
		list_imgpath = new ArrayList<String>();
		list_imgpath.addAll(dbs.image.getPathList(now_item.Id));
		gallery_img_view = (Gallery)this.findViewById(R.id.gallery_img_view);
		detail_adapter = new CRecordsDetailAdapter(this, list_imgpath);
		gallery_img_view.setAdapter(detail_adapter);
		if(list_imgpath.size() > 0){
			gallery_img_view.setSelection(list_imgpath.size()/2);
		}
		
		//����ؼ�����ʼ��
		txt_roadid = (TextView)this.findViewById(R.id.txt_roadid);
		txt_roadname = (TextView)this.findViewById(R.id.txt_roadname);
		txt_direction = (TextView)this.findViewById(R.id.txt_direction);
		txt_reportname = (TextView)this.findViewById(R.id.txt_reportname);
		txt_detetime = (TextView)this.findViewById(R.id.txt_detetime);
		txt_detailcontent = (TextView)this.findViewById(R.id.txt_detailcontent);
		
		txt_roadid.setText(now_item.roundId);
		txt_roadname.setText(now_item.roundName);
		txt_direction.setText(now_item.direction);
		txt_reportname.setText("��" + now_item.reportName + "��");
		txt_detetime.setText(now_item.deteTime);
		txt_detailcontent.setText(getContentValue());
		
		
	}
	//��ȡdetailcontentֵ
	public String getContentValue(){
		String content = "";
		switch(now_item.category){
		case 1:
			if(list_imgpath.size() > 0 ){
				content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 2:
			if(list_imgpath.size() > 0 ){
				
			}
			content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			break;
		case 3:
			if(list_imgpath.size() > 0 ){
				content = "������" +  dbs.image.getItemDescribe(list_imgpath.get(0)) + "\n";
				content += "·�����ƣ�" + dbs.image.getItemI_Name(list_imgpath.get(0)) + " \n";
				content += "������㣺" + dbs.image.getItemI_Distance(list_imgpath.get(0)) + "\n";
				content += "·����㵽��㣺" + dbs.image.getItemI_Start_From_Start(list_imgpath.get(0)) + "\n";
				content += "·���յ㵽��㣺" + dbs.image.getItemI_End_From_Start(list_imgpath.get(0));
			}
			
			break;
		case 4:
			if(list_imgpath.size() > 0 ){
				content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 5:
			if(list_imgpath.size() > 0 ){
				content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 6:
			if(list_imgpath.size() > 0 ){
				content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 7:
			if(list_imgpath.size() > 0 ){
				content = "������" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 0:
			if(now_item.reportName.equals("·�没���������¼��")){
				content += "·�����ͣ�" + now_item.r_roundTypeNum + "\n";
				content += "������㣺" + now_item.longFromStartNum + "��\n";
				content += "�������ͣ�" + now_item.diseaseString + "\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("·������״�������")){
				content += "׮�ţ�" + now_item.mark + "\n";
				content += "�������ͣ�" + now_item.diseaseString + "\n";
				content += "�����̶ȣ�" + now_item.b_diseaseDegree + "\n";
				content += "������" + now_item.discribe + "���/��\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("·������ˮ��ʩ����״������¼��")){
				content += "������㣺" + now_item.longFromStartNum + "��\n";
				content += "·��������" + now_item.d_roadbedDiseaseString + "\n";
				content += "��ˮ��ʩ��" + now_item.d_dewateringDiseaseSting + "\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("������ʩ��״�������")){
				content += "׮�ţ�" + now_item.mark + "\n";
				content += "�������ͣ�" + now_item.diseaseString + "\n";
				content += "�����̶ȣ�" + now_item.b_diseaseDegree + "\n";
				content += "������" + now_item.discribe + "��/��" + "\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("���е�����״������¼��")){
				content += "���е���" + now_item.f_footwalkwidth + "��\n";
				content += "������㣺" + now_item.longFromStartNum + "��\n";
				content += "�������ͣ�" + now_item.diseaseString + "\n";
				content += "���������" + now_item.f_damagedArea + "ƽ��\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("ƽ���ȼ��ԭʼ��¼")){
				content += now_item.e_evennessType;
				content += "������㣺" + now_item.longFromStartNum + "��\n";
				content += "ƽ���ȣ�" + now_item.e_evennessNum + "mm\n";
				content += "ƽ��ֵ��" + now_item.e_evennessMean + "mm\n";
				content += "�ϸ��ʣ�" + now_item.e_evennessPassNum + "%\n";
				content += "��ע��" + now_item.remark;
			}else if(now_item.reportName.equals("������ʩ����״������¼��")){
				content += "������㣺" + now_item.longFromStartNum + "��\n";
				content += "�����ṹ��" + now_item.o_structure + "\n";
				content += "������ʩ��" + now_item.o_facility + "\n";
				content += "��ע��" + now_item.remark;
			}
			break;
		default:
			break;
		}
		return content;
	}
	//ͷ����߰�ť����¼�������
	public class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent  = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.app.CheckHistoricalRecords.class);
			
			CheckHistoricalRecordsDetail.this.startActivity(intent);
			CheckHistoricalRecordsDetail.this.finish();
			
		}
	}
	//ͷ���ұ߰�ť����¼����޸�
	public class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(now_item.category){
			case 1:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "������������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 2:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "·�������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 3:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Intersection.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 4:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "���е������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 5:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "�����յ������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 6:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "������������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 7:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "�����յ������޸�");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 0:
				if(now_item.reportName.equals("·�没���������¼��")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Roadbed.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("·������״�������")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Bed.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("·������ˮ��ʩ����״������¼��")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Dewatering.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("������ʩ��״�������")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.AlongLine.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("���е�����״������¼��")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Footwalk.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("ƽ���ȼ��ԭʼ��¼")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Evenness.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("������ʩ����״������¼��")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.CheckOther.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}
				break;
			default:
				break;
			}
			
			CheckHistoricalRecordsDetail.this.finish();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  

			Intent intent  = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.app.CheckHistoricalRecords.class);
			CheckHistoricalRecordsDetail.this.startActivity(intent);
			CheckHistoricalRecordsDetail.this.finish();
			return true;
			
		}  
		return false;
	}
}
