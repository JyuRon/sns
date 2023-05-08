package com.example.service;

import com.example.constant.AlarmType;
import com.example.constant.ErrorCode;
import com.example.domain.*;
import com.example.domain.columnDef.AlarmArgs;
import com.example.dto.request.PostCommentRequest;
import com.example.exception.SnsApplicationException;
import com.example.repository.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AlarmRepository alarmRepository;



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
        String userName = "jyuka";
        String anotherUserName = "jyuron";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        UserAccount anotherAccount = createUserAccount(anotherUserName);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(createPost(postId, title, body, anotherAccount)));



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

    @DisplayName("포스트 삭제시 권한이 없는 경우")
    @Test
    void givenAnotherUserPost_whenDeletePost_thenPermissionDenied(){
        // Given
        String title = "modifyTitle";
        String body = "modifyBody";
        String userName = "jyuka";
        String anotherUserName = "jyuron";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        UserAccount anotherAccount = createUserAccount(anotherUserName);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(createPost(1L, title, body, anotherAccount)));



        // When
        Throwable t = catchThrowable(() -> postService.delete(userName, postId));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.INVALID_PERMISSION.getMessage(), String.format("%s has no permission with %s", userName, postId)));

    }

    @DisplayName("피드 목록 요청이 성공한 경우")
    @Test
    void givenNothing_whenSelectPostList_thenReturnSuccess(){
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findAll(eq(pageable)))
                .willReturn(Page.empty());

        // When
        Throwable t = catchThrowable(() -> postService.list(pageable));

        // Then
        assertThat(t).doesNotThrowAnyException();
    }

    @DisplayName("내 피드 목록 요청이 성공한 경우")
    @Test
    void givenUserName_whenSelectMyPostList_thenReturnSuccess(){
        // Given
        Pageable pageable = Pageable.ofSize(20);
        String userName = "jyuka";
        UserAccount userAccount = createUserAccount(userName);
        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findAllByUser(eq(userAccount), eq(pageable)))
                .willReturn(Page.empty());

        // When
        Throwable t = catchThrowable(() -> postService.my(userName, pageable));

        // Then
        assertThat(t).doesNotThrowAnyException();
    }

    @DisplayName("좋아요 버튼 클릭 후 정상적으로 카운트가 추가되는 경우")
    @Test
    void givenUserNameAndPostId_whenClickLikeButton_thenAddLike(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        Post post = createPost(postId,"title", "content", userAccount);
        Alarm alarm = createAlarm(post, AlarmType.LIKE_ON_POST, userAccount);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        given(likeRepository.findByUserAndPost(any(UserAccount.class), any(Post.class)))
                .willReturn(Optional.empty());
        given(likeRepository.save(any(Like.class)))
                .willReturn(Like.of(userAccount, post));
        given(alarmRepository.save(any(Alarm.class)))
                .willReturn(alarm);

        // When
        Throwable t = catchThrowable(() -> postService.like(postId, userName));

        // Then
        assertThat(t).doesNotThrowAnyException();
        then(likeRepository).should().save(any(Like.class));
    }

    @DisplayName("좋아요 버튼 재 클릭 후 카운트가 감소하는 경우")
    @Test
    void givenUserNameAndPostId_whenReClickLikeButton_thenRemoveLike(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        Post post = createPost(postId,"title", "content", userAccount);
        Like like = createLike(userAccount, post);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        given(likeRepository.findByUserAndPost(any(UserAccount.class), any(Post.class)))
                .willReturn(Optional.of(like));
        willDoNothing().given(likeRepository).delete(any(Like.class));

        // When
        Throwable t = catchThrowable(() -> postService.like(postId, userName));

        // Then
        assertThat(t).doesNotThrowAnyException();
        then(likeRepository).should().delete(any(Like.class));
    }

    @DisplayName("좋아요 버튼을 클릭하였지만 게시글이 존재하지 않는 경우")
    @Test
    void givenPostIdAndUserName_whenClickLikeButton_thenNotExistPost(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        // When
        Throwable t = catchThrowable(() -> postService.like(postId, userName));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.POST_NOT_FOUND.getMessage(), String.format("%s not founded",postId)));
    }

    @DisplayName("좋아요 버튼 클릭시 로그인을 하지 않은 경우")
    @Test
    void givenPostIdAndUserName_whenClickLikeButton_thenUserNotFound(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> postService.like(postId, userName));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.USER_NOT_FOUND.getMessage(), String.format("%s not founded",userName)));
    }

    @DisplayName("댓글 등록이 정상적으로 동작하는 경우")
    @Test
    void givenCommentRequestAndPostIdAndUserName_whenAddComment_thenReturnSuccess(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        UserAccount userAccount = createUserAccount(userName);
        Post post = createPost(postId,"title", "content", userAccount);
        PostCommentRequest request = PostCommentRequest.of("comment");
        Comment comment = createComment(request, post, userAccount);
        Alarm alarm = createAlarm(post, AlarmType.NEW_COMMENT_ON_POST, userAccount);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class)))
                .willReturn(comment);
        given(alarmRepository.save(any(Alarm.class)))
                .willReturn(alarm);

        // When
        Throwable t = catchThrowable(() -> postService.comment(postId, userName, request));

        // Then
        assertThat(t).doesNotThrowAnyException();
        then(commentRepository).should().save(any(Comment.class));
    }

    @DisplayName("댓글 작성시 로그인 하지 않은 경우")
    @Test
    void givenCommentRequestAndPostId_whenAddComment_thenReturnUnAuthorizedException(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        PostCommentRequest request = PostCommentRequest.of("comment");

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> postService.comment(postId, userName, request));

        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.USER_NOT_FOUND.getMessage(), String.format("%s not founded",userName)));
    }

    @DisplayName("댓글 작성시 게시글이 존재하지 않는 경우")
    @Test
    void givenCommentRequestAndPostIdAndUserName_whenAddComment_thenReturnPostNotFoundException(){
        // Given
        String userName = "jyuka";
        Long postId = 1L;
        PostCommentRequest request = PostCommentRequest.of("comment");
        UserAccount userAccount = createUserAccount(userName);

        given(userAccountRepository.findByUserName(anyString()))
                .willReturn(Optional.of(userAccount));
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        // When
        Throwable t = catchThrowable(() -> postService.comment(postId, userName, request));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.POST_NOT_FOUND.getMessage(), String.format("%s not founded",postId)));
    }

    @DisplayName("댓글 리스트 정상호출")
    @Test
    void givenPostId_whenSelectCommentList_thenReturnSuccess(){
        // Given
        Long postId = 1L;
        Pageable pageable = Pageable.ofSize(20);
        Post post = createPost(postId, "title", "conetent", createUserAccount("jyuka"));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findAllByPost(post, pageable)).willReturn(Page.empty());

        // When
        Throwable t = catchThrowable(() -> postService.getComments(postId, pageable));

        // Then
        assertThat(t).doesNotThrowAnyException();
        then(commentRepository).should().findAllByPost(post, pageable);
    }

    @DisplayName("댓글 리스트 조회시 게시글이 존재하지 않는 경우")
    @Test
    void givenPostId_whenSelectCommentList_thenReturnPostNotFoundException(){
        // Given
        Long postId = 1L;
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> postService.getComments(postId, pageable));


        // Then
        assertThat(t)
                .isInstanceOf(SnsApplicationException.class)
                .hasMessage(String.format("%s %s", ErrorCode.POST_NOT_FOUND.getMessage(), String.format("%s not founded",postId)));
    }


    private UserAccount createUserAccount(String userId){
        return UserAccount.of(userId,"1234");
    }

    private Post createPost(Long id, String title, String content, UserAccount userAccount){
        return Post.of(id, title,content,userAccount);
    }

    private Like createLike(UserAccount userAccount, Post post){
        return Like.of(userAccount, post);
    }

    private Comment createComment(PostCommentRequest request, Post post, UserAccount userAccount){
        return Comment.of(request.getComment(), post, userAccount);
    }

    private Alarm createAlarm(Post post, AlarmType alarmType, UserAccount userAccount){
        return Alarm.of(
                post.getUser(),
                alarmType,
                AlarmArgs.of(userAccount.getId(), post.getId())
        );
    }

}