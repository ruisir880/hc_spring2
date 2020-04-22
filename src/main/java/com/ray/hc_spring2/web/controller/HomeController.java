package com.ray.hc_spring2.web.controller;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by rui on 2018/8/12.
 */
@Controller
public class HomeController {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = { "/", "index"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("result","");
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = { "login" }, method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request, Model m, Map<String, Object> map) {
        ModelAndView modelAndView = new ModelAndView();
        // 登录失败从request中获取shiro处理的异常信息
        // shiroLoginFailure:就是shiro异常类的全类名
        String exception = (String) request.getAttribute("shiroLoginFailure");
        String msg = "";
        int result = 0;
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                result = 1;
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                result=2;
            } else if ("kaptchaValidateFailed".equals(exception)) {
                result=3;
            } else {
                msg = "else >> " + exception;
            }
            logger.warn("Login info:" + exception);
            modelAndView.addObject("result",result);
            modelAndView.setViewName("login");
            return modelAndView;
        }
        modelAndView.setViewName("login");
        return modelAndView;
    }


}
