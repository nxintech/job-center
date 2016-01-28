package com.nxin.framework.dao;

import com.nxin.framework.domain.JobInstanceInfo;
import com.nxin.framework.domain.JobInstanceItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Created by petzold on 2015/12/25.
 */
public interface IJobInstanceDao
{
    void newInstance(@Param("jobId") String jobId,@Param("items") List<JobInstanceItem> items);
    void updateItem(@Param("id") String id, @Param("status") int status, @Param("error") String error);
    List<JobInstanceInfo> getJobInstanceByPage(@Param("name") String name,@Param("currentIndex") int currentIndex,@Param("pageSize") int pageSize);
    List<JobInstanceItem> getJobInstanceItems(@Param("ids") List<String> ids);
    int getInstanceCount(@Param("name") String name);
}
