package com.road.check.roadselecte;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import as.relistic.list.ListView.OnRefreshListener;

import com.road.check.R;
import com.road.check.app.functionmodule.TreeAdapter;
import com.road.check.app.functionmodule.TreeListView;
import com.road.check.common.CheckApplication;
import com.road.check.common.KeyBoard;
import com.road.check.model.Node;
import com.road.check.model.NodeResource;
import com.road.check.model.Road_Check_Data_Table;
import com.road.check.model.Round;
import com.road.check.navigation.Navigation;
import com.road.check.sqlite.DatabaseService;

public class RoadSelecteList_V2 {
	private KeyBoard keyBoard;
	private Activity baseactivity;
	private ListView road_lsitview;
	private List<Round> round_list;
	private AddDatasTask addDatasTask;
	private ListAdapter roadListadapter;
	
	
	private Button btn_type_selecte;
	private LinearLayout layout_close;
	private Dialog dialog;
	private Dialog direction_dialog;
	private Dialog keyboad_dialog;
	private DatabaseService dbs;
	private CheckApplication cApp;
	
	//���νṹ
	private TreeListView tree_listView;
	
	//ҵ�����̿ؼ�
	private Button btn_start;
	private RelativeLayout btn_1;
	private RelativeLayout btn_2;
	private RelativeLayout btn_3;
	private RelativeLayout btn_4;
	private RelativeLayout btn_5;
	private RelativeLayout btn_6;
	private RelativeLayout rlayout_stop;
	private RelativeLayout rlayout_start;
	private LinearLayout layout_start;
	private ImageButton btn_direction;
	private Button btn_stop;
	private TextView txt_img_num_report_create;
	private TextView txt_img_num_footwalk_photo;
	private TextView txt_img_num_intersection_photo;
	private TextView txt_img_num_guideboard_photo;
	private TextView txt_img_num_start_photo;
	private TextView txt_img_num_end_photo;
	private TextView txt_up_start_photo;
	private TextView txt_up_end_photo;	
	private TextView txt_cl;
	private TextView txt_cr;
	private TextView txt_fl;
	private TextView txt_fr;
	public RoadSelecteList_V2(Activity activity,DatabaseService dbs,CheckApplication cApp){
		baseactivity = activity;
		this.dbs = dbs;
		this.cApp = cApp;
	}
	
	/**��ͼ��ʼ��*/
	public void initView(View view,final PopupWindow pw,final TextView txt_round_name,final TextView txt_round_direction,HashMap<String,Object> btnmap){
		road_lsitview = (ListView)view.findViewById(R.id.road_listview);
		btn_type_selecte = (Button)view.findViewById(R.id.btn_type_selecte);
		layout_close = (LinearLayout)view.findViewById(R.id.layout_close);
		btn_1 =(RelativeLayout)btnmap.get("btn_1");
		btn_2 =(RelativeLayout)btnmap.get("btn_2");
		btn_3 =(RelativeLayout)btnmap.get("btn_3");
		btn_4 =(RelativeLayout)btnmap.get("btn_4");
		btn_5 =(RelativeLayout)btnmap.get("btn_5");
		btn_6 =(RelativeLayout)btnmap.get("btn_6");
		btn_direction = (ImageButton)btnmap.get("btn_direction");
		btn_start = (Button)btnmap.get("btn_start");
		btn_stop = (Button)btnmap.get("btn_stop");
		txt_img_num_report_create = (TextView)btnmap.get("txt_img_num_report_create");
		txt_img_num_footwalk_photo = (TextView)btnmap.get("txt_img_num_footwalk_photo");
		txt_img_num_intersection_photo = (TextView)btnmap.get("txt_img_num_intersection_photo");
		txt_img_num_guideboard_photo = (TextView)btnmap.get("txt_img_num_guideboard_photo");
		txt_img_num_start_photo = (TextView)btnmap.get("txt_img_num_start_photo");
		txt_img_num_end_photo = (TextView)btnmap.get("txt_img_num_end_photo");
		rlayout_stop = (RelativeLayout)btnmap.get("rlayout_stop");
		rlayout_start = (RelativeLayout)btnmap.get("rlayout_start");
		layout_start = (LinearLayout)btnmap.get("layout_start");
		txt_up_start_photo = (TextView) btnmap.get("txt_up_start_photo");
		txt_up_end_photo = (TextView) btnmap.get("txt_up_end_photo");
		txt_cl = (TextView) btnmap.get("txt_cl");
		txt_cr = (TextView) btnmap.get("txt_cr");
		txt_fl = (TextView) btnmap.get("txt_fl");
		txt_fr = (TextView) btnmap.get("txt_fr");
		
		//�����򿪹ص���¼�����
		layout_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		});
		
		/******************************��·�б����***********************************/
		round_list = new ArrayList<Round>();
		roadListadapter = new ListAdapter(baseactivity,round_list);
		road_lsitview.setAdapter(roadListadapter);
		road_lsitview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View parent, int position,
					long arg3) {
				final Round round_item = round_list.get(position);
				
				View diaView = View.inflate(baseactivity, R.layout.roadselecte_direction_selecte_dialog, null);
				LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
				Button btn_upgoing = (Button)diaView.findViewById(R.id.btn_upgoing);
				Button btn_down = (Button)diaView.findViewById(R.id.btn_down);
				
				btn_upgoing.setText(round_item.start_location + "--" + round_item.end_location);
				btn_down.setText(round_item.end_location + "--" + round_item.start_location);
				//�ر�
				layout_close.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						direction_dialog.dismiss();
					}
				});
				//����:0
				btn_upgoing.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						direction_dialog.dismiss();
						pw.dismiss();
						cApp.RoundId = round_item.id+"";
						cApp.Direction = 0;
						dbs.round.updateState(1, round_item.id+"");
						((Navigation) baseactivity).countTimerStart();
						txt_round_name.setText(round_item.name);
						txt_round_direction.setText("�����С�");
						txt_up_start_photo.setText("�����������");
						txt_up_end_photo.setText("�����յ�����");
						txt_cl.setText("���е����ȣ�"+round_item.clength);
						txt_cr.setText("���е���ȣ�"+round_item.croadwidth);
						txt_fl.setText("���е����ȣ�"+round_item.flength);
						txt_fr.setText("���е���ȣ�"+round_item.froadwidth);
						layout_start.clearAnimation();
						rlayout_start.setVisibility(View.GONE);
						Animation anim_in = AnimationUtils.loadAnimation(baseactivity, R.anim.ficker_in);
						rlayout_stop.setVisibility(View.VISIBLE);
						rlayout_stop.startAnimation(anim_in);
						btn_1.setBackgroundResource(R.drawable.navigation_v2_app5_bg);
						btn_2.setBackgroundResource(R.drawable.navigation_v2_app2_bg);
						btn_3.setBackgroundResource(R.drawable.navigation_v2_app6_bg);
						btn_4.setBackgroundResource(R.drawable.navigation_v2_app4_bg);
						btn_5.setBackgroundResource(R.drawable.navigation_v2_app3_bg);
						btn_6.setBackgroundResource(R.drawable.navigation_v2_app1_bg);
						btn_direction.setBackgroundResource(R.drawable.direction_up);
						txt_img_num_report_create.setText(getimg_num("����",0));
						txt_img_num_footwalk_photo.setText(getimg_num("����",4));
						txt_img_num_intersection_photo.setText(getimg_num("����",3));
						txt_img_num_guideboard_photo.setText(getimg_num("����",2));
						txt_img_num_start_photo.setText(getimg_num("����",1));
						txt_img_num_end_photo.setText(getimg_num("����",5));
						btn_1.setClickable(true);
						btn_2.setClickable(true);
						btn_3.setClickable(true);
						btn_4.setClickable(true);
						btn_5.setClickable(true);
						btn_6.setClickable(true);
						btn_direction.setClickable(true);
					}
				});
				//����:1
				btn_down.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						direction_dialog.dismiss();
						pw.dismiss();
						cApp.RoundId = round_item.id+"";
						cApp.Direction = 1;
						dbs.round.updateState(1, round_item.id+"");
						((Navigation) baseactivity).countTimerStart();
						txt_round_name.setText(round_item.name);
						txt_round_direction.setText("�����С�");
						txt_up_start_photo.setText("�����������");
						txt_up_end_photo.setText("�����յ�����");
						txt_cl.setText(round_item.clength);
						txt_cr.setText(round_item.croadwidth);
						txt_fl.setText(round_item.flength);
						txt_fr.setText(round_item.froadwidth);
						layout_start.clearAnimation();
						rlayout_start.setVisibility(View.GONE);
						Animation anim_in = AnimationUtils.loadAnimation(baseactivity, R.anim.ficker_in);
						rlayout_stop.setVisibility(View.VISIBLE);
						rlayout_stop.startAnimation(anim_in);
						btn_1.setBackgroundResource(R.drawable.navigation_v2_app5_bg);
						btn_2.setBackgroundResource(R.drawable.navigation_v2_app2_bg);
						btn_3.setBackgroundResource(R.drawable.navigation_v2_app6_bg);
						btn_4.setBackgroundResource(R.drawable.navigation_v2_app4_bg);
						btn_5.setBackgroundResource(R.drawable.navigation_v2_app3_bg);
						btn_6.setBackgroundResource(R.drawable.navigation_v2_app1_bg);
						btn_direction.setBackgroundResource(R.drawable.direction_down);
						txt_img_num_report_create.setText(getimg_num("����",0));
						txt_img_num_footwalk_photo.setText(getimg_num("����",4));
						txt_img_num_intersection_photo.setText(getimg_num("����",3));
						txt_img_num_guideboard_photo.setText(getimg_num("����",2));
						txt_img_num_start_photo.setText(getimg_num("����",6));
						txt_img_num_end_photo.setText(getimg_num("����",7));
						btn_1.setClickable(true);
						btn_2.setClickable(true);
						btn_3.setClickable(true);
						btn_4.setClickable(true);
						btn_5.setClickable(true);
						btn_6.setClickable(true);
						btn_direction.setClickable(true);
					}
				});
				direction_dialog=new Dialog(baseactivity,R.style.dialog);
				direction_dialog.setContentView(diaView);
				direction_dialog.show();
			}
		});
		
		//��ʼ������
		round_list.clear();
		addDatasTask = new AddDatasTask();
		addDatasTask.execute();
		
		/******************************��·�б����END***********************************/
		
		//����
		btn_type_selecte.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				View diaView = View.inflate(baseactivity, R.layout.roadselecte_type_dialog, null);
				LinearLayout layout_close = (LinearLayout)diaView.findViewById(R.id.layout_close);
				LinearLayout layout_tree= (LinearLayout)diaView.findViewById(R.id.layout_tree);
				//�ر�
				layout_close.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						dialog.dismiss();
						return false;
					}
				});
				dialog=new Dialog(baseactivity,R.style.dialog);
				dialog.setContentView(diaView);
				dialog.show();
				//���νṹ
				tree_listView = new TreeListView(baseactivity,initNodeTree(),0);
				tree_listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						((TreeAdapter) parent.getAdapter()).ExpandOrCollapse(position);
						Node item = ((TreeAdapter) parent.getAdapter()).getNowList().get(position);
						if(item.parentId.equals("-1") && !item.value.equals("2")){
							return;
						}
						if(item.parentId.equals("-1") && item.value.equals("2")){
							View keyboaddiaView = View.inflate(baseactivity, R.layout.number_keyboard, null);
							keyBoard = new KeyBoard(keyboaddiaView, "�������", new CloseOnClickListener(), new SureOnClickListener());
							keyboad_dialog = new Dialog(baseactivity,R.style.boarddialog);
							keyboad_dialog.setContentView(keyboaddiaView);
							keyboad_dialog.show();
							return;
						}
						if(Integer.parseInt(item.parentId) > 2){
							listdatasUpdate(3,item.value);
							View keyboaddiaView = View.inflate(baseactivity, R.layout.number_keyboard, null);
							keyBoard = new KeyBoard(keyboaddiaView, "�������", new CloseOnClickListener(), new SureStreetOnClickListener());
							keyboad_dialog = new Dialog(baseactivity,R.style.boarddialog);
							keyboad_dialog.setContentView(keyboaddiaView);
							keyboad_dialog.show();
							return;
						}
						listdatasUpdate(Integer.parseInt(item.parentId),item.value);
						if(item.childrens.size() == 0){
							dialog.dismiss();
						}
					}
				});
				layout_tree.addView(tree_listView);
			}
		});
	}
	/**���ּ��̹ر��¼�*/
	class CloseOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			keyboad_dialog.dismiss();
		}
	}
	/**���ּ���ȷ���¼�*/
	class SureOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String num = keyBoard.getNum();
			if(num.equals("")){
				Toast.makeText(baseactivity, "��������ţ�", Toast.LENGTH_SHORT).show();
			}else{
				listdatasUpdate(2,num);
				keyboad_dialog.dismiss();
				dialog.dismiss();
			}
		}
	}
	class SureStreetOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String num = keyBoard.getNum();
			if(num.equals("")){
				Toast.makeText(baseactivity, "��������ţ�", Toast.LENGTH_SHORT).show();
			}else{
				List<Round> round_list_street = new ArrayList<Round>();
				for(int i = 0; i < round_list.size() ; i++){
					Round item = round_list.get(i);
					if(item.roundId.equals(num)){
						round_list_street.add(item);
					}
				}
				round_list.clear();
				round_list.addAll(round_list_street);
				roadListadapter.notifyDataSetChanged();
				
				keyboad_dialog.dismiss();
				dialog.dismiss();
			}
		}
	}
	
	/**
	 * �б����ݸ���
	 * searchType:�������࣬0������������1������������2���������,3:�ֵ�����
	 */
	public void listdatasUpdate(int searchType,String searchContex){
		
		switch(searchType){
			case 0:
				if(searchContex.equals("-1")){
					round_list.clear();
					round_list.addAll(dbs.round.getList());
					roadListadapter.notifyDataSetChanged();
				}else{
					round_list.clear();
					round_list.addAll(dbs.round.getList(searchContex));
					roadListadapter.notifyDataSetChanged();
				}
				break;
			case 1:
				round_list.clear();
				round_list.addAll(dbs.round.getListByArea(searchContex));
				roadListadapter.notifyDataSetChanged();
				break;
			case 2:
				round_list.clear();
				round_list.addAll(dbs.round.getListByRoudId(searchContex));
				roadListadapter.notifyDataSetChanged();
				break;
			case 3:
				round_list.clear();
				round_list.addAll(dbs.round.getListByStreet(searchContex));
				roadListadapter.notifyDataSetChanged();
				break;
			default:
				break;
		}
	}
	
	/**���ͽṹ���ݼ���*/
	public List<NodeResource> initNodeTree(){
    	List <NodeResource> list = new ArrayList<NodeResource>();
    	NodeResource n1 = new NodeResource("-1","0", "��������","0",0);
    	list.add(n1);
    	NodeResource n2 = new NodeResource("-1","1", "��������", "1",0);
    	list.add(n2);
    	NodeResource n3 = new NodeResource("-1","2", "�������","2",0);
    	list.add(n3);
    	NodeResource n4 = new NodeResource("0","3", "ȫ��", "-1",0);
    	list.add(n4);
    	NodeResource n5 = new NodeResource("0","4", "����", "0",0);
    	list.add(n5);
    	NodeResource n6 = new NodeResource("0","5", "��·", "1",0);
    	list.add(n6);
    	NodeResource n7 = new NodeResource("1","6", "������", "������",0);
    	list.add(n7);
    	NodeResource n8 = new NodeResource("1","7", "������", "������",0);
    	list.add(n8);
    	NodeResource n9 = new NodeResource("1","8", "�޺���", "�޺���",0);
    	list.add(n9);
    	NodeResource n10 = new NodeResource("7","9", "���Խ�", "���Խ�",0);
    	list.add(n10);
    	
    	
    	return list;   
    }
	/**��·ģ������
	 *���101
	 *����������102
	 *��������103
	 *�޺�����104
	 *ƺɽ������105*/
	public void roadlistAdd(){
		if (dbs.round.getList().size()>0) {
			return;
		}
		try {
			Workbook workbook = Workbook.getWorkbook(new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath()+ File.separator+"road.xls"));
			Sheet sheet =workbook.getSheet(0);
			int rowNumber = sheet.getRows();
//			if ((rowNumber-1)==dbs.round.getList().size()) {
//				return;
//			}
//			Log.d("DEBUGGGGGGG", "����"+cloumNumber+"����"+rowNumber);
			for(int i=3;i<rowNumber;i++){
				Cell[] row = sheet.getRow(i);
					Round roundItem =null;
					roundItem = new Round();
					roundItem.roundId = row[0].getContents();
					roundItem.name = row[1].getContents()+" "+row[2].getContents();
					roundItem.type= row[3].getContents();
					roundItem.start_location = row[4].getContents()+" "+row[5].getContents();
					roundItem.end_location = row[6].getContents()+" "+row[7].getContents();
					roundItem.clength = row[8].getContents();
					roundItem.croadwidth =row[9].getContents();
					roundItem.flength = row[10].getContents();
					roundItem.froadwidth =row[11].getContents();
					//roundItem.first_litter=row[7].getContents();
					
					//getFirstLitter(roundItem.name);
					roundItem.state =Integer.parseInt(row[12].getContents());
					roundItem.area = row[13].getContents();
					roundItem.street=row[14].getContents();
					//roundItem.areaId = Integer.parseInt(row[10].getContents());
					roundItem.divide=row[15].getContents();			
					dbs.round.add(roundItem);
				
			}
			workbook.close();
			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Round roundItem = null;
//		roundItem = new Round();
//		roundItem.id= "101";
//		roundItem.roundId = "1";
//		roundItem.name = "���Ϲ�·";
//		roundItem.type= "������·";
//		roundItem.start_location = "��ɽ·";
//		roundItem.end_location = "���ζ�ͨ��·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 101;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "102";
//		roundItem.roundId = "2";
//		roundItem.name = "�����ˮ��·";
//		roundItem.type= "������·";
//		roundItem.start_location = "K0+000";
//		roundItem.end_location = "K1+800";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 101;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "103";
//		roundItem.roundId = "3";
//		roundItem.name = "������";
//		roundItem.type= "�ļ���·";
//		roundItem.start_location = "���ִ�";
//		roundItem.end_location = "����ѧУ";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "��������";
//		roundItem.areaId = 102;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "104";
//		roundItem.roundId = "4";
//		roundItem.name = "G205ɽ����";
//		roundItem.type= "һ����·";
//		roundItem.start_location = "��ݽ���";
//		roundItem.end_location = "����·��";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "��������";
//		roundItem.areaId = 102;
//		roundItem.divide="1";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "105";
//		roundItem.roundId = "5";
//		roundItem.name = "��ͨ��";
//		roundItem.type= "���п���·";
//		roundItem.start_location = "ˮ�ٸ����շ�վ����";
//		roundItem.end_location = "��ƺ��·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		roundItem.street = "���Խ�";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "106";
//		roundItem.roundId = "6";
//		roundItem.name = "����·";
//		roundItem.type= "���п���·";
//		roundItem.start_location = "��������";
//		roundItem.end_location = "�������";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "��������";
//		roundItem.areaId = 102;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "107";
//		roundItem.roundId = "7";
//		roundItem.name = "��ƽ����·";
//		roundItem.type= "���п���·";
//		roundItem.start_location = "����·";
//		roundItem.end_location = "���ɸ���";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "�޺���";
//		roundItem.areaId = 104;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "108";
//		roundItem.roundId = "8";
//		roundItem.name = "��ƽ����";
//		roundItem.type= "���п���·";
//		roundItem.start_location = "���ż�����������";
//		roundItem.end_location = "��ƽ�ؿ�";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "�޺���";
//		roundItem.areaId = 104;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "109";
//		roundItem.roundId = "9";
//		roundItem.name = "�������";
//		roundItem.type= "���п���·";
//		roundItem.start_location = "�������";
//		roundItem.end_location = "���Ǹ���";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "ƺɽ����";
//		roundItem.areaId = 105;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "110";
//		roundItem.roundId = "10";
//		roundItem.name = "���˴��";
//		roundItem.type= "����·";
//		roundItem.start_location = "���������ߴ���վ";
//		roundItem.end_location = "��ϼ·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "111";
//		roundItem.roundId = "11";
//		roundItem.name = "����·";
//		roundItem.type= "����·";
//		roundItem.start_location = "�����ɵ�";
//		roundItem.end_location = "����·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "112";
//		roundItem.roundId = "12";
//		roundItem.name = "ɳ��·";
//		roundItem.type= "����·";
//		roundItem.start_location = "K2+760";
//		roundItem.end_location = "K13+080";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "113";
//		roundItem.roundId = "13";
//		roundItem.name = "���·";
//		roundItem.type= "����·";
//		roundItem.start_location = "��ݹ�·";
//		roundItem.end_location = "���Ǳ�·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "114";
//		roundItem.roundId = "14";
//		roundItem.name = "����·";
//		roundItem.type= "����·";
//		roundItem.start_location = "��ͨ��";
//		roundItem.end_location = "�Ƹ�·";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state =  0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "115";
//		roundItem.roundId = "15";
//		roundItem.name = "����·";
//		roundItem.type= "����·";
//		roundItem.start_location = "2+899.622";
//		roundItem.end_location = "7+127";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="0";
//		dbs.round.add(roundItem);
//		
//		roundItem = new Round();
//		roundItem.id= "116";
//		roundItem.roundId = "16";
//		roundItem.name = "����·";
//		roundItem.type= "����·";
//		roundItem.start_location = "2+899.611";
//		roundItem.end_location = "7+125";
//		roundItem.length = "";
////		roundItem.first_litter=getFirstLitter(roundItem.name);
//		roundItem.state = 0;
//		roundItem.area = "������";
//		roundItem.areaId = 103;
//		roundItem.divide="1";
//		dbs.round.add(roundItem);
	}
	/**��ȡ����ĸ*/
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
	/**�������ݻ�ȡ*/
	public java.util.List<String> getIndexDatas(){
		java.util.List<String> indexlist = new ArrayList<String>();
		indexlist.add("A");
		indexlist.add("B");
		indexlist.add("C");
		indexlist.add("D");
		indexlist.add("E");
		indexlist.add("F");
		indexlist.add("G");
		indexlist.add("H");
		indexlist.add("I");
		indexlist.add("J");
		indexlist.add("K");
		indexlist.add("M");
		indexlist.add("N");
		indexlist.add("L");
		indexlist.add("O");
		indexlist.add("P");
		indexlist.add("Q");
		indexlist.add("R");
		indexlist.add("S");
		indexlist.add("T");
		indexlist.add("U");
		indexlist.add("V");
		indexlist.add("W");
		indexlist.add("X");
		indexlist.add("Y");
		indexlist.add("Z");
		
		return indexlist;
	}
	
	/**ҳ�����ݼ���*/
	private class AddDatasTask extends AsyncTask<String, String, String>{
		@Override
		protected void onPreExecute() {
//			lastPage = false;
		}
		@Override
		protected String doInBackground(String... params) {
			//ģ�����ݼ���
			roadlistAdd();
			return "0";
		}
		@Override
		protected void onPostExecute(String result) {
			round_list.addAll(dbs.round.getList());
			roadListadapter.notifyDataSetChanged();
		}
	}
	/**��ȡͼƬ����*/
	public String getimg_num(String direction,int category){
		Round round_item = new Round();
		round_item = dbs.round.getItem(cApp.RoundId);
		
		List<Road_Check_Data_Table> i_list = new ArrayList<Road_Check_Data_Table>();
		i_list.addAll(dbs.road_check_table.getList(round_item.roundId,direction, category));
		int num = 0;
		for(int i = 0 ; i < i_list.size();i++){
			String[] imgArray = i_list.get(i).imagePathString.split(",");
			num += imgArray.length;
		}
		return num + "";
	}
}
