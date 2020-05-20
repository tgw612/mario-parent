package com.mario.common.id;

import com.mario.common.enums.AppName;
import java.util.UUID;

public class IdWorker {

  private final long workerId;
  private static final long twepoch = 1288834974657L;
  private long sequence = 0L;
  private static final long workerIdBits = 4L;
  public static final long maxWorkerId = 15L;
  private static final long sequenceBits = 10L;
  private static final long workerIdShift = 10L;
  private static final long timestampLeftShift = 14L;
  public static final long sequenceMask = 1023L;
  private long lastTimestamp = -1L;

  public IdWorker(long workerId) {
    if (workerId <= 15L && workerId >= 0L) {
      this.workerId = workerId;
    } else {
      throw new IllegalArgumentException(
          String.format("worker Id can't be greater than %d or less than 0", 15L));
    }
  }

  public long nextId() {
    long timestamp;
    long seqNum;
    synchronized (this) {
      timestamp = this.timeGen();
      if (this.lastTimestamp == timestamp) {
        this.sequence = this.sequence + 1L & 1023L;
        if (this.sequence == 0L) {
          timestamp = this.tilNextMillis(this.lastTimestamp);
        }
      } else {
        this.sequence = 0L;
      }

      if (timestamp < this.lastTimestamp) {
        throw new RuntimeException(String
            .format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                this.lastTimestamp - timestamp));
      }

      this.lastTimestamp = timestamp;
      seqNum = this.sequence;
    }

    long nextId = timestamp - 1288834974657L << 14 | this.workerId << 10 | seqNum;
    return nextId;
  }

  private long tilNextMillis(long lastTimestamp) {
    long timestamp;
    for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
    }

    return timestamp;
  }

  private long timeGen() {
    return System.currentTimeMillis();
  }

  public static void main(String[] args) {
    IdWorker worker2 = new IdWorker(14L);
    System.out.println(worker2.nextId());
    int max = 10000;
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      worker2.nextId();
    }

    long end = System.currentTimeMillis();
    long startTime1 = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      UUID.randomUUID().toString();
    }

    long end1 = System.currentTimeMillis();
    long startTime2 = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      (new ObjectId(AppName.DOUBO_ADMIN_WEB)).toString();
    }

    long end2 = System.currentTimeMillis();
    long startTime3 = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      (new IdCodeGenerator(AppName.DOUBO_ADMIN_WEB.getCode())).nextId();
    }

    long end3 = System.currentTimeMillis();
    System.out.println("end=" + end + " startTime=" + startTime);
    System.out.println(end - startTime);
    System.out.println("end1=" + end1 + " startTime1=" + startTime1);
    System.out.println(end1 - startTime1);
    System.out.println("end2=" + end2 + " startTime2=" + startTime2);
    System.out.println(end2 - startTime2);
    System.out.println("end3=" + end3 + " startTime3=" + startTime3);
    System.out.println(end3 - startTime3);
  }
}
