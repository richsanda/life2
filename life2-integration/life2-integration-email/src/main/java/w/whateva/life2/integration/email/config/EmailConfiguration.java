package w.whateva.life2.integration.email.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class EmailConfiguration {

    private String url;
    private Set<String> troves; // TODO: these troves need owners... so need some structure
}
