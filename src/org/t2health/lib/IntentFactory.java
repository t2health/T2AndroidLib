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
package org.t2health.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;

/**
 * A helper class to create commonly used intents.
 * @author robbiev
 *
 */
public class IntentFactory {
	/**
	 * Builds an intent based on a uri. The resultant intent would be used to
	 * load an activity that supports the uri. Eg.. the webbrowser app will 
	 * handle URLs passed as a uri.
	 * @param uri	The uri to handle.
	 * @return	The created intent.
	 */
	public static Intent URIIntent(Uri uri) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(uri);
		return i;
	}
	
	/**
	 * Build an intent that would be passed to Intent.createChooser(). This 
	 * intent would typically be used to start the email app.
	 * @param to	The email address of the recipient.
	 * @param subject	The subject of the message.
	 * @param body	The message.
	 * @return	The created intent.
	 */
	public static Intent shareIntent(String[] to, String subject, String body) {
		return shareIntent(to, subject, body, null, null);
	}
	
	/**
	 * Build an intent that would be passed to Intent.createChooser(). This 
	 * intent would typically be used to start the email app.
	 * @param to	The email address of the recipient.
	 * @param subject	The subject of the message.
	 * @param body	The message.
	 * @param attachmentUris	Optional attachment files, null no attachments.
	 * @param attachmentMimeType	Mime type of the attachments. (all the 
	 * 								attachments should be the same mime type)
	 * @return	The created intent.
	 */
	public static Intent shareIntent(String[] to, String subject, String body, ArrayList<Uri> attachmentUris, String attachmentMimeType) {
		Intent i = new Intent(android.content.Intent.ACTION_SEND);
		i.setType("plain/text");
		i.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
		
		if(attachmentUris != null && attachmentMimeType != null) {
			i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachmentUris);
			i.setType(attachmentMimeType);
		}
		
		return i;
	}
	
	/**
	 * A collection of security related intents.
	 * @author robbiev
	 *
	 */
	public static class Security {
		/**
		 * Get the intent responsible for configuring security.
		 * @param c
		 * @return
		 */
		public static Intent getConfigureIntent(Context c) {
			return new Intent(c, org.t2health.lib.security.ConfigureActivity.class);
		}
		
		/**
		 * Get the intent that handles the unlock screen.
		 * @param c
		 * @return
		 */
		public static Intent getUnlockIntent(Context c) {
			return new Intent(c, org.t2health.lib.security.UnlockActivity.class);
		}
		
		/**
		 * Get the intent that handles the forgot password screen.
		 * @param c
		 * @return
		 */
		public static Intent getForgotPasswordIntent(Context c) {
			return new Intent(c, org.t2health.lib.security.ForgotPasswordActivity.class);
		}
	}
	
	private static HashMap<String,Boolean> isIntentActionAvailableCache = new HashMap<String,Boolean>();
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * Taken from: http://snipplr.com/view/15639/determine-if-intent-receiver-exists-in-android/
	 * Modified to cache results.
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentActionAvailable(Context context, String action) {
		Boolean isAvailable = isIntentActionAvailableCache.get(action);
		if(isAvailable != null) {
			return isAvailable;
		}
		
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    isAvailable = list.size() > 0;
	    isIntentActionAvailableCache.put(action, isAvailable);
	    return isAvailable;
	}
}
