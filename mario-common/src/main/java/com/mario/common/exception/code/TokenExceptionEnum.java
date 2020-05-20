package com.mario.common.exception.code;

/**
 * @author 陈志杭
 * @contact 279397942@qq.com
 * @date 2017/2/6
 * @description
 */

public enum TokenExceptionEnum {

  /**
   * token 异常
   */
  TokenException(5001, "访问失败,请登录"),
  TokenExpire(5002, "访问权限已经过期,请重新登录"),
  TokenResolveException(5003, "访问失败,请登录"),
  TokenSignOnByAnotherException(5004, "已经在另一个地方登录"),
  ;

  int id;

  String text;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  private TokenExceptionEnum(int id, String text) {
    this.id = id;
    this.text = text;
  }
}
