package marcinpakulnicki.net.yetanotherincarapplication.constants;

public class YaicaConstants {

    public static final int MUSIC_STREAM_VOLUME_LEVEL = 10;
    public static final int TIME_THREAD_REFRESH_RATE = 10000;
    public static final int SPEEDOMETER_COLOR_TWEEN_TIME = 1000; // in milliseconds

    public static final String YAHOO_WEATHER_API_REST_URL = "https://query.yahooapis.com/v1/public/yql?q=select";
    public static final String GENERAL_COPY_FONT_PATH = "fonts/DroidSansHebrew.ttf";

    // CARD NAMES
    public static final String START_CARD = "start";
    public static final String INFO_CARD = "info";
    public static final String EXIT_CARD = "exit";
    public static final String MAPS_CARD = "maps";
    public static final String SHORTCUTS_CARD = "shortcuts";

    // TEXT TO SPEECH MESSAGES
    public static final String I_AM_READY_MESSAGE = "i am ready";
    public static final String START_LOADED_MESSAGE = "start loaded";
    public static final String INFO_LOADED_MESSAGE = "info loaded";
    public static final String GRID_LOADED_MESSAGE = "short cuts loaded";
    public static final String I_AM_EXITING_MESSAGE = "i am exiting";

    // ERROR MESSAGES
    public static final String TTS_FAILED_TO_INITIALIZE_TITLE = "Text To Speech error";
    public static final String TTS_FAILED_TO_INITIALIZE = "Oops, Text To Speech fails to start, this app will now close after tapping on OK..";
    public static final String DEFAULT_ERROR = "Something went wrong..";
    public static final String PACKAGE_NOT_FOUND_ERROR = "Error, I can't seem to find this application..";
    public static final String NO_LOCATION_FOUND_ERROR = "Error, no location found";

    //OTHER MESSAGES
    public static final String NO_SHORTCUT_SELECTED_MESSAGE = "No shortcut";
    public static final String LONG_PRESS_TO_SELECT_BUTTON_MESSAGE = "Long press to choose app";
    public static final String CLEAR_SHORTCUT_MESSAGE = "CLEAR SHORTCUT";
}
