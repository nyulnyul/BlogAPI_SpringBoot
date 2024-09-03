package nyullog.blog.service;

import lombok.RequiredArgsConstructor;
import nyullog.blog.DTO.AddUserRequest;
import nyullog.blog.domain.User;
import nyullog.blog.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
//        return userRepository.save(User.builder().email(dto.getEmail())
                //1, 패스워드를 저장할때 시큐리티를 설정해 인코딩용으로 등록한 빈을 사용해 암호화 후 저장
//                .password((bCryptPasswordEncoder.encode(dto.getPassword()))).build()).getId();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder().email(dto.getEmail()).password(encoder.encode(dto.getPassword())).build()).getId();
    }
    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }
    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }
}
