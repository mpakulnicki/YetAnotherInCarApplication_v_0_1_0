package marcinpakulnicki.net.yetanotherincarapplication.dataproviders.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateAndTimeUtil {

    private static String timeAndDate;
    private static SimpleDateFormat dateFormat;

    public static String provideFormattedDate() {
        dateFormat = new SimpleDateFormat("cccc dd LLLL yyyy HH:mm");
        timeAndDate = dateFormat.format(Calendar.getInstance().getTime());
        return timeAndDate;
    }

}
