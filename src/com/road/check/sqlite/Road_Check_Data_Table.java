package com.road.check.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

public class Road_Check_Data_Table {
	private DBOpenHelper dbOpenHelper;
	public Road_Check_Data_Table(DBOpenHelper dbOpenHelper){
		this.dbOpenHelper = dbOpenHelper;
	}
	/**添加*/
	public int add(com.road.check.model.Road_Check_Data_Table item){
			dbOpenHelper.getWritableDatabase().execSQL(
	                "insert into road_check_data_table (roundName,roundId,direction,deteTime,mark,longFromStartNum," +
	                "diseaseString,imagePathString,firstImageUrl,discribe,remark,isSelected,according," +
	                "reportType,category,r_roundTypeNum,b_diseaseDegree,d_roadbedDiseaseString," +
	                "d_dewateringDiseaseSting,f_footwalkwidth,f_damagedArea,e_evennessType,e_evennessNum," +
	                "e_evennessMean,e_evennessPassNum,o_structure,o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start," +
	                "intersectionend_length_from_start,reportName) " +
	                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",  
	                new Object[] {item.roundName,item.roundId,item.direction,item.deteTime,
	                		item.mark,item.longFromStartNum,item.diseaseString,item.imagePathString,
	                		item.firstImageUrl,item.discribe,item.remark,item.isSelected,item.according,item.reportType,item.category,
	                		item.r_roundTypeNum,item.b_diseaseDegree,item.d_roadbedDiseaseString,item.d_dewateringDiseaseSting,
	                		item.f_footwalkwidth,item.f_damagedArea,item.e_evennessType,item.e_evennessNum,item.e_evennessMean,
	                		item.e_evennessPassNum,item.o_structure,item.o_facility,item.isUploading,item.imageNameString,item.intersection_name,
	                		item.intersection_length_from_start,item.intersectionstart_length_from_start,item.intersectionend_length_from_start,item.reportName});
				
			Cursor cur=dbOpenHelper.getWritableDatabase().rawQuery("select LAST_INSERT_ROWID() from road_check_data_table",null);
			int id = -1;
			if(cur.moveToFirst()){
				id = cur.getInt(0);
				cur.close();
            }
            return id;
	}
	
	/**获取列表数据 */
	public ArrayList<com.road.check.model.Road_Check_Data_Table> getUploadedList(){
		ArrayList<com.road.check.model.Road_Check_Data_Table> list = new ArrayList<com.road.check.model.Road_Check_Data_Table>();
		com.road.check.model.Road_Check_Data_Table item = null;
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
				"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
				"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
				"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
				"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where isUploading=?",  
                new String[]{"1"});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Road_Check_Data_Table();
				
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Road_Check_Data_Table> getunUploadedList(){
		ArrayList<com.road.check.model.Road_Check_Data_Table> list = new ArrayList<com.road.check.model.Road_Check_Data_Table>();
		com.road.check.model.Road_Check_Data_Table item = null;
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
				"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
				"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
				"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
				"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where isUploading=?",  
                new String[]{"0"});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Road_Check_Data_Table();
				
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	/**通过道路编号，方向,分类获取列表*/
	public ArrayList<com.road.check.model.Road_Check_Data_Table> getList(String roadId,String direction,int category){
		ArrayList<com.road.check.model.Road_Check_Data_Table> list = new ArrayList<com.road.check.model.Road_Check_Data_Table>();
		com.road.check.model.Road_Check_Data_Table item = null;
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
				"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
				"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
				"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
				"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where roundId=? and direction=? and category=?",  
                new String[]{roadId,direction,category + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Road_Check_Data_Table();
				
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	
	public ArrayList<com.road.check.model.Road_Check_Data_Table> getList(int category){
		ArrayList<com.road.check.model.Road_Check_Data_Table> list = new ArrayList<com.road.check.model.Road_Check_Data_Table>();
		com.road.check.model.Road_Check_Data_Table item = null;
		
		Cursor cur = null;
		if(category != -1){
			cur = dbOpenHelper.getWritableDatabase().rawQuery(  
					"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
					"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
					"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
					"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
					"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where category=? and isUploading=?",  
					new String[]{category + "","0"});
		}else{
			cur = dbOpenHelper.getWritableDatabase().rawQuery(  
					"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
					"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
					"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
					"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
					"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where isUploading=?",  
					new String[]{"0"});
		}
		
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Road_Check_Data_Table();
				
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Road_Check_Data_Table> getSelecteList(int isSelected){
		ArrayList<com.road.check.model.Road_Check_Data_Table> list = new ArrayList<com.road.check.model.Road_Check_Data_Table>();
		com.road.check.model.Road_Check_Data_Table item = null;
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
				"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
				"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
				"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
				"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where isSelected=? and isUploading=?",  
				new String[]{isSelected + "","0"});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Road_Check_Data_Table();
				
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public com.road.check.model.Road_Check_Data_Table getItem(String id){
		com.road.check.model.Road_Check_Data_Table item = new com.road.check.model.Road_Check_Data_Table();
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select Id,roundName,roundId,direction,deteTime,mark,longFromStartNum,diseaseString," +
				"imagePathString,firstImageUrl,discribe,remark,isSelected,according,reportType,category,r_roundTypeNum," +
				"b_diseaseDegree,d_roadbedDiseaseString,d_dewateringDiseaseSting,f_footwalkwidth," +
				"f_damagedArea,e_evennessType,e_evennessNum,e_evennessMean,e_evennessPassNum,o_structure," +
				"o_facility,isUploading,imageNameString,intersection_name,intersection_length_from_start,intersectionstart_length_from_start,intersectionend_length_from_start,reportName from road_check_data_table where Id=?",  
				new String[]{id});
		if (cur != null && cur.getCount() >= 0){
			if(cur.moveToNext()){
				item.Id = cur.getInt(0);
				item.roundName = cur.getString(1);
				item.roundId = cur.getString(2);
				item.direction = cur.getString(3);
				item.deteTime = cur.getString(4);
				item.mark = cur.getString(5);
				item.longFromStartNum = cur.getString(6);
				item.diseaseString = cur.getString(7);
				item.imagePathString = cur.getString(8);
				item.firstImageUrl = cur.getString(9);
				item.discribe = cur.getString(10);
				item.remark = cur.getString(11);
				item.isSelected = cur.getInt(12);
				item.according = cur.getString(13);
				item.reportType = cur.getInt(14);
				item.category = cur.getInt(15);
				item.r_roundTypeNum = cur.getString(16);
				item.b_diseaseDegree = cur.getString(17);
				item.d_roadbedDiseaseString = cur.getString(18);
				item.d_dewateringDiseaseSting =cur.getString(19);
				item.f_footwalkwidth = cur.getString(20);
				item.f_damagedArea = cur.getString(21);
				item.e_evennessType = cur.getString(22);
				item.e_evennessNum = cur.getString(23);
				item.e_evennessMean = cur.getString(24);
				item.e_evennessPassNum = cur.getString(25);
				item.o_structure = cur.getString(26);
				item.o_facility = cur.getString(27);
				item.isUploading = cur.getInt(28);
				item.imageNameString = cur.getString(29);
				item.intersection_name = cur.getString(30);
				item.intersection_length_from_start = cur.getString(31);
				item.intersectionstart_length_from_start = cur.getString(32);
				item.intersectionend_length_from_start = cur.getString(33);
				item.reportName = cur.getString(34);
			}
			cur.close();
		}
		return item;
	}
	/**判断全部选择*/
	public boolean isAllSelected(){
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select isSelected from road_check_data_table where isUploading=?",  
				new String[]{"0"});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				if(cur.getInt(0) == 0){
					return false;
				}
			}
			cur.close();
		}
		return true;
	}
	/**获取图片地址串集合*/
	public ArrayList<String> getImagePathList(){
		ArrayList<String> list = new ArrayList<String>();
		
//		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
//				"select imagePathString from road_check_data_table",  
//				null);
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
		"select firstImageUrl from road_check_data_table",  
		null);
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				list.add(cur.getString(0));
			}
			cur.close();
		}
		return list;
	}
	/**修改选中状态*/
	public boolean updateState(int isSelected,String id){
		  ContentValues args = new ContentValues(); 
		  args.put("isSelected", isSelected);
		  
		  return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table", args,"Id=" + id, null) > 0;
	}
	public boolean updateState(int isSelected){
		  ContentValues args = new ContentValues(); 
		  args.put("isSelected", isSelected);
		  
		  return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"isUploading=0", null) > 0;
	}
	public boolean updateState(int isSelected,int category){
		ContentValues args = new ContentValues(); 
		args.put("isSelected", isSelected);
		
		if(category != -1){
			return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"category =" + category + " and isUploading=0", null) > 0;
		}else{
			return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"isUploading=0", null) > 0;
		}
	}
	public boolean updateUploadingState(){
		ContentValues args = new ContentValues(); 
		args.put("isUploading", 1);
		
		return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"isSelected = 1 and isUploading=0", null) > 0;
	}
	//取消对录检记录的selected赋值
	public boolean updateErrorUploadingState(String id){
		ContentValues args = new ContentValues(); 
		args.put("isSelected", 0);
		
		return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"isSelected = 1 and isUploading=0 and Id="+id, null) > 0;
	}
	public boolean updateOneKeyUploadingState(){
		ContentValues args = new ContentValues(); 
		args.put("isUploading", 1);
		args.put("isSelected", 1);
		
		return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"isUploading=0", null) > 0;
	}
	public boolean updateById(ContentValues args,String Id){
		return this.dbOpenHelper.getWritableDatabase().update("road_check_data_table",args,"Id=" + Id, null) > 0;
	}
	/**数据删除*/
	public boolean deleteDatas(){
		return this.dbOpenHelper.getWritableDatabase().delete("road_check_data_table", "isSelected = 1", null) > 0;
	}
	public boolean deleteDatas(String id){
		return this.dbOpenHelper.getWritableDatabase().delete("road_check_data_table", "Id = " + id, null) > 0;
	}
}
