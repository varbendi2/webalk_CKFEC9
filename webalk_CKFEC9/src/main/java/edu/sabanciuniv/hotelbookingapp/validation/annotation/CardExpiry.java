package edu.sabanciuniv.hotelbookingapp.validation.annotation;

import edu.sabanciuniv.hotelbookingapp.validation.validator.CardExpiryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// A mezőkre alkalmazható
@Target({ ElementType.FIELD })
// Futásidőben értelmezve
@Retention(RetentionPolicy.RUNTIME)
// A validálásért felelős osztály
@Constraint(validatedBy = CardExpiryValidator.class)
public @interface CardExpiry {

    // Hibaüzenet alapértelmezése
    String message() default "Érvénytelen lejárati dátum";
    
    // Csoportok (default üres)
    Class<?>[] groups() default {};
    
    // További adat (default üres)
    Class<? extends Payload>[] payload() default {};
}
