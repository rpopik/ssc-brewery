package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {

    public boolean customerIdMatches(Authentication authentication, UUID customerId) {
        Users authenticatedUser = (Users) authentication.getPrincipal();

        if (authenticatedUser != null && authenticatedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"))){
            return true;
        }
        log.debug("Auth User Customer Id: " + authenticatedUser.getCustomer().getId() + "Customer Id: " + customerId);
        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
}