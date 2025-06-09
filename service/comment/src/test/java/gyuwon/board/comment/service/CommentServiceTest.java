package gyuwon.board.comment.service;

import gyuwon.board.comment.CommentService;
import gyuwon.board.comment.entity.Comment;
import gyuwon.board.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("have a children comment, show delete")
    void deleteShouldMarkDeletedChildren(){
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId,commentId, 2L)).willReturn(2L);

        //when
        commentService.delete(commentId);
        //then
        verify(comment).delete();
    }


    @Test
    @DisplayName("After Deleting children, not deleted parents , delete children")
    void deleteShouldDeletedChildrenOnlyIfNotDeletedParents(){
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = mock(Comment.class);
        given(parentComment.getDeleted()).willReturn(false);


        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);
        given(commentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));

        //when
        commentService.delete(commentId);
        //then
        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parentComment);
    }


    private Comment createComment(Long articleId, Long commentId){
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId){
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId); // articleId 사용
        given(comment.getCommentId()).willReturn(commentId); // commentId 모킹 추가
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }

}