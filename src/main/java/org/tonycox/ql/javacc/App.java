package org.tonycox.ql.javacc;

/**
 * @author Anton Solovev
 * @since 24.05.17.
 */
public class App {

    public static void main(String args[]) {
        try {
            String query = "(true and false) or (name like 'smt**')";
            ELParser parser = new ELParser(query);
            parser.queryTerm();
            Token nextToken = parser.getNextToken();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
