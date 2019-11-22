package org.wlgzs.agro_achievement.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wlgzs.agro_achievement.base.BaseController;
import org.wlgzs.agro_achievement.entity.Achievement;
import org.wlgzs.agro_achievement.entity.Demand;
import org.wlgzs.agro_achievement.entity.Example;
import org.wlgzs.agro_achievement.entity.Experts;
import org.wlgzs.agro_achievement.service.IExampleService;
import org.wlgzs.agro_achievement.util.Result;

import java.util.List;

/**
 * @author:胡亚星
 * @createTime 2019-02-13 17:03
 * @description: 审核控制层
 **/
@Controller
@RequestMapping("/admin")
public class AuditController extends BaseController {

    //按审核状态查询成果
    @RequestMapping(value = "/AchievementStatusCode")
    public ModelAndView achievementStatusCode(Model model,@RequestParam(value = "current", defaultValue = "1") int current,
                                              @RequestParam(value = "limit", defaultValue = "8") int limit,
                                              @RequestParam(name = "statusCode",defaultValue = "1") String statusCode){
        Result result = iAchievementService.AchievementStatusCode(statusCode,current,limit);
        List<Achievement> achievementList = (List<Achievement>) result.getData();

        model.addAttribute("achievementList", achievementList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数
        return new ModelAndView("AchievementAuditList");
    }

    //去审核成果
    @RequestMapping(value = "/toAuditAchievement")
    public ModelAndView toAuditAchievement(Model model,Integer achievementId){
        Result result = iAchievementService.achievementDetails(achievementId);
        Achievement achievement = (Achievement) result.getData();
        model.addAttribute("achievement",achievement);
        if(achievement != null){
            model.addAttribute("msg","查询成功！");
        }else{
            model.addAttribute("msg","查询失败！");
        }
        return new ModelAndView("auditAchievement");
    }

    //审核成果
    @RequestMapping(value = "/auditAchievement")
    public ModelAndView auditAchievement(Model model,Integer achievementId, String statusCode){
        iAuditService.auditAchievement(achievementId,statusCode);
        model.addAttribute("msg","审核成功！");
        return new ModelAndView("redirect:/admin/AchievementStatusCode");
    }

    //按审核状态查询需求
    @RequestMapping(value = "/DemandStatusCode")
    public ModelAndView demandStatusCode(Model model,@RequestParam(value = "current", defaultValue = "1") int current,
                                              @RequestParam(value = "limit", defaultValue = "8") int limit,
                                              @RequestParam(name = "statusCode",defaultValue = "1") String statusCode){
        Result result = iDemandService.selectDemandByCode(statusCode,current,limit);
        List<Demand> demandList = (List<Demand>) result.getData();

        model.addAttribute("demandList", demandList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数
        return new ModelAndView("DemandAuditList");
    }

    //去审核需求
    @RequestMapping(value = "/toAuditDemand")
    public ModelAndView toAuditDemand(Model model,Integer demandId){
        Result result = iDemandService.demandDetails(demandId);
        Demand demand = (Demand) result.getData();
        model.addAttribute("demand",demand);
        if(demand != null){
            model.addAttribute("msg","查询成功！");
        }else{
            model.addAttribute("msg","查询失败！");
        }
        return new ModelAndView("auditDemand");
    }

    //审核需求
    @RequestMapping(value = "/auditDemand")
    public ModelAndView auditDemand(Model model,Integer demandId,String statusCode){
        iAuditService.auditDemand(demandId,statusCode);
        model.addAttribute("msg","审核成功");
        return new ModelAndView("redirect:/admin/DemandStatusCode");
    }


    //按状态查询案例
    @RequestMapping(value = "/ExampleStatusCode")
    public ModelAndView exampleStatusCode(Model model,@RequestParam(value = "current", defaultValue = "1") int current,
                                         @RequestParam(value = "limit", defaultValue = "8") int limit,
                                         @RequestParam(name = "statusCode",defaultValue = "1") String statusCode){
        Result result = iCaseService.selectExampleByCode(statusCode,current,limit);
        List<Example> exampleList = (List<Example>) result.getData();

        model.addAttribute("exampleList", exampleList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数
        return new ModelAndView("ExampleAuditList");
    }

    //去审核案例
    @RequestMapping(value = "/toAuditExample")
    public ModelAndView toAuditExample(Model model,Integer exampleId){
        Example example = iCaseService.exampleDetails(exampleId);
        model.addAttribute("example",example);
        if(example != null){
            model.addAttribute("msg","查询成功！");
        }else{
            model.addAttribute("msg","查询失败！");
        }
        return new ModelAndView("auditDemand");
    }

    //审核案例
    @RequestMapping(value = "/auditExample")
    public ModelAndView auditExample(Model model,Integer exampleId,String statusCode){
        iAuditService.auditExample(exampleId,statusCode);
        model.addAttribute("msg","审核成功");
        return new ModelAndView("redirect:/admin/ExampleStatusCode");
    }

    //按状态查询专家
    @RequestMapping(value = "/ExpertsStatusCode")
    public ModelAndView expertsStatusCode(Model model,@RequestParam(value = "current", defaultValue = "1") int current,
                                         @RequestParam(value = "limit", defaultValue = "8") int limit,
                                         @RequestParam(name = "statusCode",defaultValue = "1") String statusCode){
        Result result = iExpertsService.selectExpertsByCode(statusCode,current,limit);
        List<Experts> expertsList = (List<Experts>) result.getData();

        model.addAttribute("expertsList", expertsList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数
        return new ModelAndView("ExpertsAuditList");
    }

    //去审核专家
    @RequestMapping(value = "/toAuditExperts")
    public ModelAndView toAuditExperts(Model model,Integer expertsId){
        Experts experts = iExpertsService.expertsDetails(expertsId);
        model.addAttribute("experts",experts);
        if(experts != null){
            model.addAttribute("msg","查询成功！");
        }else{
            model.addAttribute("msg","查询失败！");
        }
        return new ModelAndView("auditDemand");
    }

    //审核专家
    @RequestMapping(value = "/auditExperts")
    public ModelAndView auditExperts(Model model,Integer expertsId,String statusCode){
        iAuditService.auditExperts(expertsId,statusCode);
        model.addAttribute("msg","审核成功");
        return new ModelAndView("redirect:/admin/ExpertsStatusCode");
    }


}
