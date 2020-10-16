package com.supwisdom.platform.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 利用反射进行操作的一个工具类
 * 
 */
public class ReflectUtil {
    /**
     * 利用反射获取指定对象的指定属性
     * 
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = ReflectUtil.getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     * 
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     * 
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        Field field = ReflectUtil.getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);

                if (fieldValue instanceof String) {
                    Object value = typeConversion(field.getType(), String.valueOf(fieldValue));
                    field.set(obj, value);
                } else {
                    field.set(obj, fieldValue);
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 反射调用对象的方法
     * 
     * @param obj 对象
     * @param methodName 方法名称
     * @param paramType 参数类型 new Class[]{int.class,double.class}
     * @param params 参数值 new Object[]{2,3.5}
     * @return
     * @throws Exception
     */
    public static Object invokeObjMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] params) throws Exception {
        //发现类型
        Class<?> cls = obj.getClass();
        //发现方法
        Method method = cls.getDeclaredMethod(methodName, paramTypes);
        //访问方法时,压制Java对访问修饰符的检查
        method.setAccessible(true);
        Object val = method.invoke(obj, params);
        return val;
    }

    /**
     * 类型转换
     * 
     * @param clazz ：目标类型
     * @param source ：待转换对象
     * @return ：目标对象
     */
    public static Object typeConversion(Class<?> clazz, String source) {

        if (clazz == null) {
            throw new IllegalArgumentException("clazz should not be null");
        }

        Object targetObj = null;
        String nameType = clazz.getName();

        if ("java.lang.Integer".equals(nameType) || "int".equals(nameType)) {
            targetObj = Integer.valueOf(source);
        } else if ("java.lang.String".equals(nameType) || "string".equals(nameType)) {
            targetObj = source;
        } else if ("java.lang.Float".equals(nameType) || "float".equals(nameType)) {
            targetObj = Float.valueOf(source);
        } else if ("java.lang.Double".equals(nameType) || "double".equals(nameType)) {
            targetObj = Double.valueOf(source);
        } else if ("java.lang.Boolean".equals(nameType) || "boolean".equals(nameType)) {
            targetObj = Boolean.valueOf(source);
        } else if ("java.lang.Long".equals(nameType) || "long".equals(nameType)) {
            targetObj = Long.valueOf(source);
        } else if ("java.lang.Short".equals(nameType) || "short".equals(nameType)) {
            targetObj = Short.valueOf(source);
        } else if ("java.lang.Character".equals(nameType) || "char".equals(nameType)) {
            targetObj = source.charAt(1);
        }

        return targetObj;
    }

}
