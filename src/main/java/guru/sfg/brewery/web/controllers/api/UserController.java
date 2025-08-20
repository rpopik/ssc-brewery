package guru.sfg.brewery.web.controllers.api;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import guru.sfg.brewery.domain.security.Users;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model){

        Users user = getUser();

        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL("SFG", user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));
        log.debug("Google QR URL: {}", url);

        model.addAttribute("googleurl",url);
        return "user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2fa(@RequestParam Integer verifyCode){

        Users user = getUser();

        log.debug("Entered Verify Code: {}", verifyCode);

        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)){
            Users savedUser = userRepository.findById(user.getId()).orElseThrow();
            savedUser.setUseGoogle2fa(true);
            userRepository.save(savedUser);
            return "/index";
        } else {
            //bad code
            return "/user/register2fa";
        }
    }

    @GetMapping("/verify2fa")
    public String verify2fa(){
        return "/user/verify2fa";
    }


    @PostMapping("/verify2fa")
    public String verifyPostOf2fa(@RequestParam Integer verifyCode){
        Users user = getUser();
        if(googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)){
            ((Users)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).setGoogle2faRequired(false);
            return "/index";
        } else {
            return "/user/verify2fa";
        }
    }

    private static Users getUser() {
        return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}