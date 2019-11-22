package org.wlgzs.agro_achievement.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wlgzs.agro_achievement.base.BaseController;
import org.wlgzs.agro_achievement.entity.*;
import org.wlgzs.agro_achievement.util.Result;

import java.util.List;

/**
 * @author:胡亚星
 * @createTime 2019-02-14 17:02
 * @description:
 **/
@Controller
@RequestMapping("/HomeController")
public class HomeController extends BaseController {

    //首页数据
    @RequestMapping("/home")
    public ModelAndView home(Model model) {
        long l = System.currentTimeMillis();
        //首页技术供给
        List<Achievement> achievementList = iAchievementService.selectAchieveByTime("home");
        model.addAttribute("achievementList", achievementList);

        //首页技术需求
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("status_code", "1");
        Page page = new Page(1, 10);
        IPage<Demand> iPage = iDemandService.page(page, queryWrapper);
        List<Demand> demandList = iPage.getRecords();
        model.addAttribute("demandList", demandList);

        //成功案例
        Result result = iCaseService.selectExample(1, 4);
        List<Example> exampleList = null;
        if (result.getCode() == 0) {
            exampleList = (List<Example>) result.getData();
        }
        model.addAttribute("exampleList", exampleList);

        //专家推荐
        Result result1 = iExpertsService.selectExperts(1, 5);
        List<Experts> expertsList = null;
        if (result1.getCode() == 0) {
            expertsList = (List<Experts>) result1.getData();
        }
        model.addAttribute("expertsList", expertsList);

        //机构推荐
        List<Organization> organizationList = iOrganizationService.selectAllOrganization(1, 5);
        model.addAttribute("organizationList", organizationList);

        //公告类型（新闻中心，交易活动，政策中心）
        List<Announcement> newsList = (List<Announcement>) iAnnouncementService.selectAnnouncement("新闻中心", 1, 4).getData();
        List<Announcement> tradingList = (List<Announcement>) iAnnouncementService.selectAnnouncement("交易活动", 1, 4).getData();
        List<Announcement> policyList = (List<Announcement>) iAnnouncementService.selectAnnouncement("政策中心", 1, 4).getData();
        model.addAttribute("newsList", newsList);
        model.addAttribute("tradingList", tradingList);
        model.addAttribute("policyList", policyList);
        System.out.println(l- System.currentTimeMillis()+"--------");
        return new ModelAndView("index");
    }

    //成果小首页
    @RequestMapping("/AchievementHome")
    public ModelAndView achievementHome(Model model) {
        //成果所有分类
        Result result1 = iTypeService.selectAllType();
        List<Type> typeList = (List<Type>) result1.getData();
        model.addAttribute("typeList", typeList);

        //最新发布
        List<Achievement> achievementTimeList = iAchievementService.selectAchieveByTime("");
        String img;
        for (int i = 0; i < achievementTimeList.size(); i++) {
            if (achievementTimeList.get(i).getPictureAddress().contains(",")) {
                img = achievementTimeList.get(i).getPictureAddress();
                img = img.substring(0, img.indexOf(","));
                achievementTimeList.get(i).setPictureAddress(img);
            }
        }
        model.addAttribute("achievementTimeList", achievementTimeList);

        //排行榜
        Result result = iAchievementService.rankingAchievement(1, 5);
        List<Achievement> achievementRankingList = (List<Achievement>) result.getData();
        for (int i = 0; i < achievementRankingList.size(); i++) {
            if (achievementRankingList.get(i).getPictureAddress().contains(",")) {
                img = achievementRankingList.get(i).getPictureAddress();
                img = img.substring(0, img.indexOf(","));
                achievementRankingList.get(i).setPictureAddress(img);
            }
        }
        model.addAttribute("achievementRankingList", achievementRankingList);

        //推荐
        List<Achievement> hotAchievement = iAchievementService.hotAchievement();
        model.addAttribute("hotAchievement", hotAchievement);
        return new ModelAndView("achievement/AchievementHome");
    }


    //专家小首页
    @RequestMapping(value = "/ExpertsHome")
    public ModelAndView expertsHome(Model model) {
        //所有专家分类
        Result result1 = iTypeService.selectAllType();
        List<Type> typeList = (List<Type>) result1.getData();
        model.addAttribute("typeList", typeList);

        //新闻中心
        Result result3 = iAnnouncementService.selectAnnouncement("新闻中心", 1, 10);
        List<Announcement> NewsCenter = (List<Announcement>) result3.getData();
        model.addAttribute("NewsCenter", NewsCenter);

        //最新专家
        Result result = iExpertsService.selectExpertsByTime(3);
        List<Experts> expertsTimeList = (List<Experts>) result.getData();
        model.addAttribute("expertsTimeList", expertsTimeList);

        //专家排行
        Result result2 = iExpertsService.expertRanking(5);
        List<Experts> expertsRankingList = (List<Experts>) result2.getData();
        model.addAttribute("expertsRankingList", expertsRankingList);

        //专家推荐
        List<Experts> recommendList = iExpertsService.recommend(3);
        model.addAttribute("recommendList", recommendList);

        return new ModelAndView("experts/ExpertsHome");
    }

    //机构小首页
    @RequestMapping(value = "/OrganizationHome")
    public ModelAndView organizationHome(Model model) {

        //查询所有机构类别
        List<OrganizationType> list = (List<OrganizationType>) iOrganizationTypeService.selectAllOrganizationType().getData();
        model.addAttribute("OrganizationTypeList", list);

        //最新加入的机构
        List<Organization> organizationTimeList = iOrganizationService.selectOrganizationByTime();
        model.addAttribute("organizationTimeList", organizationTimeList);

        //机构排行
        List<Organization> organizationRankingList = iOrganizationService.rankingOrganization(1, 10);
        model.addAttribute("organizationRankingList", organizationRankingList);

        //查询科研教学单位
        Result result = iOrganizationService.selectOrganizationByType("科研教学", 1, 3);
        List<Organization> schoolList = (List<Organization>) result.getData();
        model.addAttribute("schoolList",schoolList);

        return new ModelAndView("Organization/OrganizationHome");
    }

    //供需首页数据
    @RequestMapping(value = "/SupplyHome")
    public ModelAndView supplyHome(Model model) {

        //成果排行榜
        Result result = iAchievementService.rankingAchievement(1, 10);
        List<Achievement> achievementRankingList = (List<Achievement>) result.getData();
        model.addAttribute("achievement", achievementRankingList);

        //专家排行
        Result result1 = iExpertsService.expertRanking(10);
        List<Experts> expertsRankingList = (List<Experts>) result1.getData();
        model.addAttribute("experts", expertsRankingList);

        //首页技术需求
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("status_code", "1");
        Page page = new Page(1, 10);
        IPage<Demand> iPage = iDemandService.page(page, queryWrapper);
        List<Demand> demandList = iPage.getRecords();
        model.addAttribute("demand", demandList);

        //机构排行
        List<Organization> organizationRankingList = iOrganizationService.rankingOrganization(1, 10);
        model.addAttribute("organization", organizationRankingList);

        return new ModelAndView("demand/SupplyHome");
    }

    //政策首页
    @RequestMapping(value = "/PolicyHome")
    public ModelAndView policyHome(Model model) {
        //新闻中心
        Result result1 = iAnnouncementService.selectAnnouncement("新闻中心", 1, 10);
        List<Announcement> NewsCenter = (List<Announcement>) result1.getData();
        model.addAttribute("NewsCenter", NewsCenter);
        //交易活动
        Result result2 = iAnnouncementService.selectAnnouncement("交易活动", 1, 10);
        List<Announcement> TradingActivity = (List<Announcement>) result2.getData();
        model.addAttribute("TradingActivity", TradingActivity);
        //政策中心
        Result result3 = iAnnouncementService.selectAnnouncement("政策中心", 1, 10);
        List<Announcement> PolicyCenter = (List<Announcement>) result3.getData();
        model.addAttribute("PolicyCenter", PolicyCenter);

        return new ModelAndView("Policy/PolicyHome");
    }

    //查询成功案例（显示的）
    @GetMapping("/selectExample")
    public ModelAndView selectExample(Model model, @RequestParam(value = "current", defaultValue = "1") Integer current,
                                      @RequestParam(value = "limit", defaultValue = "8") Integer limit) {
        Result result = iCaseService.selectExample(current, limit);
        List<Example> exampleList = (List<Example>) result.getData();
        if (exampleList != null) {
            model.addAttribute("msg", "查询成功！");
        } else {
            model.addAttribute("msg", "暂无数据！");
        }
        model.addAttribute("exampleList", exampleList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数
        return new ModelAndView("example/ExampleList");
    }

    //查看专家信息
    @RequestMapping(value = "/expertsDetails")
    public ModelAndView expertsDetails(Model model, Integer expertsId) {
        Experts experts = iExpertsService.expertsDetails(expertsId);
        model.addAttribute("experts", experts);//专家信息
        if (experts == null) {
            model.addAttribute("msg", "不存在！");
        }
        return new ModelAndView("experts/expertsDetails");
    }

    //前台查询所有专家（通过的）
    @RequestMapping(value = "/selectExperts")
    public ModelAndView selectExperts(Model model, @RequestParam(value = "current", defaultValue = "1") int current,
                                      @RequestParam(value = "limit", defaultValue = "8") int limit) {
        Result result = iExpertsService.selectExperts(current, limit);
        List<Experts> expertsList = (List<Experts>) result.getData();

        model.addAttribute("expertsList", expertsList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数

        return new ModelAndView("experts/expertsList");
    }

    //前台查询所有专家
    @RequestMapping(value = "/selectAllOrganization")
    public ModelAndView selectAllOrganization(Model model, @RequestParam(value = "current", defaultValue = "1") int current,
                                              @RequestParam(value = "limit", defaultValue = "8") int limit) {
        List<Organization> organizationList = iOrganizationService.selectAllOrganization(current, limit);
        if (organizationList != null) {
            model.addAttribute("msg", "查询成功！");
        } else {
            model.addAttribute("msg", "查询失敗！");
        }
        model.addAttribute("organizationList", organizationList);
        return new ModelAndView("Organization/OrganizationList");
    }

    //前台按类型查询专家
    @RequestMapping(value = "/selectExpertsByType")
    public ModelAndView selectExpertsByType(Model model, @RequestParam(value = "type", defaultValue = "") String type, @RequestParam(value = "current", defaultValue = "1") int current,
                                            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        Result result = iExpertsService.selectExpertsByType(type, current, limit);
        List<Experts> expertsList = (List<Experts>) result.getData();
        model.addAttribute("expertsList", expertsList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数

        //所有类别
        Result result1 = iTypeService.selectAllType();
        List<Type> typeList = (List<Type>) result1.getData();
        model.addAttribute("typeList", typeList);
        model.addAttribute("type", type);

        return new ModelAndView("experts/expertsTypeList");
    }

    //前台按类型查询机构
    @RequestMapping(value = "/selectAchieveByType")
    public ModelAndView selectOrganizationByType(Model model, @RequestParam(value = "type", defaultValue = "") String type, @RequestParam(value = "current", defaultValue = "1") int current,
                                                 @RequestParam(value = "limit", defaultValue = "8") int limit) {
        Result result = iOrganizationService.selectOrganizationByType(type, current, limit);
        List<Organization> organizationList = (List<Organization>) result.getData();

        model.addAttribute("organizationList", organizationList);
        model.addAttribute("TotalPages", result.getPages());//总页数
        model.addAttribute("Number", result.getCurrent());//当前页数

        //查询所有机构类别
        List<OrganizationType> list = (List<OrganizationType>) iOrganizationTypeService.selectAllOrganizationType().getData();
        model.addAttribute("OrganizationTypeList", list);

        return new ModelAndView("Organization/OrganizationList");
    }

    //查看机构详情
    @RequestMapping(value = "/organizationDetails")
    public ModelAndView organizationDetails(Model model, Integer organizationId) {
        Organization organization = iOrganizationService.getById(organizationId);
        model.addAttribute("organization", organization);
        return new ModelAndView("Organization/OrganizationDetails");
    }

    //首页搜索（全局搜索）
    @RequestMapping(value = "/globalSearch")
    public ModelAndView globalSearch(Model model, @RequestParam(value = "findName", defaultValue = "") String findName,
                                     @RequestParam(value = "current", defaultValue = "1") int current,
                                     @RequestParam(value = "limit", defaultValue = "20") int limit) {
        findName = findName.replaceAll("\\s*", "");
        IPage<Experts> Experts = iExpertsService.findName(findName, current, limit);
        IPage<Organization> Organization = iOrganizationService.findName(findName, current, limit);
        //搜索成果
        IPage<Achievement> iPage = iAchievementService.findName(findName, current, limit);
        int a = (int) Math.ceil(iPage.getTotal() / 20f);//成果页数
        int size = 20 - iPage.getRecords().size();//当页缺少的成果条数
        if (size == 20) {
            //成果总数
            model.addAttribute("AchievementNumber", 0);
            model.addAttribute("findAchievement", null);
        } else {
            //成果总数
            model.addAttribute("AchievementNumber", iPage.getTotal());
            model.addAttribute("findAchievement", iPage.getRecords());
        }

        if (current >= a && size > 0) {//到达成果的最后一页，且数据量不足
            //搜索专家
            IPage<Experts> iPage1 = iExpertsService.findName(findName, current, size);
            int b = 0;
            if (a * 20 == iPage.getTotal()) {
                b = (int) Math.ceil(iPage1.getTotal() / 20f) + a;//专家的最后页数
            } else {
                b = (int) Math.ceil(iPage1.getTotal() / 20f) + a - 1;//专家的最后页数
            }

            int size1 = 20 - iPage1.getRecords().size();//当页缺少的专家条数
            //专家总数
            model.addAttribute("ExpertsNumber", iPage1.getTotal());
            model.addAttribute("findExperts", iPage1.getRecords());
            if (current >= b && size1 > 0) {//到达成果的最后一页，且数据量不足
                //搜索机构
                IPage<Organization> iPage2 = iOrganizationService.findName(findName, current, size1);
//                int c = (int) Math.ceil(iPage2.getTotal() / 20f);//机构页数
                //机构总数
                model.addAttribute("OrganizationNumber", iPage2.getTotal());
                model.addAttribute("findOrganization", iPage2.getRecords());
            } else {
                model.addAttribute("OrganizationNumber", 0);
                model.addAttribute("findOrganization", null);
            }
        } else {
            //专家总数
            model.addAttribute("ExpertsNumber", 0);
            model.addAttribute("findExperts", null);
        }

        //分页信息
        //总条数(可以写一个方法)
        long TotalNumber = iPage.getTotal() + Experts.getTotal() + Organization.getTotal();
        model.addAttribute("TotalNumber", TotalNumber);

        //总条数/20,向上取整
        model.addAttribute("TotalPages", Math.ceil(TotalNumber / 20f));//总页数
        model.addAttribute("Number", current);//当前页数
        model.addAttribute("findName", findName);//搜索关键字
        return new ModelAndView("findHome");
    }

    //按照类型搜索
    @RequestMapping(value = "/globalSearch/{type}")
    public ModelAndView globalSearchType(Model model, String findName, @PathVariable("type") String type,
                                         @RequestParam(value = "current", defaultValue = "1") int current,
                                         @RequestParam(value = "limit", defaultValue = "8") int limit) {
        if ("Achievement".equals(type)) {
            IPage<Achievement> iPage = iAchievementService.findName(findName, current, limit);
            model.addAttribute("findAchievement", iPage.getRecords());
            model.addAttribute("TotalPages", iPage.getPages());//总页数
            model.addAttribute("Number", iPage.getCurrent());//当前页数
        } else if ("Experts".equals(type)) {
            IPage<Experts> iPage = iExpertsService.findName(findName, current, limit);
            model.addAttribute("findExperts", iPage.getRecords());
            model.addAttribute("TotalPages", iPage.getPages());//总页数
            model.addAttribute("Number", iPage.getCurrent());//当前页数
        } else {
            IPage<Organization> iPage = iOrganizationService.findName(findName, current, limit);
            model.addAttribute("findOrganization", iPage.getRecords());
            model.addAttribute("TotalPages", iPage.getPages());//总页数
            model.addAttribute("Number", iPage.getCurrent());//当前页数
        }
        model.addAttribute("findName", findName);//搜索关键字
        model.addAttribute("type", type);//搜索类型
        return new ModelAndView("globalSearch");
    }
}
