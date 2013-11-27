package com.road.check.base;

import com.road.check.common.CheckApplication;

import android.app.Activity;
import android.os.Bundle;

public class ActivityBase extends Activity{
	protected CheckApplication cApp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(cApp == null){
			cApp = new CheckApplication();
		}
	}
}
