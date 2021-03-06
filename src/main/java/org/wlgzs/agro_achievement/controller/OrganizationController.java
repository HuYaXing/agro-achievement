package org.wlgzs.agro_achievement.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.wlgzs.agro_achievement.base.BaseController;
import org.wlgzs.agro_achievement.entity.Organization;
import org.wlgzs.agro_achievement.entity.OrganizationType;
import org.wlgzs.agro_achievement.util.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 * 机构
 *
 * @author 胡亚星
 * @since 2019-01-19
 */
@RestController
@RequestMapping("/organization")
public class OrganizationController extends BaseController {

    //去添加机构
    @RequestMapping(value = "/toAddOrganization")
    public ModelAndView toAddOrganization(Model model) {
        //查询所有类型
        Result result1 = iOrganizationTypeService.selectAllOrganizationType();
        List<OrganizationType> typeList = (List<OrganizationType>) result1.getData();
        model.addAttribute("typeList", typeList);
        return new ModelAndView("information/addOrganization");
    }
    //去详情页
    @RequestMapping("/organizationUserDetails")
    public ModelAndView organizationUserDetails(Model model, Integer organizationId) {
        Organization organization = iOrganizationService.getById(organizationId);
        model.addAttribute("organization", organization);
        return new ModelAndView("information/organizationUserDetails");
    }
    //添加机构
    @RequestMapping(value = "/addOrganization")
    public Result addOrganization(HttpServletRequest request,@RequestParam(value = "file",required = false)MultipartFile myFileName,
            Model model, HttpSession session, Organization organization) throws FileNotFoundException {
        Result result = iOrganizationService.addOrganization(request,session, organization,myFileName);
        return result;
    }

    //删除机构
    @RequestMapping(value = "/deleteOrganization")
    public Result deleteOrganization(Model model, Integer organizationId) {
        Result result = iOrganizationService.deleteOrganization(organizationId);
        return result;
    }

    //按用户查询机构（状态码）
    @RequestMapping(value = "/selectOrganizationByUser")
    public ModelAndView selectOrganizationByUser(Model model, HttpServletRequest request,
                                                 @RequestParam(value = "statusCode", defaultValue = "1") String statusCode) {
        Result result = iOrganizationService.selectOrganizationByUser(request, statusCode);
        List<Organization> organizationList = (List<Organization>) result.getData();
        model.addAttribute("organizationList", organizationList);
        return new ModelAndView("information/userOrganizationList");
    }

}
