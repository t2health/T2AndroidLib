package org.t2health.lib.qa;

import org.t2health.lib.R;
import org.t2health.lib.activity.WebViewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple implementation of a QAManager. 
 * @author robbiev
 *
 */
public abstract class SimpleQAManagerActivity extends BaseQAManagerActivity implements OnClickListener {
	/**
	 * A boolean value determining if the total score should be shown.
	 */
	public static final String EXTRA_SHOW_TOTAL_SCORE = "showTotalScore";
	private boolean showTotalScore = true;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.showTotalScore = getIntent().getBooleanExtra(EXTRA_SHOW_TOTAL_SCORE, true);

		this.setContentView(R.layout.simple_qa_manager_activity);
		this.setTitle(this.getQuestionare().title);

		if(this.getQuestionare().content != null && this.getQuestionare().content.length() > 0) {
			View v = this.findViewById(R.id.detailsButton);
			v.setOnClickListener(this);
			v.setVisibility(View.VISIBLE);
		}

		if(this.getQuestionare().desc != null && this.getQuestionare().desc.length() > 0) {
			TextView tv = (TextView)this.findViewById(R.id.text1);
			tv.setText(this.getQuestionare().desc);
			tv.setVisibility(View.VISIBLE);
		}

		this.findViewById(R.id.score_wrapper).setVisibility(View.GONE);
		this.findViewById(R.id.startButton).setOnClickListener(this);

		listView = (ListView)this.findViewById(R.id.list);
		listView.setEmptyView(this.findViewById(R.id.emptyListView));

		if(!showTotalScore) {
			this.findViewById(R.id.score_wrapper).setVisibility(View.GONE);
		}
	}

	@Override
	protected void onAllQuestionsAnswered() {

		if(showTotalScore) {
			double total = getTotalScore();
			this.findViewById(R.id.score_wrapper).setVisibility(View.VISIBLE);
			((TextView)this.findViewById(R.id.total_score)).setText(total+"");
		}

		View listHeader = this.findViewById(R.id.listHeader);
		((ViewGroup)listHeader.getParent()).removeView(listHeader);
		listHeader.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		listView.addHeaderView(listHeader);
		listView.setAdapter(
				new QuestionResultsAdapter(
						this,
						R.layout.simple_qa_manager_list_item,
						this.getQuestions(),
						this.getSelectdAnswers()
				)
		);
	}

	@Override
	protected Intent getQuestionIntent(Question question, Answer[] answers,
			int totalQuestions, int questionIndex) {
		return new Intent(this, SingleChoiceQuestionActivity.class);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		int viewId = v.getId();
		if(viewId == R.id.startButton) {
			this.startQuestionare();
		} else if(viewId == R.id.detailsButton) {
			intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(WebViewActivity.EXTRA_CONTENT, this.getQuestionare().content);
			this.startActivity(intent);
		}
	}
}
