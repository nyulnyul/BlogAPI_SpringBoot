package nyullog.blog.controller;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.ArticleListViewResponse;
import nyullog.blog.DTO.ArticleViewResponse;
import nyullog.blog.domain.Article;
import nyullog.blog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model){
        List<ArticleListViewResponse> articles = blogService.findAll().stream().map(ArticleListViewResponse::new).toList(); //findAll로 받아온 article을 ArticleListViewResponse로 변환 후 List로 저장
        model.addAttribute("articles", articles); //model에 articles를 담아 articleList.html로 전달
        return "articleList"; //articleList.html로 이동
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model){
        Article article = blogService.findById(id); //id로 article을 찾아옴
        model.addAttribute("article", new ArticleViewResponse(article));
        return "article";//model에 article을 담아 article.html로 전달
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model){ //id가 없으면 null로 받아옴
        if(id == null){
            model.addAttribute("article", new ArticleViewResponse());
        }else{
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }
        return "newArticle";
    }
}
