package example.com.gameapp.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import example.com.gameapp.R;
import example.com.gameapp.game.CelebrityManager;

public class LearnActivity extends AppCompatActivity {

    private ImageView picture;
    private TextView name;
    private Button previous, next;
    private CelebrityManager celebrityManager;
    private int currentIndex = 0;

    private Integer[] randomArrays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        picture = findViewById(R.id.picture);
        name = findViewById(R.id.name);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        celebrityManager = new CelebrityManager(getAssets(), "fruits");
        randomArrays = generateRandomArray();
        switchover(currentIndex);

        // displaying the go back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Learn Fruits");
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

    public void previous(View view) {
        if (currentIndex - 1 < 0) {
            refreshButtonState();
            return;
        }

        currentIndex--;
        switchover(currentIndex);
    }

    public void next(View view) {
        if (currentIndex + 1 >= celebrityManager.count()) {
            refreshButtonState();
            return;
        }
        currentIndex++;
        switchover(currentIndex);
    }

    private void switchover(int index) {
        picture.setImageBitmap(celebrityManager.get(randomArrays[index]));
        name.setText(celebrityManager.getName(randomArrays[index]));
        refreshButtonState();
    }

    private void refreshButtonState() {
        if (currentIndex - 1 < 0) {
            previous.setEnabled(false);
        } else {
            previous.setEnabled(true);
        }

        if (currentIndex + 1 >= celebrityManager.count()) {
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }
    }

    private Integer[] generateRandomArray() {
        Set<Integer> set = new HashSet<>();
        while (set.size() < celebrityManager.count()) {
            int random = new Random().nextInt(celebrityManager.count());
            set.add(random);
        }

        return set.toArray(new Integer[set.size()]);
    }
}
