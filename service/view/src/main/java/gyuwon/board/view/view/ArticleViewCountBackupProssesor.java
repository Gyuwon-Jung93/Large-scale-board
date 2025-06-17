package gyuwon.board.view.view;

import gyuwon.board.view.backup.ArticleViewCount;
import gyuwon.board.view.repository.ArticleViewCountBackupRepository;
import gyuwon.board.view.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor

public class ArticleViewCountBackupProssesor {
    private final ArticleViewCountBackupRepository articleViewCountBackupService;
    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackupService.updateViewCount(articleId,viewCount);
        if(result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {
                    }, () -> articleViewCountBackupRepository.save(
                            ArticleViewCount.init(articleId, viewCount)
                    )

            );
        }
    }
}
