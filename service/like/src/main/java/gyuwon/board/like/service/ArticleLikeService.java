package gyuwon.board.like.service;

import gyuwon.board.like.repository.ArticleLikeRepository;
import gyuwon.board.like.service.Response.ArticleLikeResponse;
import gyuwon.board.like.entity.ArticleLike;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleLikeResponse read(Long article, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(article, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("No Like found for articleId: " + article + " and userId: " + userId));
    }


    @Transactional
    public void like(Long articleId, Long userId) {
       articleLikeRepository.save(
               ArticleLike.create(
                       snowflake.nextId(),
                          articleId,
                          userId
               )
       );
    }

    @Transactional
    public void unlike(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLikeRepository::delete);
    }
}
