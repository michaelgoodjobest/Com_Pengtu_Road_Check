package com.road.check.sqlite;

import android.content.Context;

public class DatabaseService {
	private DBOpenHelper dBOpenHelper;
	public User user;
	public Round round;
	public Road_Check_Data_Table road_check_table;
	public VerifyDatas verifydatas;
	public Image image;
	public DatabaseService(Context context){
		if(dBOpenHelper == null){
			dBOpenHelper = new DBOpenHelper(context);
		}
		
		user = new User(dBOpenHelper);
		round = new Round(dBOpenHelper);
		road_check_table = new Road_Check_Data_Table(dBOpenHelper);
		verifydatas = new VerifyDatas(dBOpenHelper);
		image = new Image(dBOpenHelper);
	}
	public void closeDatabase(){
		dBOpenHelper.getWritableDatabase().close();
	}
}
