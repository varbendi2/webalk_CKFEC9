package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRegistrationDTO {

    @NotBlank(message = "A szálloda neve nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "A szálloda neve csak betűket és számokat tartalmazhat")
    private String name;

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private List<RoomDTO> roomDTOs = new ArrayList<>();

}
