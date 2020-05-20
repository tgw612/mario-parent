package com.mario;

import com.mario.common.util.StringUtil;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class ClassPathConsumerScanner extends ClassPathBeanDefinitionScanner {

  private Set<GroupBeanName> existBeanNames = new HashSet<>();

  private Class<? extends Annotation> annotationClass;

  public ClassPathConsumerScanner(BeanDefinitionRegistry registry) {
    super(registry, false);
  }

  public void registerFilters() {
    // if specified, use the given annotation and / or marker interface
    if (this.annotationClass != null) {
      addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
    }
  }

  public Map<String, List<RuntimeBeanReference>> doScanAndGet(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
    Map<String, List<RuntimeBeanReference>> returnGroupMap = new HashMap<>();
    if (beanDefinitions.isEmpty() && existBeanNames.isEmpty()) {
      logger.warn("No " + this.annotationClass.getSimpleName() + " was found in '" + Arrays
          .toString(basePackages) + "' package. Please check your configuration.");
    } else {
      Map<String, Set<RuntimeBeanReference>> groupBeanMap = new HashMap<>();
      processBeanDefinitions(groupBeanMap, beanDefinitions);
      processBeanDefinitionsWithExistBeanNames(groupBeanMap, existBeanNames);

      for (Map.Entry<String, Set<RuntimeBeanReference>> entry : groupBeanMap.entrySet()) {
        String group = entry.getKey();
        List<RuntimeBeanReference> referenceList = returnGroupMap.get(group);
        if (referenceList == null) {
          referenceList = new ManagedList<>();
          returnGroupMap.put(group, referenceList);
        }
        referenceList.addAll(entry.getValue());
      }
    }

    return returnGroupMap;
  }

  private void processBeanDefinitions(Map<String, Set<RuntimeBeanReference>> groupBeanMap,
      Set<BeanDefinitionHolder> beanDefinitions) {
    ScannedGenericBeanDefinition definition;
    for (BeanDefinitionHolder holder : beanDefinitions) {
      definition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();
      AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
          definition.getMetadata().getAnnotationAttributes(this.annotationClass.getName()));
      String group = annoAttrs.getString("group");
      if (StringUtil.isBlank(group)) {
        throw new IllegalArgumentException(
            "BeanName[" + holder.getBeanName() + "] " + this.annotationClass.getSimpleName()
                + " group must not empty!");
      }
      Set<RuntimeBeanReference> listenerSet = groupBeanMap.get(group);

      if (listenerSet == null) {
        listenerSet = new HashSet<>();
        groupBeanMap.put(group, listenerSet);
      }
      listenerSet.add(new RuntimeBeanReference(holder.getBeanName()));

    }
  }

  private void processBeanDefinitionsWithExistBeanNames(
      Map<String, Set<RuntimeBeanReference>> groupBeanMap, Set<GroupBeanName> existBeanNames) {
    for (GroupBeanName groupBeanName : existBeanNames) {
      String group = groupBeanName.getGroupName();
      if (StringUtil.isBlank(group)) {
        throw new IllegalArgumentException(
            "BeanName[" + groupBeanName.getBeanName() + "] " + this.annotationClass.getSimpleName()
                + " group must not empty!");
      }
      Set<RuntimeBeanReference> listenerSet = groupBeanMap.get(group);

      if (listenerSet == null) {
        listenerSet = new HashSet<>();
        groupBeanMap.put(group, listenerSet);
      }
      listenerSet.add(new RuntimeBeanReference(groupBeanName.getBeanName()));

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return super.isCandidateComponent(beanDefinition);
//        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
    if (super.checkCandidate(beanName, beanDefinition)) {
      return true;
    } else {
      ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition) beanDefinition;
      AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
          definition.getMetadata().getAnnotationAttributes(this.annotationClass.getName()));
      String group = annoAttrs.getString("group");
      existBeanNames.add(new GroupBeanName(beanName, group));

      logger.warn("Found " + this.annotationClass.getSimpleName() + " with name '" + beanName
          + "' and '" + beanDefinition.getBeanClassName() + "' " + this.annotationClass
          .getSimpleName()
          + ". Bean already defined with the same name!");
      return false;
    }
  }

  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }

  @Data
  @ToString
  @EqualsAndHashCode
  static class GroupBeanName {

    private String beanName;
    private String groupName;

    public GroupBeanName(String beanName, String groupName) {
      this.beanName = beanName;
      this.groupName = groupName;
    }
  }
}
