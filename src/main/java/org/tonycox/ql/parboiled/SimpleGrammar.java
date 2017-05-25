package org.tonycox.ql.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
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
        runner = new ReportingParseRunner<>(this.RootQuery());
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

    private static final String AVAILABLE_SIGNS = "'\"?!*%-_";
    private static final String AVAILABLE_SPACES = " \t\r\f";

    final Rule AND = ConOperator("and");
    final Rule OR = ConOperator("or");
    final Rule EQ = Operator("eq");
    final Rule NOT_EQ = Operator("ne");
    final Rule GE = Operator("ge");
    final Rule GT = Operator("gt");
    final Rule LT = Operator("lt");
    final Rule LE = Operator("le");
    final Rule LIKE = Operator("like");
    final Rule IN = Operator("in");

    @SuppressNode
    @DontLabel
    Rule Operator(String string) {
        return Sequence(IgnoreCase(string), Space()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    Rule ConOperator(String string) {
        return Sequence(IgnoreCase(string), Optional(Space(), TestNot(')'))).label('\'' + string + '\'');
    }

    @MemoMismatches
    Rule Operator() {
        return Sequence(
                FirstOf(OR, AND, EQ, GE, GT, LT, LE, LIKE, IN, NOT_EQ).label("operator"),
                push(convertOperator(match())));
    }

    String convertOperator(String operator) {
        String space = " ";
        switch (operator.trim()) {
            case "like":
                return "matches" + space;
            default:
                return operator;
        }
    }

    @MemoMismatches
    Rule Conjunction() {
        return Sequence(
                Optional(TestNot('('), Space()),
                FirstOf(OR, AND),
                push(" " + match() + pop()));
    }

    Rule RootQuery() {
        return Sequence(
                FirstOf(Expression(), ComplexQuery()).label("query"),
                Optional(Space()).label("space"),
                EOI);
    }

    Rule ComplexQuery() {
        return Sequence(
                '(',
                FirstOf(Expression(),
                        Sequence(Parenthesis(), ConjunctedComplexQuery(), push(pop() + pop()))),
                ')',
                push(String.format("(%s)", pop())),
                ConjunctedComplexQuery());
    }

    Rule ConjunctedComplexQuery() {
        return ZeroOrMore(
                Conjunction(),
                ComplexQuery());
    }

    Rule Parenthesis() {
        return Sequence(
                '(',
                Expression(),
                ')',
                push(String.format("(%s)", pop())));
    }

    Rule Expression() {
        return Sequence(
                Selector(),
                Operator(),
                Value(),
                push(String.format("%3$s%2$s%1$s", pop(), pop(), pop())));
    }

    Rule Selector() {
        return Sequence(
                Str().label("string"),
                push(match()),
                Optional(Space(), push(pop() + match())).label("space"));
    }

    Rule Value() {
        return Sequence(
                OneOrMore(FirstOf(SpecialSigns(), Str(), Number())),
                push(match()),
                Optional(Space(), push(pop() + match())));
    }

    Rule Str() {
        return Sequence(Letters(), Optional(Number()), Optional(Str()));
    }

    Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }

    Rule Letters() {
        return OneOrMore(
                FirstOf(CharRange('a', 'z'), CharRange('A', 'Z')),
                Optional(Letters()));
    }

    Rule SpecialSigns() {
        return OneOrMore(AnyOf(AVAILABLE_SIGNS));
    }

    @SuppressNode
    @DontLabel
    Rule Space() {
        return OneOrMore(AnyOf(AVAILABLE_SPACES).label("Whitespace"));
    }
}
