package com.mario.redis.config;

import io.lettuce.core.ScanArgs;
import io.lettuce.core.internal.LettuceAssert;
import io.lettuce.core.protocol.CommandArgs;
import java.nio.charset.StandardCharsets;

public class ScanArgsNode extends ScanArgs {

  /**
   * 腾讯云 集群scan 需要加节点
   */
  private String node;

  public static class Builder {


    private Builder() {
    }


    public static ScanArgsNode limit(long count) {
      return new ScanArgsNode().limit(count);
    }


    public static ScanArgsNode matches(String matches) {
      return new ScanArgsNode().match(matches);
    }

    public static ScanArgsNode node(String node) {
      return new ScanArgsNode().match(node);
    }
  }


  public ScanArgsNode match(String match) {
    super.match(match);
    return this;
  }


  public ScanArgsNode limit(long count) {
    super.limit(count);
    return this;
  }

  public ScanArgsNode node(String node) {

    LettuceAssert.notNull(node, "Match must not be null");
    this.node = node;
    return this;
  }

  public <K, V> void build(CommandArgs<K, V> args) {
    super.build(args);
    if (node != null) {
      args.add(node.getBytes(StandardCharsets.UTF_8));
    }
  }

}
