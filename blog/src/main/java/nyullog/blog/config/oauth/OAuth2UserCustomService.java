package nyullog.blog.config.oauth;

import lombok.RequiredArgsConstructor;
import nyullog.blog.domain.User;
import nyullog.blog.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest); //부모클래스에서 제공하는 OAuth 서비스 제공 정보를 기반으로 유저 객체를 만들어주는 loadUser()를 사용해 사용자 객체 불러옴
        saveOrUpdate(user);
        return user;
    }

    private User saveOrUpdate(OAuth2User oAuth2User){ //유저 테이블에 있으면 업데이트 하고 없으면 사용자를 새로 생성해 db에 저장
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email).map(entity -> entity.update(name)).orElse(User.builder().email(email).nickname(name).build());
        return userRepository.save(user);


    }
}
