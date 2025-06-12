package gyuwon.board.comment.api;

import gyuwon.board.comment.service.request.CommentCreateRequest;
import gyuwon.board.comment.service.response.CommentPageResponse;
import gyuwon.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));

//        commentId=191524287542145024
//        commentId=191524287760248832
//        commentId=191524287797997568
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 191500240452370432L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 191524287542145024L)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response = " + response);
        for (CommentResponse comment : response.getComments()) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())){}
            System.out.println("\t");
            System.out.println("Comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         *page 1 execution result
         Comment.getCommentId() = 191480369983692800

         Comment.getCommentId() = 191480370331820032

         Comment.getCommentId() = 191480370369568768

         Comment.getCommentId() = 191480466133917696

         Comment.getCommentId() = 191480466293301248

         Comment.getCommentId() = 191480466331049984

         Comment.getCommentId() = 191500070423674880

         Comment.getCommentId() = 191500070637584384

         Comment.getCommentId() = 191500070675333120

         Comment.getCommentId() = 191524287542145024
         */
    }

@Test
void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });
    System.out.println("firstPage");
    for (CommentResponse comment : responses1) {
        if (!comment.getCommentId().equals(comment.getParentCommentId())) {
            System.out.print("\t");
        }
        System.out.println("response.getCommentId() = " + comment.getCommentId());
    }
    Long lastParentCommentId = responses1.getLast().getParentCommentId();
    Long lastCommentId = responses1.getLast().getCommentId();
    List<CommentResponse> responses2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                    .formatted(lastParentCommentId, lastCommentId))
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
    System.out.println("SecondPage");
    for (CommentResponse comment : responses2) {
        if (!comment.getCommentId().equals(comment.getParentCommentId())) {
            System.out.print("\t");
        }
        System.out.println("response.getCommentId() = " + comment.getCommentId());
    }
}

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private  Long writerId;

    }



}
