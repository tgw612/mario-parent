package com.mario.relation;

import java.util.Collections;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RelationRedisHash {

    static String FOLLOW_PREFIX = "follow_";
    static String FANS_PREFIX = "fans_";

//    static Jedis jedis = new Jedis();

    public Set<String> getFollows(String id) {
//    return jedis.hgetAll(FOLLOW_PREFIX + id).keySet();
        return Collections.emptySet();
    }

    public Set<String> getfans(String id) {
//    return jedis.hgetAll(FANS_PREFIX + id).keySet();
        return Collections.emptySet();
    }

    // 获取互相关注的列表
    public Set<String> getTwoFollow(String id) {
        Set<String> follows = getFollows(id);
        Set<String> fans = getfans(id);
        follows.retainAll(fans);
        return follows;
    }

    // 判断from - > to 的关系
    public RelationShip getRelationShip(String from, String to) {
        boolean isFollow = getFollows(from).contains(to);
        boolean isFans = getfans(from).contains(to);

        if (isFollow) {
            if (isFans) {
                return RelationShip.TWOFOLLOW;
            }
            return RelationShip.FOLLOW;
        }
        return isFans ? RelationShip.FANS : RelationShip.NOONE;
    }

    // 获取共同关注列表
    public Set<String> publicFollow(String id1, String id2) {
        Set<String> id1Follow = getFollows(id1);
        Set<String> id2Follow = getfans(id2);

        id1Follow.retainAll(id2Follow);

        return id1Follow;
    }
}
