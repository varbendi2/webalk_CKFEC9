package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Long id;

    private Long hotelId;

    private RoomType roomType;

    @NotNull(message = "Szobák száma nem lehet üres")
    @PositiveOrZero(message = "A szobák száma nem lehet kevesebb, mint 0")
    private Integer roomCount;

    @NotNull(message = "Ár nem lehet üres")
    @PositiveOrZero(message = "Az éjszakai ár nem lehet kevesebb, mint 0")
    private Double pricePerNight;

}
