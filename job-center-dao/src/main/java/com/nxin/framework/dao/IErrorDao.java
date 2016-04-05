package com.nxin.framework.dao;

import com.nxin.framework.domain.SystemError;
import org.apache.ibatis.annotations.Param;

/**
 * Created by petzold on 2016/1/3.
 */
public interface IErrorDao
{
    SystemError getByCode(@Param("code") int code);
}
