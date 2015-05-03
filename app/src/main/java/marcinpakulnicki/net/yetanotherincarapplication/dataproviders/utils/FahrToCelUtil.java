package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;


public class FahrToCelUtil {

    public static String celFromFahr (String inFahrString)  {
        int cel = ((Integer.parseInt(inFahrString) - 32)*5)/9;

        return String.valueOf(cel);
    }

}
