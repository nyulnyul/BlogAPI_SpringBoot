package nyullog.blog.DTO; // 단순 데이터 옮기는 전달자 역할

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nyullog.blog.domain.Article;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author) { //빌더 패턴을 사용해 DTO를 엔티티로 만들어줌, 생성자를 사용해 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
