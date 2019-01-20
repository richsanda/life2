package w.whateva.life2.data.user.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@Document(collection = "user")
@TypeAlias("user")
@Getter
@Setter
public class User {

    @Id
    private String username;
    private String password;

    private List<TroveAccess> access;
}
