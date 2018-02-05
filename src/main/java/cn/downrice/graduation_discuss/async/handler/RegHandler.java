package cn.downrice.graduation_discuss.async.handler;

import cn.downrice.graduation_discuss.async.EventHandler;
import cn.downrice.graduation_discuss.async.EventModel;
import cn.downrice.graduation_discuss.async.EventType;
import cn.downrice.graduation_discuss.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RegHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(RegHandler.class);

    @Autowired
    private MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("varifyUrl", "http://localhost:8080/discuss/varify?val="+model.getExt("val"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "邮箱激活确认", "mail/reg_varify.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.REG);
    }
}
