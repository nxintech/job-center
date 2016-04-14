package com.nxin.framework.controllers;

import com.nxin.framework.domain.ActionResult;
import com.nxin.framework.manager.IJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by petzold on 2016/4/13.
 */
@Controller
@RequestMapping("/anymouns")
public class AnymounsController extends BaseController
{
    @Autowired
    private IJobRepository jobRepository;
    @ResponseBody
    @RequestMapping(value = "/reportJob",method = RequestMethod.POST)
    public ActionResult<Boolean> reportJob(String id, int state, String error)
    {
        try
        {
            logger.info("收到任务状态汇报【id:{}   state:{}   error:{}】",id,state,error);
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
