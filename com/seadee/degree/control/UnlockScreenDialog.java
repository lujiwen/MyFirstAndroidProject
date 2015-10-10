package com.seadee.degree.control;

import com.seadee.library.control.SDEditText;
import com.seadee.library.utils.FloatDialog;

import com.seadee.degree.R;
import com.seadee.degree.R.id;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.PowerManager;

import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class UnlockScreenDialog extends FloatDialog {
	private EditText[]  pswText = new EditText[4] ;
	private Button[]  btn = new Button[10];
	private Button confirmButton ,cancelButton; 
	private Context context ;
	private int password ;
	private TextView resetPswTv,forgetPswTv ;
	public UnlockScreenDialog(Context context, boolean ischoke) {
		super(context, ischoke);
		setContentView(R.layout.unlockscreen_layout);
		this.context  = context ;
		init();
		password =  HandleFile.getRecord(context, "SD_config","password", 1234);
	}
	
	private  int focus = 0; 
	private void init()
	{
		pswText[0] = (EditText)findViewById(R.id.psw1);
		pswText[1] = (EditText)findViewById(R.id.psw2);
		pswText[2] = (EditText)findViewById(R.id.psw3);
		pswText[3] = (EditText)findViewById(R.id.psw4);
		
		btn[0] = (Button)findViewById(R.id.btn0);
		btn[1] = (Button)findViewById(R.id.btn1);
		btn[2] = (Button)findViewById(R.id.btn2);
		btn[3] = (Button)findViewById(R.id.btn3);
		btn[4] = (Button)findViewById(R.id.btn4);
		btn[5] = (Button)findViewById(R.id.btn5);
		btn[6] = (Button)findViewById(R.id.btn6);
		btn[7] = (Button)findViewById(R.id.btn7);
		btn[8] = (Button)findViewById(R.id.btn8);
		btn[9] = (Button)findViewById(R.id.btn9);
	
		confirmButton = (Button)findViewById(R.id.lock_comfirm);		
		cancelButton = (Button)findViewById(R.id.lock_cancel);
		confirmButton.setOnClickListener(clickListener);
		cancelButton.setOnClickListener(clickListener);		
		for(int i=0;i<10;i++ )
		{
			btn[i].setOnClickListener(clickListener);
		}		
		resetPswTv = (TextView)findViewById(R.id.resetPsw);
		resetPswTv.setOnClickListener(clickListener);
		forgetPswTv = (TextView)findViewById(R.id.forgetPsw);
		forgetPswTv.setOnClickListener(clickListener);
	}
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(focus<4)
			{
				switch(v.getId())
				{
					case R.id.btn0:
						pswText[focus].setText(String.valueOf(0));
						checkUnlock();
						break ;
					case R.id.btn1:
						pswText[focus].setText(String.valueOf(1));
						checkUnlock();
						break ;
					case R.id.btn2:
						pswText[focus].setText(String.valueOf(2));
						checkUnlock();
						break ;
					case R.id.btn3:
						pswText[focus].setText(String.valueOf(3));
						checkUnlock();
						break ;
					case R.id.btn4:
						pswText[focus].setText(String.valueOf(4));
						checkUnlock();
						break ;
					case R.id.btn5:
						pswText[focus].setText(String.valueOf(5));
						checkUnlock();
						break ;
					case R.id.btn6:
						pswText[focus].setText(String.valueOf(6));
						checkUnlock();
						break ;
					case R.id.btn7:
						pswText[focus].setText(String.valueOf(7));
						checkUnlock();
						break ;
					case R.id.btn8:
						pswText[focus].setText(String.valueOf(8));
						checkUnlock();
						break ;
					case R.id.btn9:
						pswText[focus].setText(String.valueOf(9));
						checkUnlock();
						break ;
					case R.id.resetPsw:
						ResetPswDialog resetPswDialog = new ResetPswDialog(context, false);
						resetPswDialog.show();
						UnlockScreenDialog.this.dismiss();						 
						break;
					case R.id.forgetPsw:
						showFactorysetting();
						break;						
					case R.id.lock_comfirm:
						//UnlockScreenDialog.this.dismiss();
						if(checkPassword())
						{
							//½âËø
							DegreeView.getInstance().lockScreen(false);
							UnlockScreenDialog.this.dismiss();
						}
						else 
						{
							Toast.makeText(context,context.getResources().getString(R.string.pswWrong), Toast.LENGTH_SHORT).show();
						}
						break; 
					case R.id.lock_cancel:
						UnlockScreenDialog.this.dismiss();
						break ;
					 default:
						break;
				}
				
				
			}
		}
	};
	private void checkUnlock()
	{
		if(focus<3)
		{
			pswText[++focus].requestFocus();
		}
		else 
		{					
			if(checkPassword())
			{
				UnlockScreenDialog.this.dismiss();
				//½âËø
				DegreeView.getInstance().lockScreen(false);
			}
			else 
			{
				focus = 0;
				pswText[focus].requestFocus();
				for(int i=0;i<4;i++)
				{
					pswText[i].setText("");
					Toast.makeText(context, context.getResources().getString(R.string.pswWrong) , 500).show();							
				}
			}					
		}//end if 
	}
	
	private void showFactorysetting()
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle(context.getResources().getString(R.string.restorefactorySetting)  );
		dlg.setMessage( context.getResources().getString(R.string.restorePromote));
		dlg.setPositiveButton( R.string.confirm ,new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
				context.sendBroadcast(intent);
				Log.e("broadcast","send");
			}
		});
		dlg.setNegativeButton(R.string.cancel, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		dlg.show();		
	}
	
	private boolean checkPassword()
	{		
		int tempPsw = 0;		
		for(int i=0;i<4;i++)
		{
			if(!pswText[3-i].getText().toString().equals(""))
			{
				tempPsw += Integer.parseInt(pswText[3-i].getText().toString())*Math.pow(10, i) ;
			}
		}
		Log.e("psw", tempPsw+"");
		password = HandleFile.getRecord(context, "SD_config", "password", 1234);
		return (password==tempPsw) ;
	}
}
