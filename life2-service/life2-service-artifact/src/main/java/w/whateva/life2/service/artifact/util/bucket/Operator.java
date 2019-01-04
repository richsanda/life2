package w.whateva.life2.service.artifact.util.bucket;

public interface Operator<DomainType, RangeType> {

    RangeType apply(DomainType shred);

    RangeType getBucketMax(RangeType min, RangeType max, int bucket, int numBuckets);

    int compare(RangeType one, RangeType two);
}
