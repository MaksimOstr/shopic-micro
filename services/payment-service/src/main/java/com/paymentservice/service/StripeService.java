package com.paymentservice.service;

import com.paymentservice.dto.CheckoutItem;
import com.paymentservice.dto.CreateCheckoutSessionDto;
import com.paymentservice.dto.CreatePaymentDto;
import com.paymentservice.exception.InternalException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    private final PaymentService paymentService;

    public String createCheckoutSession(CreateCheckoutSessionDto dto) {
        try {
            List<SessionCreateParams.LineItem> lineItems = getLineItems(dto);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCurrency("USD")
                    .addAllLineItem(lineItems)
                    .putMetadata("order_id", String.valueOf(dto.orderId()))
                    .build();
            Session session = Session.create(params);
            String sessionId = session.getId();

            savePayment(dto.userId(), sessionId, dto.orderId());

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
                                            .setUnitAmountDecimal(item.price())
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

    private void savePayment(long userId, String paymentId, long orderId) {
        CreatePaymentDto dto = new CreatePaymentDto(
                userId,
                orderId,
                paymentId
        );

        paymentService.createPayment(dto);
    }
}
