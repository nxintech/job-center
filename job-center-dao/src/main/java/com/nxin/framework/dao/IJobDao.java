package com.nxin.framework.dao;

import com.nxin.framework.domain.JobConfiguration;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * Created by petzold on 2015/12/22.
 */
public interface IJobDao
{
    void addJob(@Param("configuration") JobConfiguration configuration);
    List<JobConfiguration> getAllJobs();
    boolean jobNameExist(@Param("name") String name);
    JobConfiguration getJob(@Param("id") String id);
    JobConfiguration getJobByName(@Param("name") String name);
    List<JobConfiguration> getJobByIdList(@Param("ids") List<String> ids);
    void updateJob(@Param("configuration") JobConfiguration configuration);
    void deleteJob(@Param("id") String id);
    void deleteJobByName(@Param("name") String name);
    List<JobConfiguration> getJobByPage(@Param("name") String name,@Param("currentIndex") int currentIndex,@Param("pageSize") int pageSize);
    int getJobCount(@Param("name") String name);
    void deleteJobs(@Param("ids") List<String> ids);
}
