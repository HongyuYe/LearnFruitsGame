package example.com.gameapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import example.com.gameapp.AnswerListener;
import example.com.gameapp.R;
import example.com.gameapp.StartActivityHelper;
import example.com.gameapp.databases.MyDatabaseHelper;
import example.com.gameapp.databases.Score;
import example.com.gameapp.fragment.QuestionFragment;
import example.com.gameapp.fragment.StatusFragment;
import example.com.gameapp.game.CelebrityManager;
import example.com.gameapp.game.Difficulty;
import example.com.gameapp.game.Game;
import example.com.gameapp.game.GameBuilder;
import example.com.gameapp.game.Question;

public class QuestionActivity extends AppCompatActivity implements AnswerListener, SensorEventListener {

    private Context context;
    private SharedPreferences preferences;
    private MediaPlayer mediaPlayer;

    private StatusFragment statusFragment;
    private QuestionFragment questionFragment;

    private GameBuilder gameBuilder;

    private Game currentGame;
    private Question currentQuestion;

    private boolean isShowResultDialog = false;

    private static final int START_SHAKE = 0x1;
    private static final int AGAIN_SHAKE = 0x2;
    private static final int END_SHAKE = 0x3;

    private SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;
    private Vibrator mVibrator;//Phone vibrator
    private MyHandler mHandler;
    private boolean isShake = false;
    private boolean isResume = false;

    private static Game tempGame;
    private static Question tempQuestion;

    private Dialog dialog;
    private TextView score;
    private ImageView scoreIcon;
    private ImageButton share;
    private Difficulty level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        this.context = this;
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        if (preferences.getBoolean("music", true)) {
            mediaPlayer = MediaPlayer.create(context, R.raw.bgsong);
            mediaPlayer.setLooping(true);
        }


        mHandler = new MyHandler(this);
        //get the Vibrator service
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        level = Difficulty.valueOf(preferences.getString("level", Difficulty.EASY.toString()));

        FragmentManager fragmentManager = getSupportFragmentManager();
        statusFragment = (StatusFragment) fragmentManager.findFragmentById(R.id.status);
        questionFragment = (QuestionFragment) fragmentManager.findFragmentById(R.id.question);

        gameBuilder = new GameBuilder(new CelebrityManager(getAssets(), "fruits"));

        if (savedInstanceState == null) {
            currentGame = gameBuilder.create(level);
            currentQuestion = currentGame.next();
        } else {
            currentGame = tempGame;
            currentQuestion = tempQuestion;
            tempGame = null;
            tempQuestion = null;
            isShowResultDialog = savedInstanceState.getBoolean("dialog", false);
        }

        initDialog();
        statusFragment.setTextScore(getScore());
        statusFragment.setPicture(currentQuestion.getCelebrityImage());
        questionFragment.optionsCount(currentGame.optionsCount());
        if (!currentGame.isGameOver()) {
            questionFragment.showQuestion(currentQuestion);
        } else {
            if (isShowResultDialog) {
                displayResultDialog();
            }
        }

        // displaying the go back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Select Correct Answer");
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

    @Override
    protected void onStart() {
        super.onStart();
        //get the Sensor Manager
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        if (mSensorManager != null) {
            //get the Accelerometer Sensor
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (mAccelerometerSensor != null) {
                mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        //must unregisterListener mSensorManager in pause,otherwise it will keep affecting after the user quit the page

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void showNextQuestion() {
        currentQuestion = currentGame.next();
        statusFragment.setPicture(currentQuestion.getCelebrityImage());
        questionFragment.showQuestion(currentQuestion);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dialog", isShowResultDialog);
        //do not use outState to store data directly, otherwise the app will crash
        tempGame = currentGame;
        tempQuestion = currentQuestion;
    }

    @Override
    public void answer(String answer) {
        if (currentQuestion.check(answer)) {
            currentGame.updateScore(true);
            statusFragment.setTextScore(getScore());
        }
        if (!currentGame.isGameOver()) {
            showNextQuestion();
        } else {
            //game finishing
            questionFragment.clear();
            //insert data into database
            MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);
            Score score = new Score(level.toString(), currentGame.getScore(), currentGame.count(),
                    System.currentTimeMillis());
            databaseHelper.insertScore(score);
            displayResultDialog();
        }
    }

    private String getScore() {
        return "Score: " + currentGame.getScore() + "/" + currentGame.count();
    }

    private void initDialog() {
        dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_score_display, null);
        score = view.findViewById(R.id.score);
        scoreIcon = view.findViewById(R.id.score_icon);
        share = view.findViewById(R.id.share);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
    }

    private void displayResultDialog() {
        isShowResultDialog = true;
        float percentage = (float) currentGame.getScore() / currentGame.count();
        String scoreString = getScore();
        if (percentage >= 0.6f) {
            if (percentage == 1.0f) {
                score.setText(scoreString + "\n" + getString(R.string.result_keep));
            } else {
                score.setText(scoreString + "\n" + getString(R.string.result_encourage));
            }
            scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.score_good));
        } else {
            score.setText(scoreString + "\n" + getString(R.string.result_comfort));
            scoreIcon.setImageDrawable(getResources().getDrawable(R.drawable.score_bad));
        }
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.setVisibility(View.GONE);
                String path = saveScreen();
                StartActivityHelper.startShareActivity(context, path);
                isShowResultDialog = false;
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private String saveScreen() {
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gameapp";
        File fileDir = new File(directory);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        String path = directory + "/score" + System.currentTimeMillis() + ".png";
        View view = dialog.getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap temBitmap = view.getDrawingCache();
        try {
            FileOutputStream foStream = new FileOutputStream(path);
            temBitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.flush();
            foStream.close();
        } catch (Exception e) {
            Log.i("QuestionActivity", "save screen error " + e);
        }
        return path;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            //get the values of the three dimensions in space
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if ((Math.abs(x) > 24 || Math.abs(y) > 24 || Math
                    .abs(z) > 24) && !isShake) {
                isShake = true;
                // implement the logic of the shaking, after shaking, the phone vibrates
                Thread thread = new Thread() {
                    @Override
                    public void run() {


                        super.run();
                        try {
                            Log.d("QuestionActivity", "onSensorChanged: Shake");

                            //start vibration and make sound, display animation
                            mHandler.obtainMessage(START_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            //vibrate again
                            mHandler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                            Thread.sleep(500);
                            mHandler.obtainMessage(END_SHAKE).sendToTarget();


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static class MyHandler extends Handler {
        private WeakReference<QuestionActivity> mReference;
        private QuestionActivity mActivity;

        public MyHandler(QuestionActivity activity) {
            mReference = new WeakReference<QuestionActivity>(activity);
            if (mReference != null) {
                mActivity = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    //This method requires the caller to hold the permission VIBRATE.
                    mActivity.mVibrator.vibrate(300);
                    break;
                case AGAIN_SHAKE:
                    mActivity.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    mActivity.restartGame();
                    //the shaking completed
                    mActivity.isShake = false;
                    break;
            }
        }
    }

    private void restartGame() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        currentGame = gameBuilder.create(level);
        currentQuestion = currentGame.next();
        statusFragment.setTextScore(getScore());
        statusFragment.setPicture(currentQuestion.getCelebrityImage());
        questionFragment.showQuestion(currentQuestion);
    }
}
