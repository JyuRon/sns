package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.Like;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import com.example.dto.PostDto;
import com.example.exception.SnsApplicationException;
import com.example.repository.LikeRepository;
import com.example.repository.PostRepository;
import com.example.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public void create(String title, String body, String userName){
        UserAccount userAccount = checkInvalidUserName(userName);
        postRepository.save(Post.of(title, body, userAccount));
    }

    @Transactional
    public PostDto modify(String title, String body, String userName, Long postId){
        UserAccount userAccount = checkInvalidUserName(userName);
        Post post = getPost(postId);

        checkAccessPost(userAccount, post);

        post.setTitle(title);
        post.setBody(body);
        return PostDto.fromEntity(postRepository.save(post));
    }

    @Transactional
    public void delete(String userName, Long postId){
        UserAccount userAccount = checkInvalidUserName(userName);
        Post post = getPost(postId);
        checkAccessPost(userAccount, post);
        postRepository.delete(post);
    }

    public Page<PostDto> list(Pageable pageable){
        return postRepository.findAll(pageable).map(PostDto::fromEntity);
    }

    public Page<PostDto> my(String userName, Pageable pageable){
        UserAccount userAccount = checkInvalidUserName(userName);
        return postRepository.findAllByUser(userAccount, pageable).map(PostDto::fromEntity);
    }

    @Transactional
    public void like(Long postId, String userName) {
        UserAccount userAccount = checkInvalidUserName(userName);
        Post post = getPost(postId);

        Optional<Like> like = likeRepository.findByUserAndPost(userAccount, post);

        if(like.isPresent()){
            likeRepository.delete(like.get());
        }else {
            likeRepository.save(Like.of(userAccount, post));
        }
    }

    @Transactional(readOnly = true)
    public Long likeCount(Long postId){
        Post post = getPost(postId);

        return likeRepository.countByPost(post);
    }

    private UserAccount checkInvalidUserName(String userName){
        return userAccountRepository.findByUserName(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    private Post getPost(Long postId){
        return  postRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private void checkAccessPost(UserAccount userAccount, Post post){
        if (post.getUser() != userAccount){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userAccount.getUserName(), post.getId()));
        }
    }

}
