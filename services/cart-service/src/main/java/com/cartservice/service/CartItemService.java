package com.cartservice.service;

import com.cartservice.dto.CreateCartItem;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.projection.CartItemProjection;
import com.cartservice.repository.CartItemRepository;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.CartItemAddGrpcResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final EntityManager entityManager;
    private final GrpcProductService grpcProductService;

    public List<CartItemProjection> getCartItems(long cartId) {
        return cartItemRepository.findByCart_Id(cartId);
    }

    @Transactional
    public void createCartItem(CreateCartItem dto) {
        CartItemAddGrpcResponse response = grpcProductService.fetchProductInfoForCart(dto.productId(), dto.quantity());

        cartItemRepository.findByCart_IdAndProductId(dto.cartId(), dto.productId())
                .ifPresentOrElse(
                        value -> {
                            value.setQuantity(dto.quantity() + value.getQuantity());
                        },
                        () -> {
                            createAndSaveCartItem(dto, response.getProductPrice());
                        }
                );
    }

    private void createAndSaveCartItem(CreateCartItem dto, String priceAtAdd) {
        CartItem cartItem = CartItem.builder()
                .productId(dto.productId())
                .cart(entityManager.getReference(Cart.class, dto.cartId()))
                .priceAtAdd(new BigDecimal(priceAtAdd))
                .build();
        cartItemRepository.save(cartItem);
    }


}
