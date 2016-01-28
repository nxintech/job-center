package com.nxin.framework.controllers;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.nxin.framework.core.ConsumerType;
import com.nxin.framework.core.JobController;
import com.nxin.framework.domain.ActionResult;
import com.nxin.framework.domain.JobConfiguration;
import com.nxin.framework.domain.JobInstanceInfo;
import com.nxin.framework.domain.PageResult;
import com.nxin.framework.manager.IJobRepository;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by petzold on 2015/12/25.
 */
@Controller
@RequestMapping("/home")
public class HomeController extends BaseController
{
    @Autowired
    private IJobRepository jobRepository;
    @Autowired
    private JobController jobController;
    private Pattern urlPattern = Pattern.compile("https?://%s(:%d)?/[a-zA-Z0-9/]+");
    @ResponseBody
    @RequestMapping(value = "/addJob",method = RequestMethod.POST)
    public ActionResult<Boolean> addJob(String job)
    {
        JobConfiguration configuration = JSON.parseObject(job,JobConfiguration.class);
        if(Strings.isNullOrEmpty(configuration.getName()))
        {
            return fail(1000006);
        }
        if(Strings.isNullOrEmpty(configuration.getExpression()) || !CronExpression.isValidExpression(configuration.getExpression()))
        {
            return fail(1000002);
        }
        if (configuration.getConsumerType() == ConsumerType.HTTP.ordinal())
        {
            if(Strings.isNullOrEmpty(configuration.getCallbackUrl()))
            {
                return fail(1000003);
            }
            if(!urlPattern.matcher(configuration.getCallbackUrl()).matches())
            {
                return fail(1000004);
            }
        }
        if(configuration.isNeedSharding() && configuration.getShardingTotal() == 0)
        {
            return fail(1000005);
        }
        try
        {
            int code = jobController.newJob(configuration);
            if(code != 0)
            {
                return fail(code);
            }
        }catch (Exception e)
        {
            logger.error("添加任务失败",e);
            return fail(2000001);
        }
        return ActionResult.New(true);
    }
    @ResponseBody
    @RequestMapping(value = "/runJobs",method = RequestMethod.POST)
    public ActionResult<Boolean> runJob(String ids)
    {
        try
        {
            jobController.runJobs(Splitter.on(",").splitToList(ids));
            return ActionResult.New(true);
        }
        catch (Exception e)
        {
            logger.error("执行任务失败", e);
            return ActionResult.New(false);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/updateJob",method = RequestMethod.POST)
    public ActionResult<Boolean> updateJob(String job)
    {
        JobConfiguration configuration = JSON.parseObject(job,JobConfiguration.class);
        if(Strings.isNullOrEmpty(configuration.getId()))
        {
            return fail(1000007);
        }
        if(Strings.isNullOrEmpty(configuration.getExpression()) || !CronExpression.isValidExpression(configuration.getExpression()))
        {
            return fail(1000002);
        }
        if (configuration.getConsumerType() == ConsumerType.HTTP.ordinal())
        {
            if(Strings.isNullOrEmpty(configuration.getCallbackUrl()))
            {
                return fail(1000003);
            }
            if(!urlPattern.matcher(configuration.getCallbackUrl()).matches())
            {
                return fail(1000004);
            }
        }
        if(configuration.isNeedSharding() && configuration.getShardingTotal() == 0)
        {
            return fail(1000005);
        }
        try
        {
            jobController.updateJob(configuration);
            return ActionResult.New(true);
        }
        catch (Exception e)
        {
            logger.error("更新任务失败", e);
            return ActionResult.New(false);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/deleteJob",method = RequestMethod.POST)
    public ActionResult<Boolean> deleteJob(String name)
    {
        try
        {
            jobController.deleteJob(name);
            return ActionResult.New(true);
        }
        catch (Exception e)
        {
            logger.error("删除任务失败",e);
            return fail(2000001);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/deleteJobs",method = RequestMethod.POST)
    public ActionResult<Boolean> deleteJobs(String ids)
    {
        try
        {
            List<String> idl = Splitter.on(",").splitToList(ids);
            jobRepository.deleteJobs(idl);
            return ActionResult.New(true);
        }
        catch (Exception e)
        {
            logger.error("删除任务失败", e);
            return ActionResult.New(false);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/getJobsByPage",method = RequestMethod.POST)
    public PageResult<JobConfiguration> getJobsByPage(String name,int pageIndex,int pageSize)
    {
        try
        {
            return jobRepository.getJobByPage(name, pageIndex, pageSize);
        }
        catch (Exception e)
        {
            logger.error("分页获取任务失败", e);
            return PageResult.New("分页获取任务失败",2000001);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getJobInstanceByPage",method = RequestMethod.POST)
    public PageResult<JobInstanceInfo> getJobInstanceByPage(String name,int pageIndex,int pageSize)
    {
        try
        {
            return jobRepository.getJobInstanceByPage(name, pageIndex, pageSize);
        }
        catch (Exception e)
        {
            logger.error("获取实例列表失败");
            return PageResult.New("分页获取实例失败",2000001);
        }
    }
    @ResponseBody
    @RequestMapping(value = "/reportJob",method = RequestMethod.POST)
    public ActionResult<Boolean> reportJob(String id,int state,String error)
    {
        try
        {
            jobRepository.updateItem(id,state,error);
            return ActionResult.New(true);
        }
        catch (Exception e)
        {
            logger.error("更新任务执行状态失败",e);
            return ActionResult.New(false);
        }
    }
    @Override
    String getError(int code)
    {
        return jobRepository.getByCode(code).getError();
    }
}
