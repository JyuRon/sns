package com.example.domain;

import com.example.constant.UserRole;
import com.example.domain.converter.UserRoleConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "\"user\"") // postgresql 에는 이미 user 라는 테이블이 존재함
@Getter
@Setter
@NoArgsConstructor
public class UserAccount extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;

    @Convert(converter = UserRoleConverter.class)
    private UserRole role;

    private UserAccount(String userName, String password, UserRole role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public static UserAccount of(String userName, String password){
        return UserAccount.of(userName, password, UserRole.USER);
    }

    public static UserAccount of(String userName, String password, UserRole role){
        return new UserAccount(userName, password, role);
    }
}
