package com.mario.autoimport;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class CustomAutoConfigurationImportSelector extends AutoConfigurationImportSelector {

  public CustomAutoConfigurationImportSelector() {
  }

  @Override
  protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
      AnnotationAttributes attributes) {
    return this.asList(attributes, "value");
  }

  @Override
  protected Class<?> getAnnotationClass() {
    return CustomImportAutoConfiguration.class;
  }

  @Override
  protected Set<String> getExclusions(AnnotationMetadata metadata,
      AnnotationAttributes attributes) {
    return Collections.EMPTY_SET;
  }
}
