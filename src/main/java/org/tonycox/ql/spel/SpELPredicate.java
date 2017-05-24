package org.tonycox.ql.spel;

import lombok.Data;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@Data
public class SpELPredicate<T> implements Predicate<T> {

    private final Expression exp;
    private final StandardEvaluationContext context = new StandardEvaluationContext();

    @Override
    public boolean test(T t) {
        context.setRootObject(t);
        return exp.getValue(context, Boolean.class);
    }
}
