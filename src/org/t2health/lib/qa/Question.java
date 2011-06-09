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