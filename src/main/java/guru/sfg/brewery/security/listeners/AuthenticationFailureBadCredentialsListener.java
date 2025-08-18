package guru.sfg.brewery.security.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationFailureBadCredentialsListener {

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {

        if(event.getException() instanceof BadCredentialsException){
            BadCredentialsException badCredentialsException = (BadCredentialsException) event.getException();
            if(badCredentialsException.getAuthenticationRequest() instanceof UsernamePasswordAuthenticationToken){
                UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) badCredentialsException.getAuthenticationRequest();
                log.debug("Bad credentials for user: " + token.getName());

                if(token.getDetails() instanceof WebAuthenticationDetails){
                    WebAuthenticationDetails details = (org.springframework.security.web.authentication.WebAuthenticationDetails) token.getDetails();
                    log.debug("Bad credentials for remote address: " + details.getRemoteAddress());
                }
            }
        }
    }
}