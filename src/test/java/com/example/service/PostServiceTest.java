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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
        UserAccount userAccount = createUserAccount();
        given(userAccountRepository.findByUserName(userName)).willReturn(Optional.of(userAccount));
        given(postRepository.save(any())).willReturn(createPost(userAccount));



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

    private UserAccount createUserAccount(){
        return UserAccount.of("jyuka","1234");
    }

    private Post createPost(UserAccount userAccount){
        return Post.of("title","content",userAccount);
    }


}