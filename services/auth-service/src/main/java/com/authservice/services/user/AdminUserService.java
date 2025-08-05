package com.authservice.services.user;

import com.authservice.dto.UserDetailsDto;
import com.authservice.dto.UserSummaryDto;
import com.authservice.dto.request.UserParams;
import com.authservice.entity.User;
import com.authservice.mapper.UserMapper;
import com.authservice.utils.SpecificationUtils;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.authservice.utils.SpecificationUtils.*;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @Transactional
    public UserDetailsDto getUserDetailsById(long userId) {
        User user = userQueryService.findUserWithProfileAndRolesById(userId);

        return userMapper.toUserDetailsDto(user);
    }

    public Page<UserSummaryDto> getUserPage(UserParams params, Pageable pageable) {

        Specification<User> spec = SpecificationUtils.<User>is("isAccountNonLocked", params.isAccountNonLocked())
                .and(is("isVerified", params.isVerified()))
                .and(iLike("email", params.email()))
                .and(equalsId(params.id()))
                .and(equalsEnum("authProvider", params.provider()));

        Page<User> userPage = userQueryService.getUserPageBySpec(pageable, spec);
        List<User> userList = userPage.getContent();
        List<UserSummaryDto> summaries = userList.stream().map(userMapper::toUserSummaryDto).toList();

        return new PageImpl<>(summaries, pageable, userPage.getTotalElements());
    }
}