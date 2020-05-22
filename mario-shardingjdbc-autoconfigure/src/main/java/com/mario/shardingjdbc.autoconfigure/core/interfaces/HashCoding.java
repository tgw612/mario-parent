package com.mario.shardingjdbc.autoconfigure.core.interfaces;

public interface HashCoding<T> {
  int hashFor(T var1);
}
