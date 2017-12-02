package cn.downrice.graduation_discuss;

import cn.downrice.graduation_discuss.dao.UserDAO;
import cn.downrice.graduation_discuss.model.User;
import cn.downrice.graduation_discuss.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GraduationDiscussApplication.class)
@Sql("/test.sql")
public class MyTest {
    @Autowired
    UserDAO userDAO;

    @Autowired
    UserService userService;

    @Test
    public void myTest(){

        Map<String, String> information = new HashMap<>();
        /*
        try {
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }
        */
        information.put("username", "TestName");
        information.put("password", "TestPass");
        information.put("email", "Test@Test.com");
        information.put("sex", "男");
        information.put("headUrl", "TestUrl");
        userService.register(information);
    }
}
