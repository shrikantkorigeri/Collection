package com.san.collection;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

public class CustomDialog extends Dialog
{
	
	public CustomDialog(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
			);
		//setContentView(R.layout.passwordsetting);
		
	}
	
}

