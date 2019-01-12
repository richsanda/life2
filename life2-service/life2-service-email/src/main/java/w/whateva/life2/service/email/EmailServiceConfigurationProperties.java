package w.whateva.life2.service.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailServiceConfigurationProperties {

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public static class Address {

        AddressType type;

        public AddressType getType() {
            return type;
        }

        public void setType(AddressType type) {
            this.type = type;
        }
    }

    enum AddressType {

        INTERNET, SIMPLE;
    }
}
