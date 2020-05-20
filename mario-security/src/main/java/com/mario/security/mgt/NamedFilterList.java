package com.mario.security.mgt;

import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;

public interface NamedFilterList extends List<Filter> {

  String getName();

  FilterChain proxy(FilterChain filterChain);
}
