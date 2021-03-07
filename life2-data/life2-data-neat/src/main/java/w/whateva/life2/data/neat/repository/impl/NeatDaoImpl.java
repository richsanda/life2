package w.whateva.life2.data.neat.repository.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.domain.NeatFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class NeatDaoImpl implements NeatDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    MongoClient client = new MongoClient();
    MongoDatabase db = client.getDatabase("life2");

    @Override
    public List<String> listFolders() {
        List<String> result = new ArrayList<>();
        MongoCursor<String> c =
                db.getCollection("neat").distinct("folder", String.class).iterator();
        while (c.hasNext()) {
            result.add(c.next());
        }
        return result;
    }

    @Override
    public List<NeatFile> findByFolderSorted(String folder) {

        Criteria criteria = Criteria.where("folder").is(folder);

        Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "type", "title", "index"))
                .with(Sort.by(Sort.Direction.ASC, "page"));

        return mongoTemplate.find(query, NeatFile.class);
    }
}
