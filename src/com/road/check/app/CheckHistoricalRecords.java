package com.road.check.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.road.check.R;
import com.road.check.base.ActivityBase;
import com.road.check.common.Header;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.sqlite.DatabaseService;

public class CheckHistoricalRecords extends ActivityBase{
	private Header header;
	private DatabaseService dbs;
	private ProgressDialog progressDialog;
	private AlertDialog delete_dialog;
	//列表
	private ListView record_listview;
	private CRecordsAdapter cAdapter;
	private List<Road_Check_Data_Table> record_list;
	private AddDatasTask addDatasTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_checkhistoricalrecords_list);
		//加载头部
		header = new Header(this,"历史记录",
				"返回",new HeaderLeftOnClickListener(),
				"",null);
		
		dbs = new DatabaseService(this);
		
		//列表
		record_list = new ArrayList<Road_Check_Data_Table>();
		record_listview = (ListView)this.findViewById(R.id.record_listview);
		cAdapter = new CRecordsAdapter(this, record_list, record_listview);
		record_listview.setAdapter(cAdapter);
		record_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Road_Check_Data_Table item = record_list.get(position);
				Intent intent = new Intent(CheckHistoricalRecords.this,CheckHistoricalRecordsDetail.class);
				
				Bundle bundle = new Bundle();
				bundle.putString("id", item.Id + "");
				intent.putExtras(bundle);
				CheckHistoricalRecords.this.startActivity(intent);
				CheckHistoricalRecords.this.finish();
				
			}
		});
		record_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				AlertDialog.Builder ad = new AlertDialog.Builder(CheckHistoricalRecords.this);
				ad.setTitle("删除提示");
				ad.setMessage("您确认要删除该项吗？");
				ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Road_Check_Data_Table item = record_list.get(position);
						dbs.road_check_table.deleteDatas(item.Id + "");
						dbs.image.deleteDatas(item.Id + "");
						
						File d_file = null;
						String[] imgDeleteArray = item.imagePathString.split(",");
						for(int i = 0 ; i < imgDeleteArray.length ; i++){
							d_file = new File(imgDeleteArray[i]);
							if(d_file.exists()){
								d_file.delete();
							}
						}
						
						record_list.remove(position);
						cAdapter.notifyDataSetChanged();
						delete_dialog.dismiss();
					}
				});
				ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete_dialog.dismiss();
					}
				});
				delete_dialog = ad.create();
				delete_dialog.show();
				return false;
			}
		});
		//执行列表数据加载任务
		addDatasTask = new AddDatasTask();
		addDatasTask.execute(-1);
	}
	/**头部左边按钮点击事件*/
	public class HeaderLeftOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			CheckHistoricalRecords.this.finish();
		}
	}
	/**头部右边按钮点击事件*/
	public class HeaderRigthOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
		}
	}
	
	/**数据加载*/
	class AddDatasTask extends AsyncTask<Integer, Integer, String>{
		@Override
		protected void onPreExecute() {
			record_list.clear();
			progressDialog = ProgressDialog.show(CheckHistoricalRecords.this, 
					"请稍候", "正在加载列表数据...", true);
		}
		@Override
		protected String doInBackground(Integer... params) {
			record_list.addAll(dbs.road_check_table.getList(params[0]));
			return "0";
		}
		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			cAdapter.notifyDataSetChanged();
		}
	}
}
