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
