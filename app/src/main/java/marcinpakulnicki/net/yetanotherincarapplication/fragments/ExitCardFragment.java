package marcinpakulnicki.net.yetanotherincarapplication.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import marcinpakulnicki.net.yetanotherincarapplication.R;
import marcinpakulnicki.net.yetanotherincarapplication.YaicaMainActivity;
import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;

public class ExitCardFragment extends Fragment implements TextToSpeech.OnInitListener {

    private static final String THIS_CLASS_NAME  = "ExitCardFragment";

    private TextToSpeech textToSpeech;
    private YaicaMainActivity yaicaMain;

    private AnimationSet splashActivityAnimeSet;
    private Animation fadeOutAnime;
    private Animation fadeInAnime;

    private ImageView image_imageView;
    private TextView title_textView;
    private Typeface title_typeFace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exit_card_fragment_layout, container, false);

        startExitSequence(rootView);

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(THIS_CLASS_NAME, "onResume(): Error, This Language is not supported");
                    }
                    else {
                        new Handler().postDelayed (new Runnable() {
                            public void run() {
                                textToSpeech.speak(YaicaConstants.I_AM_EXITING_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        getActivity().finish();
                                    }
                                }, 3500);
                            }
                        }, 100);
                    }
                }
                else {
                    Log.e(THIS_CLASS_NAME, "onResume(): Error, TTS failed to initialize");
                }
            }
        });

        return rootView;
    }

    private void startExitSequence(View inRootView) {

        image_imageView = (ImageView) inRootView.findViewById(R.id.yaica_exit_imageView);
        title_textView = (TextView) inRootView.findViewById(R.id.yaica_exit_textView);
        title_typeFace = Typeface.createFromAsset(getActivity().getAssets(), YaicaConstants.GENERAL_COPY_FONT_PATH);
        title_textView.setTypeface(title_typeFace);

        fadeInAnime = new AlphaAnimation(0, 1);
        fadeInAnime.setInterpolator(new DecelerateInterpolator());
        fadeInAnime.setDuration(1000);

        fadeOutAnime = new AlphaAnimation(1, 0);
        fadeOutAnime.setInterpolator(new AccelerateInterpolator());
        fadeOutAnime.setStartOffset(1000);
        fadeOutAnime.setDuration(1000);

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
    public void onInit(int status) {

    }
}
