package w.whateva.life2.api.email.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class LocalDateTimeXmlAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public String marshal(LocalDateTime localDateTime) throws Exception {
        return localDateTime.toString();
    }

    @Override
    public LocalDateTime unmarshal(String str) throws Exception {

        String dateStr = null;

        String[] parts = str.split("[-|T|Z]");
        if (parts.length == 4) {
            dateStr = parts[0] +
                    "-" +
                    (parts[1].length() == 1 ? "0" : "") + parts[1] +
                    "-" +
                    (parts[2].length() == 1 ? "0" : "") + parts[2] +
                    "T" +
                    (parts[3].length() == 7 ? "0" : "") + parts[3];
        }

        try {
            return LocalDateTime.parse(null != dateStr ? dateStr : str);
        } catch (Exception e) {
            //
        }
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(null != dateStr ? dateStr : str);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            //
        }

        System.out.println("Problem with date: " + str);

        return null;
    }
}