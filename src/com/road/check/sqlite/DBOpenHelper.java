package com.road.check.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
	private static final String name = "roadcheck.db";
	private static final int version = 2;
	public DBOpenHelper(Context context) {
		super(context, name,null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS user (phoneId varchar(200),teamMember varchar(60),pricipal varchar(60),mobileNo varchar(60),dutyArea varchar(200),remark varchar(200))");
		db.execSQL("CREATE TABLE IF NOT EXISTS round (id INTEGER PRIMARY KEY,roundId varchar(60),name varchar(100),type varchar(60),start_location varchar(60),end_location varchar(60),clength varchar(60),first_litter varchar(60),state integer,area varchar(100),areaId integer,divide varchar(60),street varchar(100),croadwidth varchar(60),flength varchar(60),froadwidth varchar(60))");
		db.execSQL("CREATE TABLE IF NOT EXISTS road_check_data_table (Id INTEGER PRIMARY KEY,roundName varchar(60)," +
				"roundId varchar(60),direction varchar(60),deteTime varchar(60),mark varchar(60),longFromStartNum varchar(60),diseaseString varchar(500),imagePathString varchar(500)," +
				"firstImageUrl varchar(100),discribe varchar(200),remark varchar(60),isSelected integer,according varchar(60),reportType integer,category integer,r_roundTypeNum varchar(60)," +
				"b_diseaseDegree varchar(60),d_roadbedDiseaseString varchar(200),d_dewateringDiseaseSting varchar(200),f_footwalkwidth varchar(60)," +
				"f_damagedArea varchar(60),e_evennessType varchar(60),e_evennessNum varchar(60),e_evennessMean varchar(60),e_evennessPassNum varchar(60)," +
				"o_structure varchar(60),o_facility varchar(60),isUploading integer,imageNameString varchar(500),intersection_name varchar(60)," +
				"intersection_length_from_start varchar(60),intersectionstart_length_from_start varchar(60),intersectionend_length_from_start varchar(60),reportName varchar(100))");  
		db.execSQL("CREATE TABLE IF NOT EXISTS verifydatas (id INTEGER PRIMARY KEY,roadId varchar(60),check_road_lenth varchar(60),footwalk_width varchar(200),addTime varchar(100))");
		db.execSQL("CREATE TABLE IF NOT EXISTS image (id INTEGER PRIMARY KEY,roadcheckid varchar(60), imagepath varchar(200),imagename varchar(200),imagedescribe varchar(200),imagelocation varchar(200),i_name varchar(100),i_distance varchar(60),i_start_from_start varchar(60),i_end_from_start varchar(60))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS user");
		db.execSQL("DROP TABLE IF EXISTS round");
		db.execSQL("DROP TABLE IF EXISTS road_check_data_table");
		db.execSQL("DROP TABLE IF EXISTS verifydatas");
		db.execSQL("DROP TABLE IF EXISTS image");
		onCreate(db);
	}

}
