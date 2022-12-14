package app.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.aspect.Logging;
import app.model.Payment;
import app.repository.PaymentRepository;
import app.util.Luhn;

@Service
public class PaymentService {
    private PaymentRepository pr;

    @Autowired
    public PaymentService(PaymentRepository pr) {
        this.pr = pr;
    }

    @Transactional
    public void defaultPayment(String username) { // Placeholder in the Payment table for new users
        Logging.LOG.info("Payment information retrieved");
        this.pr.save(new Payment(username, null, null, null, null, null));
    }

    public Payment getPayment(String username) {
        Logging.LOG.info("Payment information retrieved");
        return this.pr.findById(username).get();
    }

    // Input-checking here could probably be done in the front-end, but here it is just in case
    @Transactional
    public String updatePayment(Payment payment) {
        LocalDate now = LocalDate.now();
        Boolean validFormat = payment.getExpirationMonth().length() == 2 && payment.getExpirationYear().length() == 4;
        Integer year = Integer.parseInt(payment.getExpirationMonth());
        Integer month = Integer.parseInt(payment.getExpirationYear());
        Boolean validExpiration = year > now.getYear()
            || (year == now.getYear() && month >= now.getMonthValue());
        String cardNumber = payment.getCardNumber();
        Boolean validCardNumber = Luhn.validate(cardNumber);

        if (cardNumber.length() != 16) {
            Logging.LOG.error("Invalid card number");
            return "Invalid card number";
            
        } else if (payment.getCVV().length() != 3) {
            Logging.LOG.error("Invalid security code");
            return "Invalid security code";

        } else if (!(validExpiration || validFormat)) {
            Logging.LOG.error("Invalid expiration date");
            return "Invalid expiration date";

        } else if (!validCardNumber) {
            Logging.LOG.error("Invalid card number");
            return "Invalid card number";
            
        } else {
            this.pr.save(payment);

            Logging.LOG.info("Payment information updated");
            return "Payment information updated!";
        }
    }

    @Transactional
    public void deletePayment(String username) {
        this.pr.deleteById(username);
        Logging.LOG.info("Payment information removed");
    }
}