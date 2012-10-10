/*
 * 
 * T2AndroidLib
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.lib.security;

import org.t2health.lib.R;
import org.t2health.lib.SharedPref;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class ForgotPasswordActivity extends BaseNavigationActivity implements OnKeyListener, OnEditorActionListener, OnClickListener {
	private EditText mAnswer1EditText;
	private EditText mAnswer2EditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.security_forgot_password_activity);
		this.setTitle(R.string.security_forgot_password_title);
		
		((TextView)this.findViewById(R.id.question1)).setText(
				SharedPref.Security.getQuestion1(this)
		);
		((TextView)this.findViewById(R.id.question2)).setText(
				SharedPref.Security.getQuestion2(this)
		);
		
		mAnswer1EditText = (EditText)this.findViewById(R.id.answer1);
		mAnswer1EditText.setOnKeyListener(this);
		mAnswer1EditText.setOnEditorActionListener(this);
		
		mAnswer2EditText = (EditText)this.findViewById(R.id.answer2);
		mAnswer2EditText.setOnKeyListener(this);
		mAnswer1EditText.setOnEditorActionListener(this);
		
		this.findViewById(R.id.unlockButton).setOnClickListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		checkForMatchedAnswers();
		return false;
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		checkForMatchedAnswers();
		return false;
	}
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if(viewId == R.id.unlockButton) {
			checkForMatchedAnswers();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private void checkForMatchedAnswers() {
		boolean match1 = SharedPref.Security.doesAnswer1Match(
				this, 
				mAnswer1EditText.getText().toString()
		);
			
		boolean match2 = SharedPref.Security.doesAnswer2Match(
				this, 
				mAnswer2EditText.getText().toString()
		);
		
		if(match1 && match2) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
	
	@Override
	protected boolean isSecurityEnabled() {
		return false;
	}
}
