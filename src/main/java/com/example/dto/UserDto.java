package com.example.dto;


import com.example.constant.UserRole;
import com.example.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
public class UserDto implements UserDetails {

    private Long id;
    private String userName;
    private String password;
    private UserRole userRole;
    protected LocalDateTime registerAt;
    protected LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static UserDto fromEntity(UserAccount entity){
        return new UserDto(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisterAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    public static UserDto of(Long id, String userName, String password, UserRole userRole, LocalDateTime registerAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        return new UserDto(id, userName, password, userRole, registerAt, updatedAt, null);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return deletedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }
}
