package edu.sabanciuniv.hotelbookingapp.controller;

import edu.sabanciuniv.hotelbookingapp.model.dto.HotelAvailabilityDTO;
import edu.sabanciuniv.hotelbookingapp.model.dto.HotelSearchDTO;
import edu.sabanciuniv.hotelbookingapp.service.HotelSearchService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HotelSearchController {

    private final HotelSearchService hotelSearchService;

    // Keresési űrlap megjelenítése
    @GetMapping("/search")
    public String showSearchForm(@ModelAttribute("hotelSearchDTO") HotelSearchDTO hotelSearchDTO) {
        return "hotelsearch/search";
    }

    // Szállodák keresése város és dátum alapján
    @PostMapping("/search")
    public String findAvailableHotelsByCityAndDate(@Valid @ModelAttribute("hotelSearchDTO") HotelSearchDTO hotelSearchDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "hotelsearch/search";
        }
        try {
            validateCheckinAndCheckoutDates(hotelSearchDTO.getCheckinDate(), hotelSearchDTO.getCheckoutDate());
        } catch (IllegalArgumentException e) {
            result.rejectValue("checkoutDate", null, e.getMessage());
            return "hotelsearch/search";
        }

        // Átirányítás a keresési eredmények oldalra paraméterekkel
        return String.format("redirect:/search-results?city=%s&checkinDate=%s&checkoutDate=%s", hotelSearchDTO.getCity(), hotelSearchDTO.getCheckinDate(), hotelSearchDTO.getCheckoutDate());
    }

    // Keresési eredmények megjelenítése
    @GetMapping("/search-results")
    public String showSearchResults(@RequestParam String city, @RequestParam String checkinDate, @RequestParam String checkoutDate, Model model, RedirectAttributes redirectAttributes) {
        try {
            LocalDate parsedCheckinDate = LocalDate.parse(checkinDate);
            LocalDate parsedCheckoutDate = LocalDate.parse(checkoutDate);

            validateCheckinAndCheckoutDates(parsedCheckinDate, parsedCheckoutDate);

            log.info("Szállodák keresése: város = {}, dátumok = {} - {}", city, checkinDate, checkoutDate);
            List<HotelAvailabilityDTO> hotels = hotelSearchService.findAvailableHotelsByCityAndDate(city, parsedCheckinDate, parsedCheckoutDate);

            if (hotels.isEmpty()) {
                model.addAttribute("noHotelsFound", true);
            }

            long durationDays = ChronoUnit.DAYS.between(parsedCheckinDate, parsedCheckoutDate);

            model.addAttribute("hotels", hotels);
            model.addAttribute("city", city);
            model.addAttribute("days", durationDays);
            model.addAttribute("checkinDate", checkinDate);
            model.addAttribute("checkoutDate", checkoutDate);

        } catch (DateTimeParseException e) {
            log.error("Érvénytelen dátumformátum", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Érvénytelen dátumformátum. Kérjük, használja a keresési űrlapot.");
            return "redirect:/search";
        } catch (IllegalArgumentException e) {
            log.error("Érvénytelen argumentumok", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/search";
        } catch (Exception e) {
            log.error("Hiba történt a szállodák keresésekor", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja újra később.");
            return "redirect:/search";
        }

        return "hotelsearch/search-results";
    }

    // Szálloda részleteinek megjelenítése
    @GetMapping("/hotel-details/{id}")
    public String showHotelDetails(@PathVariable Long id, @RequestParam String checkinDate, @RequestParam String checkoutDate, Model model, RedirectAttributes redirectAttributes) {
        try {
            LocalDate parsedCheckinDate = LocalDate.parse(checkinDate);
            LocalDate parsedCheckoutDate = LocalDate.parse(checkoutDate);

            validateCheckinAndCheckoutDates(parsedCheckinDate, parsedCheckoutDate);

            HotelAvailabilityDTO hotelAvailabilityDTO = hotelSearchService.findAvailableHotelById(id, parsedCheckinDate, parsedCheckoutDate);

            long durationDays = ChronoUnit.DAYS.between(parsedCheckinDate, parsedCheckoutDate);

            model.addAttribute("hotel", hotelAvailabilityDTO);
            model.addAttribute("durationDays", durationDays);
            model.addAttribute("checkinDate", checkinDate);
            model.addAttribute("checkoutDate", checkoutDate);

            return "hotelsearch/hotel-details";

        } catch (DateTimeParseException e) {
            log.error("Érvénytelen dátumformátum", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Érvénytelen dátumformátum. Kérjük, használja a keresési űrlapot.");
            return "redirect:/search";
        } catch (IllegalArgumentException e) {
            log.error("Érvénytelen argumentumok", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/search";
        } catch (EntityNotFoundException e) {
            log.error("Nincs szálloda a megadott ID-vel: {}", id);
            redirectAttributes.addFlashAttribute("errorMessage", "A kiválasztott szálloda már nem érhető el. Kérjük, indítson új keresést.");
            return "redirect:/search";
        } catch (Exception e) {
            log.error("Hiba történt a szállodák keresésekor", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Váratlan hiba történt. Kérjük, próbálja újra később.");
            return "redirect:/search";
        }
    }

    // Dátumok validálása
    private void validateCheckinAndCheckoutDates(LocalDate checkinDate, LocalDate checkoutDate) {
        if (checkinDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A bejelentkezés dátuma nem lehet a múltban");
        }
        if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
            throw new IllegalArgumentException("A kijelentkezés dátuma nem lehet korábbi a bejelentkezésnél");
        }
    }

}
