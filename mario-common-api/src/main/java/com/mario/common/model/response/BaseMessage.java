package com.mario.common.model.response;

import java.io.Serializable;

public interface BaseMessage<T> extends Serializable {

  T getContent();
}

