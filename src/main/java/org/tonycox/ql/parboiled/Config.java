package org.tonycox.ql.parboiled;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Anton Solovev
 * @since 26.05.17.
 */
@Configuration
public class Config extends WebMvcConfigurerAdapter {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new QueryToPredicateConverterFactory<>());
        super.addFormatters(registry);
    }
}
