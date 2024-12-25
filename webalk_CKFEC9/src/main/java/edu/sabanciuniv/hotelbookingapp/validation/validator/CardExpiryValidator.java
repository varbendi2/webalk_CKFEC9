package edu.sabanciuniv.hotelbookingapp.validation.validator;

import edu.sabanciuniv.hotelbookingapp.validation.annotation.CardExpiry;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DateTimeException;
import java.time.YearMonth;

public class CardExpiryValidator implements ConstraintValidator<CardExpiry, String> {

    @Override
    public void initialize(CardExpiry constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Ha az érték null vagy nem felel meg az érvényes hónap/év formátumnak, érvénytelen
        if (value == null || !value.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            return false;
        }

        try {
            // Az értéket hónapra és évre bontjuk
            String[] parts = value.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]); // Feltételezzük, hogy a 21. században van

            // A kártya lejárati dátuma
            YearMonth cardExpiry = YearMonth.of(year, month);
            // Az aktuális hónap
            YearMonth currentMonth = YearMonth.now();

            // Ellenőrizzük, hogy a kártya lejárati dátuma nem korábbi hónapra vonatkozik
            return cardExpiry.isAfter(currentMonth) || cardExpiry.equals(currentMonth);
        } catch (NumberFormatException | DateTimeException e) {
            // Ha hiba történik a hónap vagy év értelmezése közben, érvénytelen
            return false;
        }
    }
}
