package marcinpakulnicki.net.yetanotherincarapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.TypeFaceUtil;

public class YaicaSplashActivity extends Activity {

    private static final String THIS_CLASS_NAME  = "YaicaSplashActivity";

    private final Handler handler = new Handler();
    private AnimationSet splashActivityAnimeSet;
    private Animation fadeOutAnime;
    private Animation fadeInAnime;

    private ImageView image_imageView;
    private TextView title_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yaica_splash_activity_layout);
        Log.i(THIS_CLASS_NAME, "onCreate reached");

        image_imageView = (ImageView) findViewById(R.id.yaica_splash_imageView);
        title_textView = (TextView) findViewById(R.id.yaica_splash_textView);
        title_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(this));

        fadeInAnime = new AlphaAnimation(0, 1);
        fadeInAnime.setInterpolator(new DecelerateInterpolator());
        fadeInAnime.setDuration(700);

        fadeOutAnime = new AlphaAnimation(1, 0);
        fadeOutAnime.setInterpolator(new AccelerateInterpolator());
        fadeOutAnime.setStartOffset(1700);
        fadeOutAnime.setDuration(700);

        splashActivityAnimeSet = new AnimationSet(false);
        splashActivityAnimeSet.addAnimation(fadeInAnime);
        splashActivityAnimeSet.addAnimation(fadeOutAnime);
        title_textView.setAnimation(splashActivityAnimeSet);
        image_imageView.setAnimation(splashActivityAnimeSet);

        splashActivityAnimeSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                title_textView.setVisibility(View.INVISIBLE);
                image_imageView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            public void run() {
                continueToNextActivity();
            }
        }, 2700);
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    private void continueToNextActivity() {
        Intent intent = new Intent(YaicaSplashActivity.this, YaicaMainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yaica_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
