package com.mario.shardingjdbc.autoconfigure.core.properties;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.shardingsphere.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public final class CustomShardingProperties {

  private final Properties props;

  public CustomShardingProperties(Properties props) {
    this.props = props;
    this.validate();
  }

  private void validate() {
    Set<String> propertyNames = this.props.stringPropertyNames();
    Collection<String> errorMessages = new ArrayList(propertyNames.size());
    Iterator var3 = propertyNames.iterator();

    while (true) {
      while (true) {
        String each;
        CustomShardingPropertiesConstant shardingPropertiesConstant;
        do {
          if (!var3.hasNext()) {
            if (!errorMessages.isEmpty()) {
              throw new IllegalArgumentException(Joiner.on(" ").join(errorMessages));
            }

            return;
          }

          each = (String) var3.next();
          shardingPropertiesConstant = CustomShardingPropertiesConstant.findByKey(each);
        } while (null == shardingPropertiesConstant);

        Class<?> type = shardingPropertiesConstant.getType();
        String value = this.props.getProperty(each);
        if (type == Boolean.TYPE && !StringUtil.isBooleanValue(value)) {
          errorMessages.add(this.getErrorMessage(shardingPropertiesConstant, value));
        } else if (type == Integer.TYPE && !StringUtil.isIntValue(value)) {
          errorMessages.add(this.getErrorMessage(shardingPropertiesConstant, value));
        } else if (type == Long.TYPE && !StringUtil.isLongValue(value)) {
          errorMessages.add(this.getErrorMessage(shardingPropertiesConstant, value));
        }
      }
    }
  }

  private String getErrorMessage(CustomShardingPropertiesConstant shardingPropertiesConstant,
      String invalidValue) {
    return String.format("Value '%s' of '%s' cannot convert to type '%s'.", invalidValue,
        shardingPropertiesConstant.getKey(), shardingPropertiesConstant.getType().getName());
  }

  public <T> T getValue(CustomShardingPropertiesConstant shardingPropertiesConstant) {
    String result = this.props.getProperty(shardingPropertiesConstant.getKey());
    if (Strings.isNullOrEmpty(result)) {
      Object obj = this.props.get(shardingPropertiesConstant.getKey());
      if (null == obj) {
        result = shardingPropertiesConstant.getDefaultValue();
      } else {
        result = obj.toString();
      }
    }

    if (Boolean.TYPE == shardingPropertiesConstant.getType()) {
      return (T) Boolean.valueOf(result);
    } else if (Integer.TYPE == shardingPropertiesConstant.getType()) {
      return (T) Integer.valueOf(result);
    } else {
      return Long.TYPE == shardingPropertiesConstant.getType() ? (T) Long.valueOf(result)
          : (T) result;
    }
  }

  public Properties getProps() {
    return this.props;
  }
}
