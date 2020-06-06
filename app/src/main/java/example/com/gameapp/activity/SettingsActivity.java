package example.com.gameapp.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import example.com.gameapp.R;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox bgMusic;
    private Spinner difficulty;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);

        bgMusic = findViewById(R.id.bg_music);
        difficulty = findViewById(R.id.difficulty);

        boolean bgMusicIsOpen = preferences.getBoolean("music", true);
        bgMusic.setChecked(bgMusicIsOpen);
        bgMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("music", isChecked).commit();
            }
        });

        String level = preferences.getString("level", "Easy");
        difficulty.setSelection(getLevelSelectedPosition(level));
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = ((String) parent.getAdapter().getItem(position)).toUpperCase();
                preferences.edit().putString("level", selection).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // displaying the go back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Settings");
    }

    @Override
    // click the go back button will go back to home page
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //return the id of the key
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getLevelSelectedPosition(String level) {
        String[] array = getResources().getStringArray(R.array.level);
        for (int i = 0; i < array.length; i++) {
            if (array[i].toUpperCase().equals(level.toUpperCase())) {
                return i;
            }
        }
        return 0;
    }
}
