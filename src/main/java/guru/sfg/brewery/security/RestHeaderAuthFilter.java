package guru.sfg.brewery.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class RestHeaderAuthFilter extends AbstractRestAuthFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public String getName(HttpServletRequest request) {
        return request.getHeader("Api-Key")!=null ? request.getHeader("Api-Key") : "";
    }

    @Override
    public String getPassword(HttpServletRequest request) {
        return request.getHeader("Api-Secret")!=null ? request.getHeader("Api-Secret") : "";
    }
}