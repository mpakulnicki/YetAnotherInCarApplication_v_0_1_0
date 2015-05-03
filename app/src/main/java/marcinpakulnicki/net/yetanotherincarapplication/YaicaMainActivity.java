package marcinpakulnicki.net.yetanotherincarapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;
import marcinpakulnicki.net.yetanotherincarapplication.events.FragmentGuiAlphaTweenEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEventListener;
import marcinpakulnicki.net.yetanotherincarapplication.fragments.ExitCardFragment;
import marcinpakulnicki.net.yetanotherincarapplication.fragments.GridCardFragment;
import marcinpakulnicki.net.yetanotherincarapplication.fragments.InfoCardFragment;
import marcinpakulnicki.net.yetanotherincarapplication.fragments.StartCardFragment;

public class YaicaMainActivity extends FragmentActivity implements YaicaEventListener{

    private static final String THIS_CLASS_NAME  = "YaicaMainActivity";

    private InfoCardFragment infoCardFragment;
    private StartCardFragment startCardFragment;
    private GridCardFragment shortcutsCardFragment;
    private ExitCardFragment exitCardFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private Intent recognizeIntent;
    private SpeechRecognizer speechRecognizer;
    private RecognitionListener recognitionListener;
    private FragmentGuiAlphaTweenEvent fragmentGuiAlphaTweenEvent;
    YaicaEventListener yaicaEventListener;
    private AudioManager audioManager;
    private TextToSpeech textToSpeech;

    private boolean isFragmentReady = false;
    private boolean isStartCardChosen = false;
    private boolean isGridCardChosen = false;
    private boolean isInfoCardChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yaica_main_activity_layout);
        Log.i(THIS_CLASS_NAME, "onCreate reached");

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, YaicaConstants.MUSIC_STREAM_VOLUME_LEVEL, 0);
    }

    private void addOrRemoveChosenCard(String inTask, Fragment inFragment) {

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (inTask.equals("add")) {
            fragmentTransaction.add(R.id.activity_yaica_core_prototype_main, inFragment);
        }

        if (inTask.equals("remove")) {
            fragmentTransaction.remove(inFragment);
        }
        fragmentTransaction.commit();
    }

    private void startFragment(String inName) {

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, YaicaConstants.MUSIC_STREAM_VOLUME_LEVEL, 0);

        switch (inName) {
            case YaicaConstants.START_CARD:

                startCardFragment = new StartCardFragment();
                addOrRemoveChosenCard("add",startCardFragment);
                isStartCardChosen = true;

                break;

            case YaicaConstants.SHORTCUTS_CARD:

                shortcutsCardFragment = new GridCardFragment();
                isGridCardChosen = true;

                if (isStartCardChosen == true) {
                    isStartCardChosen = false;

                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.START_CARD);
                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.SHORTCUTS_CARD);

                    try {
                        startCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                        infoCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                    } catch (Exception e) {
                        Log.e("Exception : ", e.getStackTrace().toString());
                    }
                }

                if (isInfoCardChosen == true) {
                    isInfoCardChosen = false;

                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.INFO_CARD);
                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.SHORTCUTS_CARD);

                    try {

                        infoCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                    } catch (Exception e) {
                        Log.e("Exception : ", e.getStackTrace().toString());
                    }
                }

                break;

            case YaicaConstants.INFO_CARD:
                infoCardFragment = new InfoCardFragment();
                isInfoCardChosen = true;

                if (isStartCardChosen == true) {
                    isStartCardChosen = false;

                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.START_CARD);
                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.INFO_CARD);

                    try {
                        startCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                    } catch (Exception e) {
                        Log.e("Exception : ", e.getStackTrace().toString());
                    }
                }

                if (isGridCardChosen == true) {
                    isGridCardChosen = false;

                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.SHORTCUTS_CARD);
                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.INFO_CARD);

                    try {
                        shortcutsCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                    } catch (Exception e) {
                        Log.e("Exception : ", e.getStackTrace().toString());
                    }
                }

                break;

            case YaicaConstants.EXIT_CARD:

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(YaicaMainActivity.this);
                alertDialog.setTitle(getApplicationContext().getResources().getString(R.string.exiting_yaica_dialog_title));
                alertDialog.setMessage(getApplicationContext().getResources().getString(R.string.exit_yaica_text));

                alertDialog.setPositiveButton(getResources().getString(R.string.exiting_yaica_dialog_button_text_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (isGridCardChosen == true) {
                                    isGridCardChosen = false;

                                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.SHORTCUTS_CARD);
                                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.EXIT_CARD);

                                    try {
                                        shortcutsCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                                    } catch (Exception e) {
                                        Log.e("Exception : ", e.getStackTrace().toString());
                                    }
                                }

                                if (isInfoCardChosen == true) {
                                    isInfoCardChosen = false;

                                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.INFO_CARD);
                                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.EXIT_CARD);

                                    try {
                                        infoCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                                    } catch (Exception e) {
                                        Log.e("Exception : ", e.getStackTrace().toString());
                                    }
                                }

                                if (isStartCardChosen == true) {
                                    isStartCardChosen = false;

                                    fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
                                    fragmentGuiAlphaTweenEvent.setFadeOutCardName(YaicaConstants.START_CARD);
                                    fragmentGuiAlphaTweenEvent.setFadeInCardName(YaicaConstants.EXIT_CARD);

                                    try {
                                        startCardFragment.receiveEvent(fragmentGuiAlphaTweenEvent);
                                    } catch (Exception e) {
                                        Log.e("Exception : ", e.getStackTrace().toString());
                                    }
                                }

                                dialog.cancel();
                            }
                    });

                alertDialog.setNegativeButton(getResources().getString(R.string.exiting_yaica_dialog_button_text_cancel),

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

                break;
            default:
        }
    }

    private void startVoiceRecognition () {
        recognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.domain.app");

        if(isFragmentReady == true) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.getApplicationContext());
        recognitionListener = new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {

                ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (result == null) {
                    Log.e(THIS_CLASS_NAME, "No results");
                } else {
                    Log.d(THIS_CLASS_NAME, "Printing recordings: ");
                    for (String match : result) {
                        Log.d(THIS_CLASS_NAME, match);

                        switch (match) {
                            case "info":
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, YaicaConstants.MUSIC_STREAM_VOLUME_LEVEL, 0);
                                startFragment(YaicaConstants.INFO_CARD);
                                break;
                            case "shortcuts":
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, YaicaConstants.MUSIC_STREAM_VOLUME_LEVEL, 0);
                                startFragment(YaicaConstants.SHORTCUTS_CARD);
                                break;
                            case "exit":

                                startFragment(YaicaConstants.EXIT_CARD);
                                break;

                            default:
                                isFragmentReady = false;
                                break;
                        }
                    }
                }
            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(THIS_CLASS_NAME, "Ready for speech");
            }

            @Override
            public void onError(int error) {
                Log.d(THIS_CLASS_NAME, "Error listening for speech: " + error);

                recognizeIntent = null;
                speechRecognizer = null;
                recognitionListener = null;
                startVoiceRecognition();
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(THIS_CLASS_NAME, "Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(THIS_CLASS_NAME, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(THIS_CLASS_NAME, "onEndOfSpeech");
                startVoiceRecognition();
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(THIS_CLASS_NAME, "onEvent");
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(THIS_CLASS_NAME, "onPartialResults");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }
        };

        speechRecognizer.setRecognitionListener(recognitionListener);
        speechRecognizer.startListening(recognizeIntent);
    }

    public void openAudio() {
        isFragmentReady = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (shortcutsCardFragment != null) {
            shortcutsCardFragment.hideShortcutGrid();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

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
                                textToSpeech.speak(YaicaConstants.I_AM_READY_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        isFragmentReady = true;
                                        startVoiceRecognition();
                                        startFragment(YaicaConstants.START_CARD);
                                        isStartCardChosen = true;

                                    }
                                }, 1000);

                            }
                        }, 100);
                    }
                }
                else {
                    Log.e(THIS_CLASS_NAME, "onResume(): Error, TTS failed to initialize");
                    showTTSErrorDialog();
                }
            }
        });
    }

    public void showTTSErrorDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(YaicaMainActivity.this);
        alertDialog.setTitle(getApplicationContext().getResources().getString(R.string.tts_failed_to_initialize_title));
        alertDialog.setMessage(getApplicationContext().getResources().getString(R.string.tts_failed_to_initialize));

        alertDialog.setPositiveButton(getResources().getString(R.string.tts_failed_to_initialize_button_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void receiveEvent(YaicaEvent yaicaEvent) throws Exception {

        if (yaicaEvent instanceof FragmentGuiAlphaTweenEvent) {

            switch (yaicaEvent.getFadeInCardName()) {
                case YaicaConstants.INFO_CARD:
                    addOrRemoveChosenCard("add",infoCardFragment);

                    break;
                case YaicaConstants.START_CARD:
                    addOrRemoveChosenCard("add", startCardFragment);
                    break;

                case YaicaConstants.SHORTCUTS_CARD:
                    addOrRemoveChosenCard("add", shortcutsCardFragment);
                    break;

                case YaicaConstants.EXIT_CARD:
                    exitCardFragment = new ExitCardFragment();
                    addOrRemoveChosenCard("add", exitCardFragment);
                    break;

                default:
                    break;
            }

        }
    }
}
