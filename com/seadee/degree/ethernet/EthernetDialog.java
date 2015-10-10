/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seadee.degree.ethernet;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.net.ethernet.EthernetDevInfo;
import com.seadee.degree.R;
import com.seadee.degree.control.KeyBoardDialog;
import com.seadee.degree.control.SDEditText;
import com.seadee.degree.control.KeyBoardDialog.KeyboardHandleListener;
import com.seadee.degree.control.SDEditText.INPUT_MODE;


public class EthernetDialog extends AlertDialog implements TextWatcher,
        View.OnClickListener, AdapterView.OnItemSelectedListener,KeyboardHandleListener {

    public static final String TAG = "EthernetDialog";
    private final DialogInterface.OnClickListener mListener;
	private EthernetDevInfo mInterfaceInfo;

    private boolean mEditing;

    private View mView;

    private CheckBox mDhcp_choice;
    private SDEditText mIpaddr;
    private SDEditText mMask;
    private SDEditText mDns;
    private SDEditText mGw;
    private SDEditText mMacaddr;
    private boolean inputEnabled;
    private KeyBoardDialog keyBoardDialog;
    private Context context;
    public EthernetDialog(Context context, DialogInterface.OnClickListener listener, EthernetDevInfo interfaceinfo, boolean editing) {
        super(context);
        this.context = context;
        mListener = listener;
        mEditing = editing;
		mInterfaceInfo = interfaceinfo;
		
		if(mInterfaceInfo != null){
		}
    }
    
	    public EditText getmIpaddr(){
	    	return mIpaddr;
	    }
	    
	   public EditText getmDns(){
	    	return mDns;
	    }
	  
	   public EditText getmGw(){
	 	  return mGw;
	  }
	   public EditText getmMask(){
		  return mMask;
	  }
    
    @Override
    protected void onCreate(Bundle savedState) {
		mView = getLayoutInflater().inflate(R.layout.ethernet_dialog, null);
		setTitle(R.string.eth_advanced_configure);
		setView(mView);
		setInverseBackgroundForced(true);
		Context context = getContext();

		// First, find out all the fields.
		mDhcp_choice = (CheckBox) mView.findViewById(R.id.dhcp_choice);
		mIpaddr = (SDEditText) mView.findViewById(R.id.ipaddr_edit);
		mMask = (SDEditText) mView.findViewById(R.id.netmask_edit);
		mGw = (SDEditText) mView.findViewById(R.id.gw_edit);
		mDns = (SDEditText) mView.findViewById(R.id.dns_edit);
		mMacaddr= (SDEditText) mView.findViewById(R.id.macaddr_edit);
		//set input mode 
		mIpaddr.setInputMode(INPUT_MODE.IP_MODE);
		mMask.setInputMode(INPUT_MODE.IP_MODE); 
		mGw.setInputMode(INPUT_MODE.IP_MODE); 
		mDns.setInputMode(INPUT_MODE.IP_MODE); 
		mMacaddr.setInputMode(INPUT_MODE.IP_MODE);
		
		mIpaddr.setOnTouchListener(listener);
		mMask.setOnTouchListener(listener); 
		mGw.setOnTouchListener(listener); 
		mDns.setOnTouchListener(listener); 
		mMacaddr.setOnTouchListener(listener);
		// Second, copy values from the profile.
		String IpAddress;
		if(!(IpAddress=mInterfaceInfo.getIpAddress()).equals("0.0.0.0"))
				mIpaddr.setText(IpAddress);
		mMask.setText(mInterfaceInfo.getNetMask());
		mGw.setText(mInterfaceInfo.getGateWay());
		mDns.setText(mInterfaceInfo.getDnsAddr());
		mMacaddr.setText(mInterfaceInfo.getHwaddr().toUpperCase());
		if(mInterfaceInfo.getConnectMode() == EthernetDevInfo.ETHERNET_CONN_MODE_DHCP){
			mDhcp_choice.setChecked(true);
			mIpaddr.setEnabled(false);
			mMask.setEnabled(false);
			mGw.setEnabled(false);
			mDns.setEnabled(false);
		}else{
			mDhcp_choice.setChecked(false);
					
		}
		mMacaddr.setEnabled(false);

		// Third, add listeners to required fields.
		mDhcp_choice.setOnClickListener(this);
		mIpaddr.addTextChangedListener(this);
		mMask.addTextChangedListener(this);
		mGw.addTextChangedListener(this);
		mDns.addTextChangedListener(this);

 		setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.eth_cancel), mListener);
		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.eth_ok), mListener);
		setButton(DialogInterface.BUTTON_NEUTRAL, context.getString(R.string.eth_advand), mListener);
		// Workaround to resize the dialog for the input method.
		super.onCreate(savedState);

 		if(mEditing){
			mView.findViewById(R.id.eth_conf_editor).setVisibility(View.VISIBLE);
			getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.GONE);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
					WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}else{
			mView.findViewById(R.id.eth_message_dialog).setVisibility(View.VISIBLE);
			getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
		}  
    }

    @Override
    public void afterTextChanged(Editable field) {
        //getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(validate(mEditing));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) 
    {
    	
    }
 
    private View.OnTouchListener listener = new View.OnTouchListener() 
    {		
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				 
				if(inputEnabled)
				{
					switch (v.getId()) {			
					case R.id.ipaddr_edit:					
						keyBoardDialog = new KeyBoardDialog(context, true,mIpaddr);					
						break;
					case R.id.netmask_edit:
						keyBoardDialog = new KeyBoardDialog(context, true,mMask);
						break;
					case R.id.gw_edit:
						keyBoardDialog = new KeyBoardDialog(context, true,mGw);
						break;
					case R.id.dns_edit:
						keyBoardDialog = new KeyBoardDialog(context, true,mDns);
						break;
					case R.id.macaddr_edit:
						keyBoardDialog = new KeyBoardDialog(context, true,mMacaddr);
						break;
					default:
						break;
					}
					keyBoardDialog.setKeyboardEventListener(EthernetDialog.this);
					keyBoardDialog.setPointEnabel(true);// allow point(.) inputed				
					keyBoardDialog.setPos(400, 200);
					keyBoardDialog.show();
				}
			}
			return false;
		}
	};
    
    @Override
    public void onClick(View v) {

		if(mDhcp_choice.isChecked()){
			mIpaddr.setEnabled(false);
			mMask.setEnabled(false);
			mDns.setEnabled(false);
			mGw.setEnabled(false);
			inputEnabled =false ;
		}else{
			mIpaddr.setEnabled(true);
			mMask.setEnabled(true);
			mDns.setEnabled(true);
			mGw.setEnabled(true);
			mDhcp_choice.setChecked(false);	
			inputEnabled = true ;
		}
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    boolean isEditing() {
        return mEditing;
    }
 
    public EthernetDevInfo getDevInfo() {
        // First, save common fields.
		if(mEditing){
			if(!mDhcp_choice.isChecked()){
				mInterfaceInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
				mInterfaceInfo.setIpAddress(mIpaddr.getText().toString());
				mInterfaceInfo.setNetMask(mMask.getText().toString());
				mInterfaceInfo.setDnsAddr(mDns.getText().toString());
				mInterfaceInfo.setGateWay(mGw.getText().toString());
			}else{
				mInterfaceInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
			}
		}

        return mInterfaceInfo;
    }
    public EthernetDevInfo setIPInfo(){
    	mInterfaceInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
		mInterfaceInfo.setIpAddress(mIpaddr.getText().toString());
		mInterfaceInfo.setNetMask(mMask.getText().toString());
		mInterfaceInfo.setDnsAddr(mDns.getText().toString());
		mInterfaceInfo.setGateWay(mGw.getText().toString());
		return mInterfaceInfo;
    }

	@Override
	public Handler getKeyboardEventHandler() {
		// TODO Auto-generated method stub
		return  handler;
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(keyBoardDialog != null)
			{
			
				keyBoardDialog = null ;
			}
			super.handleMessage(msg);
		}
		
	};
	
}
