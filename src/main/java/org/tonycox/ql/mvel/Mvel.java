package org.tonycox.ql.mvel;

import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tonycox.ql.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@RestController
@SpringBootApplication
public class Mvel {

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public List<User> parsedPredicateRSql(@RequestParam("query") String query) {
        String mvelQuery = query.replaceAll("or", "||").replaceAll("and", "&&");
        ExecutableStatement compiled = (ExecutableStatement) MVEL.compileExpression(mvelQuery);
        MvelPredicate predicate = new MvelPredicate(compiled);
        return Stream
                .of(new User().setName("Ned fflanders").setPhone("stupid"),
                        new User().setName("Simpson aaffaa").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        SpringApplication.run(Mvel.class, args);

        RestTemplate rest = new RestTemplate();
        List answer = rest
                .getForObject("http://localhost:8080/users?query=(true and false) or (phone=='stupid') and (name.matches('.*ff.*'))",
                        List.class);
        System.out.println(answer);
    }
}
