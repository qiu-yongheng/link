package com.omni.support.ble.rover;

import android.util.Log;

import com.omni.support.ble.core.BufferSerializable;
import com.omni.support.ble.rover.annotations.*;
import com.omni.support.ble.rover.factory.ICommandFactory;
import com.omni.support.ble.utils.BufferBuilder;
import com.omni.support.ble.utils.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * @author 邱永恒
 * @time 2018/5/14  10:16
 * @desc
 */

public class ProtocolRetrofit {
    private Map<Class<?>, ICommandFactory> factoryMap;

    private ProtocolRetrofit(Builder builder) {
        this.factoryMap = builder.factoryMap;
    }

    /**
     * 使用动态代理创建一个接口实体
     *
     * @param service 接口
     * @param <T>     接口
     * @return 接口动态代理
     */
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    /**
                     * 调用代理类中的方法
                     * @param proxy  代理类
                     * @param method 调用方法
                     * @param args   参数
                     * @return 返回Command<RESULT>对象
                     */
                    @Override
                    public Object invoke(Object proxy, final Method method, final Object[] args) {
                        Type returnType = parseReturnType(method);
                        byte[] data = parseParams(method, args);

                        Class<?> returnClass = method.getReturnType();
                        if (factoryMap == null || factoryMap.isEmpty()) {
                            throw new IllegalArgumentException("ICommandFactory isEmpty");
                        }

                        ICommandFactory commandFactory = factoryMap.get(returnClass);

                        if (commandFactory == null) {
                            throw new IllegalArgumentException("Method return type not register: " + returnClass.getSimpleName());
                        }

                        return commandFactory.getCommand(method, returnType, data);
                    }
                });
    }

    /**
     * 解析返回类型
     */
    private Type parseReturnType(Method method) {
        Type returnType = method.getGenericReturnType();
        TypeUtils.checkNotNull(returnType, "returnType == null");
        if (TypeUtils.hasUnresolvableType(returnType)) {
            throw new IllegalArgumentException(String.format("Method return type must not include a type variable or wildcard: %s", returnType));
        }
        return TypeUtils.getCallResponseType(returnType);
    }

    /**
     * 解析参数
     */
    private byte[] parseParams(Method method, Object[] args) {
        if (args != null && args.length > 0) {
            final Annotation[][] asArray = method.getParameterAnnotations();
            final BufferBuilder bufferBuilder = new BufferBuilder();

            for (int i = 0; i < args.length; i++) {
                final Object arg = args[i];
                if (arg instanceof Integer) {
                    Annotation[] annotations = asArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof U8) {
                            bufferBuilder.putU8((Integer) arg);
                        } else if (annotation instanceof U16) {
                            bufferBuilder.putU16((Integer) arg);
                        } else if (annotation instanceof S32) {
                            bufferBuilder.putS32((Integer) arg);
                        }
                    }
                } else if (arg instanceof Long) {
                    Annotation[] annotations = asArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof U32) {
                            bufferBuilder.putU32((Long) arg);
                        } else if (annotation instanceof U64) {
                            bufferBuilder.putU64((Long) arg);
                        }
                    }
                } else if (arg instanceof Float) {
                    bufferBuilder.putFloat((Float) arg);
                } else if (arg instanceof byte[]) {
                    bufferBuilder.putBytes((byte[]) arg);
                } else if (arg instanceof String) {
                    bufferBuilder.putString((String) arg);
                } else if (arg instanceof BufferSerializable) {
                    byte[] serialize = ((BufferSerializable) arg).getBuffer();

                    if (serialize == null) {
                        serialize = new byte[0];
                    }
                    bufferBuilder.putBytes(serialize);
                }
            }

            if (bufferBuilder.size() > 0) {
                return bufferBuilder.buffer();
            }
        }

        return null;
    }


    public static final class Builder {
        Map<Class<?>, ICommandFactory> factoryMap = new HashMap<>();

        public Builder addCommandFactory(Class<?> service, ICommandFactory factory) {
            factoryMap.put(service, factory);
            return this;
        }


        public ProtocolRetrofit build() {
//            addCommandFactory(FCCommand.class, new FCCommandFactory());
            return new ProtocolRetrofit(this);
        }
    }
}
