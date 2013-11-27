//package com.road.check.roadselecte;
//
//import java.util.ArrayList;
//
//import net.sourceforge.pinyin4j.PinyinHelper;
//import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
//import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.AsyncTask.Status;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import as.relistic.list.ListView.OnRefreshListener;
//
//import com.road.check.R;
//import com.road.check.app.RoadPointPhoto;
//import com.road.check.common.CheckApplication;
//import com.road.check.model.Round;
//import com.road.check.report.Create;
//import com.road.check.sqlite.DatabaseService;
//
//public class RoadSelecteList {
//	private Activity baseactivity;
//	private as.relistic.list.ListView road_lsitview;
//	private java.util.List<Round> round_list;
//	private boolean lastPage = true;
//	private int pageSize = 15;
//	private int pageNum = 0;
//	private int totalCounts = 15;
//	private AddDatasTask addDatasTask;
//	private ListAdapter roadListadapter;
//	
//	private Button btn_type_selecte;
//	private Dialog dialog;
//	private Dialog direction_dialog;
//	private DatabaseService dbs;
//	private CheckApplication cApp;
//	public RoadSelecteList(Activity activity,DatabaseService dbs,CheckApplication cApp){
//		baseactivity = activity;
//		this.dbs = dbs;
//		this.cApp = cApp;
//	}
//	
//	/**视图初始化*/
//	public void initView(View view,final Dialog sdialog,final TextView txt_round_name,final TextView txt_round_direction){
//		road_lsitview = (as.relistic.list.ListView)view.findViewById(R.id.road_listview);
//		btn_type_selecte = (Button)view.findViewById(R.id.btn_type_selecte);
//		
//		/******************************道路列表相关***********************************/
//		/*listView 数据*/
//		road_lsitview.setMore();
//		road_lsitview.button_more.setOnClickListener(new OnClickListener(){
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(lastPage){
//					return;
//				}
//				
//				if(addDatasTask == null || addDatasTask.getStatus() == Status.FINISHED){
//					addDatasTask = new AddDatasTask();
//					addDatasTask.execute();
//				}
//			}} );
//		//滚动到底部自动加载
//		road_lsitview.setOnScrollListener(new AbsListView.OnScrollListener() {
//			public void onScrollStateChanged(AbsListView view, int scrollState) {}
//			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//				road_lsitview.firstItemIndex = firstVisibleItem;
//				if (totalItemCount >= pageSize && (firstVisibleItem + visibleItemCount) == totalItemCount){
//					if(lastPage){
//						return;
//					}
//					
//					if(addDatasTask == null || addDatasTask.getStatus() == Status.FINISHED){
//						addDatasTask = new AddDatasTask();
//						addDatasTask.execute();
//					}
//				}
//			}
//		});
//		
//		//刷新
//		road_lsitview.setOnRefreshListener(new OnRefreshListener() {
//			public void onRefresh() {
//				pageNum = 0;
//				round_list.clear();
//				dbs.round.clear();
//				addDatasTask = new AddDatasTask();
//				addDatasTask.execute();
//
//			}
//		});
//
//		road_lsitview.setScrollbarFadingEnabled(true);
//		round_list = new ArrayList<Round>();
//		roadListadapter = new ListAdapter(baseactivity,round_list);
//		road_lsitview.setVisibility(View.VISIBLE);
//		road_lsitview.setAdapter(roadListadapter);
//		road_lsitview.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> arg0, View parent, int position,
//					long arg3) {
//				if(position <= 0 || round_list.size() <= position - 1){
//					return;
//				}
//				final Round round_item = round_list.get(position - 1);
//				
//				View diaView = View.inflate(baseactivity, R.layout.roadselecte_direction_selecte_dialog, null);
//				LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
//				Button btn_upgoing = (Button)diaView.findViewById(R.id.btn_upgoing);
//				Button btn_down = (Button)diaView.findViewById(R.id.btn_down);
//				
//				btn_upgoing.setText(round_item.start_location + "--" + round_item.end_location);
//				btn_down.setText(round_item.end_location + "--" + round_item.start_location);
//				//关闭
//				layout_close.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						direction_dialog.dismiss();
//					}
//				});
//				//上行:0
//				btn_upgoing.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						@SuppressWarnings("rawtypes")
//						Class now_position_class = null;
//						if(cApp.ViewPosition == 0){
//							now_position_class = RoadPointPhoto.class;
//						}else if(cApp.ViewPosition == 1){
//							now_position_class = Create.class;
//						}
//						Intent intent = new Intent(baseactivity,now_position_class);
//						Bundle bundle = new Bundle();
//						bundle.putInt("direction", 0);
//						bundle.putString("id", round_item.id);
//						intent.putExtras(bundle);
//						baseactivity.startActivity(intent);
//						direction_dialog.dismiss();
//						sdialog.dismiss();
//						cApp.RoundId = round_item.id;
//						cApp.Direction = 0;
//						txt_round_name.setText("【"+round_item.name+"】");
//						txt_round_direction.setText("【上行】");
//					}
//				});
//				//下行:1
//				btn_down.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						@SuppressWarnings("rawtypes")
//						Class now_position_class = null;
//						if(cApp.ViewPosition == 0){
//							now_position_class = RoadPointPhoto.class;
//						}else if(cApp.ViewPosition == 1){
//							now_position_class = Create.class;
//						}
//						Intent intent = new Intent(baseactivity,now_position_class);
//						Bundle bundle = new Bundle();
//						bundle.putInt("direction", 1);
//						bundle.putString("id", round_item.id);
//						intent.putExtras(bundle);
//						baseactivity.startActivity(intent);
//						direction_dialog.dismiss();
//						sdialog.dismiss();
//						cApp.RoundId = round_item.id;
//						cApp.Direction = 1;
//						
//						txt_round_name.setText("【"+round_item.name+"】");
//						txt_round_direction.setText("【下行】");
//					}
//				});
//				direction_dialog=new Dialog(baseactivity,R.style.dialog);
//				direction_dialog.setContentView(diaView);
//				direction_dialog.show();
//			}
//		});
//		
//		//初始化数据
//		round_list.clear();
//		addDatasTask = new AddDatasTask();
//		addDatasTask.execute();
//		
//		/******************************道路列表相关END***********************************/
//		
//		//分类
//		btn_type_selecte.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				View diaView = View.inflate(baseactivity, R.layout.roadselecte_type_dialog, null);
//				LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
//				//关闭
//				layout_close.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dialog.dismiss();
//					}
//				});
//				dialog=new Dialog(baseactivity,R.style.dialog);
//				dialog.setContentView(diaView);
//				dialog.show();
//			}
//		});
//		
//	}
//	/**道路模拟数据
//	 *盐田：101
//	 *光明新区：102
//	 *龙岗区：103
//	 *罗湖区：104
//	 *坪山新区：105*/
//	public void roadlistAdd(){
//		Round roundItem = null;
//		roundItem = new Round();
//		roundItem.id= "001";
//		roundItem.name = "恩上公路";
//		roundItem.type= "三级公路";
//		roundItem.start_location = "海山路";
//		roundItem.end_location = "深盐二通道路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "盐田区";
//		roundItem.areaId = 101;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "002";
//		roundItem.name = "叠翠湖水库路";
//		roundItem.type= "三级公路";
//		roundItem.start_location = "K0+000";
//		roundItem.end_location = "K1+800";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "盐田区";
//		roundItem.areaId = 101;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "003";
//		roundItem.name = "育茶线";
//		roundItem.type= "四级公路";
//		roundItem.start_location = "茶林村";
//		roundItem.end_location = "育新学校";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "光明新区";
//		roundItem.areaId = 102;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "004";
//		roundItem.name = "G205山深线";
//		roundItem.type= "一级公路";
//		roundItem.start_location = "深惠交界";
//		roundItem.end_location = "富民路口";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "光明新区";
//		roundItem.areaId = 102;
//		roundItem.divide="1";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "005";
//		roundItem.name = "北通道";
//		roundItem.type= "城市快速路";
//		roundItem.start_location = "水官高速收费站出口";
//		roundItem.end_location = "横坪公路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "006";
//		roundItem.name = "福龙路";
//		roundItem.type= "城市快速路";
//		roundItem.start_location = "香莲立交";
//		roundItem.end_location = "龙大高速";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "光明新区";
//		roundItem.areaId = 102;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "007";
//		roundItem.name = "丹平快速路";
//		roundItem.type= "城市快速路";
//		roundItem.start_location = "爱国路";
//		roundItem.end_location = "机荷高速";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "罗湖区";
//		roundItem.areaId = 104;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "008";
//		roundItem.name = "清平高速";
//		roundItem.type= "城市快速路";
//		roundItem.start_location = "道桥集团人行天桥";
//		roundItem.end_location = "玉平关口";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "罗湖区";
//		roundItem.areaId = 104;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "009";
//		roundItem.name = "绿梓大道";
//		roundItem.type= "城市快速路";
//		roundItem.start_location = "丹梓大道";
//		roundItem.end_location = "深汕高速";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "坪山新区";
//		roundItem.areaId = 105;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "010";
//		roundItem.name = "龙兴大道";
//		roundItem.type= "主干路";
//		roundItem.start_location = "地铁三号线大运站";
//		roundItem.end_location = "清霞路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "011";
//		roundItem.name = "红棉路";
//		roundItem.type= "主干路";
//		roundItem.start_location = "东西干道";
//		roundItem.end_location = "宝荷路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "012";
//		roundItem.name = "沙河路";
//		roundItem.type= "主干路";
//		roundItem.start_location = "K2+760";
//		roundItem.end_location = "K13+080";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "013";
//		roundItem.name = "深峰路";
//		roundItem.type= "主干路";
//		roundItem.start_location = "深惠公路";
//		roundItem.end_location = "环城北路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "014";
//		roundItem.name = "如意路";
//		roundItem.type= "主干路";
//		roundItem.start_location = "北通道";
//		roundItem.end_location = "黄阁路";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "015";
//		roundItem.name = "宝荷路";
//		roundItem.type= "主干路";
//		roundItem.start_location = "2+899.622";
//		roundItem.end_location = "7+127";
//		roundItem.length = "";
//		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "龙岗区";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//	}
//	/**获取首字母*/
//	private String _first_char = null;
//	public String getFirstLitter(String roundname){
//		 String first_litter = "";
//		 char[] arr = roundname.toCharArray();
//		 HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
//		 defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//		 defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//		 if(arr[0] > 128){
//			 try{
//				 String[] pingyin_array = PinyinHelper.toHanyuPinyinStringArray(arr[0], defaultFormat);
//				 if(pingyin_array != null) {
//	                	first_litter = pingyin_array[0].charAt(0) + "";
//	                	if(!first_litter.equals(_first_char)){
//	                		_first_char = first_litter;
//	                	}
//	             }
//			 }catch(BadHanyuPinyinOutputFormatCombination e){
//				 
//			 }
//		 }else{
//			 first_litter = arr[0] + "";
//		 }
//		
//		 return first_litter;  
//	} 
//	/**索引数据获取*/
//	public java.util.List<String> getIndexDatas(){
//		java.util.List<String> indexlist = new ArrayList<String>();
//		indexlist.add("A");
//		indexlist.add("B");
//		indexlist.add("C");
//		indexlist.add("D");
//		indexlist.add("E");
//		indexlist.add("F");
//		indexlist.add("G");
//		indexlist.add("H");
//		indexlist.add("I");
//		indexlist.add("J");
//		indexlist.add("K");
//		indexlist.add("M");
//		indexlist.add("N");
//		indexlist.add("L");
//		indexlist.add("O");
//		indexlist.add("P");
//		indexlist.add("Q");
//		indexlist.add("R");
//		indexlist.add("S");
//		indexlist.add("T");
//		indexlist.add("U");
//		indexlist.add("V");
//		indexlist.add("W");
//		indexlist.add("X");
//		indexlist.add("Y");
//		indexlist.add("Z");
//		
//		return indexlist;
//	}
//	/**获取当前页数据开始条数*/
//	public int getStartRow(){
//		return (pageNum - 1)*pageSize;
//	}
//	
//	/**页面数据加载*/
//	private class AddDatasTask extends AsyncTask<String, String, String>{
//		@Override
//		protected void onPreExecute() {
//			pageNum ++;
////			lastPage = false;
//			road_lsitview.startLoadMore();
//		}
//		@Override
//		protected String doInBackground(String... params) {
//			//模拟数据加载
//			roadlistAdd();
//			return "0";
//		}
//		@Override
//		protected void onPostExecute(String result) {
//			if(totalCounts <= round_list.size() ){
//				lastPage = true;
//			}
//			round_list.addAll(dbs.round.getList());
//			roadListadapter.notifyDataSetChanged();
//			road_lsitview.endLoadMore(lastPage,"已经是最后一页了");
//			road_lsitview.onRefreshComplete();
//		}
//	}
//}
