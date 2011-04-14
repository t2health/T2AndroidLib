package org.t2health.qa;

import java.util.LinkedHashMap;

/**
 * A QA manager who's job is to average the answer values using the total
 * selected answers as a base.
 * @author robbiev
 *
 */
public class AverageQAManagerActivity extends SimpleQAManagerActivity {
	@Override
	protected double getTotalScore() {
		LinkedHashMap<String, Answer[]> selectedAnswers = this.getSelectdAnswers();
		int total = 0;
		for(Answer[] ans: selectedAnswers.values()) {
			for(int i = 0; i < ans.length; ++i) {
				total += ans[i].value;
			}
		}
		return (int)(total / selectedAnswers.size());
	}
}
