package gyuwon.board.comment.service.data;

import gyuwon.board.comment.entity.Comment;
import gyuwon.board.comment.entity.CommentPath;
import gyuwon.board.comment.entity.CommentV2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class DataInitializerV2 {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;
    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int Bulk_INSERT_SIZE = 2000;
    static final int EXECUTE_COUNT = 6000;

    @Test
    void initialise() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0 ; i<EXECUTE_COUNT; i++) {
            int start = i * Bulk_INSERT_SIZE;
            int end = (i + 1) * Bulk_INSERT_SIZE;
            executorService.submit(() -> {
                insert(start, end);
                latch.countDown();
                System.out.println("latch.getCount() = " + latch.getCount());

            });
        }
           latch.await();
           executorService.shutdown();

        }
    void insert(int start, int  end){
        transactionTemplate.executeWithoutResult(status->{
            Comment prev = null;
            for(int i = start ;i < end; i++){
                CommentV2 comment = CommentV2.create(
                        snowflake.nextId(),
                        "content",
                        1L,
                        1L,
                        toPath(i)
                );
                entityManager.persist(comment);
            }
        });
    }
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int DEPTH_CHUNK_SIZE = 5;
CommentPath toPath(int value) {
    String result = "";
    for (int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
        result = CHARSET.charAt(value % CHARSET.length()) + result;
        value /= CHARSET.length();
    }
    return CommentPath.create(result);
}

}
