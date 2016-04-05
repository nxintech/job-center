package com.nxin.framework.manager;

import com.nxin.framework.domain.*;

import java.util.List;

/**
 * Created by petzold on 2015/12/25.
 */
public interface IJobRepository
{
    void addJob(JobConfiguration configuration);
    List<JobConfiguration> getAllJobs();
    boolean jobNameExist(String name);
    JobConfiguration getJob(String id);
    PageResult<JobConfiguration> getJobByPage(String name,int pageIndex,int pageSize);
    JobConfiguration getJobByName(String name);
    void updateJob(JobConfiguration configuration);
    void deleteJob(String id);
    void deleteJobByName(String name);
    void newInstance(String jobId, List<JobInstanceItem> items);
    void updateItem(String id, int status, String error);
    SystemError getByCode(int code);
    void deleteJobs(List<String> ids);
    List<JobConfiguration> getJobByIdList(List<String> ids);
    PageResult<JobInstanceInfo> getJobInstanceByPage(String name,int pageIndex,int pageSize);
}
