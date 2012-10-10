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

import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * Base activity for displaying a question and its possible answers.
 * @author robbiev
 *
 */
public abstract class BaseQAQuestionActivity extends BaseNavigationActivity {
	/**
	 * The Question object
	 */
	public static final String EXTRA_QUESTION = "questionObject";

	/**
	 * An Answer[] of answers to be shown with the question.
	 */
	public static final String EXTRA_ANSWERS = "answersList";

	/**
	 * The total number of questions for this questionare.
	 */
	public static final String EXTRA_TOTAL_QUESIONS = "questionsCount";

	/**
	 * The current question index (zero based)
	 */
	public static final String EXTRA_QUESTION_INDEX = "questionIndex";

	protected Question question;
	protected Answer[] answers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		question = (Question)intent.getParcelableExtra(EXTRA_QUESTION);

		Parcelable[] ansParcels = intent.getParcelableArrayExtra(EXTRA_ANSWERS);
		answers = new Answer[ansParcels.length];
		for(int i = 0; i < answers.length; ++i) {
			answers[i] = (Answer)ansParcels[i];
		}

		if(question == null) {
			this.finish();
			return;
		}

		if(answers == null || answers.length <= 0) {
			this.finish();
			return;
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.setTitle(question.title);
	}

	/**
	 * Finish this activity. Pass back the question and the selected answer.
	 * @param q	The current question, usually the one passed via the activity's
	 * intent.
	 * @param selectedAnswer The answer that was selected.
	 */
	protected final void finish(Question q, Answer selectedAnswer) {
		this.finish(q, new Answer[]{selectedAnswer});
	}

	/**
	 * Finish this activity. Pass back the question and the selected answers.
	 * @param q The current question, usually the one passed via the activity's
	 * intent.
	 * @param selectedAnswers An array of the answers that were selected.
	 */
	protected final void finish(Question q, Answer[] selectedAnswers) {
		Intent i = new Intent();
		i.putExtra(EXTRA_QUESTION, q);
		i.putExtra(EXTRA_ANSWERS, selectedAnswers);
		this.setResult(RESULT_OK, i);
		this.finish();
	}
}
