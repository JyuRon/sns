package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import com.example.exception.SnsApplicationException;
import com.example.repository.PostRepository;
import com.example.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public void create(String title, String body, String userName){
        UserAccount userAccount = userAccountRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        postRepository.save(new Post());
    }
}
