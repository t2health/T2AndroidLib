package org.t2health.lib.security;

import java.util.Random;

import org.t2health.lib.IntentFactory;
import org.t2health.lib.R;
import org.t2health.lib.SharedPref;
import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class UnlockActivity extends BaseNavigationActivity implements OnKeyListener, OnClickListener {
	private static final int FORGOT_PIN_ACTIVITY = 235;
	private EditText pinEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.security_unlock_activity);
		this.setTitle(R.string.security_unlock_title);
		pinEditText = (EditText)this.findViewById(R.id.passwordEditText);
		pinEditText.setOnKeyListener(this);
		
		this.findViewById(R.id.forgotPasswordButton).setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == FORGOT_PIN_ACTIVITY && resultCode == RESULT_OK) {
			AppSecurityManager.setIsUnlocked(true);
			this.setResult(RESULT_OK);
			this.finish();
			
			// Start the configure activity so the password can be changed.
			startActivity(IntentFactory.Security.getConfigureIntent(this));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.moveTaskToBack(true);
			return true;
		}
		
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(v.getId() == R.id.passwordEditText) {
			String enteredPin = pinEditText.getText().toString().trim();
			if(SharedPref.Security.doesPasswordMatch(this, enteredPin)) {
				AppSecurityManager.setIsUnlocked(true);
				this.setResult(RESULT_OK);
				this.finish();
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.forgotPasswordButton:
				this.startActivityForResult(
						IntentFactory.Security.getForgotPasswordIntent(this), 
						FORGOT_PIN_ACTIVITY
				);
				break;
		}
	}

	@Override
	protected boolean isSecurityEnabled() {
		return false;
	}
}
