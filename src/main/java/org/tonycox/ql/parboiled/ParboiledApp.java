package org.tonycox.ql.parboiled;

import org.parboiled.Parboiled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tonycox.ql.User;
import org.tonycox.ql.spel.SpELPredicate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@RestController
@SpringBootApplication
public class ParboiledApp {

    private SimpleGrammar grammar = Parboiled.createParser(SimpleGrammar.class);

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public List<User> getUsersByQuery(@RequestParam("query") String query) {
        String parsedQuery = grammar.parseQuery(query).getValue();
        SpELPredicate<User> predicate = new SpELPredicate<>(parsedQuery);
        return Stream
                .of(new User().setName("Ned Flanders").setPhone("stupid"),
                        new User().setName("Homer Simpson").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        SpringApplication.run(ParboiledApp.class, args);
        String query = "(name like 'Ned Fl.*') and (phone eq 'stupid')";

        RestTemplate rest = new RestTemplate();
        List answer = rest
                .getForObject("http://localhost:8080/users?query=" + query, List.class);
        System.out.println(answer);
    }
}
