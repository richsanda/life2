package w.whateva.life2.integration.email.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class EmailConfiguration {

    private String url;
    private Map<String, List<String>> troves;
}
