package example.com.gameapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "game";
    private static final int DB_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS score(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "difficulty TEXT, score INTEGER, total INTEGER, time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Score> getScoreList(String difficulty) {
        Cursor cursor = getReadableDatabase().query("score", new String[]{"difficulty", "score", "total", "time"},
                "difficulty = ?", new String[]{difficulty}, null, null,
                "score DESC, time DESC");
        ArrayList<Score> scores = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Score score = new Score();
                score.difficulty = cursor.getString(0);
                score.score = cursor.getInt(1);
                score.total = cursor.getInt(2);
                score.time = cursor.getLong(3);
                scores.add(score);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scores;
    }

    public boolean insertScore(Score score) {
        ContentValues values = new ContentValues();
        values.put("difficulty", score.difficulty);
        values.put("score", score.score);
        values.put("total", score.total);
        values.put("time", score.time);
        long result = getWritableDatabase().insert("score", null, values);
        return result != -1;
    }
}
