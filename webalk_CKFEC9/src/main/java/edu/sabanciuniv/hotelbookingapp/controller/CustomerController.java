package edu.sabanciuniv.hotelbookingapp.controller;

import edu.sabanciuniv.hotelbookingapp.model.dto.BookingDTO;
import edu.sabanciuniv.hotelbookingapp.service.BookingService;
import edu.sabanciuniv.hotelbookingapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/bookings")
    public String listBookings(Model model, RedirectAttributes redirectAttributes) {
        try {
            Long customerId = getCurrentCustomerId();
            List<BookingDTO> bookingDTOs = bookingService.findBookingsByCustomerId(customerId);
            model.addAttribute("bookings", bookingDTOs);
            return "customer/bookings";
        } catch (EntityNotFoundException e) {
            log.error("Nem található ügyfél a megadott azonosítóval", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Az ügyfél nem található. Kérjük, jelentkezzen be újra.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Hiba történt a foglalások listázása közben", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja meg később.");
            return "redirect:/";
        }
    }

    @GetMapping("/bookings/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Long customerId = getCurrentCustomerId();
            BookingDTO bookingDTO = bookingService.findBookingByIdAndCustomerId(id, customerId);
            model.addAttribute("bookingDTO", bookingDTO);

            LocalDate checkinDate = bookingDTO.getCheckinDate();
            LocalDate checkoutDate = bookingDTO.getCheckoutDate();
            long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
            model.addAttribute("days", durationDays);

            return "customer/bookings-details";
        } catch (EntityNotFoundException e) {
            log.error("Nem található foglalás a megadott azonosítóval", e);
            redirectAttributes.addFlashAttribute("errorMessage", "A foglalás nem található. Kérjük, próbálja meg később.");
            return "redirect:/customer/bookings";
        } catch (Exception e) {
            log.error("Hiba történt a foglalás részleteinek megjelenítése közben", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja meg később.");
            return "redirect:/";
        }
    }

    private Long getCurrentCustomerId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUsername(username).getCustomer().getId();
    }

}
