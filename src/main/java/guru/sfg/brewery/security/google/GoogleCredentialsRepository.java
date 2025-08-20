package guru.sfg.brewery.security.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialsRepository implements ICredentialRepository {
    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String username) {
        Users user = userRepository.findByUsername(username).orElseThrow();
        return user.getGoogle2faSecretKey();
    }

    @Override
    public void saveUserCredentials(String username, String secretKey, int validationCode,
                                    List<Integer> scratchCodes) {
        Users user = userRepository.findByUsername(username).orElseThrow();
        user.setGoogle2faSecretKey(secretKey);
        user.setUseGoogle2fa(true);
        userRepository.save(user);
    }
}