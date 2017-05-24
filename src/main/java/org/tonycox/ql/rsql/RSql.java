package org.tonycox.ql.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tonycox.ql.User;

import java.util.function.Predicate;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@RestController
@SpringBootApplication
public class RSql {

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public String parsedPredicateRSql(@RequestParam("query") String query) {
        Node rootNode = new RSQLParser().parse(query);
        Predicate<User> predicate = rootNode.accept(new CustomRsqlVisitor());
        return predicate.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(RSql.class, args);

        RestTemplate rest = new RestTemplate();
        String answer = rest
                .getForObject("http://localhost:8080/users?query=name==Flanders,phone==+987456,name==stupid",
                        String.class);
        System.out.println(answer);
    }
}


