package org.tonycox.ql.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Node;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.stream.Collectors;

/**
 * @author Anton Solovev
 * @since 24.05.17.
 */
@BuildParseTree
public class SimpleGrammar extends BaseParser<Object> {

    private ReportingParseRunner<Object> runner;

    public SimpleGrammar() {
        runner = new ReportingParseRunner<>(this.RootQuery());
    }

    public Node<Object> parseQuery(String query) {
        ParsingResult<Object> parsingResult = runner.run(query);
        if (parsingResult.hasErrors()) {
            String errorMessages = parsingResult.parseErrors.stream()
                    .map(ParseError::getErrorMessage)
                    .collect(Collectors.joining(", "));
            throw new RuntimeException(errorMessages);
        } else {
            return parsingResult.parseTreeRoot;
        }
    }

    //-------------------------------------------------------------------------
    //  Parser itself
    //-------------------------------------------------------------------------

    private static final String AVAILABLE_SIGNS = "'\"?!*%-_";
    private static final String AVAILABLE_SPACES = " \t\r\f";

    private final Rule NOT = Operator("not");
    private final Rule AND = Operator("and");
    private final Rule OR = Operator("or");
    private final Rule EQ = Operator("eq");
    private final Rule GE = Operator("ge");
    private final Rule GT = Operator("gt");
    private final Rule LT = Operator("lt");
    private final Rule LE = Operator("le");
    private final Rule LIKE = Operator("like");
    private final Rule IN = Operator("in");

    @SuppressNode
    @DontLabel
    Rule Operator(String string) {
        return Sequence(IgnoreCase(string), Space()).label('\'' + string + '\'');
    }

    @MemoMismatches
    Rule Operator() {
        return Sequence(
                Optional(NOT).label("optional not"),
                FirstOf(OR, AND, EQ, GE, GT, LT, LE, LIKE, IN).label("operator"));
    }

    @MemoMismatches
    Rule Conjunction() {
        return Sequence(Space(), Optional(NOT), FirstOf(OR, AND));
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
                        Sequence(Parenthesis(), ConjunctedComplexQuery())),
                ')',
                ConjunctedComplexQuery());
    }

    Rule ConjunctedComplexQuery() {
        return ZeroOrMore(Conjunction(), ComplexQuery());
    }

    Rule Parenthesis() {
        return Sequence('(', Expression(), ')');
    }

    Rule Expression() {
        return Sequence(
                Selector(),
                Operator(),
                Value());
    }

    Rule Selector() {
        return Sequence(
                Str().label("string"),
                Optional(Space()).label("space"));
    }

    Rule Value() {
        return OneOrMore(FirstOf(SpecialSigns(), Str(), Number()));
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
