package org.t2health.lib.qa;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Stack;

import org.t2health.lib.activity.BaseNavigationActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * A base class for managing a questionare loaded from an XML file.
 * @author robbiev
 */
public abstract class BaseQAManagerActivity extends BaseNavigationActivity {
	/**
	 * The resource ID of the xml file located in res/xml
	 */
	public static final String EXTRA_XML_RESOURCE = "xmlResourceID";
	
	private static final String XML_TAG_QUESTIONARE = "questionare";
	private static final String XML_TAG_TITLE = "title";
	private static final String XML_TAG_DESC = "desc";
	private static final String XML_TAG_CONTENT = "content";
	private static final String XML_TAG_QUESTIONS = "questions";
	private static final String XML_TAG_QUESTION = "question";
	private static final String XML_TAG_ANSWERS = "answers";
	private static final String XML_TAG_ANSWER = "answer";
	
	private static final String XML_ATTR_ID = "id";
	private static final String XML_ATTR_TITLE = "title";
	private static final String XML_ATTR_DESC = "desc";
	private static final String XML_ATTR_VALUE = "value";
	private static final String XML_ATTR_ANSWER_IDS = "answerIds";
	
	//private static final String TAG = BaseQAManagerActivity.class.getSimpleName();
	private Questionare questionare = new Questionare();
	private LinkedHashMap<String,Question> questions = new LinkedHashMap<String,Question>();
	private LinkedHashMap<String,Answer> answers = new LinkedHashMap<String,Answer>();
	private LinkedHashMap<String,Answer[]> selectdAnswers = new LinkedHashMap<String,Answer[]>();
	private Question currentQuestion;

	private static final int QUESTION_ACTIVITY = 309;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int xmlResId = getIntent().getIntExtra(EXTRA_XML_RESOURCE, -1);
		if(xmlResId == -1 || !loadData(xmlResId)) {
			this.finish();
			return;
		}
	}

	protected void startQuestionare() {
		Question nextQuestion = getNextQuestion(this.currentQuestion);
		this.currentQuestion = nextQuestion;
		showQuestion(this.currentQuestion);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == QUESTION_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				if(data != null) {
					Parcelable[] parcels = data.getParcelableArrayExtra(BaseQAQuestionActivity.EXTRA_ANSWERS);
					Answer[] qAnswers = new Answer[parcels.length];
					for(int i = 0; i < qAnswers.length; ++i) {
						qAnswers[i] = (Answer)parcels[i];
					}
					this.addSelectedAnswer(
							(Question)data.getParcelableExtra(BaseQAQuestionActivity.EXTRA_QUESTION),
							qAnswers
					);
				}


				Question nextQuestion = getNextQuestion(this.currentQuestion);

				// this was the last question.
				if(nextQuestion == null) {
					onAllQuestionsAnswered();
					return;

				// load the next question
				} else {
					this.currentQuestion = nextQuestion;
					showQuestion(this.currentQuestion);
				}

			} else if(resultCode == RESULT_BACK) {
				onQuestionCancelled();
				return;
			}
		}
	}

	private void addSelectedAnswer(Question q, Answer[] ans) {
		getSelectdAnswers().put(
				q.id,
				ans
		);
	}

	private boolean loadData(int xmlResourceId) {
		Stack<String> tagsStack = new Stack<String>();
		tagsStack.push("");
		try {
			XmlResourceParser parser = this.getResources().getXml(xmlResourceId);

			int eventType = parser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				String prevTag = tagsStack.peek();
				String tag = parser.getName();

				if(eventType == XmlPullParser.START_TAG) {
					tagsStack.push(tag);

					if(prevTag.equals(XML_TAG_QUESTIONARE)) {
						if(tag.equals(XML_TAG_TITLE)) {
							eventType = parser.next();
							if(eventType == XmlPullParser.TEXT) {
								getQuestionare().title = parser.getText();
							}
							continue;
						} else if(tag.equals(XML_TAG_DESC)) {
							eventType = parser.next();
							if(eventType == XmlPullParser.TEXT) {
								getQuestionare().desc = parser.getText();
							}
							continue;
						} else if(tag.equals(XML_TAG_CONTENT)) {
							eventType = parser.next();
							if(eventType == XmlPullParser.TEXT) {
								getQuestionare().content = parser.getText();
							}
							continue;
						}
					} else if(prevTag.equals(XML_TAG_QUESTIONS)) {
						if(tag.equals(XML_TAG_QUESTION)) {
							Question q = new Question();
							q.id = parser.getAttributeValue(null, XML_ATTR_ID);
							q.desc = parser.getAttributeValue(null, XML_ATTR_DESC);
							q.title = parser.getAttributeValue(null, XML_ATTR_TITLE);
							q.answerIds = parser.getAttributeValue(null, XML_ATTR_ANSWER_IDS).split(",");
							this.getQuestions().put(q.id, q);
						}
					} else if(prevTag.equals(XML_TAG_ANSWERS)) {
						if(tag.equals(XML_TAG_ANSWER)) {
							Answer a = new Answer();
							a.id = parser.getAttributeValue(null, XML_ATTR_ID);
							a.title = parser.getAttributeValue(null, XML_ATTR_TITLE);
							a.desc = parser.getAttributeValue(null, XML_ATTR_DESC);
							a.value = parser.getAttributeIntValue(null, XML_ATTR_VALUE, 0);
							this.getAnswers().put(a.id, a);
						}
					}

				} else if(eventType == XmlPullParser.END_TAG) {
					tagsStack.pop();
				}

				eventType = parser.next();
			}
			parser.close();
			
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Called when a question is canceled (not answered). One way this can 
	 * happen is by pressing the hardware back button on the device.
	 * The default behavior, is to cancel the entire questioner. Override
	 * this method in order to prevent this behavior.
	 */
	protected void onQuestionCancelled() {
		this.finish();
	}

	/**
	 * Called when it is time to calculate the total score for the questionare.
	 * @return return the score.
	 */
	protected abstract double getTotalScore();
	
	/**
	 * This event is fired after the last question has been answered.
	 */
	protected abstract void onAllQuestionsAnswered();

	/**
	 * By using the previous question as a reference, will return the next
	 * question in the list. This can be overridden in order to implement
	 * skip-logic and dependent question relationships.
	 * @param previousQuestion
	 * @return
	 */
	protected Question getNextQuestion(Question previousQuestion) {
		boolean loadNext = (previousQuestion == null);
		for(Question q: getQuestions().values()) {
			if(loadNext) {
				return q;
			}

			if(q == previousQuestion) {
				loadNext = true;
			}
		}
		return null;
	}

	private void showQuestion(Question q) {
		Answer[] ans = q.getAnswers(this.getAnswers());

		int questionCount = getQuestions().size();
		int questionIndex = indexOfQuestion(q);

		Intent intent = getQuestionIntent(
				q,
				ans,
				questionCount,
				questionIndex
		);

		intent.putExtra(BaseQAQuestionActivity.EXTRA_TOTAL_QUESIONS, questionCount);
		intent.putExtra(BaseQAQuestionActivity.EXTRA_QUESTION_INDEX, questionIndex);
		intent.putExtra(BaseQAQuestionActivity.EXTRA_QUESTION, q);
		intent.putExtra(BaseQAQuestionActivity.EXTRA_ANSWERS, ans);

		startActivityForResult(intent, QUESTION_ACTIVITY);
	}

	/**
	 * Get the intent to use when showing the next question. This is called
	 * each time a question is to be shown. So overriding this can provide
	 * different intents per question.
	 * @param question
	 * @param answers
	 * @param totalQuestions
	 * @param questionIndex
	 * @return
	 */
	protected abstract Intent getQuestionIntent(Question question, Answer[] answers, int totalQuestions, int questionIndex);

	protected int indexOfQuestion(Question question) {
		int i = 0;
		for(Question q: getQuestions().values()) {
			if(q == question) {
				return i;
			}
			++i;
		}
		return -1;
	}

	protected Questionare getQuestionare() {
		return questionare;
	}

	protected LinkedHashMap<String,Question> getQuestions() {
		return questions;
	}

	protected LinkedHashMap<String,Answer> getAnswers() {
		return answers;
	}

	protected LinkedHashMap<String,Answer[]> getSelectdAnswers() {
		return selectdAnswers;
	}
}
