package nserve.delivery_application_backend.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import nserve.delivery_application_backend.dto.response.PaymentIntentResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Lombok will generate a constructor with all the required fields
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class StripeService {

    private String stripeSecretKey = "sk_test_51MPfTSA5vKPlbljExViQrrELBRTDDeoi3UskDs4Auz0eRsv7NwqzzcRjVpD2viglbbfsSnEcGGVXWIm2eWqRZXBD00bCckigP8";

    public PaymentIntentResponse createPaymentIntent(double amount) {
        Stripe.apiKey = stripeSecretKey;

        long amountInCents = Math.round(amount * 100);

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = null;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info("Payment Intent: {}", paymentIntent.getId());

        String clientSecret = paymentIntent.getClientSecret();

        log.info("Client Secret: {}", clientSecret);
        return PaymentIntentResponse.builder()
                .clientSecret(clientSecret)
                .paymentIntentId(paymentIntent.getId())
                .build();
    }

    public String checkPaymentStatus(String paymentIntentId) throws StripeException {
        Stripe.apiKey =  stripeSecretKey;

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        log.info("Payment Amount: {}", paymentIntent.getAmount());
        log.info("Payment Status: {}", paymentIntent.getStatus());

        return paymentIntent.getStatus();
    }

    public boolean checkPaymentAmount(String paymentIntentId, double amount) throws StripeException {
        Stripe.apiKey =  stripeSecretKey;

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        log.info("Payment Amount: {}", Math.round(amount * 100));
        log.info("Stripe amount: {}", paymentIntent.getAmount());
        log.info("Payment Status: {}", paymentIntent.getStatus());

        return paymentIntent.getAmount() == Math.round(amount * 100);
    }

    public Refund refundPayment(String paymentIntentId, double amount) {
        Stripe.apiKey = stripeSecretKey;

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            long amountToRefund = paymentIntent.getAmount();

            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amountToRefund)
                    .build();

            Refund refund = Refund.create(refundParams);
            log.info("Refund created: {}", refund.getId());
            return refund;
        } catch (StripeException e) {
            log.error("Error creating refund: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
