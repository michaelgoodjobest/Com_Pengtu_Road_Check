package com.road.check.model;

public class Road_Check_Data_Table {
	public int Id = 0;//编号
	public String roundName = "";//道路名称
	public String roundId = "";//道路序号
	public String direction = "";//方向
	public String deteTime = "";//时间
	public String mark = "";//桩号
	public String longFromStartNum = "";//距离起点
	public String diseaseString = "";//病害串
	public String imagePathString = "";//图片地址串
	public String firstImageUrl = "";//第一张图片路径
	public String discribe = "";//描述
	public String remark = "";//备注
	public int isSelected = 0;//0未选择，1选择
	public String according = "";//调查依据
	public String reportName = "";//报表名名称
	public int reportType = 0;//报表类型
	public int category = 0;//分类0:病害，1：上行起点，2：路牌，3：路口，4：人行道，5：上行终点，6：下行起点，7：下行终点
	public String r_roundTypeNum = "";//路面类型
	public String b_diseaseDegree = "";//病害程度
	public String d_roadbedDiseaseString = "";//路基病害串
	public String d_dewateringDiseaseSting ="";//排水设施病害串
	public String f_footwalkwidth = "";//人行道宽
	public String f_damagedArea = "";//破损面积
	public String e_evennessType = "";//车道/人行道
	public String e_evennessNum = "";//平整度
	public String e_evennessMean = "";//平均值
	public String e_evennessPassNum = "";//合格率
	public String o_structure = "";//附属结构
	public String o_facility = "";//附属设施
	public int isUploading = 0;//是否已经上传0:否，1：是
	public String imageNameString = "";//图片名称
	public String intersection_name ="";//路口名称
	public String intersection_length_from_start = "";//距离起点
	public String intersectionstart_length_from_start = "";//路口起点距离起点
	public String intersectionend_length_from_start = "";//路口终点距离起点
}
