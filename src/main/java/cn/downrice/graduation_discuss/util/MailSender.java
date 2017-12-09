package cn.downrice.graduation_discuss.util;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private JavaMailSenderImpl javaMailSender;

    @Resource
    FreeMarkerConfigurer freeMarkerConfigurer;

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String templateUrl, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("管理员");
            InternetAddress from = new InternetAddress(nick + "<admin@downrice.cn>");
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateUrl);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template,model);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }




    @Override
    public void afterPropertiesSet() throws Exception {
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername("admin@downrice.cn");
        javaMailSender.setPassword("qq896882378A");
        javaMailSender.setHost("smtp.exmail.qq.com");
        //javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(465);
        javaMailSender.setProtocol("smtps");
        javaMailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        //javaMailProperties.put("mail.smtp.auth", true);
        //javaMailProperties.put("mail.smtp.starttls.enable", true);
        javaMailSender.setJavaMailProperties(javaMailProperties);
    }
}
