package gr.grodov.grsso.controller;

import gr.grodov.grsso.controller.dto.RegistrationRequest;
import gr.grodov.grsso.security.exceptions.EmailAlreadyExistsException;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserInfoService userInfoService;

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request, @RequestParam(value = "error", required = false) String error) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        model.addAttribute("request", new RegistrationRequest());
        return "register";
    }

    @GetMapping("/provider-error")
    public String providerError(Authentication authentication) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        return "provider-error";
    }

    @GetMapping(value = "/")
    public String home() {
        return "home";
    }

    @PostMapping("/register")
    public String register(
        @Valid @ModelAttribute("request") RegistrationRequest request,
        BindingResult bindingResult,
        Authentication authentication
    ) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userInfoService.createNewUser(request.getEmail(), request.getPassword());
        } catch (EmailAlreadyExistsException _) {
            bindingResult.rejectValue("email", "errors.email_exist");
        } catch (Exception _) {
            bindingResult.rejectValue("email", "errors.unknown_registration");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }
        return "redirect:login/";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null &&
            authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken);
    }
}
