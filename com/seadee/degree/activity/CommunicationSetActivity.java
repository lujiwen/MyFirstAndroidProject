package com.seadee.degree.activity;

import android.app.Activity;
import android.os.Bundle;

import com.seadee.degree.comunication.CommunicationSetting;

public class CommunicationSetActivity extends Activity 
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new CommunicationSetting()).commit();
	}
	
}
