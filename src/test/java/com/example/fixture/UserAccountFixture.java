package com.example.fixture;

import com.example.domain.UserAccount;

public class UserAccountFixture {

    public static UserAccount get(String userName, String password){
        UserAccount result = new UserAccount();
        result.setId(1L);
        result.setUserName(userName);
        result.setPassword(password);
        return result;
    }
}
