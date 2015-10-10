package com.seadee.degree.comunication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.control.KeyBoardDialog;
import com.seadee.degree.control.KeyBoardDialog.KeyboardHandleListener;
import com.seadee.degree.control.SDEditText;

public class AddressDialog extends AlertDialog implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener ,KeyboardHandleListener
{
	private SDEditText addressEditText;
//	private Button cancelButton,comfirmButton;
	private  Context context;
	KeyBoardDialog keyBoardDialog ;
	private int mAddressValue;
    private View mView;
    private final DialogInterface.OnClickListener mListener;
	public AddressDialog(Context context,DialogInterface.OnClickListener listener) 
	{
		super(context);
		this.context =  context;
		mListener = listener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//setContentView(R.layout.address_input_layout);
		mView = getLayoutInflater().inflate(R.layout.address_input_layout, null);
		setView(mView);
		setInverseBackgroundForced(true);		
		setTitle(R.string.address_setting);	
		init();	

		setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), mListener);
		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.confirm), clickListener);
		setButton(DialogInterface.BUTTON_NEUTRAL, context.getString(R.string.eth_advand), mListener);
		
		super.onCreate(savedInstanceState);//必须放到这  不然没标题栏   擦！
		
	 //	mView.findViewById(R.id.address_editor).setVisibility(View.VISIBLE);
	 	getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.GONE);		
		keyBoardDialog =  new KeyBoardDialog(this.context,true,addressEditText);
		keyBoardDialog.setPos(380, 200);
		keyBoardDialog.setPointEnabel(false); //point(.) can't be inputed  
		keyBoardDialog.setKeyboardEventListener(this);			
	}
	
	private void init()
	{
		addressEditText = (SDEditText)mView.findViewById(R.id.addressText);
	//	cancelButton = (Button)mView.findViewById(R.id.addressInput_cancel);
		//comfirmButton = (Button)mView.findViewById(R.id.addressInput_comfirm);
		
		addressEditText.setMaxLength(3);// the max length of the input address 
		addressEditText.setMaxValue(255); // input address required to be less than 255  greater than 0
		
		addressEditText.setOnClickListener(listener);
		//cancelButton.setOnClickListener(listener);
	//	comfirmButton.setOnClickListener(listener);
		
 
	}
	
	private OnClickListener clickListener = new OnClickListener() 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			if(which == DialogInterface.BUTTON_POSITIVE)
			{
				String addstr = addressEditText.getText().toString();
				if(!addstr.equals(""))
				{
					int add = Integer.parseInt(addstr); 
					if(add>addressEditText.getMaxValue())
					{
						Toast.makeText(context,context.getResources().getString(R.string.no_more_than)+addressEditText.getMaxValue()+"!", Toast.LENGTH_SHORT).show();
						addressEditText.setText("");
					}
					else 
					{
						dismiss();
					}
				}
			}
		}
	};
	
	public int getAddreess()
	{
		return mAddressValue;
	}
	public SDEditText getAddressEditText()
	{
		return addressEditText;
	}
	
	private View.OnClickListener listener = new  View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addressText:
				keyBoardDialog.show();
				break;
		/*	case R.id.addressInput_cancel:
				dismiss();
				break;
			case R.id.addressInput_comfirm:				
				dismiss();
				break;*/
			default:
				break;
			}			
		}
	};
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		Log.e("addressDlg","beforeTextChanged" );
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		Log.e("addressDlg","onTextChanged" );
	}
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		Log.e("addressDlg","afterTextChanged" );
	}
	@Override
	public Handler getKeyboardEventHandler() {
		// TODO Auto-generated method stub
		return keyboardHandler;
	}
	private Handler keyboardHandler = new Handler() 
	{
		@Override 
		public void handleMessage(Message msg)
		{
			Log.e("address",  "reiceive!");
			Bundle bundle = msg.getData();
			if(bundle.containsKey(String.valueOf(addressEditText.getId())))
			{
				mAddressValue = bundle.getInt(String.valueOf(addressEditText.getId()));
				Log.e("address",  mAddressValue+"");
			}
			super.handleMessage(msg);
		}
	};

}
