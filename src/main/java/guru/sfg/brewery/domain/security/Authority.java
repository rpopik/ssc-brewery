package guru.sfg.brewery.domain.security;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String permission;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}