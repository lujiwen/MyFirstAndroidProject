package com.seadee.degree.activity;

import com.seadee.degree.ethernet.EthernetSettings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class EthernetSettingActivity extends Activity  {

	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new EthernetSettings()).commit();
	}
}
