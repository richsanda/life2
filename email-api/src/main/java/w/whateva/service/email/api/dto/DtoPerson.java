package w.whateva.service.email.api.dto;

import java.util.List;

public class DtoPerson {

    private String name;
    private List<String> emails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCount() {
        return emails;
    }

    public void setCount(List<String> emails) {
        this.emails = emails;
    }
}
