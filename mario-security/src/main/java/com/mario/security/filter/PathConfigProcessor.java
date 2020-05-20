package com.mario.security.filter;

import javax.servlet.Filter;

public interface PathConfigProcessor {

  Filter processPathConfig(String path, String config);
}
