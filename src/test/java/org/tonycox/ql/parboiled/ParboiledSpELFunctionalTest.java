package org.tonycox.ql.parboiled;

import org.parboiled.Parboiled;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tonycox.ql.User;
import org.tonycox.ql.spel.SpELPredicate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Solovev
 * @since 25.05.17.
 */
public class ParboiledSpELFunctionalTest {

    private SimpleGrammar grammar;
    private SpelExpressionParser spelParser;

    @BeforeMethod
    public void setUp() {
        grammar = Parboiled.createParser(SimpleGrammar.class);
        spelParser = new SpelExpressionParser();
    }

    @Test
    public void evaluateParsedLikeQuery() {
        String query = "(name like 'Ned Fl.*') and (phone eq 'stupid')";
        String parsedQuery = grammar.parseQuery(query).getValue();
        SpELPredicate<User> predicate = new SpELPredicate<>(spelParser.parseRaw(parsedQuery));
        String userName = "Ned Flanders";
        List<User> actual = Stream
                .of(new User().setName(userName).setPhone("stupid"),
                        new User().setName("Homer Simpson").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());
        AssertJUnit.assertTrue(actual.size() == 1);
        AssertJUnit.assertEquals(userName, actual.get(0).getName());
    }

    @Test
    public void evaluateParsedNotLikeQuery() {
        String query = "name not_like 'Ned Fl.*'";
        String parsedQuery = grammar.parseQuery(query).getValue();
        SpELPredicate<User> predicate = new SpELPredicate<>(spelParser.parseRaw(parsedQuery));
        String userName = "Homer Simpson";
        List<User> actual = Stream
                .of(new User().setName("Ned Flanders").setPhone("stupid"),
                        new User().setName(userName).setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());
        AssertJUnit.assertTrue(actual.size() == 1);
        AssertJUnit.assertEquals(userName, actual.get(0).getName());
    }
}
