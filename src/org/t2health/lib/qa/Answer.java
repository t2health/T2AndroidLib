package org.t2health.lib.qa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple class designed to hold the data necessary for a question.
 * @author robbiev
 *
 */
public class Answer implements Parcelable {
	public String id;
	public String title;
	public String desc;
	public int value;

	public static final Parcelable.Creator<Answer> CREATOR
		= new Parcelable.Creator<Answer>() {
			@Override
			public Answer createFromParcel(Parcel source) {
				return new Answer(source);
			}

			@Override
			public Answer[] newArray(int size) {
				return new Answer[size];
			}
		};

	public Answer() {

	}

	private Answer(Parcel in) {
		this.id = in.readString();
		this.title = in.readString();
		this.desc = in.readString();
		this.value = in.readInt();
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
		dest.writeInt(this.value);
	}
}