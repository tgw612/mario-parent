package com.mario.shardingjdbc.autoconfigure;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import io.shardingsphere.core.rule.DataSourceParameter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class DataSourceParameterUtil {
    private static final String SET_METHOD_PREFIX = "set";
    private static Collection<Class<?>> generalClassType;

    public static DataSourceParameter getDataSourceParameter(String dataSourceClassName, Map<String, Object> dataSourceProperties) throws ReflectiveOperationException {
        DataSourceParameter dataSourceParameter = (DataSourceParameter)Class.forName(dataSourceClassName).newInstance();
        return getDataSourceParameter(dataSourceParameter, dataSourceProperties);
    }

    public static DataSourceParameter getDataSourceParameter(DataSourceParameter dataSourceParameter, Map<String, Object> dataSourceProperties) throws ReflectiveOperationException {
        Iterator var2 = dataSourceProperties.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Object> entry = (Entry)var2.next();
            callSetterMethod(dataSourceParameter, getSetterMethodName((String)entry.getKey()), null == entry.getValue() ? null : entry.getValue().toString());
        }

        return dataSourceParameter;
    }

    private static String getSetterMethodName(String propertyName) {
        return propertyName.contains("-") ? CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "set-" + propertyName) : "set" + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1, propertyName.length());
    }

    private static void callSetterMethod(DataSourceParameter dataSource, String methodName, String setterValue) {
        Iterator var3 = generalClassType.iterator();

        while(var3.hasNext()) {
            Class each = (Class)var3.next();

            try {
                Method method = dataSource.getClass().getMethod(methodName, each);
                if (Boolean.TYPE != each && Boolean.class != each) {
                    if (Integer.TYPE != each && Integer.class != each) {
                        if (Long.TYPE != each && Long.class != each) {
                            method.invoke(dataSource, setterValue);
                        } else {
                            method.invoke(dataSource, Long.parseLong(setterValue));
                        }
                    } else {
                        method.invoke(dataSource, Integer.parseInt(setterValue));
                    }
                } else {
                    method.invoke(dataSource, Boolean.valueOf(setterValue));
                }

                return;
            } catch (ReflectiveOperationException var6) {
            }
        }

    }

    private DataSourceParameterUtil() {
    }

    static {
        generalClassType = Sets.newHashSet(new Class[]{Boolean.TYPE, Boolean.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, String.class});
    }
}
