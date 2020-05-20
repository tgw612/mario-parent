package com.mario.common.id;

import com.mario.common.util.DateUtil;
import com.mario.common.util.StringUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeqGenUtil {

  private static final Logger log = LoggerFactory.getLogger(SeqGenUtil.class);
  private static final IdCodeGenerator LOG_ID_GENERATOR;
  private static final IdCodeGenerator ACTIVITY_CODE_GENERATOR;
  private static final IdCodeGenerator ORDER_CODE_GENERATOR;
  private static final IdCodeGenerator TRANSFER_ACCOUNT_CODE_GENERATOR;
  private static final IdCodeGenerator WITHDRAW_CODE_GENERATOR;
  private static final IdCodeGenerator PROMOTION_ACTIVITY_CODE_GENERATOR;
  private static final IdCodeGenerator PROMOTION_PRODUCT_CODE_GENERATOR;
  private static final IdCodeGenerator FINANCE_MARKET_CODE_GENERATOR;
  private static final IdCodeGenerator FINANCE_COMMISSON_CODE_GENERATOR;
  private static final IdCodeGenerator SERIAL_CODE_GENERATOR;

  public SeqGenUtil() {
  }

  public static String getPromotionActivityCode() {
    return PROMOTION_ACTIVITY_CODE_GENERATOR.nextId();
  }

  public static String getPromotionProductCode() {
    return PROMOTION_PRODUCT_CODE_GENERATOR.nextId();
  }

  public static String getActivityCode() {
    return ACTIVITY_CODE_GENERATOR.nextId();
  }

  public static String getFinanceMarketCodeCode() {
    return FINANCE_MARKET_CODE_GENERATOR.nextId();
  }

  public static String getFinanceCommissonCode() {
    return FINANCE_COMMISSON_CODE_GENERATOR.nextId();
  }

  public static String getOrderCode() {
    return ORDER_CODE_GENERATOR.nextId();
  }

  public static String getTransferAccountCode() {
    return TRANSFER_ACCOUNT_CODE_GENERATOR.nextId();
  }

  public static String getWithdrawCode() {
    return WITHDRAW_CODE_GENERATOR.nextId();
  }

  public static String getSerialCode() {
    return SERIAL_CODE_GENERATOR.nextId();
  }

  public static String getLogId() {
    return LOG_ID_GENERATOR.nextId();
  }

//  public static String getId() {
//    return ObjectId.get(AppName.SIBU_MALL_ORDER).toHexString();
//  }
//
//  public static String getCommissionId() {
//    return ObjectId.get(AppName.SIBU_MALL_COMMISSION).toHexString();
//  }
//
//  public static String getMarketingId() {
//    return ObjectId.get(AppName.SIBU_MALL_MARKETING).toHexString();
//  }
//
//  public static String getJutebagId() {
//    return ObjectId.get(AppName.SIBU_MALL_JUTEBAG).toHexString();
//  }
//
//  public static String getAccountantId() {
//    return ObjectId.get(AppName.SIBU_MALL_ACCOUNTANT).toHexString();
//  }

  public static boolean isUUID(String uuid) {
    return StringUtil.isUUID(uuid);
  }

  public static boolean isNotUUID(String uuid) {
    return StringUtil.isNotUUID(uuid);
  }

//  public static String genReqSeq() {
//    return DateUtil.getNowTimeYYYYMMddHHMMSS() + DateUtil.getNowTimeYYYYMMddHHMMSS();
//  }

  public static void main(String[] args) {
    System.out.println(getOrderCode());
    String dateSuffix = DateUtil
        .formatDate(new Date(System.currentTimeMillis()), "yyMMddHHmmssSSS");
    System.out.println(dateSuffix);
    System.out.println(dateSuffix.substring("yyMMddHHmmss".length()));
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
    System.out.println(sdf.format(new Date()));
  }

  static {
    LOG_ID_GENERATOR = new IdCodeGenerator(IdCodeTypeEnum.LOG_ID.getCode());
    ACTIVITY_CODE_GENERATOR = new IdCodeGenerator(IdCodeTypeEnum.ACTIVITY_CODE.getCode());
    ORDER_CODE_GENERATOR = new IdCodeGenerator(IdCodeTypeEnum.ORDER_CODE.getCode());
    TRANSFER_ACCOUNT_CODE_GENERATOR = new IdCodeGenerator(
        IdCodeTypeEnum.TRANSFER_ACCOUNT_CODE.getCode());
    WITHDRAW_CODE_GENERATOR = new IdCodeGenerator(IdCodeTypeEnum.WITHDRAW_CODE.getCode());
    PROMOTION_ACTIVITY_CODE_GENERATOR = new IdCodeGenerator(
        IdCodeTypeEnum.PROMOTION_ACTIVITY_CODE.getCode());
    PROMOTION_PRODUCT_CODE_GENERATOR = new IdCodeGenerator(
        IdCodeTypeEnum.PROMOTION_PRODUCT_CODE.getCode());
    FINANCE_MARKET_CODE_GENERATOR = new IdCodeGenerator(
        IdCodeTypeEnum.FINANCE_MARKET_CODE.getCode());
    FINANCE_COMMISSON_CODE_GENERATOR = new IdCodeGenerator(
        IdCodeTypeEnum.FINANCE_COMMISSON_CODE.getCode());
    SERIAL_CODE_GENERATOR = new IdCodeGenerator(IdCodeTypeEnum.SERIAL_CODE.getCode());
  }
}
