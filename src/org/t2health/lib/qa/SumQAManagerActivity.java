package org.t2health.lib.qa;

/**
 * A QA Manager that will sum the values of all the answers selected.
 * @author robbiev
 *
 */
public class SumQAManagerActivity extends SimpleQAManagerActivity {
	@Override
	protected double getTotalScore() {
		int total = 0;
		for(Answer[] ans: this.getSelectdAnswers().values()) {
			for(int i = 0; i < ans.length; ++i) {
				total += ans[i].value;
			}
		}
		return (int)total;
	}
}
