package org.t2health.lib.activity.security;

import org.t2health.lib.AppSecurityManager;
import org.t2health.lib.R;
import org.t2health.lib.SharedPref;
import org.t2health.lib.activity.BaseNavigationActivity;
import org.t2health.lib.activity.BaseSecurityActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	private CheckBox enabledCheckbox;
	private ViewGroup inputsContainer;
	private EditText passwordEditText;
	private EditText question1EditText;
	private EditText answer1EditText;
	private EditText question2EditText;
	private EditText answer2EditText;
	private boolean changesMade = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.security_configure_title);
		this.setContentView(R.layout.security_configure_activity);
		
		// get the form elements and assign their events.
		inputsContainer = (ViewGroup)this.findViewById(R.id.inputsContainer);
		
		passwordEditText = (EditText)this.findViewById(R.id.passwordEditText);
		passwordEditText.setOnKeyListener(this);
		
		question1EditText = (EditText)this.findViewById(R.id.question1);
		question1EditText.setOnKeyListener(this);
		
		answer1EditText = (EditText)this.findViewById(R.id.answer1);
		answer1EditText.setOnKeyListener(this);
		
		question2EditText = (EditText)this.findViewById(R.id.question2);
		question2EditText.setOnKeyListener(this);
		
		answer2EditText = (EditText)this.findViewById(R.id.answer2);
		answer2EditText.setOnKeyListener(this);
		
		enabledCheckbox = (CheckBox)this.findViewById(R.id.securityEnabled);
		enabledCheckbox.setOnCheckedChangeListener(this);
		enabledCheckbox.setChecked(SharedPref.Security.isEnabled(this));

		// Indicate that these fields have data behind them via hint text.
		if(SharedPref.Security.isPasswordSet(this)) {
			passwordEditText.setHint(R.string.security_password_already_set_hint);
		}
		if(SharedPref.Security.isAnswer1Set(this)) {
			answer1EditText.setHint(R.string.security_answer_already_set_hint);
		}
		if(SharedPref.Security.isAnswer2Set(this)) {
			answer2EditText.setHint(R.string.security_answer_already_set_hint);
		}
		
		// set the values of the question edit text fields.
		question1EditText.setText(SharedPref.Security.getQuestion1(this));
		question2EditText.setText(SharedPref.Security.getQuestion2(this));
		
		setFieldsEnabled(enabledCheckbox.isChecked());
		setRightNavigationButtonEnabled(validSecurityDataEntererd());
		setRightNavigationButtonText(R.string.save);
		setRightNavigationButtonVisibility(View.VISIBLE);
		setLeftNavigationButtonText(R.string.cancel);
		
		this.changesMade = false;
		
		// Hide the keyboard unless the user chooses a text view.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	protected void onRightNavigationButtonPressed() {
		if(!validSecurityDataEntererd()) {
			Toast.makeText(this, R.string.security_bad_input_message, Toast.LENGTH_LONG).show();
			return;
		}
		
		boolean enabled = enabledCheckbox.isChecked();
		String password = passwordEditText.getText().toString().trim();
		String q1 = question1EditText.getText().toString().trim();
		String a1 = answer1EditText.getText().toString().trim();
		String q2 = question2EditText.getText().toString().trim();
		String a2 = answer2EditText.getText().toString().trim();
		
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
		String password = passwordEditText.getText().toString().trim();
		String q1 = question1EditText.getText().toString().trim();
		String a1 = answer1EditText.getText().toString().trim();
		String q2 = question2EditText.getText().toString().trim();
		String a2 = answer2EditText.getText().toString().trim();
		
		return !enabledCheckbox.isChecked() || 
			(
				(password.length() > 0 || SharedPref.Security.isPasswordSet(this)) && 
				q1.length() > 0 &&
				(a1.length() > 0 || SharedPref.Security.isAnswer1Set(this)) &&
				q2.length() > 0 &&
				(a2.length() > 0 || SharedPref.Security.isAnswer2Set(this))
			);
	}
	
	private void setFieldsEnabled(boolean b) {
		inputsContainer.setEnabled(b);
		for(int i = 0; i < inputsContainer.getChildCount(); ++i) {
			inputsContainer.getChildAt(i).setEnabled(b);
		}
		
		if(b) {
			passwordEditText.requestFocus();
		} else {
			enabledCheckbox.requestFocus();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.changesMade = true;
		setFieldsEnabled(isChecked);
		setRightNavigationButtonEnabled(validSecurityDataEntererd());
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		this.changesMade = true;
		this.setRightNavigationButtonEnabled(validSecurityDataEntererd());
		return false;
	}
}
