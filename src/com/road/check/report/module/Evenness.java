package com.road.check.report.module;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.road.check.R;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.navigation.Navigation;
import com.road.check.sqlite.DatabaseService;
/**
 * 平整度检测原始记录
 * */
public class Evenness {
	List<EditText> editList;
	private Dialog verify_dialog;
	private View baseView;
	private Activity baseactivity;
	//定义控件
	private TextView txt_round_name;
	private TextView txt_roundid;
	private TextView txt_startend_name;
	private TextView txt_direction;
	private TextView txt_time;
	private RadioGroup rg_type;
	private RadioButton rbtn_lane;
	private RadioButton rbtn_footwalk;
	private EditText edt_distance;
	private Button edt_evenness;
	private EditText edt_mean;
	private EditText edt_qualified;
	private EditText edt_remark;
	private EditText edt_width1;
	private EditText edt_width2;
	private EditText edt_width3;
	private EditText edt_width4;
	private EditText edt_width5;
	private EditText edt_width6;
	private EditText edt_width7;
	private EditText edt_width8;
	private EditText edt_width9;
	private EditText edt_width10;

	
	
	private SavaDatasTask savaDatasTask;
	private String evennessType = "";
	private DatabaseService dbs;
	String footwalk_width = "";
	public Evenness(View view,Activity activity,DatabaseService dbs){
		baseView = view;
		baseactivity = activity;
		this.dbs = dbs;
	}
	/**初始化*/
	public void init(Round roundItem,String direction){
		txt_round_name = (TextView)baseView.findViewById(R.id.txt_round_name);
		txt_roundid = (TextView)baseView.findViewById(R.id.txt_roundid);
		txt_startend_name = (TextView)baseView.findViewById(R.id.txt_startend_name);
		txt_direction = (TextView)baseView.findViewById(R.id.txt_direction);
		txt_time = (TextView)baseView.findViewById(R.id.txt_time);
		rg_type = (RadioGroup)baseView.findViewById(R.id.rg_type);
		rbtn_lane = (RadioButton)baseView.findViewById(R.id.rbtn_lane);
		rbtn_footwalk = (RadioButton)baseView.findViewById(R.id.rbtn_footwalk);
		edt_distance = (EditText)baseView.findViewById(R.id.edt_distance);
		edt_evenness = (Button)baseView.findViewById(R.id.btn_evenness);
		edt_mean = (EditText)baseView.findViewById(R.id.edt_mean);
		edt_qualified = (EditText)baseView.findViewById(R.id.edt_qualified);
		edt_remark = (EditText)baseView.findViewById(R.id.edt_remark);
		
		txt_round_name.setText(roundItem.name);
		txt_roundid.setText(roundItem.roundId);
		if (direction.equals("上行")) {
			txt_startend_name.setText(roundItem.start_location + " 至 " + roundItem.end_location);
			Log.d("上行起点终点显示", "上行起点终点显示");
		}else{
			txt_startend_name.setText( roundItem.end_location+ " 至 " + roundItem.start_location);
			Log.d("下行起点终点显示", "下行起点终点显示");
		}
		txt_direction.setText(direction);
		txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
		
		rbtn_lane.setChecked(true);
		//车道，人行道单选监听
		rg_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//人行道\车道判断
				if(rbtn_lane.getId() == checkedId){//车道
					evennessType = "车道";
				}else if(rbtn_footwalk.getId() == checkedId){//人行道
					evennessType = "人行道";
				}
			}
		});
		edt_evenness.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				View eView = View.inflate(baseactivity, R.layout.report_create_module_evenness_dialog, null);
				edt_width1 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_1);
				edt_width2 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_2);
				edt_width3 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_3);
				edt_width4 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_4);
				edt_width5 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_5);
				edt_width6 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_6);
				edt_width7 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_7);
				edt_width8 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_8);
				edt_width9 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_9);
				edt_width10 = (EditText) eView.findViewById(R.id.edt_footwalk_evenness_10);
				Button btn_sure = (Button) eView.findViewById(R.id.btn_sure);
				editList = new ArrayList<EditText>();
				editList.add(edt_width1);
				editList.add(edt_width2);
				editList.add(edt_width3);
				editList.add(edt_width4);
				editList.add(edt_width5);
				editList.add(edt_width6);
				editList.add(edt_width7);
				editList.add(edt_width8);
				editList.add(edt_width9);
				editList.add(edt_width10);
				btn_sure.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						for (int i = 0; i<editList.size(); i++) {
							String width = editList.get(i).getText().toString();
							if(i==9){
								footwalk_width+=width;
							}
							else {
								footwalk_width+=width+",";
							}
							
						}
						verify_dialog.dismiss();
					}
				});

				
				
				
				verify_dialog = new Dialog(baseactivity,
						R.style.dialog);
				verify_dialog
						.setContentView(eView);
				verify_dialog.show();
				
			}
		});
	}
	//获取距离起点项是否有数据
	public int getDirectionHavaDatas(){
		return edt_distance.getText().toString().length();
	}
	
	/**数据保存*/
    public void savaDatas(){
    	if (edt_distance.getText().toString().length()>0) {
        	savaDatasTask = new SavaDatasTask();
        	savaDatasTask.execute();
		}else {
			Toast.makeText(baseactivity, "请填入数据后再保存", 0).show();
		}

    }
    /**保存任务*/
    class SavaDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected String doInBackground(String... params) {
	    	Road_Check_Data_Table item = new Road_Check_Data_Table();
	    	item.roundName = txt_round_name.getText().toString();
	    	item.roundId = txt_roundid.getText().toString();
	    	item.direction = txt_direction.getText().toString();
	    	item.deteTime = txt_time.getText().toString();
	    	item.e_evennessType = evennessType;
	    	item.longFromStartNum = edt_distance.getText().toString();
	    	item.e_evennessNum = footwalk_width;
	    	item.e_evennessMean = edt_mean.getText().toString();
	    	item.e_evennessPassNum = edt_qualified.getText().toString();
	    	item.remark = edt_remark.getText().toString();
	    	item.reportName = "平整度检测原始记录";
	    	dbs.road_check_table.add(item);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			rbtn_lane.setChecked(true);
			edt_distance.setText("");
			edt_mean.setText("");
			edt_qualified.setText("");
			edt_remark.setText("");
			txt_time.setText(as.relistic.common.Helper.getNowSystemTimeToSecond());
			Toast.makeText(baseactivity, "记录保存成功！！", Toast.LENGTH_LONG).show();
		}
    }
}
