package com.seadee.degree.control;

import java.io.File;
import java.lang.reflect.Field;

import com.seadee.library.R;
import com.seadee.library.common.SDParsePadding;
import com.seadee.library.utils.Log;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class SDEditText extends EditText {
	
	/*
	 * virtualink:hint string
	 */
	public enum INPUT_MODE{PASSWORD_MODE,NORMAL_MODE,IP_MODE};
	int paddingleft = 5;
	int paddingright = 5;
	int paddingtop = 5;
	int paddingbottom = 5;
	
	String hint = "";
	private int mMaxValue;
	private int mMaxLength   ;
	private boolean isPassword;
	private boolean isIP;
	public boolean isCheackValue; //是否检测输入的值
	private INPUT_MODE curInputMode;
	
	public SDEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context,null);
	}
	
	public SDEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context,attrs);
	}

	public SDEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context,attrs);
	}
	
	private void init(Context context,AttributeSet attrs)
	{
		setBackgroundResource(R.drawable.sd_icon_edit_background);
		if(attrs != null)
		{
			int padding = SDParsePadding.getPadding(context, attrs, 5);
			paddingleft = SDParsePadding.getPaddingLeft(context, attrs, 0);
			paddingtop = SDParsePadding.getPaddingTop(context, attrs, 0);
			paddingright = SDParsePadding.getPaddingRight(context, attrs, 0);
			paddingbottom = SDParsePadding.getPaddingBottom(context, attrs, 0);
			
			if(paddingleft == 0)
				paddingleft = padding;
			
			if(paddingtop == 0)
				paddingtop = padding;

			if(paddingright == 0)
				paddingright = padding;
			
			if(paddingbottom == 0)
				paddingbottom = padding;
			
			 TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.virtualink);
			 hint = type.getString(R.styleable.virtualink_hint);
			 type.recycle();
		}
		
		this.setPadding(paddingleft, paddingtop, paddingright, paddingbottom);	
		isPassword= isPasswordType();	
		if(isPassword)
		{
			//mMaxValue = 0x0FFFFFFF;		
			curInputMode = INPUT_MODE.PASSWORD_MODE;
		}
		else if(isIP)
		{
			curInputMode = INPUT_MODE.IP_MODE;
		}
		else 
		{
			curInputMode = INPUT_MODE.NORMAL_MODE;
		}

		if(curInputMode == INPUT_MODE.PASSWORD_MODE)
		 {
				isCheackValue = false;
				 
		 }
		 else if(curInputMode == INPUT_MODE.IP_MODE)	
		 {
				setMaxLength(15);
				isCheackValue = false;
		 }
		 else 
		 {
			 	curInputMode = INPUT_MODE.NORMAL_MODE;
				setMaxLength(100);
				isCheackValue = true ; // normal need to check the value
				setMaxValue(0x0FFFFFFF);				
		 }
	}
	public void setInputMode(INPUT_MODE mode)
	{
		this.curInputMode = mode;	
		if(curInputMode == INPUT_MODE.PASSWORD_MODE)
		 {
				isCheackValue = false;
				isPassword = true ;
				 
		 }
		 else if(curInputMode == INPUT_MODE.IP_MODE)	
		 {
				setMaxLength(15);
				isCheackValue = false;
		 }
	}

	/**
	 * judge wether the editTextView's inputType is password or normal  
	 * 
	 **/	
	public  boolean isPasswordType()
	{
		int inputType = this.getInputType();
        final int variation =
                inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        return variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
	}
	@Override
	public boolean onHoverEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction())
		{
		case MotionEvent.ACTION_HOVER_ENTER:
			this.setPadding(paddingleft, paddingtop, paddingright, paddingbottom);	
			if(hintTv != null && hint != null && !hint.isEmpty())
			{
				hintTv.setText(hint);
			}
			break;
		case MotionEvent.ACTION_HOVER_EXIT:
			this.setPadding(paddingleft, paddingtop, paddingright, paddingbottom);	
			if(hintTv != null && hint != null && !hint.isEmpty())
			{
				hintTv.setText("");
			}
			break;
		}
		return super.onHoverEvent(event);
	}
	public void backDelete()
	{
		Editable editable = getText();	
	 	int index = getCurrentLength();
		if(getCurrentLength()>0)
		{		
			editable.delete(index-1, index);
		} 
/*		String str = getText().toString(); 
		int size = str.length();
		if(getCurrentLength() > 0)
		{
			str = str.substring(0, size-1) ;
			setText(str) ; 
			setSelection(str.length());	//光标的位置			
		}	*/
	}
	
	public int getCurrentLength()
	{
		String str = getText().toString();	
		return  str.length() ;			 	
	}
	
	public boolean appendText(CharSequence text)
	{
		if(curInputMode == INPUT_MODE.NORMAL_MODE)
		{
			if(getText().toString().equals("0"))
			{
				backDelete();
			}
		}	
		if(getCurrentLength()<getMaxLength())
		{
			append(text);
			return true ;
		}
		return false;
	}
	public void  setMaxValue(int value)
	{
		if(!isPassword)
		{
			mMaxValue = value ;
		}	
	}
	public int getMaxValue()
	{
		return mMaxValue;
	}
	public void  setMaxLength(int len)
	{
		setFilters(new InputFilter[]{new InputFilter.LengthFilter(len)});
		mMaxLength = len;
	}
	public void clear()
	{
		setText("");
	}
	public int getMaxLength()
	{
		int length =0;
		try 
		{
			InputFilter[] inputFilters = getFilters();
			for(InputFilter filter:inputFilters)
			{
				Class<?> c = filter.getClass();
				if(c.getName().equals("android.text.InputFilter$LengthFilter"))
				{
					Field[] f = c.getDeclaredFields();
					for(Field field:f)
					{
						if(field.getName().equals("mMax"))
						{
							field.setAccessible(true);
							length = (Integer)field.get(filter);
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		mMaxLength = length;
		return length;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			this.setPadding(paddingleft, paddingtop, paddingright, paddingbottom);	
			if(hintTv != null && hint != null && !hint.isEmpty())
			{
				hintTv.setText(hint);
			}
			break;
		case MotionEvent.ACTION_UP:
			this.setPadding(paddingleft, paddingtop, paddingright, paddingbottom);		
			if(hintTv != null && hint != null && !hint.isEmpty())
			{
				hintTv.setText("");
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public void setPasswdVisible(boolean isVisible)
    {
        if(isVisible)
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
           setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
    
    public void oppositePwdVisible()
    {
        setPasswdVisible(getTransformationMethod()==PasswordTransformationMethod.getInstance());
    }

    @Override
	public void setPadding(int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		this.paddingleft = left;
		this.paddingright = right;
		this.paddingtop = top;
		this.paddingbottom =  bottom;
		super.setPadding(left, top, right, bottom);
	}
    
    TextView hintTv;
	public void setHintTextView(TextView tv)
	{
		this.hintTv = tv;
	}
	
	public void setHint(String hint)
	{
		this.hint = hint;
	}

}
