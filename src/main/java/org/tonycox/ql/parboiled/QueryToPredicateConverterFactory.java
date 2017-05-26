package org.tonycox.ql.parboiled;

import org.parboiled.Parboiled;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.tonycox.ql.spel.SpELPredicate;

import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 26.05.17.
 */
@Component
public class QueryToPredicateConverterFactory<E> implements ConverterFactory<String, Predicate<E>> {

    private final SpelExpressionParser spelParser = new SpelExpressionParser();

    @Override
    public <T extends Predicate<E>> Converter<String, T> getConverter(Class<T> targetType) {
        return new QueryToPredicateConverter(targetType);
    }

    private final class QueryToPredicateConverter<T extends Predicate<E>> implements Converter<String, Predicate<E>> {

        private SimpleGrammar grammar = Parboiled.createParser(SimpleGrammar.class);

        public QueryToPredicateConverter(Class<T> targetType) {
            this.grammar = Parboiled.createParser(SimpleGrammar.class);
        }

        @Override
        public Predicate<E> convert(String source) {
            if (source == null) {
                return (e) -> true;
            }
            String query = grammar.parseQuery(source).getValue();
            SpelExpression expression = spelParser.parseRaw(query);
            return new SpELPredicate<>(expression);
        }
    }

}
