package cn.downrice.graduation_discuss.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于整合数据展示
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
