package nyullog.blog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity //테이블과 매핑
@Getter //getter 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본 생성자 생성해주는 어노테이션
@Table(name = "users") //테이블 이름 지정
public class User implements UserDetails { //스프링 시큐리티에서 사용자 인증 정보를 담는 인터페이스

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동 생성
    @Column(name = "id", updatable = false) //컬럼 이름 지정
    private Long id;

    @Column(name = "name", nullable = false, unique = true) //컬럼 이름 지정, null 허용 안함
    private String email;

    @Column(name = "password")
    private String password;//컬럼 이름 지정

    @Builder //빌더 패턴이란 객체를 생성할 때 사용되는 패턴이며 객체 생성 시 매개변수가 많을 경우 사용하면 편리
    public User(String email, String password, String auth){
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true;//사용가능
    }

}
