package com.nxin.framework.manager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.nxin.collection.ListAdapter;
import com.nxin.framework.dao.IErrorDao;
import com.nxin.framework.dao.IJobDao;
import com.nxin.framework.dao.IJobInstanceDao;
import com.nxin.framework.domain.*;
import com.nxin.functions.Action2;
import com.nxin.functions.Predicate2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by petzold on 2015/12/25.
 */
public class JobRepository implements IJobRepository
{
    private IJobDao jobDao;
    private IJobInstanceDao jobInstanceDao;
    private IErrorDao errorDao;
    @Override
    public void addJob(JobConfiguration configuration)
    {
        jobDao.addJob(configuration);
    }

    @Override
    public List<JobConfiguration> getAllJobs()
    {
        return jobDao.getAllJobs();
    }

    @Override
    public boolean jobNameExist(String name)
    {
        return jobDao.jobNameExist(name);
    }

    @Override
    public JobConfiguration getJob(String id)
    {
        return jobDao.getJob(id);
    }

    @Override
    public PageResult<JobConfiguration> getJobByPage(String name, int pageIndex, int pageSize)
    {
        List<JobConfiguration> items = jobDao.getJobByPage(name, (pageIndex-1)*pageSize, pageSize);
        int count = jobDao.getJobCount(name);
        return PageResult.New(items, count);
    }

    @Override
    public JobConfiguration getJobByName(String name)
    {
        return jobDao.getJobByName(name);
    }

    @Override
    public void updateJob(JobConfiguration configuration)
    {
        jobDao.updateJob(configuration);
    }

    @Override
    public void deleteJob(String id)
    {
        jobDao.deleteJob(id);
    }

    @Override
    public void deleteJobByName(String name)
    {
        jobDao.deleteJobByName(name);
    }

    @Override
    public void newInstance(String jobId, List<JobInstanceItem> items)
    {
        jobInstanceDao.newInstance(jobId, items);
    }

    @Override
    public void updateItem(String id, int status, String error)
    {
        jobInstanceDao.updateItem(id, status, error);
    }

    @Override
    public SystemError getByCode(int code)
    {
        return errorDao.getByCode(code);
    }

    @Override
    public void deleteJobs(List<String> ids)
    {
        jobDao.deleteJobs(ids);
    }

    @Override
    public List<JobConfiguration> getJobByIdList(List<String> ids)
    {
        return jobDao.getJobByIdList(ids);
    }

    @Override
    public PageResult<JobInstanceInfo> getJobInstanceByPage(String name, int pageIndex, int pageSize)
    {
        List<JobInstanceInfo> infos = jobInstanceDao.getJobInstanceByPage(name, (pageIndex-1)*pageSize, pageSize);
        int count = jobInstanceDao.getInstanceCount(name);
        List<String> ids = Lists.transform(infos, new Function<JobInstanceInfo, String>()
        {
            @Override
            public String apply(JobInstanceInfo info)
            {
                return info.getId();
            }
        });
        if(infos.size() > 0)
        {
            List<JobInstanceItem> items = jobInstanceDao.getJobInstanceItems(ids);
            ListAdapter.forEachWith(new Action2<JobInstanceInfo, List<JobInstanceItem>>()
            {
                @Override
                public void call(JobInstanceInfo info, List<JobInstanceItem> items)
                {
                    List<JobInstanceItem> il = ListAdapter.selectWith(new Predicate2<JobInstanceItem, String>()
                    {
                        @Override
                        public boolean accept(JobInstanceItem jobInstanceItem, String id)
                        {
                            return jobInstanceItem.getInstanceId().equals(id);
                        }
                    }, items, info.getId());
                    info.setItems(il);
                }
            }, infos, items);
        }
        return PageResult.New(infos,count);
    }

    public void setJobDao(IJobDao jobDao)
    {
        this.jobDao = jobDao;
    }

    public void setJobInstanceDao(IJobInstanceDao jobInstanceDao)
    {
        this.jobInstanceDao = jobInstanceDao;
    }

    public void setErrorDao(IErrorDao errorDao)
    {
        this.errorDao = errorDao;
    }
}
