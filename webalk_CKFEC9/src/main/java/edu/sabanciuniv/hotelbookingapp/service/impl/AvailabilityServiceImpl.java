package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Availability;
import edu.sabanciuniv.hotelbookingapp.model.Hotel;
import edu.sabanciuniv.hotelbookingapp.model.Room;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomSelectionDTO;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import edu.sabanciuniv.hotelbookingapp.repository.AvailabilityRepository;
import edu.sabanciuniv.hotelbookingapp.service.AvailabilityService;
import edu.sabanciuniv.hotelbookingapp.service.HotelService;
import edu.sabanciuniv.hotelbookingapp.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final HotelService hotelService;
    private final RoomService roomService;

    @Override
    public Integer getMinAvailableRooms(Long roomId, LocalDate checkinDate, LocalDate checkoutDate) {
        log.info("Legkisebb elérhető szobák lekérése a(z) {} szobaazonosítóhoz {} és {} között", roomId, checkinDate, checkoutDate);

        Room room = roomService.findRoomById(roomId).orElseThrow(() -> new EntityNotFoundException("A szoba nem található"));

        // Lekérjük a foglalási időszak alatt elérhető minimum szobaszámot egy adott szobaazonosítóhoz.
        return availabilityRepository.getMinAvailableRooms(roomId, checkinDate, checkoutDate)
                .orElse(room.getRoomCount()); // Ha nincs rekord, teljes elérhetőséget feltételezünk
    }

    @Override
    public void updateAvailabilities(long hotelId, LocalDate checkinDate, LocalDate checkoutDate, List<RoomSelectionDTO> roomSelections) {
        Hotel hotel = hotelService.findHotelById(hotelId).orElseThrow(() -> new EntityNotFoundException("A hotel nem található"));
        log.info("Elérhetőségek frissítésének megkísérlése a(z) {} hotelazonosítóhoz {} és {} között", hotelId, checkinDate, checkoutDate);

        roomSelections = roomSelections.stream()
                .filter(roomSelection -> roomSelection.getCount() > 0)
                .collect(Collectors.toList());

        // A felhasználó által választott szobák feldolgozása
        for (RoomSelectionDTO roomSelection : roomSelections) {
            RoomType roomType = roomSelection.getRoomType();
            int selectedCount = roomSelection.getCount();
            log.debug("{} szoba feldolgozása a(z) {} típusból", selectedCount, roomType);

            // Szoba keresése roomType alapján az adott hotelben
            Room room = hotel.getRooms().stream()
                    .filter(r -> r.getRoomType() == roomType)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("A szobatípus nem található"));

            // Dátumok végigiterálása és elérhetőségek frissítése vagy létrehozása
            for (LocalDate date = checkinDate; date.isBefore(checkoutDate); date = date.plusDays(1)) {
                final LocalDate currentDate = date; // Ideiglenes végleges változó
                Availability availability = availabilityRepository.findByRoomIdAndDate(room.getId(), date)
                        .orElseGet(() -> Availability.builder()
                                .hotel(hotel)
                                .date(currentDate)
                                .room(room)
                                .availableRooms(room.getRoomCount())
                                .build());

                // Az elérhető szobák számának csökkentése a kiválasztott darabszámmal
                int updatedAvailableRooms = availability.getAvailableRooms() - selectedCount;
                if (updatedAvailableRooms < 0) {
                    throw new IllegalArgumentException("A kiválasztott szobák száma meghaladja az elérhető szobákat ezen a dátumon: " + currentDate);
                }
                availability.setAvailableRooms(updatedAvailableRooms);

                availabilityRepository.save(availability);
            }
        }
        log.info("Elérhetőségek sikeresen frissítve a(z) {} hotelazonosítóhoz {} és {} között", hotelId, checkinDate, checkoutDate);
    }

}
