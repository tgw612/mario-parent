那么针对这些关系有常见的以下几个需求:
查看某个用户的关注列表.
查看某个用户的粉丝列表.
查看某个人的互相关注列表,(好友圈的定义就是和你互相关注的人的微博会在这里出现.
判断两个用户之间的关系.(在微博中,你查看别人主页时左下角的集中状态).
获取两个人的共同关注.(微博中查看别人的关注列表时会有这个栏目,展示你和他共同关注的一些人).

CREATE TABLE `member_third_party_relationship` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `from_id` bigint(20) DEFAULT NULL COMMENT '关联者id',
  `to_id` bigint(20) DEFAULT NULL COMMENT '被关联者id',
  `associations_type` int(10) DEFAULT NULL COMMENT '业务类型',
  `is_having_associations` smallint(2) DEFAULT NULL COMMENT '是否存在关联关系  1：有   0：无',
  `extvalue` varchar(200) DEFAULT NULL COMMENT '额外属性',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `is_delete` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否删除    1：是   0：否',
  PRIMARY KEY (`id`),
  KEY `idx_from_id` (`from_id`),
  KEY `idx_to_id` (`to_id`),
  KEY `idx_is_having_assciations` (`is_having_associations`)
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8mb4 COMMENT='会员跟第三方关联关系表';

Redis的hash来实现
在该文中说,使用了hash数据结构,每个用户对应两个hash表,一个存储关注,一个存储粉丝.
follow_A:
	B:ds1
	C:ds2
fan_A:
	null;
follow_B:
	C:ds3
fan_B:
	A:ds1
这样在在获取列表的时候,可以直接使用hgetall来获取,十分简单.但是仍要注意一下问题,hgetall是一个时间复杂度为O(n)的命令,当用户的关注列表或者粉丝列表太大的时候,仍然有超时的可能.

使用Redis的sorted set 实现
此外,还可以使用sorted set 来实现,将关注或者粉丝的id放在set中,将其关注时间作为分值,这样也可以获取到一个有序的关注列表.
上面几条数据的存储格式为:


如果日活5亿  关联关系记100亿  单redis2.5亿 40台机器