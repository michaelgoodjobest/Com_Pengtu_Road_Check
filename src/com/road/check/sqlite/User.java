package com.road.check.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;


public class User {
	private DBOpenHelper dbOpenHelper;
	
	public User(DBOpenHelper dbOpenHelper){
		this.dbOpenHelper = dbOpenHelper;
	}
	
	/**添加*/
	public void add(com.road.check.model.User item){
		//this.dbOpenHelper.getWritableDatabase().delete("user", "", null);
		
		Cursor c = dbOpenHelper.getWritableDatabase().rawQuery(  
                "select phoneId from user where phoneId=?",  
                new String[]{ item.phoneId });
		if (c == null || c.getCount() == 0){
			dbOpenHelper.getWritableDatabase().execSQL(
	                "insert into user (phoneId,teamMember,pricipal,mobileNo,dutyArea,remark) values(?,?,?,?,?,?)",  
	                new Object[] {item.phoneId,item.teamMember,item.pricipal,item.mobileNo,item.dutyArea,item.remark});
		}
		c.close();
	}
	
	/**获取列表 （发布时间降序排列）*/
	public ArrayList<com.road.check.model.User> getList(){
		ArrayList<com.road.check.model.User> list = new ArrayList<com.road.check.model.User>();
		com.road.check.model.User item = null;
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select phoneId,teamMember,pricipal,mobileNo,dutyArea,remark from user ",  
                null);
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.User();
				
				item.phoneId = cur.getString(0);
				item.teamMember = cur.getString(1);
				item.pricipal = cur.getString(2);
				item.mobileNo = cur.getString(3);
				item.dutyArea = cur.getString(4);
				item.remark = cur.getString(5);
				
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	/**获取当前用户对象*/
	public com.road.check.model.User getItem(){
		com.road.check.model.User item =  new com.road.check.model.User();
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select phoneId,teamMember,pricipal,mobileNo,dutyArea,remark from user ",  
                null);
		if (cur != null && cur.getCount() >= 0){
			if(cur.moveToNext()){
				item.phoneId = cur.getString(0);
				item.teamMember = cur.getString(1);
				item.pricipal = cur.getString(2);
				item.mobileNo = cur.getString(3);
				item.dutyArea = cur.getString(4);
				item.remark = cur.getString(5);

			}
			cur.close();
		}
		return item;
	}
	/**修改选中状态*/
//	public boolean updateState(String sessionId){
//		  ContentValues args = new ContentValues(); 
//		  args.put("sessionId", sessionId);
//		  
//		  return this.dbOpenHelper.getWritableDatabase().update("user", args,"", null) > 0;
//	}
}
