package cn.downrice.graduation_discuss.service;

import cn.downrice.graduation_discuss.util.JedisAdapter;
import cn.downrice.graduation_discuss.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class FollowService {
    @Autowired
    private JedisAdapter jedisAdapter;

    /**
     * userId为当前用户ID
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public Boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);

        //具体实体（entityType + entityId）关注者（userId）+1，即粉丝列表
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));

        //用户（userId）所关注的 该实体类型（entityType）的具体对象（entityId)+1， 即关注列表
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));

        List<Object> result = jedisAdapter.exec(tx, jedis);

        return result.size()==2 && (Long)result.get(0)>0 && (Long)result.get(1)>0;
    }

    public Boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);

        //具体实体（entityType + entityId）关注者（userId）-1，即粉丝列表
        tx.zrem(followerKey, String.valueOf(userId));

        //用户（userId）所关注的 该实体类型（entityType）的具体对象（entityId)-1， 即关注列表
        tx.zrem(followeeKey, String.valueOf(entityId));

        List<Object> result = jedisAdapter.exec(tx, jedis);

        return result.size()==2 && (Long)result.get(0)>0 && (Long)result.get(1)>0;
    }

    /**
     * 获取粉丝列表总数
     * @param entityType
     * @param entityId
     * @return
     */
    public long getFollowersCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    /**
     * 获取关注列表总数
     * @param userId
     * @param entityType
     * @return
     */
    public long getFolloweesCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    /**
     * 获取粉丝列表
     * @param entityType
     * @param entityId
     * @param offset
     * @param per
     * @return
     */
    public Set<String> getFollowers(int entityType, int entityId, int offset, int per){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zrevrange(followerKey, offset, offset+per);
    }

    /**
     * 获取关注列表
     * @param userId
     * @param entityType
     * @param offset
     * @param per
     * @return
     */
    public Set<String> getFollowees(int userId, int entityType, int offset, int per){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zrevrange(followeeKey, offset, offset+per);
    }

    /**
     * 判断用户(userId)是否为具体实体(entityType+entityId)的粉丝
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean ifFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId))!=null;
    }

}
