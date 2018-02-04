package cn.downrice.graduation_discuss.controller;

import cn.downrice.graduation_discuss.async.EventModel;
import cn.downrice.graduation_discuss.async.EventProducer;
import cn.downrice.graduation_discuss.async.EventType;
import cn.downrice.graduation_discuss.model.HostHolder;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.service.UserService;
import cn.downrice.graduation_discuss.util.JedisAdapter;
import cn.downrice.graduation_discuss.util.MyUtil;
import cn.downrice.graduation_discuss.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = {"/reg"}, method = RequestMethod.GET)
    public String reg(Model model, @RequestParam(value="next", required = false) String next){
        model.addAttribute("next", next);
        return "reg";
    }

    @RequestMapping(path = {"/login"}, method = RequestMethod.GET)
    public String login(Model model, @RequestParam(value="next", required = false) String next){
        model.addAttribute("next", next);
        return "login";
    }

    /**
     * 邮件内链接激活-Get
     */
    @RequestMapping(path = {"/varify"}, method = RequestMethod.GET)
    public String varifyGet(Model model, @RequestParam(value="next", required = false) String next,
                         @RequestParam(value = "val", required = false) String val){
        User user = hostHolder.getUser();
        String email = "";
        if(user != null){
            email = user.getEmail();
        }

        String varifyKey = RedisKeyUtil.getVarifyKey(email);
        Boolean result = false;

        //邮箱验证成功
        if(jedisAdapter.sismember(varifyKey, val)){
            //激活
            //result = userService.updateState(MyUtil.STATE_VALID, email);
            user.setState(MyUtil.STATE_VALID);
            result = userService.updateUser(user);
        }
        if(result){
            model.addAttribute("content", "邮箱验证成功！");
        }else{
            model.addAttribute("content","链接已失效");
            model.addAttribute("email", email);
            //激活邮箱的页面
            return "varifyMail";
        }
        return "feedback";
    }

    /**
     * 激活邮箱-Post
     * @param model
     * @return
     */
    @RequestMapping(path = {"/varify"}, method = RequestMethod.POST)
    public String varifyPost(Model model, @RequestParam(value = "email", required = false) String email){
        User user = hostHolder.getUser();
        if(user != null){
            user.setEmail(email);
            userService.updateUser(user);
        }

        //邮件激活
        //随机生成字符串以进行邮箱验证（邮件激活）
        String uuid = UUID.randomUUID().toString();
        String varifyKey = RedisKeyUtil.getVarifyKey(email);
        jedisAdapter.sadd(varifyKey, uuid);
        //设置验证字符串有效期 10分钟
        jedisAdapter.expire(varifyKey, 600);

        return "feedback";
    }

    /**
     * 注册
     * @param model
     * @param username
     * @param password
     * @param email
     * @param sex
     * @param next
     * @param rememberme
     * @param response
     * @return
     */
    @RequestMapping(path = {"/reg"}, method = RequestMethod.POST)
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("email") String email,
                      @RequestParam("sex") String sex,
                      @RequestParam(value="next", required = false) String next,
                      @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response){
        try {

            Map<String, String> information = new HashMap<String, String>();
            information.put("username", username);
            information.put("password", password);
            information.put("email", email);
            information.put("sex", sex);
            //未激活时state为无效
            information.put("state", String.valueOf(MyUtil.STATE_INVALID));
            Map<String, Object> map = userService.register(information);


            //邮件激活
            //随机生成字符串以进行邮箱验证（邮件激活）
            String uuid = UUID.randomUUID().toString();
            String varifyKey = RedisKeyUtil.getVarifyKey(email);
            jedisAdapter.sadd(varifyKey, uuid);
            //设置验证字符串有效期 10分钟
            jedisAdapter.expire(varifyKey, 600);

            eventProducer.fireEvent(new EventModel(EventType.REG)
                    .setExt("username", username)
                    .setExt("email", (String)map.get("email"))
                    .setExt("val", uuid)
                    .setActorId((int)map.get("userId")));

            logger.info("参数：" + map.size());
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "reg";
            }

        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "reg";
        }
    }

    /**
     * 登陆
     * @param model
     * @param username
     * @param password
     * @param next
     * @param rememberme
     * @param response
     * @param request
     * @return
     */
    @RequestMapping(path = {"/login"}, method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username")String username,
                        @RequestParam("password")String password,
                        @RequestParam(value="next", required = false) String next,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response, HttpServletRequest request){
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);


                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username", username).setExt("email", (String)map.get("email"))
                        .setExt("ip", MyUtil.getIpAddr(request))
                        .setActorId((int)map.get("userId")));

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage() + e);
            return "login";
        }
    }

    /**
     * 登出
     * @param ticket
     * @return
     */
    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
