package com.nxin.framework.web;

import com.nxin.framework.codec.DefaultPasswordChecker;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by petzold on 2016/4/12.
 */
public class EncTest
{
    @Test
    public void test1()
    {
        PasswordEncoder encoder = new DefaultPasswordChecker();
        String s = encoder.encode("q123456");
        System.out.println(s);
        System.out.println(encoder.matches("q123456",s));
        s = encoder.encode("q123456");
        System.out.println(s);
        System.out.println(encoder.matches("q123456",s));
        s = encoder.encode("q123456");
        System.out.println(s);
        System.out.println(encoder.matches("q123456",s));
        s = encoder.encode("q123456");
        System.out.println(s);
        System.out.println(encoder.matches("q123456",s));
    }
}
