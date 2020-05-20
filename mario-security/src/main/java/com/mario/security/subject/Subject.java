package com.mario.security.subject;

public interface Subject {

//    String getLoginAccount();

  boolean isAuthenticated();

  Object getPrincipal();
}
