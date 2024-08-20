package nyullog.blog.controller;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.AddArticleRequest;
import nyullog.blog.domain.Article;
import nyullog.blog.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController //hrrp response body를 json으로 반환해주는 컨트롤러
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/article") //http 매서드가 post일때 전달받은 url과 동일시 메소드로 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){ //@RequestBody로 요청 본문 값 매핑
        Article article = blogService.save(request);
        //요청된 자원 생성 성공되었으며 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.created(URI.create("/api/article/" + article.getId()))
                .body(article);
    }
}
