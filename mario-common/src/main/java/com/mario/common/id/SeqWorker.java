package com.mario.common.id;

import com.mario.common.util.ExceptionUtil;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeqWorker {

  private static final Logger log = LoggerFactory.getLogger(SeqWorker.class);
  private static final long twepoch = 1288834974657L;
  private long sequence = 0L;
  private static final long sequenceBits = 10L;
  private static final long timestampLeftShift = 10L;
  public static final long sequenceMask = 1023L;
  private long lastTimestamp = -1L;
  private static final int LOW_ORDER_THREE_BYTES = 16777215;
  private static final int MACHINE_IDENTIFIER;
  private static final short PROCESS_IDENTIFIER;
  private static final long processBits = 16L;
  private final String workerId;

  public SeqWorker(int businessCode) {
    long counter = (long) MACHINE_IDENTIFIER;
    if (businessCode < 0) {
      throw new IllegalArgumentException("businessCode can't be less than 0");
    } else {
      this.workerId = (counter << 16 | (long) PROCESS_IDENTIFIER) + (long) businessCode + "";
    }
  }

  public String nextId() {
    long seqNum;
    long timestamp;
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

    long nextId = timestamp - 1288834974657L << 10 | seqNum;
    return (new StringBuilder(32)).append(this.workerId).append(nextId).toString();
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

  private static int createMachineIdentifier() {
    int machinePiece;
    try {
      StringBuilder sb = new StringBuilder();
      Enumeration e = NetworkInterface.getNetworkInterfaces();

      while (e.hasMoreElements()) {
        NetworkInterface ni = (NetworkInterface) e.nextElement();
        sb.append(ni.toString());
        byte[] mac = ni.getHardwareAddress();
        if (mac != null) {
          ByteBuffer bb = ByteBuffer.wrap(mac);

          try {
            sb.append(bb.getChar());
            sb.append(bb.getChar());
            sb.append(bb.getChar());
          } catch (BufferUnderflowException var7) {
          }
        }
      }

      machinePiece = sb.toString().hashCode();
    } catch (Throwable var8) {
      machinePiece = (new SecureRandom()).nextInt();
      log.error(
          "Failed to get machine identifier from network interface, using random number instead. Exception:{}",
          ExceptionUtil.getAsString(var8));
    }

    machinePiece &= 16777215;
    return machinePiece;
  }

  private static short createProcessIdentifier() {
    short processId;
    try {
      String processName = ManagementFactory.getRuntimeMXBean().getName();
      if (processName.contains("@")) {
        processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf(64)));
      } else {
        processId = (short) ManagementFactory.getRuntimeMXBean().getName().hashCode();
      }
    } catch (Throwable var2) {
      processId = (short) (new SecureRandom()).nextInt();
      log.error(
          "Failed to get process identifier from JMX, using random number instead Exception:{}",
          ExceptionUtil.getAsString(var2));
    }

    return processId;
  }

  public static void main(String[] args) {
    SeqWorker worker2 = new SeqWorker(0);
    System.out.println(worker2.nextId());
    System.out.println(worker2.nextId());
    System.out.println(worker2.nextId());
    System.out.println(worker2.nextId());
  }

  static {
    try {
      MACHINE_IDENTIFIER = createMachineIdentifier();
      PROCESS_IDENTIFIER = createProcessIdentifier();
    } catch (Exception var1) {
      throw new RuntimeException(var1);
    }
  }
}