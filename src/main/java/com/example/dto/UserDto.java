package com.example.dto;


import com.example.constant.UserRole;
import com.example.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private UserRole userRole;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registerAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
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

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return deletedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                ", registerAt=" + registerAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
