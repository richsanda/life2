package w.whateva.service.life2.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShredUtility {

    //public <S, V extends Comparable<V>> List<Bucket<S>> putIntoBuckets(Collection<ShredWithValue<S, V>> shreds, int numberOfBuckets) {

        // Bucket<S>[] buckets = Array.newInstance(S, numberOfBuckets);
    //    return null;
    //}

    /*
    public static class ShredWithValue<ShredType, ValueType extends Comparable<ValueType>> implements Comparable<ShredWithValue> {

        private final ShredType shred;
        private final ValueType value;

        ShredWithValue(ShredType shred, ValueType value) {
            this.shred = shred;
            this.value = value;
        }

        public ShredType getShred() {
            return shred;
        }

        public ValueType getValue() {
            return value;
        }

        @Override
        public int compareTo(ShredWithValue o) {
            return 0; // getValue() > o.getValue();
        }
    }
    */

    class Bucket<ShredType>  {

        private final List<ShredType> shreds;

        Bucket(Collection<ShredType> shreds) {
            this.shreds = new ArrayList<>(shreds);
        }

        List<ShredType> getShreds() {
            return shreds;
        }
    }
}
