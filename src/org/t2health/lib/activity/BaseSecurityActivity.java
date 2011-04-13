package org.t2health.lib.activity;

import org.t2health.lib.AppSecurityManager;
import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.activity.security.UnlockActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class BaseSecurityActivity extends BaseActivity {
	private boolean isEnabled = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// configure the security manager.
		this.isEnabled = ManifestMetaData.SecurityManager.isEnabled(this) && 
							this.isSecurityEnabled() &&
							SharedPref.Security.isEnabled(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		AppSecurityManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		AppSecurityManager.onWindowFocusChanged(hasFocus);
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		if(!AppSecurityManager.isUnlocked() && this.isEnabled) {
			AppSecurityManager.startUnlockActivity(this, getSecurityUnlockIntent());
		}
		
		AppSecurityManager.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		AppSecurityManager.onPause();
		super.onPause();
	}
	
	protected Intent getSecurityUnlockIntent() {
		return new Intent(this, UnlockActivity.class);
	}
	
	protected boolean isSecurityEnabled() {
		return true;
	}
}
