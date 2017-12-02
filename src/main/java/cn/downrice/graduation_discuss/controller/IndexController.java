package cn.downrice.graduation_discuss.controller;

import cn.downrice.graduation_discuss.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public String index(Model model){
        logger.info("进入方法index");
        //User user =
        return "index";
    }

    //统一异常处理（SpringMVC以外的/没处理的异常）
    @ExceptionHandler()
    public String error(Exception e){
        logger.error("发生异常：" + e.getMessage());
        return "error";
    }
}
