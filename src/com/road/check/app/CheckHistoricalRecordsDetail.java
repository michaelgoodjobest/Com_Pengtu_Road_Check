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
	//数据库
	private DatabaseService dbs;
	//参数
	private String table_data_id = "";
	//当前数据对象
	private Road_Check_Data_Table now_item;
	
	private Intent intent;
	private Bundle bundle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_checkhistoricalrecords_detail);
		//加载头部
		header = new Header(this,"详细",
				"返回",new HeaderLeftOnClickListener(),
				"",null);
		
		try{
			table_data_id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		
		//获取当前数据对象
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
		
		//定义控件及初始化
		txt_roadid = (TextView)this.findViewById(R.id.txt_roadid);
		txt_roadname = (TextView)this.findViewById(R.id.txt_roadname);
		txt_direction = (TextView)this.findViewById(R.id.txt_direction);
		txt_reportname = (TextView)this.findViewById(R.id.txt_reportname);
		txt_detetime = (TextView)this.findViewById(R.id.txt_detetime);
		txt_detailcontent = (TextView)this.findViewById(R.id.txt_detailcontent);
		
		txt_roadid.setText(now_item.roundId);
		txt_roadname.setText(now_item.roundName);
		txt_direction.setText(now_item.direction);
		txt_reportname.setText("《" + now_item.reportName + "》");
		txt_detetime.setText(now_item.deteTime);
		txt_detailcontent.setText(getContentValue());
		
		
	}
	//获取detailcontent值
	public String getContentValue(){
		String content = "";
		switch(now_item.category){
		case 1:
			if(list_imgpath.size() > 0 ){
				content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 2:
			if(list_imgpath.size() > 0 ){
				
			}
			content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			break;
		case 3:
			if(list_imgpath.size() > 0 ){
				content = "描述：" +  dbs.image.getItemDescribe(list_imgpath.get(0)) + "\n";
				content += "路口名称：" + dbs.image.getItemI_Name(list_imgpath.get(0)) + " \n";
				content += "距离起点：" + dbs.image.getItemI_Distance(list_imgpath.get(0)) + "\n";
				content += "路口起点到起点：" + dbs.image.getItemI_Start_From_Start(list_imgpath.get(0)) + "\n";
				content += "路口终点到起点：" + dbs.image.getItemI_End_From_Start(list_imgpath.get(0));
			}
			
			break;
		case 4:
			if(list_imgpath.size() > 0 ){
				content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 5:
			if(list_imgpath.size() > 0 ){
				content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 6:
			if(list_imgpath.size() > 0 ){
				content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 7:
			if(list_imgpath.size() > 0 ){
				content = "描述：" + dbs.image.getItemDescribe(list_imgpath.get(0));
			}
			break;
		case 0:
			if(now_item.reportName.equals("路面病害调查检查记录表")){
				content += "路面类型：" + now_item.r_roundTypeNum + "\n";
				content += "距离起点：" + now_item.longFromStartNum + "米\n";
				content += "病害类型：" + now_item.diseaseString + "\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("路基技术状况调查表")){
				content += "桩号：" + now_item.mark + "\n";
				content += "病害类型：" + now_item.diseaseString + "\n";
				content += "病害程度：" + now_item.b_diseaseDegree + "\n";
				content += "描述：" + now_item.discribe + "面积/处\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("路基与排水设施养护状况检查记录表")){
				content += "距离起点：" + now_item.longFromStartNum + "米\n";
				content += "路基病害：" + now_item.d_roadbedDiseaseString + "\n";
				content += "排水设施：" + now_item.d_dewateringDiseaseSting + "\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("沿线设施损坏状况调查表")){
				content += "桩号：" + now_item.mark + "\n";
				content += "病害类型：" + now_item.diseaseString + "\n";
				content += "病害程度：" + now_item.b_diseaseDegree + "\n";
				content += "描述：" + now_item.discribe + "处/米" + "\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("人行道养护状况检查记录表")){
				content += "人行道宽：" + now_item.f_footwalkwidth + "米\n";
				content += "距离起点：" + now_item.longFromStartNum + "米\n";
				content += "病害类型：" + now_item.diseaseString + "\n";
				content += "破损面积：" + now_item.f_damagedArea + "平米\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("平整度检测原始记录")){
				content += now_item.e_evennessType;
				content += "距离起点：" + now_item.longFromStartNum + "米\n";
				content += "平整度：" + now_item.e_evennessNum + "mm\n";
				content += "平均值：" + now_item.e_evennessMean + "mm\n";
				content += "合格率：" + now_item.e_evennessPassNum + "%\n";
				content += "备注：" + now_item.remark;
			}else if(now_item.reportName.equals("其他设施养护状况检查记录表")){
				content += "距离起点：" + now_item.longFromStartNum + "米\n";
				content += "附属结构：" + now_item.o_structure + "\n";
				content += "附属设施：" + now_item.o_facility + "\n";
				content += "备注：" + now_item.remark;
			}
			break;
		default:
			break;
		}
		return content;
	}
	//头部左边按钮点击事件：返回
	public class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent  = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.app.CheckHistoricalRecords.class);
			
			CheckHistoricalRecordsDetail.this.startActivity(intent);
			CheckHistoricalRecordsDetail.this.finish();
			
		}
	}
	//头部右边按钮点击事件：修改
	public class HeaderRightOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch(now_item.category){
			case 1:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "上行起点数据修改");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 2:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "路牌数据修改");
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
				bundle.putString("title", "人行道数据修改");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 5:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "上行终点数据修改");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 6:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "下行起点数据修改");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 7:
				intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.RoadPointPhoto.class);
				bundle = new Bundle();
				bundle.putString("id", now_item.Id + "");
				bundle.putString("title", "下行终点数据修改");
				intent.putExtras(bundle);
				CheckHistoricalRecordsDetail.this.startActivity(intent);
				break;
			case 0:
				if(now_item.reportName.equals("路面病害调查检查记录表")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Roadbed.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("路基技术状况调查表")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Bed.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("路基与排水设施养护状况检查记录表")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Dewatering.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("沿线设施损坏状况调查表")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.AlongLine.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("人行道养护状况检查记录表")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Footwalk.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("平整度检测原始记录")){
					intent = new Intent(CheckHistoricalRecordsDetail.this,com.road.check.report.update.Evenness.class);
					bundle = new Bundle();
					bundle.putString("id", now_item.Id + "");
					intent.putExtras(bundle);
					CheckHistoricalRecordsDetail.this.startActivity(intent);
				}else if(now_item.reportName.equals("其他设施养护状况检查记录表")){
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
