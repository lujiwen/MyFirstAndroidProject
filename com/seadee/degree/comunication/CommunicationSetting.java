package com.seadee.degree.comunication;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.renderscript.Sampler.Value;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.settting.SettingsPreferenceFragment;


public class CommunicationSetting extends SettingsPreferenceFragment implements TextWatcher,  Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener,
DialogInterface.OnClickListener, DialogInterface.OnDismissListener,View.OnKeyListener, OnSharedPreferenceChangeListener
{

	private  ListPreference baudrRateList;
	private  Preference hostAddressSetting,slaveAddressSetting; 	
	SharedPreferences sharedPreferences;
	private static Context context;
	private final static String BAUDRRATE_KEY = "Baud_rate";
	private final static String DATABIT_KEY = "dataBit";
	private final static String STOPBIT_KEY = "stopBit";
	private final static String HOST_ADDRESS_KEY = "hostAddressSetting";
	private final static String SLAVE_ADDRESS_KEY = "slaveAddressSetting";
	private  final static String SHAREPREFERENCE_FILENAME = "com.seadee.degree_comunication_pref";
	private AddressDialog[] addresDlg = new AddressDialog[2];
	private int focus ;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.e("conmunication","onCreate!");
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.communication_setting);
		 getPreferenceManager().setSharedPreferencesName(SHAREPREFERENCE_FILENAME);		 
		addresDlg = new AddressDialog[2];
		/*数据通信*/
		baudrRateList = (ListPreference)findPreference(BAUDRRATE_KEY);
		//databitList = (ListPreference)findPreference("dataBit");
		//stopbitList = (Preference)findPreference("stopBit");	
		hostAddressSetting = (Preference)findPreference(HOST_ADDRESS_KEY);
		slaveAddressSetting = (Preference)findPreference(SLAVE_ADDRESS_KEY);
		baudrRateList.setOnPreferenceChangeListener(this);
		//stopbitList.setOnPreferenceChangeListener(this);
		hostAddressSetting.setOnPreferenceChangeListener(this);  // ？？不能实现自动存储  所以自己存储到了degree_comunication_pref.xml文件中
		slaveAddressSetting.setOnPreferenceChangeListener(this); // ？？不能实现自动存储
	
	}
	private void  initSummary()
	{
		Log.e("initsummar", "123");
		int[] settings = new int[5];
		settings[0] = Integer.parseInt(HandleFile.getRecord(getActivity(), SHAREPREFERENCE_FILENAME, BAUDRRATE_KEY, "9600" )); 
		settings[1] = Integer.parseInt(HandleFile.getRecord(getActivity(), SHAREPREFERENCE_FILENAME, DATABIT_KEY, "8"));
		settings[2] = Integer.parseInt(HandleFile.getRecord(getActivity(), SHAREPREFERENCE_FILENAME, STOPBIT_KEY, "1")); 
		settings[3] = HandleFile.getRecord(getActivity(), SHAREPREFERENCE_FILENAME, HOST_ADDRESS_KEY, 0) ;
		settings[4] = HandleFile.getRecord(getActivity(), SHAREPREFERENCE_FILENAME, SLAVE_ADDRESS_KEY, 1) ;
		Log.e("settings",settings[0]+":"+settings[1]+":"+settings[2]+":"+settings[3]+":"+settings[4]);
		baudrRateList.setSummary(String.valueOf(settings[0]));
		//stopbitList.setSummary(HandleFile.getRecord(HomeActivity.getInstance(), SHAREPREFERENCE_FILENAME,"stopBit", ""));
		hostAddressSetting.setSummary(String.valueOf(settings[3]));
		slaveAddressSetting.setSummary(String.valueOf(settings[4]));
	}
	
    @Override
    public boolean onPreferenceClick(Preference preference) {
    	Log.e("ethernet", "onPreferenceClick");
        return true;
    }
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) 
    {
    	Log.e("peferenceKey", preference.getKey());
    	if(preference.getKey().equals(HOST_ADDRESS_KEY))
    	{
    		Log.e("onPreferenceTreeClick", HOST_ADDRESS_KEY);
    		focus = 0;
    		addresDlg[0] = new AddressDialog(getActivity(),this);
    		addresDlg[0].show();
    		addresDlg[0].getAddressEditText().addTextChangedListener(this);    		 
    	}
    	else if(preference.getKey().equals(SLAVE_ADDRESS_KEY))
    	{
    		Log.e("onPreferenceTreeClick", SLAVE_ADDRESS_KEY);
    		focus = 1;
    		addresDlg[1] = new AddressDialog(getActivity(),this);
    		addresDlg[1].show();
    		addresDlg[1].getAddressEditText().addTextChangedListener(this);    
    	}
    	return false ;
    }
    
	@Override
	public void onResume()
	{	
		super.onResume();		
		Log.e("communication","onResume");
		initSummary();
	 	getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);		
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		Log.e("comuniacation!", "pause");
		
		//修改之后把485设置重新传给JNI
		new Thread()
		{
			@Override
			public void run() 
			{
				int[] settting  = new int[5];
				settting[0] = Integer.parseInt(String.valueOf(baudrRateList.getSummary()));
				settting[1] = 8;
				settting[2] = 1;
				settting[3] = String.valueOf(hostAddressSetting.getSummary()).equals("")?0:Integer.parseInt(String.valueOf(hostAddressSetting.getSummary())); 
				settting[4] = String.valueOf(slaveAddressSetting.getSummary()).equals("")?0:Integer.parseInt(String.valueOf(slaveAddressSetting.getSummary()));
				Log.e("485setting_Sending", settting[0]+":"+settting[1]+":"+settting[2]+":"+settting[3]+":"+settting[4]+":");			
				HandleFile.record(getActivity(), SHAREPREFERENCE_FILENAME, HOST_ADDRESS_KEY,  settting[3]);
				HandleFile.record(getActivity(), SHAREPREFERENCE_FILENAME, SLAVE_ADDRESS_KEY, settting[4]);
				//LibDegree.send485Settings(settting, 5);   
			}
		}.start();
	}
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
	{              
		 Log.e("onKey", v.toString());
		 Log.e("onKey", keyCode+"");		 
		 return false;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		Log.e("comuniacation!", "dismiss!");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
	 	
		if(preference.getKey().equals(BAUDRRATE_KEY))
		{
			Log.e("onPreferenceChange", "BAUDRRATE_KEY");
			baudrRateList.setSummary(newValue.toString());
			HandleFile.record(HomeActivity.getInstance(), SHAREPREFERENCE_FILENAME, "Baud_rate", newValue.toString() );
		}
/*		else if(preference.getKey().equals("stopBit")) 
		{
			stopbitList.setSummary(newValue.toString());
			HandleFile.record(HomeActivity.getInstance(), SHAREPREFERENCE_FILENAME, "stopBit", newValue.toString() );
		}*/
/*		else if(preference.getKey().equals(HOST_ADDRESS_KEY))
		{
			Log.e("onPreferenceChange", "HOST_ADDRESS_KEY");
			hostAddressSetting.setSummary(newValue.toString());
			HandleFile.record(HomeActivity.getInstance(), SHAREPREFERENCE_FILENAME, HOST_ADDRESS_KEY, newValue.toString() );
		}
		else if(preference.getKey().equals(SLAVE_ADDRESS_KEY))
		{
			Log.e("onPreferenceChange", "SLAVE_ADDRESS_KEY");
			slaveAddressSetting.setSummary(newValue.toString());
			HandleFile.record(HomeActivity.getInstance(), SHAREPREFERENCE_FILENAME, SLAVE_ADDRESS_KEY, newValue.toString() );
		}*/
		return false; //Baud_rate stopBit addressSetting
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
		
	@Override
	public void afterTextChanged(Editable s)
	{
			if(focus==0)
			{
				 hostAddressSetting.setSummary(s.toString());
				Log.e("summary",hostAddressSetting.getSummary().toString());	
			}
			else 
			{
				slaveAddressSetting.setSummary(s.toString());
				Log.e("summary",slaveAddressSetting.getSummary().toString());	
			}
	}
	
 	public static int[] getCommunicateSettings(Context context)
	{
		int[] settings = new int[5];
		settings[0] = Integer.parseInt(HandleFile.getRecord(context, SHAREPREFERENCE_FILENAME, BAUDRRATE_KEY, "9600" )); 
		settings[1] = Integer.parseInt(HandleFile.getRecord(context, SHAREPREFERENCE_FILENAME, DATABIT_KEY, "8"));
		settings[2] = Integer.parseInt(HandleFile.getRecord(context, SHAREPREFERENCE_FILENAME, STOPBIT_KEY, "1")); 
		settings[3] =  HandleFile.getRecord(context, SHAREPREFERENCE_FILENAME, HOST_ADDRESS_KEY, 0) ;
		settings[4] =  HandleFile.getRecord(context, SHAREPREFERENCE_FILENAME, SLAVE_ADDRESS_KEY, 1) ;
		Log.e("settings",settings[0]+":"+settings[1]+":"+settings[2]+":"+settings[3]+":"+settings[4]);
		return settings;
	} 
}
