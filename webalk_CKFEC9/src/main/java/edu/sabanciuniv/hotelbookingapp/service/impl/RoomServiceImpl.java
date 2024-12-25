package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Hotel;
import edu.sabanciuniv.hotelbookingapp.model.Room;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomDTO;
import edu.sabanciuniv.hotelbookingapp.repository.RoomRepository;
import edu.sabanciuniv.hotelbookingapp.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room saveRoom(RoomDTO roomDTO, Hotel hotel) {
        log.info("Új szoba mentése: {}", roomDTO);
        Room room = mapRoomDtoToRoom(roomDTO, hotel);
        room = roomRepository.save(room);
        log.info("Sikeresen mentett szoba ID-ja: {}", room.getId());
        return room;
    }

    @Override
    public List<Room> saveRooms(List<RoomDTO> roomDTOs, Hotel hotel) {
        log.info("Szobák mentése: {}", roomDTOs);
        List<Room> rooms = roomDTOs.stream()
                .map(roomDTO -> saveRoom(roomDTO, hotel)) // minden szoba mentése
                .collect(Collectors.toList());
        log.info("Sikeresen mentett szobák: {}", rooms);
        return rooms;
    }

    @Override
    public Optional<Room> findRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findRoomsByHotelId(Long hotelId) {
        return null;
    }

    @Override
    public Room updateRoom(RoomDTO roomDTO) {
        log.info("Szoba frissítése ID-val: {}", roomDTO.getId());
        Room existingRoom = roomRepository.findById(roomDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Szoba nem található"));

        log.info("Szoba ID-ja: {} megtalálva", roomDTO.getId());

        existingRoom.setRoomType(roomDTO.getRoomType());
        existingRoom.setRoomCount(roomDTO.getRoomCount());
        existingRoom.setPricePerNight(roomDTO.getPricePerNight());

        Room updatedRoom = roomRepository.save(existingRoom);
        log.info("Sikeresen frissítettük a szobát ID-val: {}", existingRoom.getId());
        return updatedRoom;
    }

    @Override
    public void deleteRoom(Long id) {

    }

    @Override
    public Room mapRoomDtoToRoom(RoomDTO roomDTO, Hotel hotel) {
        log.debug("RoomDTO leképezése Room-ra: {}", roomDTO);
        Room room = Room.builder()
                .hotel(hotel)
                .roomType(roomDTO.getRoomType())
                .roomCount(roomDTO.getRoomCount())
                .pricePerNight(roomDTO.getPricePerNight())
                .build();
        log.debug("Leképezett Room: {}", room);
        return room;

    }

    @Override
    public RoomDTO mapRoomToRoomDto(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomType(room.getRoomType())
                .roomCount(room.getRoomCount())
                .pricePerNight(room.getPricePerNight())
                .build();
    }
}
