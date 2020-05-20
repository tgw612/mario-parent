package com.mario.common.id;

import com.mario.common.enums.AppName;
import com.mario.common.enums.AppNameBase;
import com.mario.common.util.ExceptionUtil;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ObjectId implements Comparable<ObjectId>, Serializable {

  private static final Logger log = LoggerFactory.getLogger(ObjectId.class);
  private static final long serialVersionUID = 3670079982654483072L;
  private static final int LOW_ORDER_THREE_BYTES = 16777215;
  private static final int MACHINE_IDENTIFIER;
  private static final short PROCESS_IDENTIFIER;
  private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(
      (new SecureRandom()).nextInt());
  private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8',
      '9', 'a', 'b', 'c', 'd', 'e', 'f'};
  private final int timestamp;
  private final int machineIdentifier;
  private final short processIdentifier;
  private final int counter;
  private final AppNameBase app;
  private static final int OBJECTID_BIT_LEN = 24;

  public static ObjectId get(AppNameBase app) {
    return new ObjectId(app);
  }

  public static boolean isValid(String hexString) {
    return getAndValid(hexString) != null;
  }

  private static AppName getAndValid(String hexString) {
    if (hexString == null) {
      throw new IllegalArgumentException();
    } else {
      int len = hexString.length();
      if (len <= 24) {
        return null;
      } else {
        int suLen = len - 24;

        AppName resolve;
        String substring;
        try {
          substring = hexString.substring(0, suLen);
          resolve = AppName.resolveCodeNumber(substring);
        } catch (Exception var7) {
          return null;
        }

        substring = hexString.substring(suLen);

        for (int i = 0; i < 24; ++i) {
          char c = substring.charAt(i);
          if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
            return null;
          }
        }

        return resolve;
      }
    }
  }

  public static int getGeneratedMachineIdentifier() {
    return MACHINE_IDENTIFIER;
  }

  public static int getGeneratedProcessIdentifier() {
    return PROCESS_IDENTIFIER;
  }

  public static int getCurrentCounter() {
    return NEXT_COUNTER.get();
  }

  public ObjectId(AppNameBase app) {
    this(app, System.currentTimeMillis());
  }

  public ObjectId(AppNameBase app, Date date) {
    this(app, dateToTimestampSeconds(date.getTime()), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER,
        NEXT_COUNTER.getAndIncrement(), false);
  }

  public ObjectId(AppNameBase app, long milliseconds) {
    this(app, dateToTimestampSeconds(milliseconds), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER,
        NEXT_COUNTER.getAndIncrement(), false);
  }

  public ObjectId(AppNameBase app, Date date, int counter) {
    this(app, date, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter);
  }

  public ObjectId(AppNameBase app, Date date, int machineIdentifier, short processIdentifier,
      int counter) {
    this(app, dateToTimestampSeconds(date.getTime()), machineIdentifier, processIdentifier,
        counter);
  }

  public ObjectId(AppNameBase app, int timestampSeconds, int machineIdentifier,
      short processIdentifier, int counter) {
    this(app, timestampSeconds, machineIdentifier, processIdentifier, counter, true);
  }

  private ObjectId(AppNameBase app, int timestampSeconds, int machineIdentifier,
      short processIdentifier, int counter, boolean checkCounter) {
    if ((machineIdentifier & -16777216) != 0) {
      throw new IllegalArgumentException(
          "The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
    } else if (checkCounter && (counter & -16777216) != 0) {
      throw new IllegalArgumentException(
          "The counter must be between 0 and 16777215 (it must fit in three bytes).");
    } else {
      this.timestamp = timestampSeconds;
      this.app = app;
      this.machineIdentifier = machineIdentifier;
      this.processIdentifier = processIdentifier;
      this.counter = counter & 16777215;
    }
  }

  public ObjectId(String hexString) {
    this(parseHexString(hexString));
  }

  public ObjectId(Object[] params) {
    this((AppName) params[0], ByteBuffer
        .wrap((byte[]) Validate.notNull((byte[]) ((byte[]) params[1]), "bytes", new Object[0])));
  }

  ObjectId(AppName app, int timestamp, int machineAndProcessIdentifier, int counter) {
    this(new Object[]{app, legacyToBytes(timestamp, machineAndProcessIdentifier, counter)});
  }

  public ObjectId(AppName app, ByteBuffer buffer) {
    Validate.notNull(buffer, "buffer", new Object[0]);
    Validate.isTrue(buffer.remaining() >= 12, "buffer.remaining() >=12", new Object[0]);
    this.timestamp = makeInt(buffer.get(), buffer.get(), buffer.get(), buffer.get());
    this.machineIdentifier = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
    this.processIdentifier = (short) makeInt((byte) 0, (byte) 0, buffer.get(), buffer.get());
    this.counter = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
    this.app = app;
  }

  private static byte[] legacyToBytes(int timestamp, int machineAndProcessIdentifier, int counter) {
    byte[] bytes = new byte[]{int3(timestamp), int2(timestamp), int1(timestamp), int0(timestamp),
        int3(machineAndProcessIdentifier), int2(machineAndProcessIdentifier),
        int1(machineAndProcessIdentifier), int0(machineAndProcessIdentifier), int3(counter),
        int2(counter), int1(counter), int0(counter)};
    return bytes;
  }

  public byte[] toByteArray() {
    ByteBuffer buffer = ByteBuffer.allocate(12);
    this.putToByteBuffer(buffer);
    return buffer.array();
  }

  public void putToByteBuffer(ByteBuffer buffer) {
    Validate.notNull(buffer, "buffer", new Object[0]);
    Validate.isTrue(buffer.remaining() >= 12, "buffer.remaining() >=12", new Object[0]);
    buffer.put(int3(this.timestamp));
    buffer.put(int2(this.timestamp));
    buffer.put(int1(this.timestamp));
    buffer.put(int0(this.timestamp));
    buffer.put(int2(this.machineIdentifier));
    buffer.put(int1(this.machineIdentifier));
    buffer.put(int0(this.machineIdentifier));
    buffer.put(short1(this.processIdentifier));
    buffer.put(short0(this.processIdentifier));
    buffer.put(int2(this.counter));
    buffer.put(int1(this.counter));
    buffer.put(int0(this.counter));
  }

  public int getTimestamp() {
    return this.timestamp;
  }

  public int getMachineIdentifier() {
    return this.machineIdentifier;
  }

  public short getProcessIdentifier() {
    return this.processIdentifier;
  }

  public int getCounter() {
    return this.counter;
  }

  public Date getDate() {
    return new Date((long) this.timestamp * 1000L);
  }

  public String toHexString() {
    char[] chars = new char[24];
    int i = 0;
    byte[] var3 = this.toByteArray();
    int var4 = var3.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      byte b = var3[var5];
      chars[i++] = HEX_CHARS[b >> 4 & 15];
      chars[i++] = HEX_CHARS[b & 15];
    }

    return this.app.getCodeNumber().concat(new String(chars));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      ObjectId objectId = (ObjectId) o;
      if (this.counter != objectId.counter) {
        return false;
      } else if (this.machineIdentifier != objectId.machineIdentifier) {
        return false;
      } else if (this.processIdentifier != objectId.processIdentifier) {
        return false;
      } else if (this.timestamp != objectId.timestamp) {
        return false;
      } else {
        return this.app == objectId.app;
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result = this.timestamp;
    result = 31 * result + this.machineIdentifier;
    result = 31 * result + this.processIdentifier;
    result = 31 * result + this.counter;
    result = 31 * result + ((String) this.app.getCode()).hashCode();
    return result;
  }

  @Override
  public int compareTo(ObjectId other) {
    if (other == null) {
      throw new NullPointerException();
    } else {
      byte[] byteArray = this.toByteArray();
      byte[] otherByteArray = other.toByteArray();

      for (int i = 0; i < 12; ++i) {
        if (byteArray[i] != otherByteArray[i]) {
          return (byteArray[i] & 255) < (otherByteArray[i] & 255) ? -1 : 1;
        }
      }

      return 0;
    }
  }

  public String toString() {
    return this.toHexString();
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
          ExceptionUtil
              .getAsString(var8));
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

  private static Object[] parseHexString(String s) {
    AppName appName = getAndValid(s);
    if (appName == null) {
      throw new IllegalArgumentException(
          "invalid hexadecimal representation of an ObjectId: [" + s + "]");
    } else {
      String substring = s.substring(s.length() - 24);
      byte[] b = new byte[12];

      for (int i = 0; i < b.length; ++i) {
        b[i] = (byte) Integer.parseInt(substring.substring(i * 2, i * 2 + 2), 16);
      }

      return new Object[]{appName, b};
    }
  }

  private static int dateToTimestampSeconds(long milliseconds) {
    return (int) (milliseconds / 1000L);
  }

  private static int makeInt(byte b3, byte b2, byte b1, byte b0) {
    return b3 << 24 | (b2 & 255) << 16 | (b1 & 255) << 8 | b0 & 255;
  }

  private static byte int3(int x) {
    return (byte) (x >> 24);
  }

  private static byte int2(int x) {
    return (byte) (x >> 16);
  }

  private static byte int1(int x) {
    return (byte) (x >> 8);
  }

  private static byte int0(int x) {
    return (byte) x;
  }

  private static byte short1(short x) {
    return (byte) (x >> 8);
  }

  private static byte short0(short x) {
    return (byte) x;
  }

  public static void main(String[] args) {
    ObjectId objectId = new ObjectId(AppName.DOUBO_ADMIN_WEB);
    System.out.println(objectId.toString());
    System.out.println(objectId.toString());
    System.out.println(objectId.getMachineIdentifier());
    System.out.println(objectId.getMachineIdentifier());
    System.out.println(objectId.getProcessIdentifier());
    System.out.println(objectId.getProcessIdentifier());
    objectId = new ObjectId("w582687c37056f01f5898dcea");
    System.out.println(objectId);
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