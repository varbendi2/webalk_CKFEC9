package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HotelDTO {

    private Long id;

    @NotBlank(message = "A szálloda neve nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "A szálloda neve csak betűket és számokat tartalmazhat")
    private String name;

    @Valid
    private AddressDTO addressDTO;

    @Valid
    private List<RoomDTO> roomDTOs = new ArrayList<>();

    private String managerUsername;

}
