package w.whateva.life2.service.artifact.util;

import org.springframework.stereotype.Service;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.service.artifact.util.bucket.Operator;
import w.whateva.life2.service.artifact.util.bucket.BucketDistributor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArtifactUtility {

    public <ItemType> List<List<ItemType>> putInBuckets(List<ItemType> items,
                                                        Operator<ItemType, LocalDateTime> operator,
                                                        int numBuckets,
                                                        LocalDateTime min,
                                                        LocalDateTime max) {

        return new BucketDistributor<>(items, operator, numBuckets, min, max).getBuckets();
    }

    public static ApiArtifactSearchSpec restrict(ApiArtifactSearchSpec searchSpec, ApiArtifactSearchSpec access) {

        // searchSpec fields are *and*, access fields are *or*
        // access fields are per field restrictions...

        ApiArtifactSearchSpec result = new ApiArtifactSearchSpec();
        result.setAfter(max(searchSpec.getAfter(), access.getAfter()));
        result.setBefore(min(searchSpec.getBefore(), access.getBefore()));
        result.setTroves(restrict(searchSpec.getTroves(), access.getTroves()));
        result.setFrom(searchSpec.getFrom());
        result.setTo(searchSpec.getTo());
        result.setWho(restrict(searchSpec.getWho(), access.getWho()));

        return result;
    }

    private static LocalDate max(LocalDate searchSpec, LocalDate access) {

        if (null == searchSpec && null == access) return null;
        if (null == searchSpec) return access;
        if (null == access) return searchSpec;
        return searchSpec.isAfter(access) ? searchSpec : access;
    }

    private static LocalDate min(LocalDate searchSpec, LocalDate access) {

        if (null == searchSpec && null == access) return null;
        if (null == searchSpec) return access;
        if (null == access) return searchSpec;
        return searchSpec.isBefore(access) ? searchSpec : access;
    }

    private static Set<String> restrict(Set<String> searchSpec, Set<String> access) {

        // empty set means no froms allowed... null means all froms... must be in access to be kept...
        if (null == searchSpec) return access; // null means all, so return default access
        if (null == access) return searchSpec; // null access means access to all, return what is searched
        return searchSpec.stream().filter(access::contains).collect(Collectors.toSet());
    }
}
