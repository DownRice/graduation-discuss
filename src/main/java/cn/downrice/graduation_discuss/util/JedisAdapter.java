package cn.downrice.graduation_discuss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool;

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://localhost:6379/1");
    }

    public Jedis getJedis(){
        return jedisPool.getResource();
    }

    /**
     * 将一个或多个成员元素加入到集合中
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("jedis发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 移除集合中的一个或多个成员元素,不存在的成员元素会被忽略
     * @param key
     * @param value
     * @return
     */
    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("jedis发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 获取存储在集合中的元素的数量
     * @param key
     * @return
     */
    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("jedis发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return -1;
    }

    /**
     * 判断成员元素是否是集合的成员
     * @param key
     * @param value
     * @return
     */
    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = getJedis();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("jedis发生异常"+e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    /**
     *移出并获取列表的最后一个元素, 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * @param timeout
     * @param key
     * @return
     */
    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 将一个或多个值插入到列表头部
     * @param key
     * @param value
     * @return
     */
    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 一个事务块的开始
     * @param jedis
     * @return
     */
    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();
        }catch (Exception e){
            logger.error("jedis发生异常" + e.getMessage());
        }
        return null;
    }

    /**
     * 执行所有事务块内的命令
     * @param tx
     * @param jedis
     * @return
     */
    public List<Object> exec(Transaction tx, Jedis jedis){
        try{
            return tx.exec();
        }catch (Exception e){
            logger.error("jedis发生异常" + e.getMessage());
        }finally {
            //关闭事务
            if(tx != null){
                try{
                    tx.close();
                }catch (Exception e){
                    logger.error("jedis发生异常" + e.getMessage());
                }
            }
            //关闭jedis
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }


    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中
     * @param key
     * @param score
     * @param member
     * @return
     */
    public long zadd(String key, double score, String member){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zadd(key, score,  member);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    /**
     * 返回在指定的键存储在集合中的元素的数量
     * @param key
     * @return
     */
    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }

    /**
     * 返回有序集中指定区间内的成员，通过索引，分数从高到底
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long expire(String key, int seconds){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            logger.error("jedis发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return -1;
    }
}
