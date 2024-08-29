package nyullog.blog.controller;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.CreateAccessTokenRequest;
import nyullog.blog.DTO.CreateAccessTokenResponse;
import nyullog.blog.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request){
        String NewAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(new CreateAccessTokenResponse(NewAccessToken));
    }
}
