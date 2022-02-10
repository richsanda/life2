package w.whateva.life2.data.pin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.pin.domain.Pin;

import java.util.List;

@Repository(value = "pin")
public interface PinRepository extends MongoRepository<Pin, String> {

    List<Pin> findAllByTypeAndTroveAndKey(String type, String trove, String key);
}