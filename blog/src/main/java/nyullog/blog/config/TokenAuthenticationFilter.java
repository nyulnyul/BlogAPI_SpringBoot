package nyullog.blog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        //이어서 가져온 토큰이 유효한지 확인하고 유효하면 시큐리티 컨텍스트에 인증 정보를 설정합니다. 위에서 작성 코드가 실행되어 컨텍스트 홀더에 getAuthentication() 메소드를 사용해 인증정보를 가져오면 유저 객체가 반환
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION); //1. HEADER_AUTHORIZATION 키의 값 조회
        String token = getAccessToken(authorizationHeader); // 가져온 값에서 접두사 제거

        if(tokenProvider.vaildToken(token)){ //가져온 토큰이 유효한지 확인하고 유효시엔 인증정보 설정
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
    private String getAccessToken(String authorizationHeader){
        //요청헤더에서 키가 'Authorization'인 필드 값을 가져온 다음 토큰 접두사(TOKEN_PREFIX) Bearer 를 제외한 값을 얻음, 만약 값이 null이거나 Bearer로 시작하지 않으면 null을 반환

        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
