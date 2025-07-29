package guru.sfg.brewery.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class RestUrlAuthFilter extends AbstractRestAuthFilter{

    public RestUrlAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public String getName(HttpServletRequest request) {
        return request.getParameter("apiKey") != null ? request.getParameter("apiKey") : "";
    }

    @Override
    public String getPassword(HttpServletRequest request) {
        return request.getParameter("apiSecret") != null ? request.getParameter("apiSecret") : "";
    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
//            throws AuthenticationException {
//        // Check if user is already authenticated
//        if (SecurityContextHolder.getContext().getAuthentication() != null &&
//            SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("User is already authenticated: " +
//                    SecurityContextHolder.getContext().getAuthentication().getName());
//            }
//            return SecurityContextHolder.getContext().getAuthentication();
//        }
//
//
//        String password = request.getParameter("apiSecret") != null ? request.getParameter("apiSecret") : "";
//
//        if (logger.isDebugEnabled()) {
//            logger.debug("Attempting authentication for user: " + username);
//        }
//
//        return getAuthentication(username, password);
//    }

}