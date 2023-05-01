package com.example.dto;


import com.example.constant.UserRole;
import com.example.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UserDto {

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
}
