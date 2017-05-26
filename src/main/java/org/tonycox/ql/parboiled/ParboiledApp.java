package org.tonycox.ql.parboiled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tonycox.ql.User;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@RestController
@SpringBootApplication
@ComponentScan(basePackageClasses = {Config.class})
public class ParboiledApp {

    @GetMapping(value = "/users")
    public List<User> getUsersByQuery(@RequestParam("query") Predicate<User> predicate) {
        return Stream
                .of(new User().setName("Ned Flanders").setPhone("stupid"),
                        new User().setName("Homer Simpson").setPhone("not stupid"))
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        SpringApplication.run(ParboiledApp.class, args);
        String query = "(name not_like 'Ned Fl.*') and (phone ne 'stupid')";
        RestTemplate rest = new RestTemplate();
        List answer = rest
                .getForObject("http://localhost:8080/users?query=" + query, List.class);
        System.out.println(answer);
    }
}
