package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

public class MpsToKmhUtil {

    public static String convertMpsToKmh (float inMps) {
        final float kmh = ((inMps * 3600 ) / 1000 );
        //final String kmhStr = String.valueOf(kmh) + " km/h";
        String kmhStr = String.format("%.1f", kmh);

        return kmhStr + " km/h";
    }
}
