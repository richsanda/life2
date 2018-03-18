package w.whateva.service.life2.service.util.bucket;

public interface Operator<DomainType, RangeType> {

    RangeType apply(DomainType shred);

    RangeType getBucketMax(RangeType min, RangeType max, int bucket, int numBuckets);

    int compare(RangeType one, RangeType two);
}
