package cn.downrice.graduation_discuss.async.handler;

import cn.downrice.graduation_discuss.async.EventHandler;
import cn.downrice.graduation_discuss.async.EventModel;
import cn.downrice.graduation_discuss.async.EventType;
import cn.downrice.graduation_discuss.model.Message;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.service.MessageService;
import cn.downrice.graduation_discuss.service.UserService;
import cn.downrice.graduation_discuss.util.MyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        logger.info("进入了LikeHandler 的 doHandle");
        Message message = new Message();
        message.setFromId(MyUtil.SYSYTEM_USER);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUserById(model.getActorId());
        message.setContent("用户"+user.getName()+"为你点赞了");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
