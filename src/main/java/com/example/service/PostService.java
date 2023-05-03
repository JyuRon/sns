package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import com.example.dto.PostDto;
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

        postRepository.save(Post.of(title, body, userAccount));
    }

    @Transactional
    public PostDto modify(String title, String body, String userName, Long postId){
        UserAccount userAccount = userAccountRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));

        if (post.getUser() != userAccount){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        post.setTitle(title);
        post.setBody(body);

        return PostDto.fromEntity(postRepository.save(post));
    }

}
