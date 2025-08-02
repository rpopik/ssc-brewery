package guru.sfg.brewery.domain.security.perms;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(value = "hasAuthority('customer.delete')")
public @interface CustomerDeletePerm {
}