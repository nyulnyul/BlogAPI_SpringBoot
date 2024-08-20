package nyullog.blog.controller;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.AddArticleRequest;
import nyullog.blog.DTO.ArticleResponse;
import nyullog.blog.domain.Article;
import nyullog.blog.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController //http response body를 json으로 반환해주는 컨트롤러
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/article") //http 매서드가 post일때 전달받은 url과 동일시 메소드로 매핑
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){ //@RequestBody로 요청 본문 값 매핑
        Article article = blogService.save(request);
        //요청된 자원 생성 성공되었으며 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.created(URI.create("/api/article/" + article.getId()))
                .body(article);
    }
    @GetMapping("/api/article") //http 매서드가 get일때 전달받은 url과 동일시 메소드로 매핑
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articles); //요청된 자원 조회 성공되었으며 글 정보를 응답 객체에 담아 전송
    }

    @GetMapping("/api/article/{id}")
    //url 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){ //path의 {id}에 있는 값이 id로 들어옴
        Article article = blogService.findById(id);
        return ResponseEntity.ok().body(new ArticleResponse(article)); //요청된 자원 조회 성공되었으며 글 정보를 응답 객체에 담아 전송
    }
}
