package edu.sabanciuniv.hotelbookingapp.controller;

import edu.sabanciuniv.hotelbookingapp.exception.UsernameAlreadyExistsException;
import edu.sabanciuniv.hotelbookingapp.model.dto.UserDTO;
import edu.sabanciuniv.hotelbookingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyAccountController {

    private final UserService userService;

    // Vásárlói fiók kezelése
    @GetMapping("/customer/account")
    public String showCustomerAccount(Model model){
        log.debug("A vásárlói fiók megjelenítése");
        addLoggedInUserDataToModel(model);
        return "customer/account";
    }

    @GetMapping("/customer/account/edit")
    public String showCustomerEditForm(Model model){
        log.debug("A vásárlói fiók szerkesztése űrlap megjelenítése");
        addLoggedInUserDataToModel(model);
        return "customer/account-edit";
    }

    @PostMapping("/customer/account/edit")
    public String editCustomerAccount(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result) {
        log.info("A vásárlói fiók adatainak szerkesztése: ID {}", userDTO.getId());
        if (result.hasErrors()) {
            log.warn("Érvényesítési hiba történt a vásárlói fiók szerkesztésekor");
            return "customer/account-edit";
        }
        try {
            userService.updateLoggedInUser(userDTO);
            log.info("Sikeres fiókszerkesztés");
        } catch (UsernameAlreadyExistsException e) {
            log.error("Felhasználónév már létezik", e);
            result.rejectValue("username", "user.exists", "A felhasználónév már regisztrálva van!");
            return "customer/account-edit";
        }
        return "redirect:/customer/account?success";
    }

    // Szállodai menedzser fiók kezelése
    @GetMapping("/manager/account")
    public String showHotelManagerAccount(Model model){
        log.debug("A szállodai menedzser fiók megjelenítése");
        addLoggedInUserDataToModel(model);
        return "hotelmanager/account";
    }

    @GetMapping("/manager/account/edit")
    public String showHotelManagerEditForm(Model model){
        log.debug("A szállodai menedzser fiók szerkesztése űrlap megjelenítése");
        addLoggedInUserDataToModel(model);
        return "hotelmanager/account-edit";
    }

    @PostMapping("/manager/account/edit")
    public String editHotelManagerAccount(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result) {
        log.info("A szállodai menedzser fiók adatainak szerkesztése: ID {}", userDTO.getId());
        if (result.hasErrors()) {
            log.warn("Érvényesítési hiba történt a szállodai menedzser fiók szerkesztésekor");
            return "hotelmanager/account-edit";
        }
        try {
            userService.updateLoggedInUser(userDTO);
            log.info("Sikeres szállodai menedzser fiók szerkesztése");
        } catch (UsernameAlreadyExistsException e) {
            log.error("Felhasználónév már létezik", e);
            result.rejectValue("username", "user.exists", "A felhasználónév már regisztrálva van!");
            return "hotelmanager/account-edit";
        }
        return "redirect:/manager/account?success";
    }

    // Bejelentkezett felhasználó adatainak hozzáadása a modellhez
    private void addLoggedInUserDataToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDTO userDTO = userService.findUserDTOByUsername(username);
        log.info("Bejelentkezett felhasználó adatainak hozzáadása a modellhez: ID {}", userDTO.getId());
        model.addAttribute("user", userDTO);
    }

}
