//package com.mario.common.util;
//
//import com.alicp.jetcache.redis.lettuce.JetCacheCodec;
//import com.mall.common.Empty;
//import com.mall.common.exception.BusinessException;
//import com.mario.common.constants.RedisConstant;
//import com.mario.redis.config.ScanArgsNode;
//import io.lettuce.core.*;
//import io.lettuce.core.api.StatefulRedisConnection;
//import io.lettuce.core.api.sync.RedisCommands;
//import io.lettuce.core.codec.RedisCodec;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.CollectionUtils;
//
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//
//@Slf4j
//public abstract class LettuceUtils {
//
//    private static final RedisCodec  jetCacheCodec = new JetCacheCodec();
//
//    /**
//     * 加锁执行 单实例
//     * @param redisClient
//     * @param lockKeyPre
//     * @param second
//     * @param load
//     * @param get
//     * @param <T>
//     * @return
//     */
//    public static  <T> T lockSyncLoad(RedisClient redisClient,
//                                      String lockKeyPre,
//                                      Integer second,
//                                      Function<RedisCommands<Object,Object>,T> load,
//                                      Function<RedisCommands<Object,Object>,T> get){
//        int i = 100;
//        //连接
//        StatefulRedisConnection<Object, Object> connect = null;
//        //同步执行命令
//        RedisCommands<Object, Object> sync = null;
//        //锁 key
//        byte[] lockKey = null;
//        try {
//            lockKey = (lockKeyPre + RedisConstant.Key.LOCK_SUF).getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            throw new BusinessException(e.getMessage());
//        }
//        //锁结果
//        String lockResult = null;
//        //结果
//        T result = null;
//        try{
//            connect = redisClient.connect(jetCacheCodec);
//            sync = connect.sync();
//            lockResult = sync.set(lockKey, "lock".getBytes("utf-8"), SetArgs.Builder.ex(second).nx());
//            if("OK".equals(lockResult)){
//                result = load.apply(sync);
//            }else{
//                while (result == null){
//                    try {
//                        //睡 50毫秒等待
//                        TimeUnit.SECONDS.sleep(50l);
//                    } catch (InterruptedException e) {
//                        log.error("错误",e);
//                    }
//                    result = get.apply(sync);
//
//                    if(--i == 0 && result == null){
//                        result = (T)Empty.empty;
//                    }
//                }
//            }
//            return result;
//        } catch (UnsupportedEncodingException e) {
//            throw new BusinessException(e.getMessage());
//        } finally {
//            if(lockResult != null){//移除锁
//                sync.del(lockKey);
//            }
//            if(connect != null){//关闭连接
//                connect.close();
//            }
//        }
//    }
//
//    /**
//     * scan
//     * @param redisClient
//     * @param match
//     * @param limit
//     * @param consumer
//     */
//    public static void scan(RedisClient redisClient,
//                     String  match,
//                     Integer limit,
//                     Consumer<List<KeyValue<String, String>>> consumer){
//        try(StatefulRedisConnection<String, String> connect = redisClient.connect();){
//            RedisCommands<String, String> sync = connect.sync();
//            List<Object> slots = sync.clusterSlots();
//            //主节点
//            List<String> masters = new ArrayList<>();
//            //从节点
//            List<String> slaves = new ArrayList<>();
//            slots.stream().forEach((s)->{
//                List s1 = (List) s;
//                List s2 = (ArrayList)s1.get(2);
//                masters.add((String) s2.get(2));
//                if(s1.size() >=4){
//                    List s3 = (ArrayList)s1.get(3);
//                    slaves.add((String) s3.get(2));
//                }
//            });
//            String nodeName  = null;
//            List<String> nodes = null;
//            if(masters.size() <= slaves.size()){
//                nodeName = "从节点";
//                nodes = slaves;
//            }else{
//                nodeName = "主节点";
//                nodes = masters;
//            }
//            for(String node : nodes){
//                log.info("使用{} node:{} 执行scan",nodeName,node);
//                ScanArgs scanArgs = ScanArgsNode.Builder.limit(limit).match(match).node(node);
//                ScanCursor cursor = ScanCursor.INITIAL;
//                while (!cursor.isFinished()){
//                    cursor = sync.scan(cursor, scanArgs);
//                    List<String> keys = ((KeyScanCursor)cursor).getKeys();
//                    if(!CollectionUtils.isEmpty(keys)){
//                        List<KeyValue<String, String>> mget = sync.mget(keys.toArray(new String[]{}));
//                        consumer.accept(mget);
//                    }
//                }
//            }
//        }
//    }
//}
