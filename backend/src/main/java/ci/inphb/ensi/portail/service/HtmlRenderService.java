package ci.inphb.ensi.portail.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

/**
 * Rend un template Thymeleaf en chaine HTML.
 */
@Service
public class HtmlRenderService {

    private final TemplateEngine templateEngine;

    public HtmlRenderService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context(Locale.FRENCH);
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
