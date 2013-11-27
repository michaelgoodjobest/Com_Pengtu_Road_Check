package com.road.check.sqlite;

import java.util.ArrayList;

import android.database.Cursor;

public class VerifyDatas {
	private DBOpenHelper dbOpenHelper;
	public VerifyDatas(DBOpenHelper dbOpenHelper){
		this.dbOpenHelper = dbOpenHelper;
	}
	
	/**添加*/
	public void add(com.road.check.model.VerifyDatas item){
		Cursor c = dbOpenHelper.getWritableDatabase().rawQuery(  
                "select id from verifydatas where id=?",  
                new String[]{ item.id + "" });
		if (c == null || c.getCount() == 0){
			dbOpenHelper.getWritableDatabase().execSQL(
	                "insert into verifydatas (roadId,check_road_lenth,footwalk_width,addTime) values(?,?,?,?)",  
	                new Object[] {item.roadId,item.check_road_lenth,item.footwalk_width,item.addTime});
		}
		c.close();
	}
	
	/**获取列表*/
	public ArrayList<com.road.check.model.VerifyDatas> getList(){
		ArrayList<com.road.check.model.VerifyDatas> list = new ArrayList<com.road.check.model.VerifyDatas>();
		com.road.check.model.VerifyDatas item = null;
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roadId,check_road_lenth,footwalk_width,addTime from verifydatas",  
                null);
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.VerifyDatas();
				
				item.id = cur.getInt(0);
				item.roadId = cur.getString(1);
				item.check_road_lenth = cur.getString(2);
				item.footwalk_width = cur.getString(3);
				item.addTime = cur.getString(4);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	
	/**删除*/
	public void clear(){
		this.dbOpenHelper.getWritableDatabase().delete("verifydatas", "", null);
	}
}
