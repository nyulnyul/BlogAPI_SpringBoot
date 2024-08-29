package nyullog.blog.config.jwt;

import io.jsonwebtoken.Jwts;
import nyullog.blog.config.JwtProperties;
import nyullog.blog.config.TokenProvider;
import nyullog.blog.domain.User;
import nyullog.blog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰 만들 수 있다.")
    @Test
    void generateToken() {
        //given 토큰에 유저정보 추가 위한 테스트 유저 만듦
        User testUser = userRepository.save(User.builder().email("user@gmail.com").password("test").build());
        //when 토큰 제공자의 generateToken 메소드로 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        //then jjwt라이브러리를 사용해 토큰을 복호화, 토큰 만들 때 클레임으로 넣어둔 id 값이 given절에서 만든 유저 id 와 동일 파악
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());

    }
    @DisplayName("validToken(): 만료된 토큰일땐 유효성 검증 실패.")
    @Test
    void validToken_invaildToken(){
        //given 토큰을 생성하고 만료시간은 현재시간을 밀리초 단위로 치환한 값에 1000을 빼 이미 만료되게 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);
        //when validToken()메서드를 호출해 유효한 토큰인지 검증 뒤 결괏값 반환
        boolean result = tokenProvider.validToken(token);
        //then 반환값이 false인 것을 확인합니다
        assertThat(result).isFalse();
    }
    @DisplayName("validToken(): 유효한 토큰일땐 유효성 검증 성공.")
    @Test
    void validToken_validToken(){
        //given 토큰을 생성 ,만료시간은 현재시간부터 14일 뒤로 만료되지 않은 토큰 생성
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        //when 토큰 제공자의 validToken()메서드를 호출해 유효한 토큰인지 검증 뒤 결괏값 반환
        boolean result = tokenProvider.validToken(token);
        //then 반환값이 true인 것을 확인합니다
        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰을 전달하면 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        //given 토큰을 생성하고 subject로 유저 이메일을 넣어 토큰 생성
        String userEmail = "user@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);
        //when getAuthentication()메서드를 호출해 인증 정보를 가져옴
        Authentication authentication = tokenProvider.getAuthentication(token);
        //then 반환받은 인증 객체의 유저 이름을 가져와 given절에서 넣은 유저 이메일과 동일한지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰을 전달하면 유저 id를 가져올 수 있다.")
    @Test
    void getUserId() {
        //given 토큰을 생성하고 클레임을 추가, 키는 "id" 값은 1이라는 유저 id
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);
        //when 토큰 제공자의 getUserId()메서드를 호출해 유저 id를 가져옴
        Long userIdByToken = tokenProvider.getUserId(token);
        //then 반환받은 유저 id가 given절에서 넣은 유저 id와 동일한지 확인
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
