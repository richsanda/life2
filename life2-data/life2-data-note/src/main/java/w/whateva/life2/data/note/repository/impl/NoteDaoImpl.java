package w.whateva.life2.data.note.repository.impl;

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
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;

import java.util.ArrayList;
import java.util.List;

@Component
public class NoteDaoImpl implements NoteDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    MongoClient client = new MongoClient();
    MongoDatabase db = client.getDatabase("life2");

    @Override
    public List<String> listTroves() {
        List<String> result = new ArrayList<>();
        MongoCursor<String> c =
                db.getCollection("note").distinct("trove", String.class).iterator();
        while (c.hasNext()) {
            result.add(c.next());
        }
        return result;
    }

    @Override
    public List<Note> findByTroveSorted(String trove) {

        Criteria criteria = Criteria.where("trove").is(trove);

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "index"));

        return mongoTemplate.find(query, Note.class);
    }
}
