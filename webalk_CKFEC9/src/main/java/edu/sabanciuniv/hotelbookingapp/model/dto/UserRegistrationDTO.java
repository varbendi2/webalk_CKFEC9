package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationDTO {

    @NotBlank(message = "Az email cím nem lehet üres")
    @Email(message = "Érvénytelen email cím")
    private String username;

    @NotBlank(message = "A jelszó nem lehet üres")
    @Size(min = 6, max = 20, message = "A jelszónak 6 és 20 karakter közöttinek kell lennie")
    private String password;

    @NotBlank(message = "A név nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "A név csak betűket tartalmazhat")
    private String name;

    @NotBlank(message = "A vezetéknév nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "A vezetéknév csak betűket tartalmazhat")
    private String lastName;

    private RoleType roleType;

}
