package marcinpakulnicki.net.yetanotherincarapplication.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import marcinpakulnicki.net.yetanotherincarapplication.R;
import marcinpakulnicki.net.yetanotherincarapplication.YaicaMainActivity;
import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.InstalledAppsListViewUtil;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.InstalledAppsUtil;
import marcinpakulnicki.net.yetanotherincarapplication.events.FragmentGuiAlphaTweenEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEventListener;
import marcinpakulnicki.net.yetanotherincarapplication.model.InstalledApp;


public class GridCardFragment extends Fragment implements TextToSpeech.OnInitListener, YaicaEventListener{

    private static final String THIS_CLASS_NAME  = "GridCardFragment";

    private LinearLayout gridcard_layout;
    private ViewGroup appShortcutGridView;
    private Dialog isntalledAppsDialog;
    private LayoutInflater shortcutGridLayoutInflater;
    private Animation fadeOutAllAnime;
    private AnimationSet gridCardFadeOutAnimeSet;

    private PackageManager packageManager;
    private SharedPreferences appShortcutPrefs;
    private SharedPreferences.Editor appShortcutsPrefsEditor;

    private List<InstalledApp> installedApps;
    private List<ViewGroup> appButtonsList = new ArrayList<ViewGroup>();
    private HashMap<Integer, Intent> buttonsPrefsSavedMap = new HashMap<Integer, Intent>();

    private int numOfshortcutInGrid = 4;

    private FragmentGuiAlphaTweenEvent fragmentGuiAlphaTweenEvent;

    private TextToSpeech textToSpeech;
    private YaicaMainActivity yaicaMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_card_fragment_layout, container, false);

        gridcard_layout = (LinearLayout) rootView.findViewById(R.id.grid_card_fragment_layout);

        shortcutGridLayoutInflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appShortcutGridView = (ViewGroup) shortcutGridLayoutInflater.inflate(R.layout.app_shortcut_grid_view, null, true);
        getActivity().addContentView(appShortcutGridView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        appShortcutGridView.setX(20);
        appShortcutGridView.setY(40);

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
                                textToSpeech.speak(YaicaConstants.GRID_LOADED_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);

                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        yaicaMain = (YaicaMainActivity) getActivity();
                                        yaicaMain.openAudio();

                                    }
                                }, 4000);

                            }
                        }, 100);
                    }
                }
                else {
                    Log.e(THIS_CLASS_NAME, "onResume(): Error, TTS failed to initialize");
                }
            }
        });


        setShortcutsGrid();
        retrieveSharedPrefs();

        return rootView;
    }


    private void retrieveSharedPrefs() {

        // retrieve shortcuts
        installedApps = InstalledAppsUtil.fetchAppsList(getActivity().getApplicationContext());

        for (int i = 0; i < numOfshortcutInGrid; i++) {
            ImageView iv = (ImageView) appButtonsList.get(i).getChildAt(0);
            TextView tv = (TextView) appButtonsList.get(i).getChildAt(1);
            tv.setTextColor(getActivity().getResources().getColor(R.color.yaica_white));
            tv.setText(YaicaConstants.LONG_PRESS_TO_SELECT_BUTTON_MESSAGE);

            appShortcutPrefs = getActivity().getSharedPreferences("button"+i+"Prefs", 0);

            // check if there are prefs settings for the buttons, if so set background image, set intent address, set name
            Map<String,?> keys = appShortcutPrefs.getAll();

            if (keys.size() > 0){

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    if (entry.getKey().equals("appName") && entry.getValue() != null ) {

                        for (InstalledApp ia: installedApps) {
                            if (ia.getAppName().equals(entry.getValue().toString())) {
                                iv.setImageDrawable(ia.getAppIcon());
                                buttonsPrefsSavedMap.put(i, getActivity().getPackageManager().getLaunchIntentForPackage(ia.getActivityPath()));
                                tv.setText(entry.getValue().toString());
                            }
                        }
                    } else {
                        // if I cannot find slot, this app may be deleted
                        iv.setBackgroundResource(R.drawable.blue_btn_bg);
                        //tv.setText(YaicaConstants.NO_SHORTCUT_SELECTED_MESSAGE);
                        Toast.makeText(getActivity().getApplicationContext(),
                                YaicaConstants.NO_SHORTCUT_SELECTED_MESSAGE + " on slot " + (i + 1) + ": Maybe it is deleted from your device ?", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void setShortcutsGrid() {

        for (int i = 0; i < numOfshortcutInGrid; i++) {

            ViewGroup appShortcutView = (ViewGroup) shortcutGridLayoutInflater.inflate(R.layout.app_shortcut_button_layout, null, true);
            final TextView txtTitle = new TextView(getActivity());
            ImageView imageView = new ImageView(getActivity());

            imageView.setBackgroundResource(R.drawable.blue_btn_bg);
            imageView.setId(i);
            imageView.setOnLongClickListener(OnLongClickListener);
            imageView.setOnClickListener(OnClickListener);

            appShortcutView.setId(i);
            appShortcutView.addView(imageView);
            appShortcutView.addView(txtTitle);
            appButtonsList.add(appShortcutView);

            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(100, 100);
            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 70);
            txtTitle.setLayoutParams(txtParams);
            imageView.setLayoutParams(imgParams);
            appShortcutGridView.addView(appShortcutView);

        }
    }

    private View.OnClickListener OnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            startChosenActivity(view.getId());
        }
    };

    private View.OnLongClickListener OnLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            showInstalledApps(v.getId());
            return true;
        }
    };

    private void startChosenActivity (int inViewId) {
        Intent appIntent = buttonsPrefsSavedMap.get(inViewId);
        if (appIntent != null) {
            appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(appIntent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                    YaicaConstants.NO_SHORTCUT_SELECTED_MESSAGE + " on slot " + (inViewId + 1), Toast.LENGTH_LONG).show();
        }
    }

    private void showInstalledApps(final int inViewId) {

        final List<String> appIntentNames = new ArrayList<String>();
        final List<Drawable> appIcons = new ArrayList<Drawable>();
        final List<String> appNames = new ArrayList<String>();

        for (InstalledApp ia: installedApps) {
            appIcons.add(ia.getAppIcon());
            appNames.add(ia.getAppName());
            appIntentNames.add(ia.getActivityPath());
        }

        InstalledAppsListViewUtil adapter = new InstalledAppsListViewUtil(getActivity(), appNames, appIcons);

        ListView listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        isntalledAppsDialog = new Dialog(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.grid_card_my_installed_apps_text);

        listView.setAdapter(adapter);

        builder.setView(listView);
        isntalledAppsDialog = builder.create();
        isntalledAppsDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                packageManager = getActivity().getPackageManager();
                ImageView iv = (ImageView) appButtonsList.get(inViewId).getChildAt(0);
                TextView tv = (TextView) appButtonsList.get(inViewId).getChildAt(1);

                appShortcutPrefs = getActivity().getApplicationContext().getSharedPreferences("button"+inViewId+"Prefs", Context.MODE_PRIVATE);
                appShortcutsPrefsEditor = appShortcutPrefs.edit();

                if (appNames.get(position).equals(YaicaConstants.CLEAR_SHORTCUT_MESSAGE)) {
                    appShortcutsPrefsEditor.clear();
                    appShortcutsPrefsEditor.commit();
                    buttonsPrefsSavedMap.remove(inViewId);

                    iv.setImageDrawable(null);
                    tv.setText(YaicaConstants.LONG_PRESS_TO_SELECT_BUTTON_MESSAGE);

                } else {
                    iv.setImageDrawable(appIcons.get(position));
                    tv.setText(appNames.get(position));

                    Map<String, Object> nameIcons = new HashMap<String, Object>();
                    nameIcons.put("appName", appNames.get(position));

                    for (String s : nameIcons.keySet()) {
                        appShortcutsPrefsEditor.putString(s, (String) nameIcons.get(s));
                    }
                    appShortcutsPrefsEditor.commit();

                    buttonsPrefsSavedMap.put(inViewId, packageManager.getLaunchIntentForPackage(appIntentNames.get(position)));
                }
                isntalledAppsDialog.dismiss();
            }
        });
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void receiveEvent(YaicaEvent yaicaEvent) throws Exception {
        if (yaicaEvent instanceof FragmentGuiAlphaTweenEvent && yaicaEvent.getFadeOutCardName().equals(YaicaConstants.SHORTCUTS_CARD)) {
            startFadeOutAnime(yaicaEvent.getFadeInCardName());
        }
    }

    private void startFadeOutAnime(final String inFadeInCardName) {

        fadeOutAllAnime = new AlphaAnimation(1, 0);
        fadeOutAllAnime.setInterpolator(new DecelerateInterpolator());
        fadeOutAllAnime.setDuration(1000);

        gridCardFadeOutAnimeSet = new AnimationSet(false);
        gridCardFadeOutAnimeSet.addAnimation(fadeOutAllAnime);
        gridcard_layout.setAnimation(gridCardFadeOutAnimeSet);
        gridcard_layout.setVisibility(View.INVISIBLE);
        appShortcutGridView.setAnimation(gridCardFadeOutAnimeSet);
        appShortcutGridView.setVisibility(View.INVISIBLE);

        fragmentGuiAlphaTweenEvent = new FragmentGuiAlphaTweenEvent();
        fragmentGuiAlphaTweenEvent.setFadeInCardName(inFadeInCardName);

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
    }

    // TO DO : make this an event
    public void hideShortcutGrid() {
        if (appShortcutGridView != null) {
            appShortcutGridView.setVisibility(View.INVISIBLE);
        }
    }
}
