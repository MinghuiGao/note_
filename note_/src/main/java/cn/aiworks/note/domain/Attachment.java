package cn.aiworks.note.domain;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 包括笔记内容中的文本信息和图片时间戳的集合。
 * @author aispeech
 *
 */
public class Attachment implements Parcelable {
	
	/**
	 * 笔记的文本内容，图片位置用“（图片）”替代。
	 */
	private String content;
	/**
	 * 图片的时间戳集合。
	 */
	private ArrayList<String> timeStamps;
	
	/**
	 * 图片的uri[]
	 */
	private Uri[] uris;

	protected Attachment(Parcel in) {
		content = in.readString();
		timeStamps = in.createStringArrayList();
		uris = in.createTypedArray(Uri.CREATOR);
	}

	public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
		@Override
		public Attachment createFromParcel(Parcel in) {
			return new Attachment(in);
		}

		@Override
		public Attachment[] newArray(int size) {
			return new Attachment[size];
		}
	};

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public ArrayList<String> getTimeStamps() {
		return timeStamps;
	}
	public void setTimeStamps(ArrayList<String> timeStamps) {
		this.timeStamps = timeStamps;
	}
	public Uri[] getUris() {
		return uris;
	}
	public void setUris(Uri[] uris) {
		this.uris = uris;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeString(content);
		dest.writeStringList(timeStamps);
		dest.writeTypedArray(uris, flags);
	}
}
