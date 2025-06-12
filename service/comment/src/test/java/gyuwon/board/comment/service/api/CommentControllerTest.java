package gyuwon.board.comment.service.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void deleteComment_shouldPrintLogs() throws Exception {
        // 실제 서버 실행 없이 컨트롤러 직접 호출 -> 로그 찍힘!
        mockMvc.perform(delete("/v1/comments/{commentId}", 191500240645308416L))
                .andExpect(status().isOk());
    }
}
