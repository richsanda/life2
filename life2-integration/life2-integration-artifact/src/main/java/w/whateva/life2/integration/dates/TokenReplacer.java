package w.whateva.life2.integration.dates;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@Builder
public class TokenReplacer {

    private final Predicate<Token>[] predicates;
    private final Function<List<Token>, Token> replacer;

    List<Token> replace(List<Token> tokens) {
        int firstMatch = DateParsingUtil.firstMatch(tokens, predicates);
        if (firstMatch < 0) return null;
        List<Token> result = new ArrayList<>();
        result.addAll(tokens.subList(0, firstMatch));
        result.add(replacer.apply(tokens.subList(firstMatch, firstMatch + predicates.length)));
        result.addAll(tokens.subList(firstMatch + predicates.length, tokens.size()));
        return Collections.unmodifiableList(result);
    }
}
