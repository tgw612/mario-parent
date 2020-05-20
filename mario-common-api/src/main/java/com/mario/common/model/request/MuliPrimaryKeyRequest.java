package com.mario.common.model.request;

import java.util.List;

public interface MuliPrimaryKeyRequest<T> {

  List<T> getIds();
}