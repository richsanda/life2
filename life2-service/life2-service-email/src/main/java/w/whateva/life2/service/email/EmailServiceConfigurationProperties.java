package w.whateva.life2.service.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Getter
@Setter
public class EmailServiceConfigurationProperties {

    private Address address;
    private Group group;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Getter
    @Setter
    public static class Address {

        private AddressStyle style;
    }

    @Getter
    @Setter
    public static class Group {

        private String recipient;
    }

    public enum AddressStyle {

        INTERNET, SIMPLE, SIMPLE_SEMICOLON;
    }
}
