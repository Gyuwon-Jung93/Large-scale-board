package gyuwon.board.view.repository;

import gyuwon.board.view.backup.ArticleViewCount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class ArticleViewCountBackupRepositoryTest {
    @Autowired
    ArticleViewCountBackupRepository articleViewCountBackupRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    @Test
    @Transactional
    void updateViewCountTest() {
        //given
        articleViewCountBackupRepository.save(
                ArticleViewCount.init(1L, 0L)

        );
        entityManager.flush();
        entityManager.clear();

        //100,300,200 protect codes
        int result1 = articleViewCountBackupRepository.updateViewCount(1L, 100L);
        int result2 = articleViewCountBackupRepository.updateViewCount(1L, 300L);
        int result3 = articleViewCountBackupRepository.updateViewCount(1L, 200L);

        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(0);

        ArticleViewCount articleViewCount = articleViewCountBackupRepository.findById(1L).get();
        assertThat(articleViewCount.getViewCount()).isEqualTo(300L);

    }

}