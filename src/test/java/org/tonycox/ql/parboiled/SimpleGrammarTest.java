package org.tonycox.ql.parboiled;

import org.parboiled.Parboiled;
import org.testng.AssertJUnit;
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
        String query = String.format("name %s 'Ned F*'", op);
        String parsedQuery = grammar.parseQuery(query).getValue();

        String expectedOp = SimpleGrammar.convertOperator(op);
        String expectedQuery = String.format("name %s 'Ned F*'", expectedOp);
        AssertJUnit.assertEquals(expectedQuery, parsedQuery);
    }

    @Test(dataProvider = OPERATORS)
    public void parseParenthesisExpression(String op) {
        String query = String.format("(a %1$s b) %2$s (wWw %1$s\t321) ", op, AND);
        String parsedQuery = grammar.parseQuery(query).getValue();
        System.out.println(parsedQuery);

        String expectedOp = SimpleGrammar.convertOperator(op);
        String expectedQuery = String.format("(wWw %1$s 321) %2$s (a %1$s b)", expectedOp, AND);
        AssertJUnit.assertEquals(expectedQuery, parsedQuery);
    }

    @Test(dataProvider = OPERATORS)
    public void parseComplexEnclosedExpression(String op) {
        String query = String.format("((a %1$s b) %2$s (c %1$s d) %3$s ((f %1$s 1) %2$s (field %1$s 'val*')))", op, AND, OR);
        String parsedQuery = grammar.parseQuery(query).getValue();

        String expectedOp = SimpleGrammar.convertOperator(op);
        String expectedQuery = String.format("(((field %1$s 'val*') %2$s (f %1$s 1)) %3$s (c %1$s d) %2$s (a %1$s b))", expectedOp, AND, OR);
        AssertJUnit.assertEquals(expectedQuery, parsedQuery);
    }

    @Test(dataProvider = OPERATORS)
    public void parseEnclosedExpression(String op) {
        String query = String.format("((a %1$s b) %2$s (c %1$s d))", op, AND);
        String parsedQuery = grammar.parseQuery(query).getValue();

        String expectedOp = SimpleGrammar.convertOperator(op);
        String expectedQuery = String.format("((c %1$s d) %2$s (a %1$s b))", expectedOp, AND);
        AssertJUnit.assertEquals(expectedQuery, parsedQuery);
    }
}