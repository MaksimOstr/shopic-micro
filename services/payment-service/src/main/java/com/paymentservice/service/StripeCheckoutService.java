package com.paymentservice.service;

import com.paymentservice.dto.CheckoutItem;
import com.paymentservice.dto.CreateCheckoutSessionDto;
import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.exception.ApiException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.paymentservice.utils.Utils.toSmallestUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeCheckoutService {
    private final PaymentService paymentService;

    @Value("${STRIPE_SUCCESS_URL}")
    private String stripeSuccessUrl;

    @Value("${STRIPE_CANCEL_URL}")
    private String stripeCancelUrl;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createCheckoutSession(CreateCheckoutSessionDto dto) {
        log.info("Initiating Stripe checkout creation for Order ID: {}, User ID: {}", dto.orderId(), dto.userId());

        try {
            List<SessionCreateParams.LineItem> lineItems = getLineItems(dto.checkoutItems());
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCurrency("USD")
                    .addAllLineItem(lineItems)
                    .setSuccessUrl(stripeSuccessUrl)
                    .setCancelUrl(stripeCancelUrl)
                    .putMetadata("order_id", String.valueOf(dto.orderId()))
                    .build();
            Session session = Session.create(params);
            String sessionId = session.getId();
            long totalInSmallestUnits = session.getAmountTotal();
            BigDecimal total = BigDecimal.valueOf(totalInSmallestUnits).divide(BigDecimal.valueOf(100));

            paymentService.createPayment(dto.userId(), sessionId, dto.orderId(), total);

            log.info("Stripe session created successfully. Session ID: {}, Order ID: {}, Amount: {} {}",
                    sessionId, dto.orderId(), total, params.getCurrency());

            return session.getUrl();
        } catch (StripeException e) {
            log.error("Failed to create Stripe session for Order ID: {}. Stripe Error: {}",
                    dto.orderId(), e.getMessage(), e);
            throw new ApiException("Internal server error, try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error during checkout creation for Order ID: {}", dto.orderId(), e);
            throw new ApiException("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private List<SessionCreateParams.LineItem> getLineItems(List<CheckoutItem> checkoutItems) {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (CheckoutItem item : checkoutItems) {
            SessionCreateParams.LineItem.PriceData.ProductData.Builder productBuilder =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(item.name());

            if (item.imageUrl() != null && !item.imageUrl().isBlank()) {
                productBuilder.addImage(item.imageUrl());
            }

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setUnitAmountDecimal(toSmallestUnit(item.price()))
                                    .setCurrency("USD")
                                    .setProductData(productBuilder.build())
                                    .build()
                    )
                    .setQuantity(item.quantity())
                    .build();

            lineItems.add(lineItem);
        }

        return lineItems;
    }
}
