package guru.sfg.brewery.security.google;


import guru.sfg.brewery.domain.security.Users;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
public class Google2faFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver
            = new AuthenticationTrustResolverImpl();
    private final Google2faFailureHandler google2faFailureHandler = new Google2faFailureHandler();
    private final RequestMatcher urlIs2fa = PathPatternRequestMatcher.withDefaults().matcher("/user/verify2fa");
    private final RequestMatcher urlResources = PathPatternRequestMatcher.withDefaults().matcher("/resources/**");

    StaticResourceRequest.StaticResourceRequestMatcher staticResourceRequestMatcher = PathRequest.toStaticResources().atCommonLocations();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (urlIs2fa.matches(request) || urlResources.matches(request) || staticResourceRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) {
            log.debug("Processing 2FA Filter");
            if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof Users) {
                Users user = (Users) authentication.getPrincipal();
                if (user.isUseGoogle2fa() && user.isGoogle2faRequired()) {
                    log.debug("2FAA Required");

                    google2faFailureHandler.onAuthenticationFailure(request,response, null);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}