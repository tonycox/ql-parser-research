package org.tonycox.ql.parboiled;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.SuppressNode;

/**
 * @author Anton Solovev
 * @since 24.05.17.
 */
@BuildParseTree
public class RestConditionParser extends BaseParser<Object> {

    private final Rule AND = Operator("and");
    private final Rule OR = Operator("or");
    private final Rule NOT = Operator("not");
    private final Rule EQ = Operator("eq");
    private final Rule GE = Operator("ge");
    private final Rule GT = Operator("gt");
    private final Rule LT = Operator("lt");
    private final Rule LE = Operator("le");
    private final Rule LIKE = Operator("like");

    public Rule Query() {
        return Sequence(Expression(), EOI);
    }

    public Rule Expression() {
        return Sequence(ZeroOrMore(Sequence(Parens(), FirstOf(AND, OR))), LetterOrDigit(), Operator(), LetterOrDigit());
    }

    public Rule Operator() {
        return Sequence(Optional(NOT), FirstOf(OR, AND, EQ, GE, GT, LT, LE, LIKE));
    }

    @SuppressNode
    @DontLabel
    public Rule Operator(String string) {
        return Sequence(IgnoreCase(string), Spacing()).label('\'' + string + '\'');
    }

    public Rule Parens() {
        return Sequence('(', Expression(), ')');
    }

    @SuppressNode
    public Rule Spacing() {
        return OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace"));
    }

    @SuppressNode
    @DontLabel
    public Rule Terminal(String string, Rule mustNotFollow) {
        return Sequence(string, TestNot(mustNotFollow), Spacing()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    public Rule Keyword(String keyword) {
        return Terminal(keyword, LetterOrDigit());
    }

    public Rule LetterOrDigit() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '-');
    }

    Rule Number() {
        return OneOrMore(Digit());
    }

    public Rule Digit() {
        return CharRange('0', '9');
    }

    /**
     * The main parsing method. Uses a ReportingParseRunner (which only reports the first error) for simplicity.
     */
//    public Node<>
}
