package gyuwon.board.article.api;
import gyuwon.board.article.entity.Article;
import gyuwon.board.article.service.request.ArticleCreateRequest;
import gyuwon.board.article.service.response.ArticlePageResponse;
import gyuwon.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootTest
public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
// INSERT INTO article (article_id, title, content, board_id, writer_id, created_at, modified_at) VALUES ((1111, 'hi1', 'my content1', 1, 2, NOW(), NOW()),)
        // INSERT INTO article (article_id, title, content, board_id, writer_id, created_at, modified_at) VALUES ((121530268440289280, 'hi', 'my content', 1, 2, NOW(), NOW()),)

        System.out.println("response = " + response);
    }
    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(121530268440289280L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        update(121530268440289280L);
        ArticleResponse response = read(121530268440289280L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2", "my content 22"))
                .retrieve();
    }

    @Test
    void deleteTest(){
restClient.delete()
        .uri("/v1/articles/{articleId}", 188728616664698880L)
        .retrieve();
    }


    @Test
    void readAllTest(){
            ArticlePageResponse response = restClient.get().uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                    .retrieve()
                    .body(ArticlePageResponse.class);
        System.out.println("Response.getArticleCount = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()){
            System.out.println("ArticleId = " + article.getArticleId());
        }
    }


    @Test
    void readAllInfiniteScrollTest(){
        List <ArticleResponse> article1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });
        System.out.println("first page");
        for(ArticleResponse articleResponse : article1){
            System.out.println("ArticleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
        Long lastArticleId = article1.getLast().getArticleId();
        List <ArticleResponse> article2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("Second page");
        for(ArticleResponse articleResponse : article2){
            System.out.println("ArticleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
    }

    @Test
void countTest() {
        ArticleResponse response  = create(new ArticleCreateRequest("hi", "my content", 1L, 2L
        ));
        Long count1 = restClient.get()
                .uri("/v1/articles/board/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1);


        restClient.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve();

    Long count2 =  restClient.get()
                .uri("/v1/articles/board/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);

       System.out.println("count2 = " + count2);


    }


    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }
    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private  String content;

    }



}
