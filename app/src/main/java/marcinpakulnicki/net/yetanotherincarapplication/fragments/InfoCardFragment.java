package marcinpakulnicki.net.yetanotherincarapplication.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import marcinpakulnicki.net.yetanotherincarapplication.R;
import marcinpakulnicki.net.yetanotherincarapplication.YaicaMainActivity;
import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.MpsToKmhUtil;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.WeatherApiUtil;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.NavDirectionUtil;
import marcinpakulnicki.net.yetanotherincarapplication.events.FragmentGuiAlphaTweenEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEvent;
import marcinpakulnicki.net.yetanotherincarapplication.events.YaicaEventListener;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.DateAndTimeUtil;
import marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils.TypeFaceUtil;

public class InfoCardFragment extends Fragment implements SensorEventListener, LocationListener,TextToSpeech.OnInitListener, YaicaEventListener, View.OnClickListener {

    private static final String THIS_CLASS_NAME  = "InfoCardFragment";

    private TextView time_textView;
    private TextView compass_textView;
    private TextView battery_textView;
    private TextView geoloc_textView;
    private TextView weather_textView;
    private TextView speedometer_textView;
    private TextView speedometer_saved_textView;
    private TextView speedometer_set_TextView;
    private TextView speedometer_clear_TextView;
    private RelativeLayout infocard_layout;

    private String currentCityName = new String();
    private String currentCountryName = new String();
    private int savedMaxSpeed = 200;

    private FragmentGuiAlphaTweenEvent fragmentGuiAlphaTweenEvent;

    private SensorManager sensorManager;
    private LocationManager locationManager;
    private BroadcastReceiver batInfoReceiver;
    private ValueAnimator colorTweenAnime;
    private Animation fadeOutAllAnime;
    private AnimationSet infoCardFadeOutAnimeSet;
    private SharedPreferences savedMaxPrefs;
    private SharedPreferences.Editor savedMaxPrefsEditor;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private float[] gravity;
    private float[] geoMagnetic;

    private TextToSpeech textToSpeech;
    private YaicaMainActivity yaicaMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info_card_fragment_layout, container, false);

        infocard_layout = (RelativeLayout) rootView.findViewById(R.id.info_card_fragment_layout);
        time_textView = (TextView) rootView.findViewById(R.id.info_card_time_text);
        time_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        compass_textView = (TextView) rootView.findViewById(R.id.info_card_compass_text);
        compass_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        battery_textView = (TextView) rootView.findViewById(R.id.info_card_battery_text);
        battery_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        geoloc_textView = (TextView) rootView.findViewById(R.id.info_card_geolocation_text);
        geoloc_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        weather_textView = (TextView) rootView.findViewById(R.id.info_card_weather_text);
        weather_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        speedometer_textView = (TextView) rootView.findViewById(R.id.speedometer_text);
        speedometer_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        speedometer_saved_textView = (TextView) rootView.findViewById(R.id.speedometer_saved_text);
        speedometer_saved_textView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        speedometer_set_TextView = (TextView) rootView.findViewById(R.id.speedometer_set_speed_image);
        speedometer_clear_TextView = (TextView) rootView.findViewById(R.id.speedometer_clear_speed_image);
        speedometer_set_TextView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        speedometer_clear_TextView.setTypeface(TypeFaceUtil.provideGlobalTypeFace(getActivity()));
        speedometer_set_TextView.setOnClickListener(this);
        speedometer_clear_TextView.setOnClickListener(this);

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
                                textToSpeech.speak(YaicaConstants.INFO_LOADED_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);

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

        retrieveSharedPrefs();
        showSystemTime();
        showCompass();
        showBattery();
        setLocationListeners();

        return rootView;
    }

    @Override
    public void onClick(View inView) {
        switch (inView.getId()) {
            case R.id.speedometer_set_speed_image:
                showSetSpeedDialog();
                break;

            case R.id.speedometer_clear_speed_image:
                clearSavedMaxSpeed();
                break;

            default:
        }
    }

    private void retrieveSharedPrefs () {
        // retrieve max speed
        savedMaxPrefs = getActivity().getSharedPreferences("savedMaxPrefs", 0);

        Map<String, ?> keys = savedMaxPrefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().equals("savedMaxSpeed") && entry.getValue() != null ) {
                savedMaxSpeed = (Integer) entry.getValue();
                speedometer_saved_textView.setText(String.valueOf(entry.getValue()) + " km/h");
            }
        }
    }

    private void setLocationListeners() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
        this.onLocationChanged(null);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        this.onLocationChanged(location);
        showCurrentLocation(location);

    }

    private void showCurrentLocation(Location inLocation) {
        if (inLocation != null) {
            try {
                double lat = inLocation.getLatitude();
                double lng = inLocation.getLongitude();
                Geocoder gc = new Geocoder(getActivity(), Locale.getDefault());

                List<Address> addresses =  gc.getFromLocation(lat, lng, 1);
                StringBuilder sb = new StringBuilder();

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getCountryName());
                    currentCityName = address.getLocality();
                    currentCountryName = address.getCountryName();
                }

                geoloc_textView.setText(getActivity().getApplicationContext().getResources().getString(R.string.info_card_location_text) + " " + sb.toString());
            } catch (IOException e) {
                geoloc_textView.setText(YaicaConstants.NO_LOCATION_FOUND_ERROR);
            }
        }
    }

    private void showSpeed(Location inLocation) {
        float currSpeed = inLocation.getSpeed();
        speedometer_textView.setText(MpsToKmhUtil.convertMpsToKmh(currSpeed));

        if (currSpeed > savedMaxSpeed) {
            loopSpeedColorTween(R.color.km_black, R.color.km_red);
        } else {
            stopSpeedColorTween();
        }
    }

    private void loopSpeedColorTween(int inColorFrom, int inColorTo) {

        Integer colorFrom = getResources().getColor(inColorFrom);
        Integer colorTo = getResources().getColor(inColorTo);

        colorTweenAnime = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorTweenAnime.setDuration(YaicaConstants.SPEEDOMETER_COLOR_TWEEN_TIME);
        colorTweenAnime.setRepeatCount(Animation.INFINITE);
        colorTweenAnime.setRepeatMode(Animation.REVERSE);

        colorTweenAnime.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                speedometer_textView.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorTweenAnime.start();
    }

    private void stopSpeedColorTween() {
        if(colorTweenAnime != null) {
            colorTweenAnime.end();
            speedometer_textView.setBackgroundColor(getResources().getColor(R.color.km_white));
        }
    }

    private void showSetSpeedDialog () {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getActivity().getApplicationContext().getResources().getString(R.string.speedometer_dialog_title));

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(3);

        final EditText maxSpeedInputText = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        maxSpeedInputText.setLayoutParams(lp);
        maxSpeedInputText.setFilters(filterArray);
        maxSpeedInputText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        alertDialog.setView(maxSpeedInputText);

        alertDialog.setPositiveButton(getResources().getString(R.string.speedometer_dialog_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!maxSpeedInputText.getText().toString().equals("")) {
                            savedMaxSpeed = Integer.valueOf(maxSpeedInputText.getText().toString());
                            savedMaxPrefs = getActivity().getApplicationContext().getSharedPreferences("savedMaxPrefs", Context.MODE_PRIVATE);
                            savedMaxPrefsEditor = savedMaxPrefs.edit();
                            savedMaxPrefsEditor.putInt("savedMaxSpeed", savedMaxSpeed);
                            savedMaxPrefsEditor.commit();

                            speedometer_saved_textView.setText(String.valueOf(savedMaxSpeed) + " km/h");
                        }
                    }
                });

        alertDialog.setNegativeButton(getResources().getString(R.string.speedometer_dialog_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void clearSavedMaxSpeed() {
        speedometer_saved_textView.setText("0 km/h");
        savedMaxSpeed = 200;

        if (savedMaxPrefsEditor != null) {
            savedMaxPrefsEditor.clear();
            savedMaxPrefsEditor.commit();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getResources().getString(R.string.speedometer_dialog_clear_title));
            alertDialog.setMessage(getResources().getString(R.string.speedometer_dialog_clear_message));

            alertDialog.setPositiveButton(getResources().getString(R.string.speedometer_dialog_got_it),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });


            alertDialog.show();
        }
    }

    private void showBattery () {
        batInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent intent) {

                int level = intent.getIntExtra("level", 0);
                battery_textView.setText(getActivity().getApplicationContext().getResources().getString(R.string.info_card_battery_text) + " " + String.valueOf(level) + "%");
            }
        };

        getActivity().registerReceiver(this.batInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void showSystemTime() {

        time_textView.setText(DateAndTimeUtil.provideFormattedDate());

        Thread timerThread = new Thread() { //new thread
            public void run() {
                Boolean b = true;
                try {
                    do {
                        sleep(YaicaConstants.TIME_THREAD_REFRESH_RATE); // 10 secs

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                time_textView.setText(DateAndTimeUtil.provideFormattedDate());
                            }
                        });
                    }
                    while (b == true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                }
            };
        };
        timerThread.start();
    }

    @Override
    public void onInit(int status) {

    }

    private void showCompass () {

        sensorManager = (SensorManager)getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            geoloc_textView.setText(YaicaConstants.NO_LOCATION_FOUND_ERROR);
            speedometer_textView.setText(getActivity().getApplicationContext().getResources().getString(R.string.default_speedometer_text));
            weather_textView.setText(YaicaConstants.NO_LOCATION_FOUND_ERROR);
        } else {
            showCurrentLocation(location);
            showSpeed(location);
            showCurrentWeather(WeatherApiUtil.composeServiceEndpoint(YaicaConstants.YAHOO_WEATHER_API_REST_URL, currentCityName, currentCountryName));
        }
    }

    private void showCurrentWeather(String inEndpoint) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            weather_textView.setText(WeatherApiUtil.parseApiResult(new URL(inEndpoint)));
        } catch (IOException e) {
            weather_textView.setText(YaicaConstants.DEFAULT_ERROR + ": " + e.getMessage());
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geoMagnetic = event.values.clone();
        if (gravity != null && geoMagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geoMagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                //azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                int azimut = (int) Math.round(Math.toDegrees(orientation[0]));

                if (azimut < 0.0f) {
                    azimut += 360.0f;
                }
                compass_textView.setText(NavDirectionUtil.processNavDirections(azimut));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void receiveEvent(YaicaEvent yaicaEvent) throws Exception {
        if (yaicaEvent instanceof FragmentGuiAlphaTweenEvent && yaicaEvent.getFadeOutCardName().equals(YaicaConstants.INFO_CARD)) {
            startFadeOutAnime(yaicaEvent.getFadeInCardName());
        }
    }

    private void startFadeOutAnime(final String inFadeInCardName) {

        fadeOutAllAnime = new AlphaAnimation(1, 0);
        fadeOutAllAnime.setInterpolator(new DecelerateInterpolator());
        fadeOutAllAnime.setDuration(1000);

        infoCardFadeOutAnimeSet = new AnimationSet(false);
        infoCardFadeOutAnimeSet.addAnimation(fadeOutAllAnime);
        infocard_layout.setAnimation(infoCardFadeOutAnimeSet);
        infocard_layout.setVisibility(View.INVISIBLE);

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
}
