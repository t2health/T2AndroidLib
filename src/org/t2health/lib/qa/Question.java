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
package org.t2health.lib.qa;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple object designed to hold the basic data parsed from the XML file.
 * @author robbiev
 *
 */
public class Question implements Parcelable {
	public String id;
	public String title;
	public String desc;
	public String[] answerIds = new String[]{};
	public String[] correctAnswerIds = new String[]{};

	public static final Parcelable.Creator<Question> CREATOR
		= new Parcelable.Creator<Question>() {
			@Override
			public Question createFromParcel(Parcel source) {
				return new Question(source);
			}

			@Override
			public Question[] newArray(int size) {
				return new Question[size];
			}
		};

	public Question() {

	}

	private Question(Parcel in) {
		this.id = in.readString();
		this.title = in.readString();
		this.desc = in.readString();
		this.answerIds = new String[in.readInt()];
		in.readStringArray(this.answerIds);
		
		this.correctAnswerIds = new String[in.readInt()];
		in.readStringArray(this.correctAnswerIds);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.title);
		dest.writeString(this.desc);
		
		dest.writeInt(this.answerIds.length);
		dest.writeStringArray(this.answerIds);
		
		dest.writeInt(this.correctAnswerIds.length);
		dest.writeStringArray(this.correctAnswerIds);
	}

	public Answer[] getAnswers(HashMap<String,Answer> answers) {
		ArrayList<Answer> ans = new ArrayList<Answer>();
		for(int i = 0; i < answerIds.length; ++i) {
			Answer answer = answers.get(answerIds[i]);
			if(answer != null) {
				ans.add(answer);
			}
		}
		return ans.toArray(new Answer[ans.size()]);
	}
}