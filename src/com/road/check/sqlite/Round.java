package com.road.check.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

public class Round {
	private DBOpenHelper dbOpenHelper;
	public Round(DBOpenHelper dbOpenHelper){
		this.dbOpenHelper = dbOpenHelper;
	}
	
	/**添加*/
	public void add(com.road.check.model.Round item){
//		Cursor c = dbOpenHelper.getWritableDatabase().rawQuery(  
//                "select id from round where id=?",  
//                new String[]{ item.id });
//		if (c == null || c.getCount() == 0){
			dbOpenHelper.getWritableDatabase().execSQL(
	                "insert into round (roundId,name,type,start_location,end_location,clength,flength,first_litter,state,area,areaId,divide,street,croadwidth,froadwidth) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",  
	                new Object[] {item.roundId,item.name,item.type,item.start_location,item.end_location,item.clength,item.flength,item.first_litter,item.state,item.area,item.areaId,item.divide,item.street,item.croadwidth,item.froadwidth});
//		}
//		c.close();
	}
	
	/**获取列表 （发布时间降序排列）*/
	public ArrayList<com.road.check.model.Round> getList(){
		ArrayList<com.road.check.model.Round> list = new ArrayList<com.road.check.model.Round>();
		com.road.check.model.Round item = null;
		String[] columnsarray = new String[]{"id","roundId","name","type","start_location","end_location","clength","first_litter","state","area","areaId","divide","street","croadwidth","flength","froadwidth"};
		Cursor cur = dbOpenHelper.getWritableDatabase().query(true,"round", columnsarray, "", null, "", "", "first_litter", null);
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Round();
				
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Round> getList(String divide){
		ArrayList<com.road.check.model.Round> list = new ArrayList<com.road.check.model.Round>();
		com.road.check.model.Round item = null;
		String[] columnsarray = new String[]{"id","roundId","name","type","start_location","end_location","clength","first_litter","state","area","areaId","divide","street","croadwidth","flength","froadwidth"};
		Cursor cur = dbOpenHelper.getWritableDatabase().query(true,"round", columnsarray,"divide=" + divide, null, "", "", "first_litter", null);
		
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Round();
				
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
				
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Round> getListByArea(String area){
		ArrayList<com.road.check.model.Round> list = new ArrayList<com.road.check.model.Round>();
		com.road.check.model.Round item = null;
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roundId,name,type,start_location,end_location,clength,first_litter,state,area,areaId,divide,street,croadwidth,flength,froadwidth from round where area=?",  
                new String[]{area});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Round();
				
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Round> getListByRoudId(String roundId){
		ArrayList<com.road.check.model.Round> list = new ArrayList<com.road.check.model.Round>();
		com.road.check.model.Round item = null;
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roundId,name,type,start_location,end_location,clength,first_litter,state,area,areaId,divide,street,croadwidth,flength,froadwidth from round where roundId=?",  
                new String[]{roundId});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Round();
				
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	public ArrayList<com.road.check.model.Round> getListByStreet(String street){
		ArrayList<com.road.check.model.Round> list = new ArrayList<com.road.check.model.Round>();
		com.road.check.model.Round item = null;
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roundId,name,type,start_location,end_location,clength,first_litter,state,area,areaId,divide,street,croadwidth,flength,froadwidth from round where street=?",  
				new String[]{street});
		if (cur != null && cur.getCount() >= 0){
			while(cur.moveToNext()){
				item = new com.road.check.model.Round();
				
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
				list.add(item);
			}
			cur.close();
		}
		return list;
	}
	/**获取当前用户对象*/
	public com.road.check.model.Round getItem(String id){
		com.road.check.model.Round item =  new com.road.check.model.Round();
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roundId,name,type,start_location,end_location,clength,first_litter,state,area,areaId,divide,street,croadwidth,flength,froadwidth from round where id=?",  
                new String[]{id});
		if (cur != null && cur.getCount() >= 0){
			if(cur.moveToNext()){
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
			}
			cur.close();
		}
		return item;
	}
	public com.road.check.model.Round getItemByRoundId(String roundId){
		com.road.check.model.Round item =  new com.road.check.model.Round();
		
		Cursor cur = dbOpenHelper.getWritableDatabase().rawQuery(  
				"select id,roundId,name,type,start_location,end_location,clength,first_litter,state,area,areaId,divide,street,croadwidth,flength,froadwidth from round where roundId=?",  
                new String[]{roundId});
		if (cur != null && cur.getCount() >= 0){
			if(cur.moveToNext()){
				item.id = cur.getInt(0);
				item.roundId = cur.getString(1);
				item.name = cur.getString(2);
				item.type = cur.getString(3);
				item.start_location = cur.getString(4);
				item.end_location = cur.getString(5);
				item.clength = cur.getString(6);
				item.first_litter = cur.getString(7);
				item.state = cur.getInt(8);
				item.area = cur.getString(9);
				item.areaId = cur.getInt(10);
				item.divide = cur.getString(11);
				item.street = cur.getString(12);
				item.croadwidth = cur.getString(13);
				item.flength = cur.getString(14);
				item.froadwidth = cur.getString(15);
			}
			cur.close();
		}
		return item;
	}
	/**修改选中状态*/
	public boolean updateState(int state,String id){
		ContentValues args_before = new ContentValues(); 
		args_before.put("state", 0);
		dbOpenHelper.getWritableDatabase().update("round", args_before,"", null);
		
		ContentValues args = new ContentValues(); 
		args.put("state", state);
		return this.dbOpenHelper.getWritableDatabase().update("round", args,"id=" + id, null) > 0;
	}
	/**删除*/
	public void clear(){
		this.dbOpenHelper.getWritableDatabase().delete("round", "", null);
	}
}
