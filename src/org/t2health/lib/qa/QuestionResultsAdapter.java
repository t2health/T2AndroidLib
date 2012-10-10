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
import java.util.LinkedHashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * Used to who a list of the questions answered and the user-generated 
 * answers associated with them.
 * @author robbiev
 *
 */
public class QuestionResultsAdapter extends BaseAdapter {
	private int layoutResId;
	private LayoutInflater layoutInflater;
	private LinkedHashMap<String, Question> questions;
	private LinkedHashMap<String, Answer[]> answers;
	private ArrayList<Question> questionsList;

	public QuestionResultsAdapter(Context c, int layoutResId, LinkedHashMap<String,Question> questions, LinkedHashMap<String,Answer[]> answers) {
		this.layoutResId = layoutResId;
		this.questions = questions;
		this.answers = answers;
		this.layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.questionsList = new ArrayList<Question>(this.questions.values());
	}

	@Override
	public int getCount() {
		return this.questions.size();
	}

	@Override
	public Question getItem(int arg0) {
		return this.questionsList.get(arg0);
	}

	public Answer[] getAnswers(int arg0) {
		return this.answers.get(this.getItem(arg0).id);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Question question = getItem(position);
		Answer[] answers = getAnswers(position);

		if(convertView == null) {
			convertView = this.layoutInflater.inflate(layoutResId, null);
		}

		View view1 = convertView.findViewById(android.R.id.text1);
		View view2 = convertView.findViewById(android.R.id.text2);
		if(view1 != null) {
			((TextView)view1).setText(question.title);
		}
		if(view2 != null) {
			String answerValue = "";
			if(answers != null && answers.length > 0) {
				answerValue = ((int)answers[0].value)+"";
			}
			((TextView)view2).setText(answerValue);
		}

		return convertView;
	}
}