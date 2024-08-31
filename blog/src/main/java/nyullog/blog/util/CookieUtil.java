package nyullog.blog.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {
    // 요청값(이름,값,만료 기간)을 바탕으로 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie); //요청값(이름,값,만료기간)을 바탕으로 http 응답에 쿠키 추가
    }

    //쿠키 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name){
        // 쿠키 이름을 입력받아 쿠키를 삭제합니다. 실제로 삭제하는 방법은 없으므로 파라미터로 넘어온 키의 쿠키를 찾아 값을 비워주고 만료기간을 0으로 설정하여 재생성 되자마자 만료 처리
        Cookie[] cookies = request.getCookies();
        if(cookies ==null){
            return;
        }
        for (Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    //객체를 직렬화해 쿠키 값으로 반환
    public static String serialize(Object obj){ //객체를 직렬화해 쿠키의 값에 들어갈 값으로 변환한다.
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    //쿠키 값으로 받은 문자열을 역직렬화해 객체로 반환
    public static <T> T deserialize(Cookie cookie, Class<T> cls){ //제너릭(T)이란 클래스나 메소드에서 사용할 데이터 타입을 미리 지정하지 않고, 인스턴스를 생성할 때 사용할 데이터 타입을 지정하는 방식
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
        //cls란 클래스를 의미하며, cast 메소드를 사용해 반환된 Object 객체를 T 타입으로 변환
    }
}
