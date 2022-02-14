package w.whateva.life2.service.note.impl;

import org.junit.Test;
import w.whateva.life2.integration.note.NoteUtil;

public class NoteUtilTest {

    private static final String basicNoteText = "@[receipt](artifact:receipt)\n@[where:](field:where) #fleet-bank-stop-shop-pembroke\n@[when:](field:when) 9.22.00\n@[withdrawal:](field:withdrawal) 1727.08\nclose account";

    @Test
    public void parseNoteText() {
        NoteUtil.fields(basicNoteText).forEach((key, value) -> System.out.println(key + "=" + value));
    }
}
