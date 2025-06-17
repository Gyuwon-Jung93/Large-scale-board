package gyuwon.board.view.view;

import gyuwon.board.view.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleViewService {
    private final ArticleViewCountBackupProssesor articleViewCountBackupProssesor;
    private final ArticleViewCountRepository articleViewCountRepository;
    private static final int BACK_UP_BATCH_SIZE = 100;

    public Long increase(Long articleId,Long userId) {
        Long count = articleViewCountRepository.increase(articleId);
        if (count % BACK_UP_BATCH_SIZE == 0) {
            articleViewCountBackupProssesor.backUp(articleId, count);
        }
        return count;
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }
}
