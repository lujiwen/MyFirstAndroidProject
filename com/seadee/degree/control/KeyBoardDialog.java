package com.seadee.degree.control;

import android.content.Context;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener ;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.view.DegreeTopView;
import com.seadee.library.utils.FloatDialog;

public class KeyBoardDialog extends  FloatDialog  {

	public  interface KeyboardHandleListener
	{
	   public abstract Handler getKeyboardEventHandler(); 
	}
	
	public void setKeyboardEventListener(KeyboardHandleListener h)
	{
		keyHandleListener = h;
	}
	public Handler getkeyBoardHanler()
	{
		return keyHandleListener.getKeyboardEventHandler();
	}
	
	private Context context ;
	private Button[] numBtn ; 
	private Button deleteBtn ,submitBtn ,pointBtn; 
	private String tag = "KeyBoardDialog";
	private boolean pointEnable;
	KeyboardHandleListener keyHandleListener ;
 
	private View topView;
	private SDEditText editText ;
	private int maxlength;
	public KeyBoardDialog(Context context,boolean isChoke)
	{
		super(context, isChoke);
		init(context);
	}
	public KeyBoardDialog(Context context,boolean isChoke,SDEditText editText)
	{
		super(context, isChoke);
		this.editText = editText;
		maxlength = editText.getMaxLength();
		Log.e("editTextLength",maxlength+"");
		init(context);
	}	
	public void setPointEnabel(boolean pointenable)
	{
		this.pointEnable = pointenable;
	}
	public boolean isPointEnable()
	{
		return pointEnable;
	}
	public KeyBoardDialog(Context context,boolean isChoke,View topView)
	{
		super(context, isChoke);
		this.topView = topView ;
		init(context);
	}
	private void init(Context context)
	{
		 setContentView(R.layout.keyboard_layout); 	
		 this.context = context ;
		 pointEnable = false ;
		 initBtn();
	}
	
	void  initBtn()
	{
		 numBtn = new Button[10] ;
		 for(int i=0;i<10;i++ )
		 {
			 numBtn[i] = new Button(context);
		 }
		 deleteBtn = new Button(context);
		 submitBtn = new Button(context);
		pointBtn = (Button)findViewById(R.id.point); 
		numBtn[0] = (Button)findViewById(R.id.num0);
		numBtn[1] = (Button)findViewById(R.id.num1);
		numBtn[2] = (Button)findViewById(R.id.num2);
		numBtn[3] = (Button)findViewById(R.id.num3);
		numBtn[4] = (Button)findViewById(R.id.num4);
		numBtn[5] = (Button)findViewById(R.id.num5);
		numBtn[6] = (Button)findViewById(R.id.num6);
		numBtn[7] = (Button)findViewById(R.id.num7);
		numBtn[8] = (Button)findViewById(R.id.num8);
		numBtn[9] = (Button)findViewById(R.id.num9);
		 for(int i=0;i<10;i++)
		 {
			 numBtn[i].setOnClickListener(onClickListener);
		 }
		 
		deleteBtn = (Button)findViewById(R.id.deleteBtn);
		submitBtn = (Button)findViewById(R.id.submitBtn) ;	
		deleteBtn.setOnClickListener(onClickListener);
		submitBtn.setOnClickListener(onClickListener);		
		pointBtn.setOnClickListener(onClickListener);
	}
 
	private View.OnClickListener onClickListener = new   View.OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == numBtn[0])
			{
				 editText.appendText("0");
			}
			else if(v == numBtn[1] )
			{
				 editText.appendText("1");
			}
			else if(v == numBtn[2])
			{
				 editText.appendText("2");
			}
			else if(v == numBtn[3])
			{
				 editText.appendText("3");
			}
			else if(v == numBtn[4])
			{
				 editText.appendText("4");
			}
			else if(v == numBtn[5])
			{
				 editText.appendText("5");
			}
			else if(v == numBtn[6])
			{
				 editText.appendText("6");
			}
			else if(v == numBtn[7])
			{
				 editText.appendText("7");
			}
			else if(v == numBtn[8])
			{
				 editText.appendText("8");
			}
			else if(v == numBtn[9])
			{
				 editText.appendText("9");
			}
			else if(v == pointBtn )
			{
 
				if(pointEnable)
				{
					editText.append(".");
				}
			}
			else if(v == deleteBtn)
			{
				editText.backDelete();
			}
			else if(v == submitBtn)
			{		
			    String str = editText.getText().toString();		 						
				//1 检查这个值是不是超过范围
				if(editText.isCheackValue) 
					{
						int value  = 0; 
						int max = editText.getMaxValue();
						if(!str.isEmpty())
						{
							value = Integer.parseInt(str);
						}								
						if(value <= max)
						{
							//2  发回调用editText  保存这个值  
							Bundle bundle = new Bundle();
							Message msg = new Message();
							bundle.putInt(String.valueOf(editText.getId()), value);
							msg.setData(bundle);
							keyHandleListener.getKeyboardEventHandler().sendMessage(msg);
							dismiss();	
						}			
						else 
						{
							 editText.clear();
							 Toast.makeText(context, context.getResources().getString(R.string.no_more_than) +max+"!" , Toast.LENGTH_SHORT).show();
						}
				}
				else 
				{
					dismiss();
				}
			}
				
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
}
