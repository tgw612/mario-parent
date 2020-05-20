package com.mario.common.id;

import com.mario.common.util.StringUtil;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class Sequence {

  private final long startTime = 1519740777809L;
  private final long workerIdBits = 5L;
  private final long dataCenterIdBits = 5L;
  private final long sequenceBits = 12L;
  private final long maxWorkerId = 31L;
  private final long maxDataCenterId = 31L;
  private final long workerIdShift = 12L;
  private final long dataCenterIdShift = 17L;
  private final long timestampLeftShift = 22L;
  private final long sequenceMask = 4095L;
  private long workerId;
  private long dataCenterId;
  private long sequence = 0L;
  private long lastTimestamp = -1L;
  private boolean isClock = true;

  public Sequence() {
    this.dataCenterId = getDataCenterId(31L);
    this.workerId = getMaxWorkerId(this.dataCenterId, 31L);
  }

  public Sequence(long workerId, long dataCenterId) {
    if (workerId <= 31L && workerId >= 0L) {
      if (dataCenterId <= 31L && dataCenterId >= 0L) {
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
      } else {
        throw new IllegalArgumentException(
            String.format("dataCenter Id can't be greater than %d or less than 0", 31L));
      }
    } else {
      throw new IllegalArgumentException(
          String.format("worker Id can't be greater than %d or less than 0", 31L));
    }
  }

  public void setClock(boolean clock) {
    this.isClock = clock;
  }

  public synchronized Long nextId() {
    long timestamp = this.timeGen();
    if (timestamp < this.lastTimestamp) {
      long offset = this.lastTimestamp - timestamp;
      if (offset > 5L) {
        throw new RuntimeException(String
            .format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
      }

      try {
        this.wait(offset << 1);
        timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
          throw new RuntimeException(String
              .format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                  offset));
        }
      } catch (Exception var6) {
        throw new RuntimeException(var6);
      }
    }

    if (this.lastTimestamp == timestamp) {
      this.sequence = this.sequence + 1L & 4095L;
      if (this.sequence == 0L) {
        timestamp = this.tilNextMillis(this.lastTimestamp);
      }
    } else {
      this.sequence = 0L;
    }

    this.lastTimestamp = timestamp;
    return timestamp - 1519740777809L << 22 | this.dataCenterId << 17 | this.workerId << 12
        | this.sequence;
  }

  private long tilNextMillis(long lastTimestamp) {
    long timestamp;
    for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
    }

    return timestamp;
  }

  private long timeGen() {
    return this.isClock ? SystemClock.now() : System.currentTimeMillis();
  }

  protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
    StringBuilder mpid = new StringBuilder();
    mpid.append(datacenterId);
    String name = ManagementFactory.getRuntimeMXBean().getName();
    if (StringUtil.isNotEmpty(name)) {
      mpid.append(name.split("@")[0]);
    }

    return (long) (mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
  }

  protected static long getDataCenterId(long maxDatacenterId) {
    long id = 0L;

    try {
      InetAddress ip = InetAddress.getLocalHost();
      NetworkInterface network = NetworkInterface.getByInetAddress(ip);
      if (network == null) {
        id = 1L;
      } else {
        byte[] mac = network.getHardwareAddress();
        if (null != mac) {
          id = (255L & (long) mac[mac.length - 1] | 65280L & (long) mac[mac.length - 2] << 8) >> 6;
          id %= maxDatacenterId + 1L;
        }
      }

      return id;
    } catch (Exception var7) {
      throw new RuntimeException("getDataCenterId: " + var7.getMessage());
    }
  }
}
