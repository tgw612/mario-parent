package com.mario.common.id;

import com.mario.common.enums.AppName;
import java.util.UUID;

public class IdCreaterTest {

  public IdCreaterTest() {
  }

  public static void main(String[] args) {
    new Sequence();
    IdWorker idWorker = new IdWorker(14L);
    SeqWorker seqWorker = new SeqWorker(0);
    IdCodeGenerator idCodeGenerator = new IdCodeGenerator("9");
    System.out.println(idWorker.nextId());
    System.out.println(seqWorker.nextId());
    System.out.println(idCodeGenerator.nextId());
    int max = 10000;
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      idWorker.nextId();
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
      idCodeGenerator.nextId();
    }

    long end3 = System.currentTimeMillis();
    long startTime4 = System.currentTimeMillis();

    for (int i = 0; i < max; ++i) {
      seqWorker.nextId();
    }

    long end4 = System.currentTimeMillis();
    System.out.println("end=" + end + " startTime=" + startTime);
    System.out.println(end - startTime);
    System.out.println("end1=" + end1 + " startTime1=" + startTime1);
    System.out.println(end1 - startTime1);
    System.out.println("end2=" + end2 + " startTime2=" + startTime2);
    System.out.println(end2 - startTime2);
    System.out.println("end3=" + end3 + " startTime3=" + startTime3);
    System.out.println(end3 - startTime3);
    System.out.println("end4=" + end4 + " startTime4=" + startTime4);
    System.out.println(end4 - startTime4);
  }
}
