package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Hotel;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import edu.sabanciuniv.hotelbookingapp.model.dto.AddressDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelAvailabilityDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomDTO;
import edu.sabanciuniv.hotelbookingapp.repository.HotelRepository;
import edu.sabanciuniv.hotelbookingapp.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelSearchServiceImpl implements HotelSearchService {

    private final HotelRepository hotelRepository;
    private final AddressService addressService;
    private final RoomService roomService;
    private final AvailabilityService availabilityService;

    @Override
    public List<HotelAvailabilityDTO> findAvailableHotelsByCityAndDate(String city, LocalDate checkinDate, LocalDate checkoutDate) {
        validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

        log.info("Szállodák keresése {} városban, elérhető szobákkal {} és {} között", city, checkinDate, checkoutDate);

        // A foglalási időtartam napjainak száma
        Long numberOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        // 1. Szállodák keresése, amelyek megfelelnek a kritériumoknak (legalább 1 elérhető szoba az időszak során)
        List<Hotel> hotelsWithAvailableRooms = hotelRepository.findHotelsWithAvailableRooms(city, checkinDate, checkoutDate, numberOfDays);

        // 2. Szállodák keresése, amelyeknél az egész időszakra nincs rendelkezésre állási adat
        List<Hotel> hotelsWithoutAvailabilityRecords = hotelRepository.findHotelsWithoutAvailabilityRecords(city, checkinDate, checkoutDate);

        // 3. Szállodák keresése, amelyek részleges elérhetőséggel rendelkeznek; néhány nap elérhetőséggel és néhány nap adat nélkül
        List<Hotel> hotelsWithPartialAvailabilityRecords = hotelRepository.findHotelsWithPartialAvailabilityRecords(city, checkinDate, checkoutDate, numberOfDays);

        // Szállodák kombinálása és duplikációk eltávolítása halmaz segítségével
        Set<Hotel> combinedHotels = new HashSet<>(hotelsWithAvailableRooms);
        combinedHotels.addAll(hotelsWithoutAvailabilityRecords);
        combinedHotels.addAll(hotelsWithPartialAvailabilityRecords);

        log.info("Összesen {} szálloda található elérhető szobákkal", combinedHotels.size());

        // A kombinált szállodalista DTO-vá alakítása a válaszhoz
        return combinedHotels.stream()
                .map(hotel -> mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate))
                .collect(Collectors.toList());
    }

    @Override
    public HotelAvailabilityDTO findAvailableHotelById(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate) {
        validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

        log.info("Szálloda keresése azonosító alapján {} elérhető szobákkal {} és {} között", hotelId, checkinDate, checkoutDate);

        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()) {
            log.error("Nem található szálloda az azonosítóval: {}", hotelId);
            throw new EntityNotFoundException("Szálloda nem található");
        }

        Hotel hotel = hotelOptional.get();
        return mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate);
    }

    @Override
    public HotelAvailabilityDTO mapHotelToHotelAvailabilityDto(Hotel hotel, LocalDate checkinDate, LocalDate checkoutDate) {
        List<RoomDTO> roomDTOs = hotel.getRooms().stream()
                .map(roomService::mapRoomToRoomDto)  // Minden szoba RoomDTO-vá alakítása
                .collect(Collectors.toList());

        AddressDTO addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());
        
        HotelAvailabilityDTO hotelAvailabilityDTO = HotelAvailabilityDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .addressDTO(addressDTO)
                .roomDTOs(roomDTOs)
                .build();
        
        // Minden szobatípusnál az elérhető szobák minimumának keresése az időszakban
        int maxAvailableSingleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.SINGLE)
                .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
                .max()
                .orElse(0); // Feltételezzük, hogy nincs elérhető szoba, ha egyik sem felel meg
        hotelAvailabilityDTO.setMaxAvailableSingleRooms(maxAvailableSingleRooms);

        int maxAvailableDoubleRooms = hotel.getRooms().stream()
                .filter(room -> room.getRoomType() == RoomType.DOUBLE)
                .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
                .max()
                .orElse(0); // Feltételezzük, hogy nincs elérhető szoba, ha egyik sem felel meg
        hotelAvailabilityDTO.setMaxAvailableDoubleRooms(maxAvailableDoubleRooms);

        return hotelAvailabilityDTO;
    }

    private void validateCheckinAndCheckoutDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A bejelentkezési dátum nem lehet a múltban");
        }
        if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
            throw new IllegalArgumentException("A kijelentkezési dátumnak későbbinek kell lennie a bejelentkezési dátumnál");
        }
    }

}
