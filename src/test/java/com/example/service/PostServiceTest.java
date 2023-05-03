package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.Post;
import com.example.domain.UserAccount;
import com.example.exception.SnsApplicationException;
import com.example.repository.PostRepository;
import com.example.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserAccountRepository userAccountRepository;



    @DisplayName("포스트 작성이 성공한 경우")
    @Test
    void givenTitleAndBodyAndUserName_whenCreatePost_thenReturnSuccess(){
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        UserAccount userAccount = createUserAccount("jyuka");
        given(userAccountRepository.findByUserName(userName)).willReturn(Optional.of(userAccount));
        given(postRepository.save(any())).willReturn(createPost(1L, title, body, userAccount));



        // When
        Throwable t = catchThrowable(() -> postService.create(title, body, userName));


        // Then
        assertThat(t).doesNotThrowAnyException();

    }

    @DisplayName("포스트작성시 요청한 유저가 존재하지 않는 경우")
    @Test
    void givenTitleAndBodyAndWrongUserName_whenCreatePost_thenReturnFail(){
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        given(userAccountRepository.findByUserName(userName)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> postService.create(title, body, userName));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.USER_NOT_FOUND.getMessage(), String.format("%s not founded",userName)))
        ;
    }

    @DisplayName("포스트 수정이 성공한 경우")
    @Test
    void givenTitleAndBodyAndUserName_whenModifyPost_thenReturnSuccess(){
        // Given
        String title = "modifyTitle";
        String body = "modifyBody";
        String userName = "userName";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount("jyuka");
        Post oldPost = createPost(postId, "title", "body",userAccount);
        Post newPost = createPost(postId, title, body, userAccount);
        given(userAccountRepository.findByUserName(userName))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(oldPost));
        given(postRepository.save(any()))
                .willReturn(newPost);




        // When
        Throwable t = catchThrowable(() -> postService.modify(title, body, userName, postId));


        // Then
        assertThat(t).doesNotThrowAnyException();

    }

    @DisplayName("포스트 수정시 포스트가 존재하지 않는 경우")
    @Test
    void givenNotExistPost_whenModifyPost_thenReturnFail(){
        // Given
        String title = "modifyTitle";
        String body = "modifyBody";
        String userName = "userName";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount("jyuka");
        Post oldPost = createPost(1L, "title", "body",userAccount);

        given(userAccountRepository.findByUserName(userName))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());



        // When
        Throwable t = catchThrowable(() -> postService.modify(title, body, userName, postId));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.POST_NOT_FOUND.getMessage(), String.format("%s not founded",postId)));

    }

    @DisplayName("포스트 수정시 권한이 없는 경우")
    @Test
    void givenAnotherUserPost_whenModifyPost_thenPermissionDenied(){
        // Given
        String title = "modifyTitle";
        String body = "modifyBody";
        String userName = "userName";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount("jyuka");
        UserAccount anotherAccount = createUserAccount("jyuron");

        given(userAccountRepository.findByUserName(userName))
                .willReturn(Optional.of(anotherAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(createPost(1L, title, body, userAccount)));



        // When
        Throwable t = catchThrowable(() -> postService.modify(title, body, userName, postId));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.INVALID_PERMISSION.getMessage(), String.format("%s has no permission with %s", userName, postId)));

    }

    @DisplayName("포스트 삭제가 성공한 경우")
    @Test
    void givenUserNameAndPostId_whenDeletePost_thenReturnSuccess(){
        // Given
        Long postId = 1L;
        String userId = "jyuka";
        UserAccount userAccount = createUserAccount(userId);
        Post post = createPost(postId, "title", "body",userAccount);
        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        willDoNothing().given(postRepository).delete(any());


        // When
        Throwable t = catchThrowable(() -> postService.delete(userId,postId));


        // Then
        assertThat(t).doesNotThrowAnyException();

    }

    @DisplayName("포스트 삭제시 포스트가 존재하지 않는 경우")
    @Test
    void givenNotExistPost_whenDeletePost_thenReturnFail(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());



        // When
        Throwable t = catchThrowable(() -> postService.delete(userName, postId));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.POST_NOT_FOUND.getMessage(), String.format("%s not founded",postId)));

    }

    @DisplayName("포스트 수정시 권한이 없는 경우")
    @Test
    void givenAnotherUserPost_whenDeletePost_thenPermissionDenied(){
        // Given
        String title = "modifyTitle";
        String body = "modifyBody";
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        UserAccount anotherAccount = createUserAccount("jyuron");

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(anotherAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(createPost(1L, title, body, userAccount)));



        // When
        Throwable t = catchThrowable(() -> postService.delete(userName, postId));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.INVALID_PERMISSION.getMessage(), String.format("%s has no permission with %s", userName, postId)));

    }

    private UserAccount createUserAccount(String userId){
        return UserAccount.of(userId,"1234");
    }

    private Post createPost(Long id, String title, String content, UserAccount userAccount){
        return Post.of(id, title,content,userAccount);
    }


}