package edu.sabanciuniv.hotelbookingapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HotelSearchDTO {

    @NotBlank(message = "A város nem lehet üres")
    @Pattern(regexp = "^(?!\\s*$)[A-Za-z '-]+$", message = "A város csak betűket, aposztrófokat (') és kötőjeleket (-) tartalmazhat")
    private String city;

    @NotNull(message = "A bejelentkezési dátum nem lehet üres")
    @FutureOrPresent(message = "A bejelentkezési dátum nem lehet a múltban")
    private LocalDate checkinDate;

    @NotNull(message = "A kijelentkezési dátum nem lehet üres")
    private LocalDate checkoutDate;
}
