package example.com.gameapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import example.com.gameapp.R;
import example.com.gameapp.StartActivityHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Learn Fruits");
    }

    public void play(View view) {
        StartActivityHelper.startQuestionActivity(this);
    }

    public void setting(View view) {
        StartActivityHelper.startSettingsctivity(this);
    }

    public void highScore(View view) {
        StartActivityHelper.startHighScoreActivity(this);
    }

    public void learn(View view) {
        StartActivityHelper.startLearnActivity(this);
    }
}
