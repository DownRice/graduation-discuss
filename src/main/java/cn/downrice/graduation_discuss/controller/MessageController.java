package cn.downrice.graduation_discuss.controller;

import cn.downrice.graduation_discuss.dao.MessageDAO;
import cn.downrice.graduation_discuss.model.HostHolder;
import cn.downrice.graduation_discuss.model.Message;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.model.ViewObject;
import cn.downrice.graduation_discuss.service.MessageService;
import cn.downrice.graduation_discuss.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MessageController {

    private static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/msg/list", method = RequestMethod.GET)
    public String conversationList(Model model, @RequestParam(value = "offset", required = false, defaultValue = "0")int offset){

        int localUserId = hostHolder.getUser().getId();

        List<Message> conversationList = messageService.getConversationList(localUserId, offset, 10);
        List<ViewObject> conversations = new ArrayList<ViewObject>();

        try {
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                //取到对方的id
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUserById(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        }catch (Exception e){
            logger.error("获取消息列表失败" + e.getMessage());
            e.printStackTrace();
        }

        return "msglist";
    }

}
