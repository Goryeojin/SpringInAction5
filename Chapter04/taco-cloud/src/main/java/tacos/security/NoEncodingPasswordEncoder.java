package tacos.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class NoEncodingPasswordEncoder implements PasswordEncoder {


    @Override
    // 로그인 시 입력된 비밀번호(rawPassword) 암호화하지 않고 String 반환
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    // encode() 에서 반환된 비밀번호를 데이터베이스에서 가져온 비밀번호(encodedPassword)와 비교
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}
