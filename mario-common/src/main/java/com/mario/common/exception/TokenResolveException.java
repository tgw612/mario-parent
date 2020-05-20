package com.mario.common.exception;

import com.mario.common.exception.code.TokenExceptionEnum;

public class TokenResolveException extends SystemException {

  public TokenResolveException() {
    super(TokenExceptionEnum.TokenResolveException.getId(),
        TokenExceptionEnum.TokenResolveException.getText());
  }

  public TokenResolveException(Throwable ex) {
    super(TokenExceptionEnum.TokenResolveException.getId(),
        TokenExceptionEnum.TokenResolveException.getText(), ex);

  }
}
