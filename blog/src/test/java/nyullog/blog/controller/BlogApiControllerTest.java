package nyullog.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nyullog.blog.DTO.AddArticleRequest;
import nyullog.blog.domain.Article;
import nyullog.blog.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
//assertj
import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle : 블로그 글 추가 성공 테스트")
    @Test
    public void addArticle() throws Exception{
        //given
        final String url = "/api/article";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);
        final String requestBody = objectMapper.writeValueAsString(userRequest); // 객체를 json으로 직렬화
        //when
        ResultActions result = mockMvc.perform(post(url) //http 메소드,url,요청 본문,요청 타입 설정 뒤 테스트 요청 보냄
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody)); //json,xml등 타입중 json을 골라 보냄

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1); //블로그 글 개수가 1인지 확인
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
    }

}