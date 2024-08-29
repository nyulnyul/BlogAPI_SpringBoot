package nyullog.blog.service;

import lombok.RequiredArgsConstructor;
import nyullog.blog.domain.RefreshToken;
import nyullog.blog.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()-> new IllegalArgumentException("Unexpected refresh token"));
    }
}
