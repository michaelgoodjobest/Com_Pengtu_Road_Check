package com.road.check.report.update;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.sqlite.DatabaseService;

public class Evenness extends ActivityBase{
	private Header header;
	//����ؼ�
	private TextView txt_round_name;
	private TextView txt_roundid;
	private TextView txt_startend_name;
	private TextView txt_direction;
	private TextView txt_time;
	private RadioGroup rg_type;
	private RadioButton rbtn_lane;
	private RadioButton rbtn_footwalk;
	private EditText edt_distance;
	private EditText edt_evenness;
	private EditText edt_mean;
	private EditText edt_qualified;
	private EditText edt_remark;
	
	private SavaDatasTask savaDatasTask;
	private String evennessType = "";
	private DatabaseService dbs;
	private String Id = "";
	private Road_Check_Data_Table now_item;
	private Round round_item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_evenness);
		try{
			Id = this.getIntent().getExtras().getString("id");
		}catch(Exception ex){}
		//����ͷ��
		header = new Header(this,"ƽ���ȼ��ԭʼ��¼��",
				"����",new HeaderLeftButtonOnClickListener(),
				"����",new HeaderRightButtonOnClickListener());
		
		//��ȡ��ǰ����
		dbs = new DatabaseService(this);
		now_item = new Road_Check_Data_Table();
		now_item = dbs.road_check_table.getItem(Id);
		//��ȡ��ǰ��·����
		round_item = new Round();
		round_item = dbs.round.getItemByRoundId(now_item.roundId);
	}
	/**ͷ����߰�ť����¼�������*/
	public class HeaderLeftButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent  = new Intent(Evenness.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Evenness.this.startActivity(intent);
			Evenness.this.finish();
		}
	}
	/**ͷ���ұ߰�ť����¼�������*/
	public class HeaderRightButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			savaDatasTask = new SavaDatasTask();
			savaDatasTask.execute();
		}
	}
	/**��ʼ��*/
	public void init(){
		txt_round_name = (TextView)Evenness.this.findViewById(R.id.txt_round_name);
		txt_roundid = (TextView)Evenness.this.findViewById(R.id.txt_roundid);
		txt_startend_name = (TextView)Evenness.this.findViewById(R.id.txt_startend_name);
		txt_direction = (TextView)Evenness.this.findViewById(R.id.txt_direction);
		txt_time = (TextView)Evenness.this.findViewById(R.id.txt_time);
		rg_type = (RadioGroup)Evenness.this.findViewById(R.id.rg_type);
		rbtn_lane = (RadioButton)Evenness.this.findViewById(R.id.rbtn_lane);
		rbtn_footwalk = (RadioButton)Evenness.this.findViewById(R.id.rbtn_footwalk);
		edt_distance = (EditText)Evenness.this.findViewById(R.id.edt_distance);
		edt_evenness = (EditText)Evenness.this.findViewById(R.id.edt_evenness);
		edt_mean = (EditText)Evenness.this.findViewById(R.id.edt_mean);
		edt_qualified = (EditText)Evenness.this.findViewById(R.id.edt_qualified);
		edt_remark = (EditText)Evenness.this.findViewById(R.id.edt_remark);
		
		txt_round_name.setText(now_item.roundName);
		txt_roundid.setText(now_item.roundId);
		txt_startend_name.setText(round_item.start_location + " �� " + round_item.end_location);
		txt_direction.setText(now_item.direction);
		txt_time.setText(now_item.deteTime);
		edt_distance.setText(now_item.longFromStartNum);
		edt_evenness.setText(now_item.e_evennessNum);
		edt_mean.setText(now_item.e_evennessMean);
		edt_qualified.setText(now_item.e_evennessPassNum);
		edt_remark.setText(now_item.remark);
		
		rbtn_lane.setChecked(true);
		//���������е���ѡ����
		rg_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//���е�\�����ж�
				if(rbtn_lane.getId() == checkedId){//����
					evennessType = "����";
				}else if(rbtn_footwalk.getId() == checkedId){//���е�
					evennessType = "���е�";
				}
			}
		});
		
		if(now_item.e_evennessType.equals("���е�")){
			rbtn_footwalk.setChecked(true);
		}else if(now_item.e_evennessType.equals("����")){
			rbtn_lane.setChecked(true);
		}
	}
	//��ȡ����������Ƿ�������
	public int getDirectionHavaDatas(){
		return edt_distance.getText().toString().length();
	}
	
	/**���ݱ���*/
    public void savaDatas(){
    	savaDatasTask = new SavaDatasTask();
    	savaDatasTask.execute();
    }
    /**��������*/
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
	    	item.e_evennessNum = edt_evenness.getText().toString();
	    	item.e_evennessMean = edt_mean.getText().toString();
	    	item.e_evennessPassNum = edt_qualified.getText().toString();
	    	item.remark = edt_remark.getText().toString();
	    	item.reportName = "ƽ���ȼ��ԭʼ��¼";
	    	dbs.road_check_table.add(item);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(Evenness.this, "����ɹ���", Toast.LENGTH_SHORT).show();
			
			Intent intent  = new Intent(Evenness.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Evenness.this.startActivity(intent);
			Evenness.this.finish();
		}
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){  
			Intent intent  = new Intent(Evenness.this,com.road.check.app.CheckHistoricalRecordsDetail.class);
			Bundle bundle  = new Bundle();
			bundle.putString("id", Id);
			intent.putExtras(bundle);
			Evenness.this.startActivity(intent);
			Evenness.this.finish();
			return true;
		}  
		return false;
	}
}
