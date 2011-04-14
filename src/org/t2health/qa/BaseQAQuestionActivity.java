package org.t2health.qa;

import org.t2health.lib.activity.BaseNavigationActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
