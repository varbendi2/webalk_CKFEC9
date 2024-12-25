package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Az email cím nem lehet üres")
    @Email(message = "Érvénytelen email cím")
    private String username;

    @NotBlank(message = "A név nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "A név csak betűket tartalmazhat")
    private String name;

    @NotBlank(message = "A vezetéknév nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "A vezetéknév csak betűket tartalmazhat")
    private String lastName;

    private Role role;

}
