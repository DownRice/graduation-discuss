package cn.downrice.graduation_discuss.interceptor;

import cn.downrice.graduation_discuss.model.HostHolder;
import cn.downrice.graduation_discuss.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 负责对非登录状态的页面访问进行拦截，进行login页面的跳转
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (hostHolder.getUser() == null) {
            httpServletResponse.sendRedirect("login?next=" + httpServletRequest.getRequestURI());
        }else if(hostHolder.getUser().getState() != MyUtil.STATE_VALID){
            //邮箱未激活
            httpServletResponse.sendRedirect("activateMail?email="+hostHolder.getUser().getEmail());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
