package gyuwon.board.article.service;

import gyuwon.board.article.entity.Article;
import gyuwon.board.article.entity.BoardArticleCount;
import gyuwon.board.article.repository.ArticleRepository;
import gyuwon.board.article.repository.BoardArticleCountRepository;
import gyuwon.board.article.service.request.ArticleCreateRequest;
import gyuwon.board.article.service.request.ArticleUpdateRequest;
import gyuwon.board.article.service.response.ArticlePageResponse;
import gyuwon.board.article.service.response.ArticleResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake= new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request){
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId())
        );
        int result = boardArticleCountRepository.increase(request.getBoardId());
        if (result == 0) {
            // If there is no record to update, it initiate as 1.
            // It could lose data if traffic is too high, so it can be initialized to 0 at the time of board creation.
            boardArticleCountRepository.save(
                    BoardArticleCount.init(request.getBoardId(), 1L)
            );
        }
        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request){
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());
        return ArticleResponse.from(article);
    }
    public ArticleResponse read(Long articleId){
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId){
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);
        boardArticleCountRepository.decrease(article.getBoardId());
    }
    @Transactional

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize){
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page-1)*pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatedPageLimit(page,pageSize,10L)
                )
        );
    }

    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId){
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize):
                articleRepository.findAllInfiniteScroll(boardId, pageSize,lastArticleId);
        return articles.stream().map(ArticleResponse::from).toList();
    }

    public Long count (Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
