package com.omni.support.ble.utils;


import androidx.annotation.Nullable;

import java.lang.reflect.*;

public class TypeUtils {
    public static <T> T checkNotNull(@Nullable T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    /**
     * 检查字节数组是否合法
     *
     * @param buffer     字节数组
     * @param expectSize 数组预计长度, 如果长度小于预计, 抛出异常
     */
    public static void checkBuffer(byte[] buffer, int expectSize) throws IllegalArgumentException {
        if (buffer == null)
            throw new IllegalArgumentException("buffer is null");
        if (buffer.length < expectSize)
            throw new IllegalArgumentException("buffer size is invalid");
    }

    /**
     * 获取原始类型
     *
     * @param type
     * @return
     */
    public static Class<?> getRawType(Type type) {
        checkNotNull(type, "type == null");

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class))
                throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }

    /**
     * 判断type类型是否可用被解析
     *
     * @param type 类型
     * @return
     */
    public static boolean hasUnresolvableType(Type type) {
        // class的参数化类型
        if (type instanceof Class<?>) {
            return false;
        }
        // 参数化类型
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            // 获取type参数的实际类型数组
            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
                if (hasUnresolvableType(typeArgument)) {
                    return true;
                }
            }
            return false;
        }
        // 数组类型, 递归检查
        if (type instanceof GenericArrayType) {
            return hasUnresolvableType(((GenericArrayType) type).getGenericComponentType());
        }
        // 变量类型, 例如 class TypeVariableBean<K extends InputStream, V> 中的K, V
        if (type instanceof TypeVariable) {
            return true;
        }
        // 通配符类型, 例如 List<? extends Number> a
        if (type instanceof WildcardType) {
            return true;
        }
        String className = type == null ? "null" : type.getClass().getName();
        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + className);
    }

    /**
     * 获取响应类型
     *
     * @param returnType
     * @return
     */
    public static Type getCallResponseType(Type returnType) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        return getParameterUpperBound(0, (ParameterizedType) returnType);
    }

    static Type getParameterUpperBound(int index, ParameterizedType type) {
        // 获取实际的参数类型
        Type[] types = type.getActualTypeArguments();
        if (index < 0 || index >= types.length) {
            throw new IllegalArgumentException(
                    "Index " + index + " not in range [0," + types.length + ") for " + type);
        }
        Type paramType = types[index];
        // 通配符
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }

    public static String getMemberString(Object object) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = object.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                if (field.getGenericType().equals(byte[].class)) {
                    sb.append(field.getName());
                    sb.append("=");
                    sb.append(HexString.valueOf((byte[]) field.get(object)));
                } else {
                    sb.append(field.getName());
                    sb.append("=");
                    sb.append(field.get(object));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (i < fields.length - 1)
                sb.append(", ");
        }
        return sb.toString();
    }
}
