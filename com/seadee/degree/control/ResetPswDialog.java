package com.seadee.degree.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.control.KeyBoardDialog.KeyboardHandleListener;
import com.seadee.degree.control.SDEditText.INPUT_MODE;
import com.seadee.degree.service.HandleFile;
import com.seadee.library.utils.FloatDialog;

public class ResetPswDialog extends FloatDialog implements KeyboardHandleListener {


	private SDEditText[] pswText = new  SDEditText[3];
	private Button confirmBtn,cancelBtn;
	private int oldPsw,newPsw,confirmNewPsw;
	private Context context ;
	private KeyBoardDialog[] keyBoardDialog = new KeyBoardDialog[3] ;
	public ResetPswDialog(Context context, boolean ischoke) {
		super(context, ischoke);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.resetpsw_layout);
		this.context = context;
		init();
	}

	private void init()
	{
		 pswText[0] = (SDEditText)findViewById(R.id.originPsw);
		 pswText[0].requestFocus();
		 pswText[1] = (SDEditText)findViewById(R.id.newPsw);
		 pswText[2] = (SDEditText)findViewById(R.id.confirmPsw);
		 pswText[0].getMaxLength();
		 for(int i=0;i<3;i++)
		 {
			 pswText[i].setOnTouchListener(touchListener);
			 pswText[i].setInputMode(INPUT_MODE.PASSWORD_MODE);
		 }
		 confirmBtn = (Button)findViewById(R.id.comfirmbtn);
		 cancelBtn = (Button)findViewById(R.id.cancelBtn);
		 confirmBtn.setOnClickListener(listener);
		 cancelBtn.setOnClickListener(listener);
		 for(int i=0;i<3;i++ )
		 {
			 keyBoardDialog[i] = new KeyBoardDialog(context, false,pswText[i]);
			 keyBoardDialog[i].setKeyboardEventListener(this);
			 keyBoardDialog[i].setPos(380, 200);
		 }
	}
	private void  clearPswText()
	{
		for(int i=0;i<3;i++)
		{
			pswText[i].setText("");
		}
	}
	private int focus = 0;
	private View.OnTouchListener touchListener = new View.OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent event) 
		{
			switch(event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					
					switch (v.getId()) {
					case R.id.originPsw:
						focus = 0;
					 break;
					case R.id.newPsw:
						focus = 1;
						break;
					case R.id.confirmPsw: 
						focus = 2;
						break;
					default:
						break;
					}
					//显示对应的keyboard
					for(int i=0;i<3;i++)
					{
						if(i!=focus)
						{
							keyBoardDialog[i].dismiss();
						}
						else 
						{
							keyBoardDialog[focus].show();
						}
					}			
			}
			return false;
		}
	};
	private View.OnClickListener listener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.comfirmbtn:	
				if(pswText[0].getText().toString().equals("")||pswText[1].getText().toString().equals("")||pswText[2].getText().toString().equals(""))
				{
					Toast.makeText(context,R.string.NoEmtyInput, Toast.LENGTH_SHORT).show();
					clearPswText();
				}
				else 
				{
					if((pswText[1].getText().toString().length()<4)||(pswText[2].getText().toString().length()<4)||(pswText[0].getText().toString().length()<4))
					{
						Log.e("password_change","less4");
						Toast.makeText(context,"屏幕解锁密码需4位！", Toast.LENGTH_SHORT).show();
						clearPswText();
					}
					else 
					{
						oldPsw = Integer.parseInt(pswText[0].getText().toString()); 
						newPsw = Integer.parseInt(pswText[1].getText().toString());
						confirmNewPsw = Integer.parseInt(pswText[2].getText().toString());
						Log.e("correctPsw",HandleFile.getRecord(context, "SD_config", "password", 1234)+"");
						Log.e("old",oldPsw+"");
						Log.e("newpsw",newPsw+"");
						Log.e("confirm",confirmNewPsw+"");
						if(oldPsw != HandleFile.getRecord(context, "SD_config","password",1234))
						{
							Toast.makeText(context, R.string.originPswWrong , Toast.LENGTH_SHORT).show();
							Log.e("originPswWrong",HandleFile.getRecord(context, "SD_config","password",1234)+"");
							clearPswText();
						}
						else if(newPsw != confirmNewPsw) 
						{
							Toast.makeText(context,R.string.confirmPswWrong , Toast.LENGTH_SHORT).show();
							clearPswText();
						}
						else if(oldPsw == newPsw)
						{
							Toast.makeText(context, R.string.newPswWrong, Toast.LENGTH_SHORT).show();
							clearPswText();
						}
						else  
						{
							HandleFile.record(context, "SD_config","password", newPsw);
							Toast.makeText(context, R.string.pswRstSuccess, Toast.LENGTH_SHORT).show();
							ResetPswDialog.this.dismiss();
						//	dismiss();
						}	
					}
				}
			
				break;
			case R.id.cancelBtn:
				ResetPswDialog.this.dismiss();

				break;
			default:
				break;
			}				
			for(int i=0;i<3;i++ )
			{
				keyBoardDialog[i].dismiss();
			}
		}
	};
	@Override
	public Handler getKeyboardEventHandler() 
	{
		return keyboardEventhandler;
	} 
	private Handler keyboardEventhandler = new Handler()
	{
		@Override 
		public void  handleMessage(Message msg )
		{
			
		}
	};
}
