package com.userservice.services;

import com.userservice.dto.UserDetailsDto;
import com.userservice.dto.UserSummaryDto;
import com.userservice.dto.request.UserParams;
import com.userservice.entity.User;
import com.userservice.mapper.UserMapper;
import com.userservice.repositories.UserRepository;
import jakarta.persistence.criteria.JoinType;
import jdk.swing.interop.SwingInterOpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.userservice.utils.SpecificationUtils.*;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final QueryUserService queryUserService;
    private final UserMapper userMapper;

    @Transactional
    public UserDetailsDto getUserDetailsById(long userId) {
        User user = queryUserService.findUserWithProfileAndRolesById(userId);

        return userMapper.toUserDetailsDto(user);
    }

    public Page<UserSummaryDto> getUserPage(UserParams params, Pageable pageable) {

        Specification<User> spec = is("isAccountNonLocked", params.isAccountNonLocked())
                .and(is("isVerified", params.isVerified()))
                .and(iLike("email", params.email()))
                .and(equalsId(params.id()))
                .and(equalsEnum("authProvider", params.provider()))
                .and(iLikeNested("profile", "firstName", params.firstName(), JoinType.LEFT))
                .and(iLikeNested("profile", "lastName", params.lastName(), JoinType.LEFT));

        Page<User> userPage = queryUserService.getUserPageBySpec(pageable, spec);
        List<User> userList = userPage.getContent();
        List<UserSummaryDto> summaries = userList.stream().map(userMapper::toUserSummaryDto).toList();

        return new PageImpl<>(summaries, pageable, userPage.getTotalElements());
    }
}
