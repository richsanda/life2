package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.api.note.NoteOperations;
import w.whateva.life2.api.note.dto.ApiNote;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static w.whateva.life2.service.note.impl.NoteUtil.enhanceNote;
import static w.whateva.life2.service.note.impl.NoteUtil.toDto;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final GenericWebApplicationContext context;

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public NoteServiceImpl(GenericWebApplicationContext context, NoteRepository noteRepository, NoteDao NoteDao) {
        this.context = context;
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
        context.registerBean("whatever_dude",
                ArtifactProvider.class,
                () -> new NoteRegistrar(mongoTemplate));
    }

    @Override
    public ApiNote update(String trove, String key, ApiNote apiNote) {

        apiNote.setTrove(trove);
        apiNote.setKey(key);

        if (null == apiNote.getKey()) return null;

        Note note = new Note();
        note.setId(composeKey(trove, key));
        BeanUtils.copyProperties(apiNote, note);
        noteRepository.save(note);

        return toApi(note);
    }

    private Note update(Note note) {
        noteRepository.save(note);
        return note;
    }

    @Override
    public List<ApiNote> update(String trove, List<ApiNote> notes) {
        return notes.stream()
                .map(note -> update(note.getTrove(), note.getKey(), note))
                .collect(Collectors.toList());
    }

    @Override
    public ApiNote read(String folder, String filename) {
        Note note = noteRepository.findById(composeKey(folder, filename)).orElse(null);
        return toApi(note);
    }

    @Override
    public List<ApiNote> readTrove(String folder) {
        Collection<Note> Notes = noteDao.findByTroveSorted(folder);
        if (CollectionUtils.isEmpty(Notes)) return Collections.emptyList();
        return Notes.stream().map(NoteServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public String test() {

//        Collection<Note> notes = noteRepository.findAll().stream()
//                .flatMap(n -> splitNote(n).stream())
//                .map(NoteUtil::enhanceNote)
//                .filter(n -> n.getData().containsKey("when") && n.getData().containsKey("type") && null != n.getData().get("type"))
//                .sorted(Comparator.comparing((n) -> n.getData().get("when").toString()))
//                .collect(Collectors.toUnmodifiableList());

        // noteRepository.saveAll(notes);

        return noteRepository.findAll().stream()
                // .map(NoteUtil::enhanceNote)
                .filter(n -> n.getData().containsKey("when") && n.getData().containsKey("type") && null != n.getData().get("type"))
                .sorted(Comparator.comparing((n) -> n.getData().get("when").toString()))
                .map(n -> "" + n.getData().get("when") + "." + n.getData().getOrDefault("where", "") + " (" + n.getData().get("people") + ") -- " + n.getData().get("type") + " -- " + n.getTrove())
                .collect(Collectors.joining("\n"));
    }

    @Override
    public List<String> listTroves() {
        return noteDao.listTroves();
    }

    public List<String> allNoteKeys() {
        return noteRepository.findAll().stream().map(Note::getId).collect(Collectors.toList());
    }

    public static ApiNote toApi(Note note) {
        if (null == note) return null;
        ApiNote result = new ApiNote();
        BeanUtils.copyProperties(enhanceNote(note), result);
        return result;
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s.jpg", trove, key).toLowerCase();
    }

    // TODO: put in a separate class or something
    public class NoteRegistrar implements ArtifactProvider {

        private final MongoTemplate mongoTemplate;

        NoteRegistrar(MongoTemplate mongoTemplate) {

            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public ApiArtifact read(String owner, String trove, String key) {
            Note note = noteRepository.findById(trove + "/" + key).orElse(null);
            if (null == note) return null;
            List<String> relatives = noteDao.findByTroveSorted(trove).stream()
                    .map(Note::getId)
                    .filter(id -> id.endsWith(".jpg"))
                    .collect(Collectors.toUnmodifiableList());
            int index = relatives.indexOf(note.getId());
            return toDto(note, relatives, index);
        }

        @Override
        public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {
            return null;
        }

        @Override
        public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

            return getEmails(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), searchSpec.getAfter().atStartOfDay(), searchSpec.getBefore().plusDays(1).atStartOfDay())
                    .stream()
                    .map(NoteUtil::toDto)
                    .collect(Collectors.toList());
        }

        @Override
        public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {
            return getMonthYearCounts(who, from, to, after.atStartOfDay(), before.atStartOfDay());
        }

        @Override
        public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {
            return count(searchSpec.getAfter(), searchSpec.getBefore(), null, null, null);
        }

        private Criteria queryCriteria(LocalDateTime after, LocalDateTime before) {
            ArrayList<Criteria> criteria = new ArrayList<>();

            if (null != after || null != before) {
                ArrayList<Criteria> sentCriteriaList = new ArrayList<>();
                if (null != after) {
                    sentCriteriaList.add(Criteria.where("sent").gte(after));
                }
                if (null != before) {
                    sentCriteriaList.add(Criteria.where("sent").lt(before));
                }
                Criteria[] sentCriteriaArray = new Criteria[sentCriteriaList.size()];
                sentCriteriaArray = sentCriteriaList.toArray(sentCriteriaArray);
                criteria.add(new Criteria().andOperator(sentCriteriaArray));
            }

            Criteria[] criteriaArray = new Criteria[criteria.size()];
            criteriaArray = criteria.toArray(criteriaArray);

            return new Criteria().andOperator(criteriaArray);
        }

        public List<ApiArtifactCount> getMonthYearCounts(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before) {

            Aggregation agg = newAggregation(
                    match(queryCriteria(after, before)),
                    project().andExpression("month(sent)").as("month").andExpression("year(sent)").as("year"),
                    group("month", "year").count().as("count"),
                    sort(Sort.Direction.ASC, "year", "month")
            );

            //Convert the aggregation result into a List
            AggregationResults<ApiArtifactCount> groupResults  = mongoTemplate.aggregate(agg, Note.class, ApiArtifactCount.class);

            return groupResults.getMappedResults();
        }

        public List<Note> getEmails(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before) {

            Criteria criteria = queryCriteria(after, before);

            // *and* with the whoCriteria if it was provided
            // if (null != whoCriteria) queryCriteria = queryCriteria.andOperator(whoCriteria);

            Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "sent"));

            return mongoTemplate.find(query, Note.class);
        }
    }
}
