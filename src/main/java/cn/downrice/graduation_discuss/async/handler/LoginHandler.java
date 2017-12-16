package cn.downrice.graduation_discuss.async.handler;

import cn.downrice.graduation_discuss.async.EventHandler;
import cn.downrice.graduation_discuss.async.EventModel;
import cn.downrice.graduation_discuss.async.EventType;
import cn.downrice.graduation_discuss.util.MailSender;
import cn.downrice.graduation_discuss.util.MyUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    @Autowired
    private MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //logger.info("进入了LoginHandler的doHandle");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", model.getExt("username"));
        //model.getExt("ip")
        JSONObject areaData = MyUtil.getAreaByIp(model.getExt("ip"));
        if(areaData != null) {
            map.put("areaData", areaData);
        }
        Date date = new Date();
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "您的账号"+model.getExt("username")+"登陆了", "mail/login_feedback.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
