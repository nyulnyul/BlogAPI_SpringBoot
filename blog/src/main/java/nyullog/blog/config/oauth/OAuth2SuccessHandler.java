package nyullog.blog.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nyullog.blog.domain.RefreshToken;
import nyullog.blog.domain.User;
import nyullog.blog.repository.RefreshTokenRepository;
import nyullog.blog.config.TokenProvider;
import nyullog.blog.service.UserService;
import nyullog.blog.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler { //기본 로직에서의 별도의

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        //1. 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        //토큰 제공자를 사용해 리프레시 토큰을 만든 뒤에, saveRefreshToken() 메소드를 호출해 해당 리프레시 토큰을 db에 유저 아이디 함께 저장 이후 클라이언트 엑세스 토큰이 만료되었을 때 addRefreshTokenToCookie()를 호출해 쿠키에 리프레시 토큰을 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);
        //2. 엑세스 토큰 생성 -> 패스에 엑세스 토큰 추가
        //토큰 제공자를 사용해 엑세스 토큰을 만든 뒤에 쿠키에서 리다이렉트 경로가 담긴 값을 가져와 쿼리 파라미터에 액세스 토큰을 추가 합니다.
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);
        //3. 인증 관련 설정값, 쿠키 제거
        //세션과 쿠키에 임시로 저장해둔 인증관련 데이터를 제거한다. 기본적 제공하는 메소드인 clearAuthenticationAttributes()는 그대로 호출하고 remove AuthorizationRequsetCookies()를 추가로 호출해 OAuth 인증을 위해 저장된 정보도 삭제
        clearAuthenticationAttributes(request, response);
        //4. 리다이렉트
        //2에서 만든 url로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //생성된 리프레시 토큰을 전달받아 db에 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    //생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //인증 관련 설정값, 쿠키제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //엑세스 토큰을 패스에 추가
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}