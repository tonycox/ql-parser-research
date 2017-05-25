package org.tonycox.ql.parboiled;

import org.parboiled.Node;
import org.parboiled.Parboiled;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */

public class ParboiledApp {

    public static void main(String[] args) {

        String query = "(true131AWvAW1243 and fa1lse) and ((zeliboba and 'whaaaaat*?') or ((whatze1 like '123') and (tre lt a132r2fw))) or (ok lt 123) and (a eq TSystemexit)";

        SimpleGrammar grammar = Parboiled.createParser(SimpleGrammar.class);
        Node<String> ast = grammar.parseQuery(query);
        System.out.println(ast);
    }
}
