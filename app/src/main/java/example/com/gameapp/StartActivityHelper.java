package example.com.gameapp;

import android.content.Context;
import android.content.Intent;

import example.com.gameapp.activity.HighScoreActivity;
import example.com.gameapp.activity.LearnActivity;
import example.com.gameapp.activity.QuestionActivity;
import example.com.gameapp.activity.SettingsActivity;
import example.com.gameapp.activity.ShareActivity;

public class StartActivityHelper {

    public static void startSettingsctivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startQuestionActivity(Context context) {
        Intent intent = new Intent(context, QuestionActivity.class);
        context.startActivity(intent);
    }

    public static void startHighScoreActivity(Context context) {
        Intent intent = new Intent(context, HighScoreActivity.class);
        context.startActivity(intent);
    }

    public static void startShareActivity(Context context, String path) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    public static void startLearnActivity(Context context) {
        Intent intent = new Intent(context, LearnActivity.class);
        context.startActivity(intent);
    }
}
