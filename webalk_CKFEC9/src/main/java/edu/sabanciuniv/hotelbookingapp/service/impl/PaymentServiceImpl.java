package edu.sabanciuniv.hotelbookingapp.service.impl;

import edu.sabanciuniv.hotelbookingapp.model.Booking;
import edu.sabanciuniv.hotelbookingapp.model.Payment;
import edu.sabanciuniv.hotelbookingapp.model.dto.BookingInitiationDTO;
import edu.sabanciuniv.hotelbookingapp.model.enums.Currency;
import edu.sabanciuniv.hotelbookingapp.model.enums.PaymentMethod;
import edu.sabanciuniv.hotelbookingapp.model.enums.PaymentStatus;
import edu.sabanciuniv.hotelbookingapp.repository.PaymentRepository;
import edu.sabanciuniv.hotelbookingapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking) {
        Payment payment = Payment.builder()
                .booking(booking)
                .totalPrice(bookingInitiationDTO.getTotalPrice())
                .paymentStatus(PaymentStatus.COMPLETED) // Feltételezve, hogy a fizetés befejeződött
                .paymentMethod(PaymentMethod.CREDIT_CARD) // Alapértelmezett: HITELKÁRTYA
                .currency(Currency.HUF) // Alapértelmezett: HUF
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Fizetés mentve a tranzakciós azonosítóval: {}", savedPayment.getTransactionId());

        return savedPayment;
    }
}
