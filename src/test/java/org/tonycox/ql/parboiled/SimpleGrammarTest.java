package org.tonycox.ql.parboiled;

import org.parboiled.Parboiled;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Anton Solovev
 * @since 25.05.17.
 */
public class SimpleGrammarTest {

    private static final String OPERATORS = "operators";
    private static final String OR = "or";
    private static final String AND = "and";

    private SimpleGrammar grammar;

    @BeforeMethod
    public void setUp() {
        grammar = Parboiled.createParser(SimpleGrammar.class);
    }

    @DataProvider(name = OPERATORS)
    public Object[][] operators() {
        return new String[][]{{"eq"}, {"ne"}, {"like"}, {"le"}, {"lt"}, {"gt"}, {"ge"}, {AND}, {OR}, {"in"}};
    }

    @Test(dataProvider = OPERATORS)
    public void parseSimpleExpression(String op) {
        String query = String.format("a1 %s 'b2'", op);
        String value = grammar.parseQuery(query).getValue();
        System.out.println(value);
    }

    @Test(dataProvider = OPERATORS)
    public void parseParenthesisExpression(String op) {
        String query = String.format("(a %s b) %s (wWw %s\t321) ", op, AND, op);
        String value = grammar.parseQuery(query).getValue();
        System.out.println(value);
    }

    @Test(dataProvider = OPERATORS)
    public void parseComplexEnclosedExpression(String op) {
        String query = String.format("((a %1$s b) %2$s(c %1$s d)%3$s((f %1$s 1) %2$s (field %1$s 'val*')))", op, AND, OR);
        String value = grammar.parseQuery(query).getValue();
        System.out.println(value);
    }

    @Test(dataProvider = OPERATORS)
    public void parseEnclosedExpression(String op) {
        String query = String.format("((a %1$s b) %2$s (c %1$s d))", op, AND);
        String value = grammar.parseQuery(query).getValue();
        System.out.println(value);
    }
}