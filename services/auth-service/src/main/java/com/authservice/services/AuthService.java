package com.authservice.services;

import com.authservice.exception.ApiException;
import com.authservice.security.CustomUserDetails;
import com.authservice.dto.LocalRegisterResult;
import com.authservice.dto.TokenPairDto;
import com.authservice.dto.LocalRegisterRequest;
import com.authservice.dto.SignInRequestDto;
import com.authservice.entity.Code;
import com.authservice.entity.CodeScopeEnum;
import com.authservice.entity.RefreshToken;
import com.authservice.entity.User;
import com.authservice.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RoleMapper roleMapper;
    private final CodeService codeService;
    private final MailService mailService;


    @Transactional
    public LocalRegisterResult localRegister(LocalRegisterRequest dto){
        User user = userService.createUser(dto);
        Code code = codeService.create(user, CodeScopeEnum.EMAIL_VERIFICATION);

        mailService.sendEmailVerificationCode(user.getEmail(), code.getCode());

        return new LocalRegisterResult(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public TokenPairDto signIn(SignInRequestDto dto) {
        Authentication authReq = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication authenticatedUser = authenticationManager.authenticate(authReq);
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticatedUser.getPrincipal();

        return getTokenPair(customUserDetails.getUser());
    }

    @Transactional
    public TokenPairDto refreshTokens(String refreshToken) {
        RefreshToken validatedToken = refreshTokenService.validate(refreshToken);
        User user = validatedToken.getUser();
        return getTokenPair(user);
    }

    private TokenPairDto getTokenPair(User user) {
        String newRefreshToken = refreshTokenService.create(user);
        String newAccessToken = jwtService.generateToken(String.valueOf(user.getId()), roleMapper.mapRolesToNames(user.getRoles()));

        return new TokenPairDto(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }
}
