package com.paymentservice.service;

import com.paymentservice.dto.CheckoutItem;
import com.paymentservice.dto.CreateCheckoutSessionDto;
import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.exception.InternalException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    private final PaymentService paymentService;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createCheckoutSession(CreateCheckoutSessionDto dto) {
        try {
            List<SessionCreateParams.LineItem> lineItems = getLineItems(dto);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCurrency("USD")
                    .addAllLineItem(lineItems)
                    .setSuccessUrl("https://stripe.com")
                    .setCancelUrl("https://stripe.com")
                    .putMetadata("order_id", String.valueOf(dto.orderId()))
                    .build();
            Session session = Session.create(params);
            String sessionId = session.getId();
            long amountInCents = session.getAmountTotal();
            BigDecimal amountInDollars = BigDecimal.valueOf(amountInCents).divide(BigDecimal.valueOf(100));
            savePayment(dto.userId(), sessionId, dto.orderId(), session.getCurrency(), amountInCents, amountInDollars);

            return session.getUrl();
        } catch (StripeException e) {
            log.error(e.getMessage());
            throw new InternalException("Internal payment error");
        }
    }


    private List<SessionCreateParams.LineItem> getLineItems(CreateCheckoutSessionDto dto) {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (CheckoutItem item : dto.checkoutItems()) {
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setUnitAmountDecimal(convertToCents(item.price()))
                                            .setCurrency("USD")
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .addImage(item.imageUrl())
                                                            .setName(item.name())
                                                            .build()
                                            )
                                            .build()
                            )
                            .setQuantity(item.quantity())
                            .build()
            );
        }
        return lineItems;
    }

    private BigDecimal convertToCents(BigDecimal price) {
        return price.multiply(new BigDecimal(100));
    }

    private void savePayment(long userId, String sessionId, long orderId, String currency, Long totalInSmallestUnit, BigDecimal amount) {
        CreatePaymentDto dto = new CreatePaymentDto(
                userId,
                orderId,
                sessionId,
                currency,
                amount,
                totalInSmallestUnit
        );

        paymentService.createPayment(dto);
    }
}
