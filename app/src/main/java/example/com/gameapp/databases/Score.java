package example.com.gameapp.databases;

import android.os.Parcel;
import android.os.Parcelable;

//store the scores in database using Parcelable interface which allows data transferred between Buddles
public class Score implements Parcelable {

    public String difficulty;

    public int score;

    public int total;

    public long time;

    public Score() {

    }

    public Score(final String difficulty, final int score, final int total, final long time) {
        this.difficulty = difficulty;
        this.score = score;
        this.total = total;
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.difficulty);
        dest.writeInt(this.score);
        dest.writeInt(this.total);
        dest.writeLong(this.time);
    }

    protected Score(Parcel in) {
        this.difficulty = in.readString();
        this.score = in.readInt();
        this.total = in.readInt();
        this.time = in.readLong();
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel source) {
            return new Score(source);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };
}
