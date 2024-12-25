package edu.sabanciuniv.hotelbookingapp.initialize;

import edu.sabanciuniv.hotelbookingapp.model.*;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoleType;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import edu.sabanciuniv.hotelbookingapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final HotelManagerRepository hotelManagerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final HotelRepository hotelRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    @Transactional
    public void run(String... args) {

        try {
            log.warn("Ellenőrzés, hogy szükséges-e tesztadatok mentése...");

            if (roleRepository.count() == 0 && userRepository.count() == 0) {
                log.info("Tesztadatok mentése elindítva");

                Role adminRole = new Role(RoleType.ADMIN);
                Role customerRole = new Role(RoleType.CUSTOMER);
                Role hotelManagerRole = new Role(RoleType.HOTEL_MANAGER);

                roleRepository.save(adminRole);
                roleRepository.save(customerRole);
                roleRepository.save(hotelManagerRole);
                log.info("Szerepkör adat mentése");

                User user1 = User.builder().username("admin@szalloda.hu").password(passwordEncoder.encode("1")).name("Admin").lastName("Admin").role(adminRole).build();
                User user2 = User.builder().username("vasarlo1@szalloda.hu").password(passwordEncoder.encode("1")).name("Kaya Alp").lastName("Kökér").role(customerRole).build();
                User user3 = User.builder().username("manager1@szalloda.hu").password(passwordEncoder.encode("1")).name("János").lastName("Nagy").role(hotelManagerRole).build();
                User user4 = User.builder().username("manager2@szalloda.hu").password(passwordEncoder.encode("1")).name("Miklós").lastName("Kovács").role(hotelManagerRole).build();

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);

                Admin admin1 = Admin.builder().user(user1).build();
                Customer c1 = Customer.builder().user(user2).build();
                HotelManager hm1 = HotelManager.builder().user(user3).build();
                HotelManager hm2 = HotelManager.builder().user(user4).build();

                adminRepository.save(admin1);
                customerRepository.save(c1);
                hotelManagerRepository.save(hm1);
                hotelManagerRepository.save(hm2);
                log.info("Felhasználói adatok mentése");

                Address addressBp1 = Address.builder().addressLine("Kossuth Lajos utca 19, 1055").city("Budapest")
                        .country("Magyarország").build();
                Address addressBp2 = Address.builder().addressLine("Váci utca 28, 1052").city("Budapest")
                        .country("Magyarország").build();
                Address addressBp3 = Address.builder().addressLine("Andrássy út 32, 1061").city("Budapest")
                        .country("Magyarország").build();

                Address addressBerlin1 = Address.builder().addressLine("Unter den Linden 77").city("Berlin")
                        .country("Németország").build();
                Address addressBerlin2 = Address.builder().addressLine("Potsdamer Platz 3, Mitte, 10785").city("Berlin")
                        .country("Németország").build();
                Address addressBerlin3 = Address.builder().addressLine("Budapester Str. 2, Mitte, 10787").city("Berlin")
                        .country("Németország").build();

                addressRepository.save(addressBp1);
                addressRepository.save(addressBp2);
                addressRepository.save(addressBp3);
                addressRepository.save(addressBerlin1);
                addressRepository.save(addressBerlin2);
                addressRepository.save(addressBerlin3);

                Hotel hotelBp1 = Hotel.builder().name("Gellért Szálló")
                        .address(addressBp1).hotelManager(hm1).build();
                Hotel hotelBp2 = Hotel.builder().name("Hilton Budapest")
                        .address(addressBp2).hotelManager(hm1).build();
                Hotel hotelBp3 = Hotel.builder().name("Four Seasons Budapest")
                        .address(addressBp3).hotelManager(hm1).build();

                Hotel hotelBerlin1 = Hotel.builder().name("Hotel Adlon Kempinski Berlin")
                        .address(addressBerlin1).hotelManager(hm2).build();
                Hotel hotelBerlin2 = Hotel.builder().name("The Ritz-Carlton Berlin")
                        .address(addressBerlin2).hotelManager(hm2).build();
                Hotel hotelBerlin3 = Hotel.builder().name("InterContinental Berlin")
                        .address(addressBerlin3).hotelManager(hm2).build();

                Room singleRoomBp1 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(370).roomCount(35).hotel(hotelBp1).build();
                Room doubleRoomBp1 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(459).roomCount(45).hotel(hotelBp1).build();

                Room singleRoomBp2 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(700).roomCount(25).hotel(hotelBp2).build();
                Room doubleRoomBp2 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(890).roomCount(30).hotel(hotelBp2).build();

                Room singleRoomBp3 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(691).roomCount(30).hotel(hotelBp3).build();
                Room doubleRoomBp3 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(800).roomCount(75).hotel(hotelBp3).build();

                Room singleRoomBerlin1 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(120.0).roomCount(25).hotel(hotelBerlin1).build();
                Room doubleRoomBerlin1 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(250.0).roomCount(15).hotel(hotelBerlin1).build();

                Room singleRoomBerlin2 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(300).roomCount(50).hotel(hotelBerlin2).build();
                Room doubleRoomBerlin2 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(400).roomCount(50).hotel(hotelBerlin2).build();

                Room singleRoomBerlin3 = Room.builder().roomType(RoomType.SINGLE)
                        .pricePerNight(179).roomCount(45).hotel(hotelBerlin3).build();
                Room doubleRoomBerlin3 = Room.builder().roomType(RoomType.DOUBLE)
                        .pricePerNight(256).roomCount(25).hotel(hotelBerlin3).build();

                hotelBp1.getRooms().addAll(Arrays.asList(singleRoomBp1,doubleRoomBp1));
                hotelBp2.getRooms().addAll(Arrays.asList(singleRoomBp2,doubleRoomBp2));
                hotelBp3.getRooms().addAll(Arrays.asList(singleRoomBp3,doubleRoomBp3));
                hotelBerlin1.getRooms().addAll(Arrays.asList(singleRoomBerlin1,doubleRoomBerlin1));
                hotelBerlin2.getRooms().addAll(Arrays.asList(singleRoomBerlin2,doubleRoomBerlin2));
                hotelBerlin3.getRooms().addAll(Arrays.asList(singleRoomBerlin3,doubleRoomBerlin3));

                hotelRepository.save(hotelBp1);
                hotelRepository.save(hotelBp2);
                hotelRepository.save(hotelBp3);
                hotelRepository.save(hotelBerlin1);
                hotelRepository.save(hotelBerlin2);
                hotelRepository.save(hotelBerlin3);
                log.info("Szállodai adatok mentése");

                Availability av1Berlin1 = Availability.builder().hotel(hotelBerlin1)
                        .date(LocalDate.of(2023,9,1)).room(singleRoomBerlin1).availableRooms(5).build();
                Availability av2Berlin1 = Availability.builder().hotel(hotelBerlin1)
                        .date(LocalDate.of(2023,9,2)).room(doubleRoomBerlin1).availableRooms(7).build();

                availabilityRepository.save(av1Berlin1);
                availabilityRepository.save(av2Berlin1);
                log.info("Elérhetőségi adatok mentése");

            } else {
                log.info("Tesztadatok mentése nem szükséges");
            }
            log.warn("Alkalmazás kész");
        } catch (DataAccessException e) {
            log.error("Hiba történt az adatmentés közben: " + e.getMessage());
        } catch (Exception e) {
            log.error("Váratlan hiba történt: " + e.getMessage());
        }
    }
}
