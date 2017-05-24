package org.tonycox.ql.spel;


import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.tonycox.ql.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Solovev
 * @since 22.05.17.
 */
public class SpEL {

    public static void main(String[] args) {

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("(True and False) or (phone.equals('stupid')) " +
                "and (name matches '(.*ff.*)') and ");

        SpELPredicate<User> predicate = new SpELPredicate<>(exp);

        List<User> collect = Stream
                .of(new User().setName("Ned fflanders").setPhone("stupid"),
                        new User().setName("Simpson aaffaa").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());

        System.out.println(collect);
    }
}
