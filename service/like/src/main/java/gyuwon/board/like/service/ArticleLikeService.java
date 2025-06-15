package gyuwon.board.like.service;

import gyuwon.board.like.entity.ArticleLikeCount;
import gyuwon.board.like.repository.ArticleLikeCountRepository;
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
    private final ArticleLikeCountRepository articleLikeCountRepository;

    public ArticleLikeResponse read(Long article, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(article, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("No Like found for articleId: " + article + " and userId: " + userId));
    }

    /**
     *  Update
     * @param articleId
     * @param userId
     */
    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
       articleLikeRepository.save(
               ArticleLike.create(
                       snowflake.nextId(),
                          articleId,
                          userId
               )
       );
        int result = articleLikeCountRepository.increase(articleId);
        // If there is no record to update, it initiate as 1.
        //It could lose data if traffic is too high, so it can be initialized to 0 at the time of article creation.
        if (result == 0) {
            articleLikeCountRepository.save(
                    ArticleLikeCount.init(articleId, 1L)
            );
        }

    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike ->{
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.decrease(articleId);
                });
    }

    /**
     *  select for Update + update
     */
    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
        };

@Transactional
public void unlikePessimisticLock2(Long articleId, Long userId) {
    articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent(articleLike -> {
                articleLikeRepository.delete(articleLike);
                ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                articleLikeCount.decrease();
            });
}



@Transactional
public void likeOptimisticLock(Long articleId, Long userId) {
    articleLikeRepository.save(
            ArticleLike.create(
                    snowflake.nextId(),
                    articleId,
                    userId
            )
    );
    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
            .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
    articleLikeCount.increase();
    articleLikeCountRepository.save(articleLikeCount);
    }

@Transactional
public void unlikeOptimisticLock(Long articleId, Long userId) {
    articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent(articleLike -> {
                articleLikeRepository.delete(articleLike);
                ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
                articleLikeCount.decrease();
            });
}
    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }

}

