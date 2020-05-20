package com.mario.security.mgt;

import com.mario.common.util.StringUtil;
import com.mario.security.servlet.ProxiedFilterChain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;

public class SimpleNamedFilterList implements NamedFilterList {

  private String name;
  private List<Filter> backingList;

  public SimpleNamedFilterList(String name) {
    this(name, new ArrayList<Filter>());
  }

  public SimpleNamedFilterList(String name, List<Filter> backingList) {
    if (backingList == null) {
      throw new NullPointerException("backingList constructor argument cannot be null.");
    }
    this.backingList = backingList;
    setName(name);
  }

  protected void setName(String name) {
    if (!StringUtil.hasText(name)) {
      throw new IllegalArgumentException("Cannot specify a null or empty name.");
    }
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public FilterChain proxy(FilterChain orig) {
    return new ProxiedFilterChain(orig, this);
  }

  @Override
  public boolean add(Filter filter) {
    return this.backingList.add(filter);
  }

  @Override
  public void add(int index, Filter filter) {
    this.backingList.add(index, filter);
  }

  @Override
  public boolean addAll(Collection<? extends Filter> c) {
    return this.backingList.addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends Filter> c) {
    return this.backingList.addAll(index, c);
  }

  @Override
  public void clear() {
    this.backingList.clear();
  }

  @Override
  public boolean contains(Object o) {
    return this.backingList.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return this.backingList.containsAll(c);
  }

  @Override
  public Filter get(int index) {
    return this.backingList.get(index);
  }

  @Override
  public int indexOf(Object o) {
    return this.backingList.indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return this.backingList.isEmpty();
  }

  @Override
  public Iterator<Filter> iterator() {
    return this.backingList.iterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return this.backingList.lastIndexOf(o);
  }

  @Override
  public ListIterator<Filter> listIterator() {
    return this.backingList.listIterator();
  }

  @Override
  public ListIterator<Filter> listIterator(int index) {
    return this.backingList.listIterator(index);
  }

  @Override
  public Filter remove(int index) {
    return this.backingList.remove(index);
  }

  @Override
  public boolean remove(Object o) {
    return this.backingList.remove(o);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return this.backingList.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return this.backingList.retainAll(c);
  }

  @Override
  public Filter set(int index, Filter filter) {
    return this.backingList.set(index, filter);
  }

  @Override
  public int size() {
    return this.backingList.size();
  }

  @Override
  public List<Filter> subList(int fromIndex, int toIndex) {
    return this.backingList.subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray() {
    return this.backingList.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    //noinspection SuspiciousToArrayCall
    return this.backingList.toArray(a);
  }
}

