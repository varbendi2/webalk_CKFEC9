package edu.sabanciuniv.hotelbookingapp.controller;

import edu.sabanciuniv.hotelbookingapp.exception.HotelAlreadyExistsException;
import edu.sabanciuniv.hotelbookingapp.model.dto.BookingDTO;
import edu.sabanciuniv.hotelbookingapp.model.enums.RoomType;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelRegistrationDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.RoomDTO;
import edu.sabanciuniv.hotelbookingapp.service.BookingService;
import edu.sabanciuniv.hotelbookingapp.service.HotelService;
import edu.sabanciuniv.hotelbookingapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
@Slf4j
public class HotelManagerController {

    private final HotelService hotelService;
    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "hotelmanager/dashboard";
    }

    @GetMapping("/hotels/add")
    public String showAddHotelForm(Model model) {
        HotelRegistrationDTO hotelRegistrationDTO = new HotelRegistrationDTO();

        // Kezdeti szobák inicializálása: EGYÁGYAS és KÉTÁGYAS típusok
        RoomDTO singleRoom = new RoomDTO(null, null, RoomType.SINGLE, 0, 0.0);
        RoomDTO doubleRoom = new RoomDTO(null, null, RoomType.DOUBLE, 0, 0.0);
        hotelRegistrationDTO.getRoomDTOs().add(singleRoom);
        hotelRegistrationDTO.getRoomDTOs().add(doubleRoom);

        model.addAttribute("hotel", hotelRegistrationDTO);
        return "hotelmanager/hotels-add";
    }

    @PostMapping("/hotels/add")
    public String addHotel(@Valid @ModelAttribute("hotel") HotelRegistrationDTO hotelRegistrationDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("A szálloda létrehozása érvényességi hibák miatt nem sikerült: {}", result.getAllErrors());
            return "hotelmanager/hotels-add";
        }
        try {
            hotelService.saveHotel(hotelRegistrationDTO);
            redirectAttributes.addFlashAttribute("message", "Szálloda (" + hotelRegistrationDTO.getName() + ") sikeresen hozzáadva");
            return "redirect:/manager/hotels";
        } catch (HotelAlreadyExistsException e) {
            result.rejectValue("name", "hotel.exists", e.getMessage());
            return "hotelmanager/hotels-add";
        }
    }

    @GetMapping("/hotels")
    public String listHotels(Model model) {
        Long managerId = getCurrentManagerId();
        List<HotelDTO> hotelList = hotelService.findAllHotelDtosByManagerId(managerId);
        model.addAttribute("hotels", hotelList);
        return "hotelmanager/hotels";
    }

    @GetMapping("/hotels/edit/{id}")
    public String showEditHotelForm(@PathVariable Long id, Model model) {
        Long managerId = getCurrentManagerId();
        HotelDTO hotelDTO = hotelService.findHotelByIdAndManagerId(id, managerId);
        model.addAttribute("hotel", hotelDTO);
        return "hotelmanager/hotels-edit";
    }

    @PostMapping("/hotels/edit/{id}")
    public String editHotel(@PathVariable Long id, @Valid @ModelAttribute("hotel") HotelDTO hotelDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "hotelmanager/hotels-edit";
        }
        try {
            Long managerId = getCurrentManagerId();
            hotelDTO.setId(id);
            hotelService.updateHotelByManagerId(hotelDTO, managerId);
            redirectAttributes.addFlashAttribute("message", "Szálloda (ID: " + id + ") sikeresen frissítve");
            return "redirect:/manager/hotels";

        } catch (HotelAlreadyExistsException e) {
            result.rejectValue("name", "hotel.exists", e.getMessage());
            return "hotelmanager/hotels-edit";
        } catch (EntityNotFoundException e) {
            result.rejectValue("id", "hotel.notfound", e.getMessage());
            return "hotelmanager/hotels-edit";
        }
    }

    @PostMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id) {
        Long managerId = getCurrentManagerId();
        hotelService.deleteHotelByIdAndManagerId(id, managerId);
        return "redirect:/manager/hotels";
    }

    @GetMapping("/bookings")
    public String listBookings(Model model, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            List<BookingDTO> bookingDTOs = bookingService.findBookingsByManagerId(managerId);
            model.addAttribute("bookings", bookingDTOs);

            return "hotelmanager/bookings";
        } catch (EntityNotFoundException e) {
            log.error("Nem található foglalás a megadott menedzser ID-hoz", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Foglalások nem találhatóak. Kérjük, próbálja meg később.");
            return "redirect:/manager/dashboard";
        } catch (Exception e) {
            log.error("Hiba történt a foglalások listázása közben", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja meg később.");
            return "redirect:/manager/dashboard";
        }
    }

    @GetMapping("/bookings/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Long managerId = getCurrentManagerId();
            BookingDTO bookingDTO = bookingService.findBookingByIdAndManagerId(id, managerId);
            model.addAttribute("bookingDTO", bookingDTO);

            LocalDate checkinDate = bookingDTO.getCheckinDate();
            LocalDate checkoutDate = bookingDTO.getCheckoutDate();
            long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            model.addAttribute("days", durationDays);

            return "hotelmanager/bookings-details";
        } catch (EntityNotFoundException e) {
            log.error("Nem található foglalás a megadott azonosítóval", e);
            redirectAttributes.addFlashAttribute("errorMessage", "A foglalás nem található. Kérjük, próbálja meg később.");
            return "redirect:/manager/dashboard";
        } catch (Exception e) {
            log.error("Hiba történt a foglalás részleteinek megjelenítése közben", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja meg később.");
            return "redirect:/manager/dashboard";
        }
    }

    private Long getCurrentManagerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUsername(username).getHotelManager().getId();
    }
}
