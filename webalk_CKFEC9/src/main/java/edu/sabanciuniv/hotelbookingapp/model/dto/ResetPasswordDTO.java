package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordDTO {

    @NotBlank(message = "A jelszó nem lehet üres")
    @Size(min = 6, max = 20, message = "A jelszónak 6 és 20 karakter között kell lennie")
    private String oldPassword;

    @NotBlank(message = "A jelszó nem lehet üres")
    @Size(min = 6, max = 20, message = "A jelszónak 6 és 20 karakter között kell lennie")
    private String newPassword;

    @NotBlank(message = "A jelszó nem lehet üres")
    @Size(min = 6, max = 20, message = "A jelszónak 6 és 20 karakter között kell lennie")
    private String confirmNewPassword;

}
