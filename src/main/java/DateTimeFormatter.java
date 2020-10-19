import org.joda.time.format.DateTimeFormat;

public class DateTimeFormatter {

    public static void main(String args[]){
        System.out.println(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime("2020-09-29").plusDays(1).minusSeconds(1));
    }

}
