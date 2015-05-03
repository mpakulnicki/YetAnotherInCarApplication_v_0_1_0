package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

import android.app.Activity;
import android.graphics.Typeface;

import marcinpakulnicki.net.yetanotherincarapplication.constants.YaicaConstants;

public class TypeFaceUtil {

    public static Typeface provideGlobalTypeFace (Activity inActivity) {

        Typeface tf = Typeface.createFromAsset(inActivity.getAssets(), YaicaConstants.GENERAL_COPY_FONT_PATH);
        return tf;
    }
}
