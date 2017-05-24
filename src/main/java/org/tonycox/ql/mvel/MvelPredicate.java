package org.tonycox.ql.mvel;

import lombok.Data;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.tonycox.ql.User;

import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@Data
public class MvelPredicate implements Predicate<User> {

    private final ExecutableStatement expression;

    @Override
    public boolean test(User user) {
        return MVEL.executeExpression(expression, user, Boolean.class);
    }
}
