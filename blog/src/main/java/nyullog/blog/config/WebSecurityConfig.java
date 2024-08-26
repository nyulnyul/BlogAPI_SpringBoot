package nyullog.blog.config;

import lombok.RequiredArgsConstructor;
import nyullog.blog.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final UserDetailService userService;

    //1. 스프링 시큐리티 기능 비활성화로 인증 인가를 정적 리소스(이미지,html파일)에만 설정한다.
    @Bean//H2 콘솔 접근을 위한 설정
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers(toH2Console()).requestMatchers(new AntPathRequestMatcher("/static/**"));
        //H2 콘솔 접근을 위한 설정하기 위해 configure 메소드를 오버라이딩해서 toH2Console() 메소드를 사용한 뒤 requestMatchers 메소드를 사용해 /static/** 경로에 대한 요청을 무시하도록 설정
    }

    @Bean//2. 특정 http 요청에 대한 웹 기반 보안 구성해서 이 메서드로 인증/인가/로그인/로그아웃 관련 설정
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { //SecurityFilterChain을 반환하는 메소드
        return http.authorizeRequests(auth -> auth //3. 특정 경로에따른 엑세스 설정 requestMatchers: 특정 요청 일치 url 엑세스 설정
                        .requestMatchers( //authorizeRequests 메소드를 사용해 요청에 대한 접근 권한을 설정
                                new AntPathRequestMatcher("/login"),  //AntPathRequestMatcher를 사용해 요청 경로를 지정
                                new AntPathRequestMatcher("/signup"),
                                new AntPathRequestMatcher("/user")
                        ).permitAll().anyRequest().authenticated() //permitAll :누구나 접근 가능하게 설정
                // anyRequest(): 위에서 설정 url이와 요청 설정
                // authenticated() : 별도 인가 x지만 인증 성공시에만 접근 가능
        ).formLogin(formLogin -> formLogin // 4. form 기반 로그인 설정, loginPage: 로그인페이지 경로, defaultSuccessUrl: 로그인 완료시 이동 경로
                .loginPage("/login").defaultSuccessUrl("/articles") //formLogin 메소드를 사용해 로그인 페이지와 로그인 성공 시 이동할 페이지를 설정
        ).logout(logout -> logout//5. 로그아웃 설정
                .logoutSuccessUrl("/login").invalidateHttpSession(true) //logout 메소드를 사용해 로그아웃 시 세션을 무효화하고 로그아웃 성공 시 이동할 페이지를 설정
        ).csrf(AbstractHttpConfigurer::disable).build(); //6. csrf 비활성화 실제론 하는게 좋지만 실습 위해 비활성

    }

    //7. 인증 관리자 관련 설정으로 사용자 정보 가져올 서비스 재정의나 인증방법 설정시 사용
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) //AuthenticationManager를 반환하는 메소드
            throws Exception { //UserDetailService: 사용자 정보를 가져올 서비스 설정
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); //DaoAuthenticationProvider 객체 생성
        authProvider.setUserDetailsService(userService); //8. 사용자 정보 서비스 설정 PasswordEncoder:비밀번호 암호화 위한 인코더 설정
        authProvider.setPasswordEncoder(bCryptPasswordEncoder); // 패스워드 인코더 설정
        return new ProviderManager(authProvider);
    }

    @Bean //9. 패스워드 인코더로 사용할 빈 등록
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
