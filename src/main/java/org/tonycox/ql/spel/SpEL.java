package org.tonycox.ql.spel;


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

        SpelExpressionParser spelParser = new SpelExpressionParser();
        String query = "(True and False) or (phone eq 'stupid')\tand\t(name\rmatches '(.*ff.*)')";

        SpELPredicate<User> predicate = new SpELPredicate<>(spelParser.parseRaw(query));
        List<User> collect = Stream
                .of(new User().setName("Ned fflanders").setPhone("stupid"),
                        new User().setName("Simpson aaffaa").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());

        System.out.println(collect);
    }
}
