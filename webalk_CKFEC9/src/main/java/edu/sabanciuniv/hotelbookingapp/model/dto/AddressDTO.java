package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;

    @NotBlank(message = "A cím mező nem lehet üres")
    @Pattern(regexp = "^[A-Za-z0-9 .,:-]*$", message = "A cím csak betűket, számokat és néhány speciális karaktert tartalmazhat (. , : - )")
    private String addressLine;

    @NotBlank(message = "A város nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "A város csak betűket tartalmazhat")
    private String city;

    @NotBlank(message = "Az ország nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Az ország csak betűket tartalmazhat")
    private String country;
}
