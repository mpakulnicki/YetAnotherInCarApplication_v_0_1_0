package marcinpakulnicki.net.yetanotherincarapplication.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import marcinpakulnicki.net.yetanotherincarapplication.R;
import marcinpakulnicki.net.yetanotherincarapplication.YaicaMainActivity;
import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;
import marcinpakulnicki.net.yetanotherincarapplication.events.FragmentGuiAlphaTweenEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEventListener;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.TypeFaceUtil;

public class StartCardFragment extends Fragment implements TextToSpeech.OnInitListener, YaicaEventListener {

    private static final String THIS_CLASS_NAME  = "StartCardFragment";

    private AnimationSet splashActivitySetTitle1Anime;
    private AnimationSet splashActivitySetTitle2Anime;
    private AnimationSet splashActivityAnimeSetCardNames;
    private AnimationSet startCardFadeOutAnimeSet;
    private Animation fadeInTitle2Anime;
    private Animation fadeInTitle1Anime;
    private Animation fadeInCardsAnime;
    private Animation fadeOutAllAnime;
    private TextView title1_textView;
    private TextView title2_textView;
    private TextView info_textView;
    private TextView shortcuts_textView;
    private TextView exit_textView;
    private LinearLayout cardsTextLinearLayout;
    private LinearLayout allTextLinearLayout;
    private FragmentGuiAlphaTweenEvent fragmentGuiAlphaTweenEvent;

    private TextToSpeech textToSpeech;
    private YaicaMainActivity yaicaMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.start_card_fragment_layout, container, false);

        title1_textView = (TextView) rootView.findViewById(R.id.start_card_title_text_1);
        title2_textView = (TextView) rootView.findViewById(R.id.start_card_title_text_2);
        info_textView = (TextView) rootView.findViewById(R.id.start_card_title_info);
        shortcuts_textView = (TextView) rootView.findViewById(R.id.start_card_title_shortcuts);
        exit_textView = (TextView) rootView.findViewById(R.id.start_card_title_exit);
        cardsTextLinearLayout = (LinearLayout) rootView.findViewById(R.id.start_card_cards_text_layout);
        allTextLinearLayout = (LinearLayout) rootView.findViewById(R.id.yaica_start_card_anime_layout);

        title1_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        title2_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        info_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        shortcuts_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        exit_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));

        startFadeInAnime();

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int ttsStatus) {
                if (ttsStatus == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.US);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(THIS_CLASS_NAME, "onResume(): Error, This Language is not supported");
                    }
                    else {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                textToSpeech.speak(YaicaConstants.START_LOADED_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        yaicaMain = (YaicaMainActivity) getActivity();
                                        yaicaMain.openAudio();

                                    }
                                }, 200);

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


    private void startFadeInAnime() {
        fadeInTitle1Anime = new AlphaAnimation(0, 1);
        fadeInTitle1Anime.setInterpolator(new DecelerateInterpolator());
        fadeInTitle1Anime.setDuration(700);

        fadeInTitle2Anime = new AlphaAnimation(0, 1);
        fadeInTitle2Anime.setInterpolator(new DecelerateInterpolator());
        fadeInTitle2Anime.setStartOffset(600);
        fadeInTitle2Anime.setDuration(700);

        fadeInCardsAnime = new AlphaAnimation(0, 1);
        fadeInCardsAnime.setInterpolator(new DecelerateInterpolator());
        fadeInCardsAnime.setStartOffset(1500);
        fadeInCardsAnime.setDuration(700);

        splashActivitySetTitle1Anime = new AnimationSet(false);
        splashActivitySetTitle2Anime = new AnimationSet(false);
        splashActivityAnimeSetCardNames = new AnimationSet(false);
        splashActivitySetTitle1Anime.addAnimation(fadeInTitle1Anime);
        splashActivitySetTitle2Anime.addAnimation(fadeInTitle2Anime);
        splashActivityAnimeSetCardNames.addAnimation(fadeInCardsAnime);
        title1_textView.setAnimation(splashActivitySetTitle1Anime);
        title2_textView.setAnimation(splashActivitySetTitle2Anime);
        cardsTextLinearLayout.setAnimation(splashActivityAnimeSetCardNames);

    }

    private void startFadeOutAnime(final String inFadeInCardName) {

        fadeOutAllAnime = new AlphaAnimation(1, 0);
        fadeOutAllAnime.setInterpolator(new DecelerateInterpolator());
        fadeOutAllAnime.setDuration(1000);

        startCardFadeOutAnimeSet = new AnimationSet(false);
        startCardFadeOutAnimeSet.addAnimation(fadeOutAllAnime);
        allTextLinearLayout.setAnimation(startCardFadeOutAnimeSet);
        allTextLinearLayout.setVisibility(View.INVISIBLE);

        fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
        fragmentGuiAlphaTweenEvent.setFadeInCardName(inFadeInCardName);

        /*
            Due to bug in Android os, onAnimationEnd is never called. See :
            http://stackoverflow.com/questions/4750939/android-animation-is-not-finished-in-onanimationend

             Used Handler and Runnable instead
         */

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    yaicaMain.receiveEvent(fragmentGuiAlphaTweenEvent);
                } catch (Exception e) {
                    Log.e("Exception : ", e.getStackTrace().toString());
                }
            }
        }, 2000);

//        fadeOutAllAnime.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation arg0) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
//                fragmentGuiAlphaTweenEvent.setFadeInCardName(inFadeInCardName);
//
//                try {
//                    Log.d("bang3! : ", inFadeInCardName);
//                    yaicaMain.receiveEvent(fragmentGuiAlphaTweenEvent);
//                } catch (Exception e) {
//                    Log.e("FragmentGuiAlphaTweenEvent Exception : ", e.getStackTrace().toString());
//                }
//            }
//        });
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void receiveEvent(YaicaEvent yaicaEvent) throws Exception {
        if (yaicaEvent instanceof FragmentGuiAlphaTweenEvent && yaicaEvent.getFadeOutCardName().equals(YaicaConstants.START_CARD)) {
            startFadeOutAnime(yaicaEvent.getFadeInCardName());
        }
    }
}
