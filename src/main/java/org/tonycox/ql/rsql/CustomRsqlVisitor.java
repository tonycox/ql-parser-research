package org.tonycox.ql.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.NoArgRSQLVisitorAdapter;
import cz.jirutka.rsql.parser.ast.OrNode;
import org.tonycox.ql.User;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
public class CustomRsqlVisitor extends NoArgRSQLVisitorAdapter<Predicate<User>> {

    @Override
    public Predicate<User> visit(AndNode node) {
        Optional<Predicate<User>> reduced = node.getChildren()
                .stream()
                .map(n -> n.accept(this))
                .reduce(Predicate::and);
        return reduced.orElseThrow(RuntimeException::new);
    }

    @Override
    public Predicate<User> visit(OrNode node) {
        Optional<Predicate<User>> reduced = node.getChildren()
                .stream()
                .map(n -> n.accept(this))
                .reduce(Predicate::or);
        return reduced.orElseThrow(RuntimeException::new);
    }

    @Override
    public Predicate<User> visit(ComparisonNode node) {
        return user -> {
            try {
                Field field = user.getClass().getField(node.getSelector());
                return field.get(new Object()).equals(node.getArguments().get(0));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
