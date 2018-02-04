package cn.downrice.graduation_discuss.async;

import cn.downrice.graduation_discuss.util.JedisAdapter;
import cn.downrice.graduation_discuss.util.RedisKeyUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;
    ThreadFactory eventConsumerFactory = new ThreadFactoryBuilder()
            .setNameFormat("pool-%d").build();
    private ExecutorService cachedThreadPool = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), eventConsumerFactory,
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有实现EventHandler的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);

        if(beans != null){
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
                //找到Handler关心哪些EventType
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

                //将这些EventType与这个Handler关联起来
                for(EventType type : eventTypes){
                    //第一次注册
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        cachedThreadPool.execute(new Runnable() {
                                     @Override
                                     public void run() {
                                         while(true){
                                             String key = RedisKeyUtil.getEventQueueKey();
                                             List<String> events = jedisAdapter.brpop(0, key);
                                             for(String message : events){
                                                 if(message.equals(key)){
                                                     continue;
                                                 }

                                                 EventModel eventModel = JSON.parseObject(message, EventModel.class);
                                                 if(!config.containsKey(eventModel.getType())){

                                                     logger.error("无法识别事件类型"+eventModel.getType());
                                                     continue;
                                                 }
                                                 for(EventHandler eventHandler : config.get(eventModel.getType())){
                                                     eventHandler.doHandle(eventModel);
                                                 }
                                             }
                                         }
                                     }
                                 }
        );
        /*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);
                    for(String message : events){
                        if(message.equals(key)){
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("无法识别事件类型");
                            continue;
                        }
                        for(EventHandler eventHandler : config.get(eventModel.getType())){
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });

        thread.start();
        */
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
