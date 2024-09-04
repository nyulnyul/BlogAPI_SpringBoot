package nyullog.blog.service;

import lombok.RequiredArgsConstructor;
import nyullog.blog.config.TokenProvider;
import nyullog.blog.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String reFreshToken){
        //토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(reFreshToken)){
            throw new IllegalArgumentException("Unexpected token");
        }
        Long userId = refreshTokenService.findByRefreshToken(reFreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
