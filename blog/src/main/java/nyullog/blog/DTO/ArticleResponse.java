package nyullog.blog.DTO;

import lombok.Getter;
import nyullog.blog.domain.Article;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = getTitle();
        this.content = getContent();
    }
}
