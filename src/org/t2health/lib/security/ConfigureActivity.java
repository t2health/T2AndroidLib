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
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;


public class ConfigureActivity extends BaseNavigationActivity implements OnCheckedChangeListener, OnKeyListener {
	private CheckBox mEnabledCheckbox;
	private ViewGroup mInputsContainer;
	private EditText mPasswordEditText;
	private EditText mQuestion1EditText;
	private EditText mAnswer1EditText;
	private EditText mQuestion2EditText;
	private EditText mAnswer2EditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.security_configure_activity);
		this.setTitle(R.string.security_configure_title);
		
		// get the form elements and assign their events.
		mInputsContainer = (ViewGroup)this.findViewById(R.id.inputsContainer);
		
		mPasswordEditText = (EditText)this.findViewById(R.id.passwordEditText);
		mPasswordEditText.setOnKeyListener(this);
		
		mQuestion1EditText = (EditText)this.findViewById(R.id.question1);
		mQuestion1EditText.setOnKeyListener(this);
		
		mAnswer1EditText = (EditText)this.findViewById(R.id.answer1);
		mAnswer1EditText.setOnKeyListener(this);
		
		mQuestion2EditText = (EditText)this.findViewById(R.id.question2);
		mQuestion2EditText.setOnKeyListener(this);
		
		mAnswer2EditText = (EditText)this.findViewById(R.id.answer2);
		mAnswer2EditText.setOnKeyListener(this);
		
		mEnabledCheckbox = (CheckBox)this.findViewById(R.id.securityEnabled);
		mEnabledCheckbox.setOnCheckedChangeListener(this);
		mEnabledCheckbox.setChecked(SharedPref.Security.isEnabled(this));

		// Indicate that these fields have data behind them via hint text.
		if(SharedPref.Security.isPasswordSet(this)) {
			mPasswordEditText.setHint(R.string.security_password_already_set_hint);
		}
		if(SharedPref.Security.isAnswer1Set(this)) {
			mAnswer1EditText.setHint(R.string.security_answer_already_set_hint);
		}
		if(SharedPref.Security.isAnswer2Set(this)) {
			mAnswer2EditText.setHint(R.string.security_answer_already_set_hint);
		}
		
		// set the values of the question edit text fields.
		mQuestion1EditText.setText(SharedPref.Security.getQuestion1(this));
		mQuestion2EditText.setText(SharedPref.Security.getQuestion2(this));
		
		setFieldsEnabled(mEnabledCheckbox.isChecked());
		setRightNavigationButtonEnabled(validSecurityDataEntererd());
		setRightNavigationButtonText(R.string.save);
		setRightNavigationButtonVisibility(View.VISIBLE);
		setLeftNavigationButtonText(R.string.cancel);
		
		// Hide the keyboard unless the user chooses a text view.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	protected void onRightNavigationButtonPressed() {
		if(!validSecurityDataEntererd()) {
			Toast.makeText(this, R.string.security_bad_input_message, Toast.LENGTH_LONG).show();
			return;
		}
		
		boolean enabled = mEnabledCheckbox.isChecked();
		String password = mPasswordEditText.getText().toString().trim();
		String q1 = mQuestion1EditText.getText().toString().trim();
		String a1 = mAnswer1EditText.getText().toString().trim();
		String q2 = mQuestion2EditText.getText().toString().trim();
		String a2 = mAnswer2EditText.getText().toString().trim();
		
		SharedPref.Security.setEnabled(this, enabled);
		if(enabled) {
			SharedPref.Security.setPassword(this, password);
			SharedPref.Security.setChallenge1(
					this, 
					q1, 
					a1
			);
			SharedPref.Security.setChallenge2(
					this, 
					q2, 
					a2
			);
		}
		
		AppSecurityManager.setIsUnlocked(true);
		
		this.setResult(RESULT_OK);
		this.finish();
	}
	
	private boolean validSecurityDataEntererd() {
		String password = mPasswordEditText.getText().toString().trim();
		String q1 = mQuestion1EditText.getText().toString().trim();
		String a1 = mAnswer1EditText.getText().toString().trim();
		String q2 = mQuestion2EditText.getText().toString().trim();
		String a2 = mAnswer2EditText.getText().toString().trim();
		
		return !mEnabledCheckbox.isChecked() || 
			(
				(password.length() > 0 || SharedPref.Security.isPasswordSet(this)) && 
				q1.length() > 0 &&
				(a1.length() > 0 || SharedPref.Security.isAnswer1Set(this)) &&
				q2.length() > 0 &&
				(a2.length() > 0 || SharedPref.Security.isAnswer2Set(this))
			);
	}
	
	private void setFieldsEnabled(boolean b) {
		mInputsContainer.setEnabled(b);
		for(int i = 0; i < mInputsContainer.getChildCount(); ++i) {
			mInputsContainer.getChildAt(i).setEnabled(b);
		}
		
		if(b) {
			mPasswordEditText.requestFocus();
		} else {
			mEnabledCheckbox.requestFocus();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setFieldsEnabled(isChecked);
		setRightNavigationButtonEnabled(validSecurityDataEntererd());
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		this.setRightNavigationButtonEnabled(validSecurityDataEntererd());
		return false;
	}
}
