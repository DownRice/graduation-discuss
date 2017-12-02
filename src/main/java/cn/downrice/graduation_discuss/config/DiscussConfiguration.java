package cn.downrice.graduation_discuss.config;

import cn.downrice.graduation_discuss.interceptor.LoginRequiredInterceptor;
import cn.downrice.graduation_discuss.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class DiscussConfiguration extends WebMvcConfigurerAdapter{
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/login", "/reg", "/error");
    }
}
