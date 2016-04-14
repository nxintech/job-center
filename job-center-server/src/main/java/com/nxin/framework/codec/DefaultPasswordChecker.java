package com.nxin.framework.codec;

import com.google.common.base.Strings;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by petzold on 2014/7/8.
 */
public class DefaultPasswordChecker implements PasswordEncoder
{
    private String[] encodingAddtion = new String[]{"!","@","#","$","%","^","&","*","(",")","_","+","=","d","b","n"};

    @Override
    public String encode(CharSequence rawPassword)
    {
        String mr = DigestUtils.md5Hex(rawPassword.toString());
        int i = Integer.parseInt(mr.substring(0, 1), 16);
        int j = Integer.parseInt(mr.substring(31, 32), 16);
        String mf = mr.substring(0, i) + encodingAddtion[j] + mr.substring(i);
        return DigestUtils.md5Hex(mf);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword)
    {
        if(rawPassword == null || Strings.isNullOrEmpty(rawPassword.toString()) || Strings.isNullOrEmpty(encodedPassword))
        {
            return false;
        }
        return encodedPassword.equals(encode(rawPassword));
    }
}
