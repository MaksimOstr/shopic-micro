package com.authservice.services;

import com.authservice.dto.TokenPairDto;
import com.authservice.entity.RefreshToken;
import com.authservice.exceptions.EntityDoesNotExistException;
import com.shopic.grpc.userservice.UserRolesRequest;
import com.shopic.grpc.userservice.UserServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenCreationService refreshTokenCreationService;
    private final JwtTokenService jwtTokenService;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceGrpc;
    private final RefreshTokenValidationService refreshTokenService;


    @Transactional
    public TokenPairDto refreshTokens(String refreshToken, String deviceId) {
        RefreshToken validatedRefreshToken = refreshTokenService.validate(refreshToken, deviceId);
        long userId = validatedRefreshToken.getUserId();
        String newRefreshToken = refreshTokenCreationService.updateRefreshToken(validatedRefreshToken);
        List<String> roleNames = getUserRoleNames(userId);
        return new TokenPairDto(getAccessToken(userId, roleNames), newRefreshToken);
    }


    public TokenPairDto getTokenPair(long userId, Set<String> userRoles, String deviceId) {
        String accessToken = getAccessToken(userId, userRoles);
        String refreshToken = refreshTokenCreationService.create(userId, deviceId);

        return new TokenPairDto(accessToken, refreshToken);
    }


    private String getAccessToken(long userId, Collection<String> roleNames) {
        return jwtTokenService.getJwsToken(roleNames, userId);
    }


    private List<String> getUserRoleNames(long userId) {
        try {
            UserRolesRequest request = UserRolesRequest.newBuilder().setUserId(userId).build();
            return userServiceGrpc.getUserRoles(request).getRoleNamesList();
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            switch (code) {
                case NOT_FOUND -> throw new EntityDoesNotExistException("User with id " + userId + " not found");
                default -> throw e;
            }
        }
    }
}
