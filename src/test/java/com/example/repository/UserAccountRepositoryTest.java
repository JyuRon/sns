package com.example.repository;

import com.example.config.JpaConfig;
import com.example.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(JpaConfig.class)
@DataJpaTest
class UserAccountRepositoryTest {

    private final UserAccountRepository userAccountRepository;

    public UserAccountRepositoryTest(
            @Autowired UserAccountRepository userAccountRepository
    ) {
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("@Where 조건 확인 테스트")
    @Test
    void test(){
        List<UserAccount> userList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            userList.add(createUserAccount("jyuka"+i, "1234" + i));
        }

        userAccountRepository.saveAllAndFlush(userList);
        userAccountRepository.delete(userList.get(0));

        assertThat(userAccountRepository.count()).isEqualTo(9);


    }

    private UserAccount createUserAccount(String userName, String password){
        return UserAccount.of(userName, password);
    }
}