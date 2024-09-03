package nyullog.blog.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import nyullog.blog.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user){ //1. 토큰을 만드는 메소드
        Date now = new Date();

        return Jwts.builder().setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더 type을 JWT로 설정
                .setIssuer(jwtProperties.getIssuer()) //내용 issuer를 propertise 파일에서 설정한 값으로 설정
                .setIssuedAt(now) // 내용 발행 시간을 현재 시간으로 설정
                .setSubject(user.getEmail()) // 내용 주제를 user의 email로 설정
                .setExpiration(expiry) // 내용 만료 시간을 파라미터로 받은 값으로 설정
                .claim("id", user.getId()) // 클레임 id : 유저 id
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey()) // HS256방식으로 secretKey와 함께 해시값을 서명
                .compact();
    }

    //2, 토큰 유효성 검증 메소드로 jwtProperties에 선언한 비밀값과 함께 복호화 진행

    public boolean vaildToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //3. 토큰에서 인증 정보 객체를 가져오는 메소드로 복호화 후 클레임을 가져오는 getClaims를 호출해 클레임 정보를 받아 토큰 기반으로 인증 정보를 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }

    //4. 토큰에서 유저 id를 가져오는 메소드로 비밀값으로 토큰을 복호화 후 getClaims를 호출해 클레임 정보를 반환 받고 클레임에서 id키로 저장된 값을 가져와 반환
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    //5. 토큰에서 클레임을 가져오는 메소드
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }


}
