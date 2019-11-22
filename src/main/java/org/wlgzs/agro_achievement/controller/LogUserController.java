package org.wlgzs.agro_achievement.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.wlgzs.agro_achievement.base.BaseController;
import org.wlgzs.agro_achievement.entity.User;
import org.wlgzs.agro_achievement.util.Result;
import org.wlgzs.agro_achievement.util.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;

/**
 * @author:胡亚星
 * @createTime 2019-01-19 11:23
 * @description:
 **/
@RestController
@RequestMapping("/LogUser")
public class LogUserController extends BaseController {

    //去登录
    @RequestMapping(value = "/toLogin")
    public ModelAndView toLogin() {
        return new ModelAndView("login");
    }

    //去注册
    @RequestMapping(value = "/toRegister")
    public ModelAndView toRegister() {
        return new ModelAndView("information/register");
    }

    /**
     * 登录
     *
     * @param request
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping(value = "/userlogin")
    public ModelAndView userlogin(Model model, HttpServletRequest request, String userName, String password) {
        Result result = loginService.login(request, userName, password);
        int code = result.getCode();
        if (code == 1) {//管理员登录
            return new ModelAndView("admin/adminIndex");
        } else if (code == 2 || code == 0) {
            //普通用户（或者专家）登录
            String url = "redirect:/HomeController/home";
            return new ModelAndView(url);
        } else {//登录失败
            model.addAttribute("msg", "账号或密码错误");
            return new ModelAndView("login");
        }
    }

    /**
     * 注册
     *
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "/register")
    public Result register(Model model, HttpServletRequest request, User user, String code) {
        if (iUserService.contrastCode(request, user.getUserEmail(), code).getCode() == 0) {
            //成功
            loginService.register(request, user);
            model.addAttribute("msg", "注册成功！");
            return new Result(ResultCode.SUCCESS);
        }
        model.addAttribute("msg", "您的信息有误！");
        System.out.println("您的信息有误");
        return new Result(ResultCode.FAIL);
    }

    /**
     * 检测用户名是否存在
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public Result checkName(String userName) {
        return loginService.checkName(userName);
    }

    /**
     * 检测收手机号是否存在
     *
     * @param userPhone
     * @return
     */
    @RequestMapping(value = "/checkPhone", method = RequestMethod.GET)
    public Result checkPhone(String userPhone) {
        return loginService.checkPhone(userPhone);
    }

    /**
     * 检测邮箱是否存在
     *
     * @param userEmail
     * @return
     */
    @RequestMapping(value = "/checkEmail", method = RequestMethod.GET)
    public Result checkEmail(String userEmail) {
        return loginService.checkEmail(userEmail);
    }

    //普通用户退出登录
    @RequestMapping(value = "/exit")
    public ModelAndView exit(HttpSession session){
        session.removeAttribute("user");
        return new ModelAndView("redirect:/HomeController/home");
    }

    //管理员退出登录
    @RequestMapping(value = "/adminExit")
    public ModelAndView adminExit(HttpSession session){
        session.removeAttribute("adminUser");
        return new ModelAndView("redirect:/LogUser/toLogin");
    }

}
