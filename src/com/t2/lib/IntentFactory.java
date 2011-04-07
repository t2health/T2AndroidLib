package com.t2.lib;

import java.util.ArrayList;

import android.content.Intent;
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
}
