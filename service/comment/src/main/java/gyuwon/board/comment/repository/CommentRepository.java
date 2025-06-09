package gyuwon.board.comment.repository;

import gyuwon.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository

public interface CommentRepository extends JpaRepository<Comment, Long>{


@Query(
        value = "SELECT count(*) FROM (" +
                " SELECT  comment_id FROM comment " +
                " WHERE aritcle_id = :articleId and parent_comment_id = :parentCommentId" +
                " limit :limit" +
                ") t ",
        nativeQuery = true
)
    Long countBy(
            @Param("articleId") Long articleId,
            @Param("parentCommentId") Long parentCommentId,
            @Param("limit") Long limit
);


}
