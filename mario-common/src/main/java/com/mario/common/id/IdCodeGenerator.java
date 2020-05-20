package com.mario.common.id;

import com.mario.common.util.DateUtil;
import com.mario.common.util.LocalHostUtil;
import com.mario.common.util.StringUtil;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdCodeGenerator {

  private static final Logger log = LoggerFactory.getLogger(IdCodeGenerator.class);
  private static int ip = 0;

  static {
    String ipStr = LocalHostUtil
        .getIpCode()
        .substring(LocalHostUtil.getIpCode().length() - 3, LocalHostUtil.getIpCode().length());
    ip = Integer.parseInt(ipStr);
  }

  private long workerId;
  private String businessCode;
  private volatile long sequence = 0L;
  private long workerIdBits = 8L;
  private long maxWorkerId;
  private long sequenceBits;
  private long workerIdShift;
  private long sequenceMask;
  private volatile long lastTimestamp;

  public IdCodeGenerator(String businessCode) {
    this.maxWorkerId = ~(-1L << (int) this.workerIdBits);
    this.sequenceBits = 11L;
    this.workerIdShift = this.sequenceBits;
    this.sequenceMask = ~(-1L << (int) this.sequenceBits);
    this.lastTimestamp = this.timeGen();
    int workerId = ip;
    if ((long) workerId <= this.maxWorkerId && workerId >= 0) {
      if (StringUtil.isBlank(businessCode)) {
        throw new IllegalArgumentException("businessCode can't be empty");
      } else {
        this.workerId = (long) workerId;
        this.businessCode = businessCode;
        log.info(String.format(
            "IdCodeGenerator starting. timestamp is %s, businessCode is %s, workerId is %d, worker id bits %d, sequence bits %d",
            "yyMMddHHmmssSSS", businessCode, workerId, this.workerIdBits, this.sequenceBits));
      }
    } else {
      throw new IllegalArgumentException(
          String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
    }
  }

  public IdCodeGenerator(long workerId, String businessCode) {
    this.maxWorkerId = ~(-1L << (int) this.workerIdBits);
    this.sequenceBits = 11L;
    this.workerIdShift = this.sequenceBits;
    this.sequenceMask = ~(-1L << (int) this.sequenceBits);
    this.lastTimestamp = this.timeGen();
    if (workerId <= this.maxWorkerId && workerId >= 0L) {
      if (StringUtil.isBlank(businessCode)) {
        throw new IllegalArgumentException("businessCode can't be empty");
      } else {
        this.workerId = workerId;
        this.businessCode = businessCode;
        log.info(String.format(
            "IdCodeGenerator starting. timestamp is %s, businessCode is %s, workerId is %d, worker id bits %d, sequence bits %d",
            "yyMMddHHmmssSSS", businessCode, workerId, this.workerIdBits, this.sequenceBits));
      }
    } else {
      throw new IllegalArgumentException(
          String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
    }
  }

  public static void main(String[] args) {
    IdCodeGenerator idCodeGenerator = new IdCodeGenerator(255L, "1");
    System.out.println(idCodeGenerator.nextId());
    System.out.println(idCodeGenerator.nextId());
    System.out.println(idCodeGenerator.nextId());
    System.out.println(idCodeGenerator.nextId());
    System.out.println(idCodeGenerator.nextId());
  }

  public String nextId() {
    long seqNum = 0L;
    long timestamp;
    synchronized (this) {
      timestamp = this.timeGen();
      if (timestamp < this.lastTimestamp) {
        log.error(String.format("clock is moving backwards.  Rejecting requests until %d.",
            this.lastTimestamp));
        throw new RuntimeException(String
            .format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                this.lastTimestamp - timestamp));
      }

      if (this.lastTimestamp == timestamp) {
        this.sequence = this.sequence + 1L & this.sequenceMask;
        if (this.sequence == 0L) {
          timestamp = this.tilNextMillis(this.lastTimestamp);
        }
      } else {
        this.sequence = 0L;
      }

      seqNum = this.sequence;
      this.lastTimestamp = timestamp;
    }

    long suffix = this.workerId << (int) this.workerIdShift | seqNum;
    String datePrefix = DateUtil.formatDate(new Date(timestamp), "yyMMddHHmmssSSS");
    return (new StringBuilder(25)).append(datePrefix.substring(0, "yyMMddHHmmss".length()))
        .append(this.businessCode).append(suffix)
        .append(datePrefix.substring("yyMMddHHmmss".length())).toString();
  }

  protected long tilNextMillis(long lastTimestamp) {
    long timestamp;
    for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
    }

    return timestamp;
  }

  protected long timeGen() {
    return System.currentTimeMillis();
  }
}
