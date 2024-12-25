package edu.sabanciuniv.hotelbookingapp.model.dto;

import edu.sabanciuniv.hotelbookingapp.validation.annotation.CardExpiry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDTO {

    @NotBlank(message = "A kártyatulajdonos neve nem lehet üres")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "A kártyatulajdonos neve csak betűket és szóközöket tartalmazhat")
    @Size(min = 3, max = 50, message = "A kártyatulajdonos neve 3 és 50 karakter között kell legyen")
    private String cardholderName;

    // 16 számjegy + Luhn ellenőrzés
    @CreditCardNumber(message = "Érvénytelen bankkártya szám")
    private String cardNumber;

    @CardExpiry
    private String expirationDate;

    @Pattern(regexp = "^\\d{3}$", message = "A CVC kódnak 3 számjegyűnek kell lennie")
    private String cvc;
}
