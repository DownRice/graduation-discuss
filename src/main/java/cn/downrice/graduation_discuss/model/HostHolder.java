package cn.downrice.graduation_discuss.model;

import org.springframework.stereotype.Component;

/**
 * 存储登录用户的容器
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void remove(){
        users.remove();
    }
}
