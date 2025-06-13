package gyuwon.board.like.api;


import gyuwon.board.like.service.Response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class LikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");
    @Test
    void likeAndUnlike() {
        // Given
        Long articleId = 9999L;

        like(articleId, 1L);
        like(articleId, 2L);
        like(articleId, 3L);

        ArticleLikeResponse response1 = read(articleId, 1L);
        ArticleLikeResponse response2 = read(articleId, 1L);
        ArticleLikeResponse response3 = read(articleId, 1L);
        System.out.println("response1"+response1);
        System.out.println("response2"+response2);
        System.out.println("response3"+response3);

        unlike(articleId, 1L);
        unlike(articleId, 2L);
        unlike(articleId, 3L);



    }
    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("/v1/articles-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("/v1/articles-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }
    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("/v1/articles-like/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }
}
