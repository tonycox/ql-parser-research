package org.tonycox.ql.spel;

import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
public class SpELPredicate<T> implements Predicate<T> {

    private final SpelExpression expression;

    public SpELPredicate(String query) {
        expression = new SpelExpressionParser().parseRaw(query);
    }

    private final StandardEvaluationContext context = new StandardEvaluationContext();

    @Override
    public boolean test(T t) {
        context.setRootObject(t);
        return expression.getValue(context, Boolean.class);
    }
}
