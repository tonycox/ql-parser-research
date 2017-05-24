package org.tonycox.ql.parboiled;

import org.parboiled.Node;
import org.parboiled.Parboiled;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */

public class ParboiledApp {

    public static void main(String[] args) {

        String query = "(true131AWvAW1243 and fa1lse) and ((zeliboba not and 'whaaaaat?') " +
                "or (tre lt a132r2fw)) not or (ok not lt 123) and (a eq TSystemexit)";

        SimpleGrammar grammar = Parboiled.createParser(SimpleGrammar.class);
        Node<Object> ast = grammar.parseQuery(query);

        System.out.println(ast);
    }
}
