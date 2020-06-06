package example.com.gameapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.com.gameapp.Background;
import example.com.gameapp.R;
import example.com.gameapp.databases.MyDatabaseHelper;
import example.com.gameapp.databases.Score;
import example.com.gameapp.fragment.HighScoreFragment;
import example.com.gameapp.game.Difficulty;

public class HighScoreActivity extends AppCompatActivity {

    private Context context;
    private ArrayList<TextView> mTitleTabs;
    private TextView easy, medium, hard, expert;
    private ViewPager viewPager;

    private MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        this.context = this;
        easy = findViewById(R.id.easy);
        medium = findViewById(R.id.medium);
        hard = findViewById(R.id.hard);
        expert = findViewById(R.id.expert);
        viewPager = findViewById(R.id.view_pager);

        mTitleTabs = new ArrayList<>();
        mTitleTabs.add(easy);
        mTitleTabs.add(medium);
        mTitleTabs.add(hard);
        mTitleTabs.add(expert);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                changeTabStatus(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        databaseHelper = new MyDatabaseHelper(this);

        // displaying the go back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("High Scores");

        loadScore();
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

    private void changeTabStatus(int position) {
        for (int i = 0; i < mTitleTabs.size(); i++) {
            mTitleTabs.get(i).setSelected(i == position);
        }
    }

    private void loadScore() {
        Background.run(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Score>[] scoreArray = new ArrayList[4];
                scoreArray[0] = databaseHelper.getScoreList(Difficulty.EASY.toString());
                scoreArray[1] = databaseHelper.getScoreList(Difficulty.MEDIUM.toString());
                scoreArray[2] = databaseHelper.getScoreList(Difficulty.HARD.toString());
                scoreArray[3] = databaseHelper.getScoreList(Difficulty.EXPERT.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAdapter(scoreArray);
                    }
                });
            }
        });
    }

    private void createAdapter(final ArrayList<Score>[] arrays) {
        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int i) {
                HighScoreFragment highScoreFragment = HighScoreFragment.getInstance(arrays[i]);
                return highScoreFragment;
            }

            @Override
            public int getCount() {
                return arrays.length;
            }
        };

        viewPager.setAdapter(adapter);
        changeTabStatus(0);
    }

    public void easy(View view) {
        viewPager.setCurrentItem(0, false);
        changeTabStatus(0);
    }

    public void medium(View view) {
        viewPager.setCurrentItem(1, false);
        changeTabStatus(1);
    }

    public void hard(View view) {
        viewPager.setCurrentItem(2, false);
        changeTabStatus(2);
    }

    public void expert(View view) {
        viewPager.setCurrentItem(3, false);
        changeTabStatus(3);
    }
}
