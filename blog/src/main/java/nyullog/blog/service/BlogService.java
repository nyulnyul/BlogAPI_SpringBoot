package nyullog.blog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.AddArticleRequest;
import nyullog.blog.DTO.UpdateArticleRequest;
import nyullog.blog.domain.Article;
import nyullog.blog.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service //해당 클래스를 빈으로 서블릿 컨테이너에 저장
@RequiredArgsConstructor// final or @NotNull 필드의 빈을 생성자로 생성
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity()); //jparepository의 save 메소드를 사용해 데이터 저장
    }

    public List<Article> findAll(){
        return blogRepository.findAll(); //jparepository의 findAll 메소드를 사용해 데이터 조회
    }

    public Article findById(long id){
        return blogRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("not found: "+id)); //jparepository의 findById 메소드를 사용해 데이터 조회 없으면 예외 처리
    }
    public void delete(long id){
        blogRepository.deleteById(id); //jparepository의 deleteById 메소드를 사용해 데이터 삭제
    }

    @Transactional //매칭한 메서드를 하나의 트렌젝션으로 묶는 역할 + 트렌젝션이란 데이터 베이스의 데이터를 바꾸기 위해 묶은 작업 단위
    //입금은 되는데 출금은 안되는 상황을 맊기 위해 입금과 출금을 한 단위로 실행해 둘중 하나라도 안되면 되돌리는 것
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        article.update(request.getTitle(), request.getContent());
        return article;
    }

}
