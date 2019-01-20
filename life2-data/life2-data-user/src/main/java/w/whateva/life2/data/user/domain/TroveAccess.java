package w.whateva.life2.data.user.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TroveAccess {

    private String owner;
    private String person;
    private Set<String> troves = new HashSet<>();
    private Set<TroveAccessRole> roles = new HashSet<>();

    public TroveAccess(String owner, String person) {
        this.owner = owner;
        this.person = person;
    }

    public TroveAccess withTroves(String... troves) {
        this.troves.addAll(Arrays.asList(troves));
        return this;
    }

    public TroveAccess withRoles(TroveAccessRole... roles) {
        this.roles.addAll(Arrays.asList(roles));
        return this;
    }
}
