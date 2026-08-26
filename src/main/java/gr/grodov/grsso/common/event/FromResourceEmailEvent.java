package gr.grodov.grsso.common.event;

import java.util.Locale;
import java.util.Map;

public record FromResourceEmailEvent(
    String toAddress,
    String subjectMessageCode,
    String pathToTemplate,
    Map<String, Object> contextTemplate,
    Locale locale
) {
}
