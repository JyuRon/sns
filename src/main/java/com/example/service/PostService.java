package com.example.service;

import com.example.constant.AlarmType;
import com.example.constant.ErrorCode;
import com.example.domain.*;
import com.example.domain.columnDef.AlarmArgs;
import com.example.dto.CommentDto;
import com.example.dto.PostDto;
import com.example.dto.request.PostCommentRequest;
import com.example.exception.SnsApplicationException;
import com.example.repository.*;
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
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmService alarmService;

    @Transactional
    public void create(String title, String body, Long userId){
        UserAccount userAccount = checkInvalidUserName(userId);
        postRepository.save(Post.of(title, body, userAccount));
    }

    @Transactional
    public PostDto modify(String title, String body, Long userId, Long postId){
        Post post = getPost(postId);
        checkAccessPost(userId, post);

        post.setTitle(title);
        post.setBody(body);
        return PostDto.fromEntity(postRepository.save(post));
    }

    @Transactional
    public void delete(Long userId, Long postId){
        Post post = getPost(postId);
        checkAccessPost(userId, post);

        postRepository.delete(post);
        likeRepository.deleteAllByPost(post);
        commentRepository.deleteAllByPost(post);
    }

    public Page<PostDto> list(Pageable pageable){
        return postRepository.findAll(pageable).map(PostDto::fromEntity);
    }

    public Page<PostDto> my(Long userId, Pageable pageable){
        return postRepository.findAllByUserId(userId, pageable).map(PostDto::fromEntity);
    }

    @Transactional
    public void like(Long postId, Long userId) {

        Optional<Like> like = likeRepository.findByUserIdAndPostId(userId, postId);

        if(like.isPresent()){
            likeRepository.delete(like.get());
        }else {
            UserAccount userAccount = checkInvalidUserName(userId);
            Post post = getPost(postId);
            likeRepository.save(Like.of(userAccount, post));
            Alarm alarm = alarmRepository.save(
                    Alarm.of(
                            post.getUser(),
                            AlarmType.LIKE_ON_POST,
                            AlarmArgs.of(userAccount.getId(), post.getId())
                    )
            );

            alarmService.send(alarm.getId(), post.getUser().getId());
        }
    }

    @Transactional(readOnly = true)
    public Long likeCount(Long postId){
        return likeRepository.countByPostId(postId);
    }

    @Transactional
    public void comment(Long postId, Long userId, PostCommentRequest postCommentRequest){
        UserAccount userAccount = checkInvalidUserName(userId);
        Post post = getPost(postId);

        commentRepository.save(
                Comment.of(postCommentRequest.getComment(), post, userAccount)
        );

        Alarm alarm = alarmRepository.save(
                Alarm.of(
                        post.getUser(),
                        AlarmType.NEW_COMMENT_ON_POST,
                        AlarmArgs.of(userAccount.getId(), postId)
                )
        );

        alarmService.send(alarm.getId(), post.getUser().getId());
    }


    @Transactional(readOnly = true)
    public Page<CommentDto> getComments(Long postId, Pageable pageable) {
        return commentRepository.findAllByPostId(postId, pageable)
                .map(CommentDto::fromEntity);
    }

    private UserAccount checkInvalidUserName(Long userId){
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userId)));
    }

    private Post getPost(Long postId){
        return  postRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private void checkAccessPost(Long userId, Post post){
        if (!post.getUser().getId().equals(userId)){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userId, post.getId()));
        }
    }

}
