import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateFormatter {

    public static void main(String args[]) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime startDateTime = dateTimeFormatter.parseDateTime("2020-01");
//        DateTime endDateTime = dateTimeFormatter.parseDateTime(endDate)

    }


}
