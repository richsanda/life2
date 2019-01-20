package w.whateva.life2.data.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.user.domain.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);
}