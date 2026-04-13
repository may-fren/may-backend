package com.may.backend.security;

import com.may.backend.entity.RolePermissionEntity;
import com.may.backend.entity.UserEntity;
import com.may.backend.entity.UserRoleEntity;
import com.may.backend.enums.Status;
import com.may.backend.repository.RolePermissionRepository;
import com.may.backend.repository.UserRepository;
import com.may.backend.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        List<UserRoleEntity> userRoles = userRoleRepository.findAllByUser_Id(user.getId());

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (UserRoleEntity userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName()));

            List<RolePermissionEntity> rolePermissions =
                    rolePermissionRepository.findAllByRole_Id(userRole.getRole().getId());
            for (RolePermissionEntity rp : rolePermissions) {
                authorities.add(new SimpleGrantedAuthority(rp.getPermission().getName()));
            }
        }

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getStatus() == Status.ACTIVE,
                true,
                true,
                user.getStatus() != Status.BLOCKED,
                authorities
        );
    }
}
