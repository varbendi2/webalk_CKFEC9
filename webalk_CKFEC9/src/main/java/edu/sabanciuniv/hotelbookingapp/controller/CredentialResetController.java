package edu.sabanciuniv.hotelbookingapp.controller;

/*@Controller
import edu.sabanciuniv.hotelbookingapp.model.dto.ResetPasswordDTO;
import edu.sabanciuniv.hotelbookingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CredentialResetController {

    private final UserService userService;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@ModelAttribute("resetPassword") ResetPasswordDTO resetPasswordDTO) {
        return "reset-password";
    }

    @PostMapping("/reset-password/save")
    public String resetUserPassword(@Valid @ModelAttribute("resetPassword") ResetPasswordDTO resetPasswordDTO, BindingResult result) {
        if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "password.mismatch", "Az új jelszavak nem egyeznek meg!");
            return "reset-password";
        }

        try {
            userService.resetPassword(resetPasswordDTO);
        } catch (IllegalArgumentException e) {
            result.rejectValue("oldPassword", "password.incorrect", "A régi jelszó helytelen!");
            return "reset-password";
        }

        return "redirect:/user-dashboard?resetSuccess";
    }

}


 */
