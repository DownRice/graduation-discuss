package cn.downrice.graduation_discuss.service;

import cn.downrice.graduation_discuss.dao.LoginTicketDAO;
import cn.downrice.graduation_discuss.dao.UserDAO;
import cn.downrice.graduation_discuss.model.LoginTicket;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.util.JedisAdapter;
import cn.downrice.graduation_discuss.util.MyUtil;
import cn.downrice.graduation_discuss.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * 注册验证服务
     * @param information
     * @return
     */
    public Map<String, Object> register(Map<String, String> information){
        Map<String, Object> map = new HashMap<String, Object>();
        String name = (String)information.get("username");
        String password = (String)information.get("password");

        //用户名是否为空验证
        if (StringUtils.isBlank(name)) {
            map.put("msg", "用户名不能为空");
            return map;
        }

        //密码是否为空验证
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDAO.selectUserByName(name);

        //用户名是否已存在验证
        if (user != null) {
            map.put("msg", "用户名已经被注册");
            return map;
        }

        //验证通过
        user = new User();
        user.setName(name);
        user.setEmail((String)information.get("email"));
        user.setHeadUrl("默认头像URL");
        user.setSex((String)information.get("sex"));
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(MyUtil.MD5(password + user.getSalt()));
        //未邮件激活之前都是无效状态(0)
        user.setState((Integer.parseInt(information.get("state"))));

        userDAO.insertUser(user);




        //注册成功，登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("email", user.getEmail());
        map.put("userId", user.getId());
        return map;

    }

    /**
     * 登陆验证服务
     * @param username
     * @param password
     * @return
     */
    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();

        logger.info("进入方法login"+username+password);

        //用户名是否为空验证
        if(StringUtils.isBlank(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }

        //密码是否为空验证
        if(StringUtils.isBlank(password)){
            map.put("msg", "密码不能为空");
            return map;
        }

        User user = userDAO.selectUserByName(username);

        logger.info("进入方法login"+(user==null));
        //用户名是否存在验证
        if(user == null){
            map.put("msg", "用户名不存在");
            return map;
        }

        //密码验证
        if(!MyUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }

        //验证成功
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());
        map.put("email", user.getEmail());
        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(MyUtil.STATE_VALID);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.insertTicket(ticket);
        return ticket.getTicket();
    }

    //登出服务
    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUserById(int id){
        return userDAO.selectUserById(id);
    }

    public User getUserByEmail(String email){
        return userDAO.selectUserByEmail(email);
    }

    public Boolean updateState(int state, String email){
        if(userDAO.updateUserStateByEmail(state, email) > 0){
            return true;
        }
        return false;
    }

    public Boolean updateUser(User user){
        if(userDAO.updateUser(user) > 0){
            return true;
        }
        return false;
    }

}
