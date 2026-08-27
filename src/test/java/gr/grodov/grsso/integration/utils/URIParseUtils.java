package gr.grodov.grsso.integration.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URIParseUtils {
    public static MultiValueMap<String, String> getQueryParams(URI uri) {
        String[] queries = uri.getQuery().split("&");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        Pattern pattern = Pattern.compile("^([^=]+)=([^;]*)");
        for (String query: queries) {
            Matcher matcher = pattern.matcher(query);
            if (matcher.find()) {
                queryParams.add(matcher.group(1), matcher.group(2));
            }
        }
        return queryParams;
    }
}
