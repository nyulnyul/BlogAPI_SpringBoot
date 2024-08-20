package nyullog.blog.service;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.AddArticleRequest;
import nyullog.blog.domain.Article;
import nyullog.blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

@Service //해당 클래스를 빈으로 서블릿 컨테이너에 저장
@RequiredArgsConstructor// final or @NotNull 필드의 빈을 생성자로 생성
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity()); //jparepository의 save 메소드를 사용해 데이터 저장
    }
}
