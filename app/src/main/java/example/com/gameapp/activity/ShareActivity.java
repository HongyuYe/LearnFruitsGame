package example.com.gameapp.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import example.com.gameapp.Background;
import example.com.gameapp.R;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class ShareActivity extends AppCompatActivity {

    private Activity activity;
    private Twitter twitter;

    private WebView webView;
    private ConstraintLayout constraintLayout;
    private TextView title;
    private EditText content;
    private ImageView screen, close;
    private Button submit;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        activity = this;
        path = getIntent().getStringExtra("path");

        webView = findViewById(R.id.web_view);
        constraintLayout = findViewById(R.id.constraint_layout);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        screen = findViewById(R.id.screen);
        close = findViewById(R.id.close);
        submit = findViewById(R.id.submit);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("pD4zZDMSb783jxQS14mKusttW")
                .setOAuthConsumerSecret("ImDxuzhSQuNzuDC7hGhpG330V2DPq2OYK2CSMOQRIrTViroWoH");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(final WebView view, final String url) {
                super.onLoadResource(view, url);
                if (url.startsWith("http://jsplayground.crunchycodes.net/")) {
                    Uri uri = Uri.parse(url);
                    final String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    if (oauthVerifier != null) {
                        Background.run(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    AccessToken accessToken = twitter.getOAuthAccessToken(oauthVerifier);
                                    twitter.setOAuthAccessToken(accessToken);
                                    Log.i("ShareActivity", "authorise success, accessToken " + accessToken);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            authorisedResult(true);
                                        }
                                    });
                                } catch (TwitterException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            authorisedResult(false);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

        if (!isAuthorised()) {
            Background.run(new Runnable() {
                @Override
                public void run() {
                    try {
                        RequestToken requestToken = twitter.getOAuthRequestToken();
                        final String requestUrl = requestToken.getAuthenticationURL();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl(requestUrl);
                            }
                        });
                    } catch (TwitterException e) {
                        Log.i("ShareActivity", "login err " + e);
                    }
                }
            });
        } else {
            Log.i("ShareActivity", "authorise success");
            authorisedResult(true);
        }
        // displaying the go back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Share Your Score");
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

    private void authorisedResult(boolean successful) {
        webView.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
        if (successful) {
            submit.setEnabled(true);
        } else {
            submit.setEnabled(false);
            title.setText(R.string.twitter_verify_err);
        }

        if (path != null && new File(path).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            screen.setImageBitmap(bitmap);
        } else {
            close.setVisibility(View.GONE);
            screen.setVisibility(View.GONE);
        }
    }

    private boolean isAuthorised() {
        try {
            twitter.verifyCredentials();
            Log.i("ShareActivity", "verified");
            return true;
        } catch (Exception e) {
            Log.i("ShareActivity", "not verified" + e);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void close(View view) {
        close.setVisibility(View.GONE);
        screen.setVisibility(View.GONE);
        path = null;
    }

    public void submit(View view) {
        final String shareContent = content.getText().toString();
        final StatusUpdate update = new StatusUpdate(shareContent);
        Background.run(new Runnable() {
            @Override
            public void run() {
                if (path != null && new File(path).exists()) {
                    try {
                        UploadedMedia uploadedMedia = twitter.uploadMedia(new File(path));
                        update.setMediaIds(uploadedMedia.getMediaId());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {

                    }
                }
                try {
                    Status status = twitter.updateStatus(update);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.share_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (TwitterException | IllegalStateException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.share_fail, Toast.LENGTH_SHORT).show();

                        }
                    });
                    e.printStackTrace();
                } finally {
                    activity.finish();
                }
            }
        });
    }
}
