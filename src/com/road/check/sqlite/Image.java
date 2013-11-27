package com.road.check.sqlite;

import java.util.ArrayList;
import android.database.Cursor;

public class Image {
	private DBOpenHelper dbOpenHelper;
	public Image(DBOpenHelper dbOpenHelper){
		this.dbOpenHelper = dbOpenHelper;
	}
	
	/**添加*/
	public void add(com.road.check.model.Image item){
		Cursor c = dbOpenHelper.getWritableDatabase().rawQuery(  
                "select imagepath from image where imagepath=?",  
                new String[]{ item.imagepath });
		if (c == null || c.getCount() == 0){
			dbOpenHelper.getWritableDatabase().execSQL(
	                "insert into image (roadcheckid,imagepath,imagename,imagedescribe,imagelocation,i_name,i_distance,i_start_from_start,i_end_from_start) values(?,?,?,?,?,?,?,?,?)",  
	                new Object[] {item.roadcheckid,item.imagepath,item.imagename,item.imagedescribe,item.imglocation,item.i_name,item.i_distance,item.i_start_from_start,item.i_end_from_start});
		}
		c.close();
	}
	
	/**获取列表 */
	public ArrayList<com.road.check.model.Image> getList(int roadcheckid){
		ArrayList<com.road.check.model.Image> list = new ArrayList<com.road.check.model.Image>();
		com.road.check.model.Image item = null;
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roadcheckid,imagepath,imagename,imagedescribe,i_name,i_distance,i_start_from_start,i_end_from_start from image where roadcheckid=?",  
                new String[]{roadcheckid + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Image();
				item.Id = cur.getInt(0);
				item.roadcheckid = cur.getString(1);
				item.imagepath = cur.getString(2);
				item.imagename = cur.getString(3);
				item.imagedescribe = cur.getString(4);
				item.i_name = cur.getString(5);
				item.i_distance = cur.getString(6);
				item.i_start_from_start = cur.getString(7);
				item.i_end_from_start = cur.getString(8);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<String> getPathList(int roadcheckid){
		ArrayList<String> list = new ArrayList<String>();
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select imagepath from image where roadcheckid=?",  
				new String[]{roadcheckid + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				list.add(cur.getString(0));
			}
			cur.close();
		}
		return list;
	}
	public String getItemName(String imagepath){
		String imagename = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select imagename from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagename = cur.getString(0);
			}
			cur.close();
		}
		return imagename;
	}
	public String getItemDescribe(String imagepath){
		String imagedescribe = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select imagedescribe from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagedescribe = cur.getString(0);
			}
			cur.close();
		}if (imagedescribe==null) {
			return " ";
		}else {
			return imagedescribe;
		}

	}
	public String getItemLocation(String imagepath){
		String imagelocation = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select imagelocation from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagelocation = cur.getString(0);
			}
			cur.close();
		}
		return imagelocation;
	}
	public String getItemI_Name(String imagepath){
		String imagei_name = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select i_name from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagei_name = cur.getString(0);
			}
			cur.close();
		}
		return imagei_name;
	}
	public String getItemI_Distance(String imagepath){
		String imagei_distance = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select i_distance from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagei_distance = cur.getString(0);
			}
			cur.close();
		}
		return imagei_distance;
	}
	public String getItemI_Start_From_Start(String imagepath){
		String imagei_start_from_start = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select i_start_from_start from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagei_start_from_start = cur.getString(0);
			}
			cur.close();
		}
		return imagei_start_from_start;
	}
	public String getItemI_End_From_Start(String imagepath){
		String imagei_end_from_start = "";
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select i_end_from_start from image where imagepath=?",  
				new String[]{imagepath + ""});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				imagei_end_from_start = cur.getString(0);
			}
			cur.close();
		}
		return imagei_end_from_start;
	}
	
	/**删除*/
	public void clear(){
		this.dbOpenHelper.getWritableDatabase().delete("image", "", null);
	}
	public boolean deleteDatas(String id){
		return this.dbOpenHelper.getWritableDatabase().delete("image", "roadcheckid = " + id, null) > 0;
	}
}

