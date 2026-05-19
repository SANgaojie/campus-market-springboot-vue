package com.campus.market.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.market.common.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserService 业务组件。
 *
 * @author 阿德
 * @date 2026/05/12
 */
@Service
public class UserService {

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, UserRoleMapper userRoleMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (existsByUsername(request.username())) {
            throw new BusinessException(409, "用户名已存在");
        }

        var user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setStatus(1);
        userMapper.insert(user);

        var role = new UserRole();
        role.setUserId(user.getId());
        role.setRoleCode(DEFAULT_USER_ROLE);
        userRoleMapper.insert(role);

        return toProfile(user, List.of(DEFAULT_USER_ROLE));
    }

    public User findEnabledByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getStatus, 1));
    }

    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    public List<String> findRoleCodes(Long userId) {
        return userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, userId))
                .stream()
                .map(UserRole::getRoleCode)
                .toList();
    }

    public UserProfileResponse toProfile(User user) {
        return toProfile(user, findRoleCodes(user.getId()));
    }

    public UserProfileResponse toProfile(User user, List<String> roles) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getNickname(), user.getStatus(), roles);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        var user = findById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setNickname(request.nickname().trim());
        userMapper.updateById(user);
        return toProfile(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        var user = findById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BusinessException(400, "原密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userMapper.updateById(user);
    }

    @Transactional
    public void ensureAdminUser(String username, String password, String nickname) {
        var user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setNickname(nickname);
            user.setStatus(1);
            userMapper.insert(user);
        }
        ensureRole(user.getId(), DEFAULT_USER_ROLE);
        ensureRole(user.getId(), ADMIN_ROLE);
    }

    public List<UserProfileResponse> listUsers() {
        return userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getCreatedAt))
                .stream()
                .map(this::toProfile)
                .toList();
    }

    @Transactional
    public UserProfileResponse updateStatus(Long userId, UserStatus status) {
        var user = findById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status == UserStatus.ENABLED ? 1 : 0);
        userMapper.updateById(user);
        return toProfile(user);
    }

    private void ensureRole(Long userId, String roleCode) {
        var exists = userRoleMapper.exists(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleCode, roleCode));
        if (exists) {
            return;
        }
        var role = new UserRole();
        role.setUserId(userId);
        role.setRoleCode(roleCode);
        userRoleMapper.insert(role);
    }

    private boolean existsByUsername(String username) {
        return userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
