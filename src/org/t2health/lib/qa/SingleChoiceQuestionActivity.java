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


import org.t2health.lib.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A question activity to handle a single answer choice.
 * @author robbiev
 *
 */
public class SingleChoiceQuestionActivity extends BaseQAQuestionActivity implements OnItemClickListener, OnClickListener {
	private Answer selectedAnswer;
	private View nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.single_choice_question_activity);

		((TextView)this.findViewById(R.id.text1)).setText(this.question.title);
		ListView listView = (ListView) this.findViewById(R.id.list);
		listView.setAdapter(new AnswerAdapter(
				this,
				android.R.layout.simple_list_item_single_choice,
				this.answers
		));
		listView.setOnItemClickListener(this);

		this.nextButton = this.findViewById(R.id.nextButton);
		this.nextButton.setOnClickListener(this);
		this.nextButton.setEnabled(false);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.selectedAnswer = (Answer)arg0.getItemAtPosition(arg2);
		this.nextButton.setEnabled(true);
	}

	@Override
	public void onClick(View arg0) {
		int viewId = arg0.getId();
		if(viewId == R.id.nextButton) {
			this.finish(question, selectedAnswer);
		}
	}
}
