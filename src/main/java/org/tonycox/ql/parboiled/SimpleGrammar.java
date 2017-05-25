package org.tonycox.ql.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.errors.InvalidInputError;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.MatcherPath;
import org.parboiled.support.ParsingResult;

import java.util.stream.Collectors;

/**
 * @author Anton Solovev
 * @since 24.05.17.
 */
@BuildParseTree
public class SimpleGrammar extends BaseParser<String> {

    private ReportingParseRunner<String> runner;

    public SimpleGrammar() {
        runner = new ReportingParseRunner<>(this.rootQuery());
    }

    /**
     * The main parsing method. Uses a ReportingParseRunner (which only reports the first error) for simplicity.
     */
    public Node<String> parseQuery(String query) {
        ParsingResult<String> parsingResult = runner.run(query);
        if (parsingResult.hasErrors()) {
            ParseError error = parsingResult.parseErrors.get(0);
            String errorMessage = error.getErrorMessage();
            if (error instanceof InvalidInputError) {
                errorMessage = ((InvalidInputError) error).getFailedMatchers().stream()
                        .map(MatcherPath::toString)
                        .collect(Collectors.joining(", \n"));
            }
            throw new GrammarException(errorMessage);
        } else {
            return parsingResult.parseTreeRoot;
        }
    }

    //-------------------------------------------------------------------------
    //  Parser itself
    //-------------------------------------------------------------------------

    private static final String AVAILABLE_SIGNS = "?!*-_";
    private static final String AVAILABLE_IDENTIFIER_SIGNS = "._";
    private static final String AVAILABLE_SPACES = " \t\r\f";

    final Rule AND = conOperator("and");
    final Rule OR = conOperator("or");
    final Rule EQ = operator("eq");
    final Rule NOT_EQ = operator("ne");
    final Rule GE = operator("ge");
    final Rule GT = operator("gt");
    final Rule LT = operator("lt");
    final Rule LE = operator("le");
    final Rule LIKE = operator("like");
    final Rule IN = operator("in");

    @SuppressNode
    @DontLabel
    Rule operator(String string) {
        return Sequence(IgnoreCase(string), space()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    Rule conOperator(String string) {
        return Sequence(IgnoreCase(string), Optional(space(), TestNot(')'))).label('\'' + string + '\'');
    }

    @MemoMismatches
    Rule operator() {
        return Sequence(
                FirstOf(OR, AND, EQ, GE, GT, LT, LE, LIKE, IN, NOT_EQ).label("operator"),
                push(convertOperator(match()) + " "));
    }

    static String convertOperator(String operator) {
        String op = operator.trim();
        switch (op) {
            case "like":
                return "matches";
            default:
                return op;
        }
    }

    Rule rootQuery() {
        return Sequence(
                FirstOf(expression(), complexQuery(), sequenceOfComplexQuery()).label("query"),
                Optional(space()).label("space"),
                EOI);
    }

    Rule complexQuery() {
        return Sequence(
                Optional(space()),
                '(',
                FirstOf(expression(), sequenceOfComplexQuery()),
                ')',
                push(String.format("(%s)", pop())),
                conjunctedComplexQuery());
    }

    Rule sequenceOfComplexQuery() {
        return Sequence(parenthesis(), conjunctedComplexQuery());
    }

    Rule conjunctedComplexQuery() {
        return ZeroOrMore(
                conjunction(),
                complexQuery(),
                push(pop() + pop()));
    }

    @MemoMismatches
    Rule conjunction() {
        return Sequence(
                Optional(TestNot('('), space()),
                FirstOf(OR, AND),
                push(" " + match() + pop()));
    }

    Rule parenthesis() {
        return Sequence(
                Optional(space()),
                '(',
                expression(),
                ')',
                Optional(space()),
                push(String.format("(%s)", pop())));
    }

    Rule expression() {
        return Sequence(
                selector(),
                operator(),
                value(),
                push(String.format("%3$s%2$s%1$s", pop(), pop(), pop())));
    }

    Rule selector() {
        return Sequence(
                identifier().label("identifier"),
                push(match()),
                Optional(space(), push(pop() + match())).label("space"));
    }

    Rule value() {
        return Sequence(
                FirstOf(string(), quotedString()),
                push(match()),
                Optional(space(), push(pop() + match())));
    }

    Rule quotedString() {
        return Sequence('\'', ZeroOrMore(FirstOf(string(), space())), '\'');
    }

    @SuppressSubnodes
    Rule string() {
        return OneOrMore(FirstOf(specialSigns(), letters(), number()));
    }

    Rule identifier() {
        return Sequence(letters(), Optional(number()), Optional(identifier()));
    }

    @SuppressSubnodes
    Rule number() {
        return OneOrMore(CharRange('0', '9'));
    }

    @SuppressSubnodes
    Rule letters() {
        return OneOrMore(
                FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), AnyOf(AVAILABLE_IDENTIFIER_SIGNS)),
                Optional(letters()));
    }

    @SuppressSubnodes
    Rule specialSigns() {
        return OneOrMore(AnyOf(AVAILABLE_SIGNS));
    }

    @SuppressNode
    @DontLabel
    Rule space() {
        return OneOrMore(AnyOf(AVAILABLE_SPACES).label("Whitespace"));
    }
}
