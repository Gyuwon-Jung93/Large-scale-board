package gyuwon.board.comment.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class CommentPathTest {
    @Test
    void createChildCommentPath(){
    // 00000 <- create
        createChildCommentTest(CommentPath.create(""),null,"00000");
        //00000
        //      00000<-create
        createChildCommentTest(CommentPath.create("00000"),null,"0000000000");

        // 00000
        //00001 <- create

        createChildCommentTest(CommentPath.create(""),"00000","00001");

        // 0000z
        //        abcdz
        //               zzzzz
        //                     zzzzz
        // abce0 <- create
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzzz", "0000zabce0");
    }

    void createChildCommentTest(CommentPath commentPath, String descendantsTopPath, String expectedChildPath) {
        CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
        Assertions.assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest() {
        assertThatThrownBy(() ->
                CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest() {
        // given
        CommentPath commentPath = CommentPath.create("");

        // when, then
        assertThatThrownBy(() -> commentPath.createChildCommentPath("zzzzz"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}