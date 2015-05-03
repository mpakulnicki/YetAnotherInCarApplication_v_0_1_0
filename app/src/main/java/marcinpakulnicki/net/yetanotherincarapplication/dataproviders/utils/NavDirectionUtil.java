package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

public class NavDirectionUtil {

    private static String navDirection;

    public static String processNavDirections (int inAzimut) {

        navDirection = new String();
        navDirection = " ";

        if (inAzimut > 0 && inAzimut < 45) {
            // north
            navDirection = "North  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 45 && inAzimut < 90) {
            // northeast
            navDirection = "Northeast  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 90 && inAzimut < 135) {
            // east
            navDirection = "East  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 135 && inAzimut < 180) {
            // south east
            navDirection = "Southeast  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 180 && inAzimut < 225) {
            // south
            navDirection = "South  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 225 && inAzimut < 270) {
            // south west
            navDirection = "Southwest  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 270 && inAzimut < 315) {
            // west
            navDirection = "West  " + String.valueOf(inAzimut);
        }
        if (inAzimut > 315 && inAzimut < 360) {
            // north west
            navDirection = "Northwest  " + String.valueOf(inAzimut);
        }
        return navDirection + "°C";
    }
}
