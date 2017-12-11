package cn.downrice.graduation_discuss;

import cn.downrice.graduation_discuss.dao.CommentDAO;
import cn.downrice.graduation_discuss.dao.UserDAO;
import cn.downrice.graduation_discuss.model.Comment;
import cn.downrice.graduation_discuss.model.EntityType;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.service.SensitiveService;
import cn.downrice.graduation_discuss.service.UserService;
import cn.downrice.graduation_discuss.util.MyUtil;
import com.alibaba.fastjson.JSONObject;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GraduationDiscussApplication.class)
@Sql("/test.sql")
public class MyTest {

    private static Logger logger = LoggerFactory.getLogger(MyTest.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserService userService;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    CommentDAO commentDAO;

    @Resource
    FreeMarkerConfigurer freeMarkerConfigurer;

    @Test
    public void myTest(){
        Map<String, String> information = new HashMap<>();

        //JSONObject object = MyUtil.getAreaByIp("xxx");
            //logger.info(object.get("data").getClass().getName());


//        /*
//        try {
//            Thread.sleep(10000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        */
        information.put("username", "TestName");
        information.put("password", "TestPass");
        information.put("email", "844439133@qq.com");
        information.put("sex", "男");
        information.put("headUrl", "TestUrl");
        userService.register(information);
//        sensitiveService.addWord("草泥马");
//       sensitiveService.addWord("欧洲人");
//      System.out.println(sensitiveService.filter("<script>我草♥泥♥马的欧洲人吃屎吧"));

        Comment comment = new Comment();
        comment.setStatus(0);
        comment.setContent("testContent");
        comment.setCreatedDate(new Date());
        comment.setEntityId(0);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setUserId(0);

        commentDAO.insertComment(comment);
        comment.setUserId(1);
        commentDAO.insertComment(comment);
        comment.setUserId(2);
        commentDAO.insertComment(comment);

        List<Comment> list  = commentDAO.selectByEntity(0, 0);
        logger.info("查询出来的list长度为：" + list.size());

        commentDAO.updateStatus(0, 0, 0, -1);


//        try {
//            Configuration configuration = freeMarkerConfigurer.getConfiguration();
//            Template template = configuration.getTemplate("mail/login_feedback.html");
//            Map<String, String> map = new HashMap<>();
//            map.put("user", "草泥马");
//            logger.info("看看吧：" + FreeMarkerTemplateUtils.processTemplateIntoString(template,map));
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }
}
