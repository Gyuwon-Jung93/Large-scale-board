package gyuwon.board.article.repository;

import gyuwon.board.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {


    @Query(
            value = "update board_like_count set article_count = article_count + 1 where board_id = :board_id",
            nativeQuery = true
    )
    @Modifying
    int increase(@Param("board_id") Long board_id);

    @Query(
            value = "update board_like_count set article_count = article_count - 1 where board_id = :board_id",
            nativeQuery = true
    )
    @Modifying
    int decrease(@Param("board_id") Long board_id);
}


