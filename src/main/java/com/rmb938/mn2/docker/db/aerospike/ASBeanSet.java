package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ASBeanSet<T> extends ASSet {

    private final Class<T> type;

    public ASBeanSet(ASNamespace asNamespace, String name, Class<T> type) {
        super(asNamespace, name);
        this.type = type;
    }

    public T getBean(String key) throws AerospikeException {
        T bean;
        try {
            bean = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AerospikeException("Bean access error "+e.getMessage());
        }

        Record record = getRecord(key);
        if (record == null) {
            return null;
        }

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new AerospikeException("Bean info error "+e.getMessage());
        }

        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            Object value = record.bins.get(propertyDescriptor.getName());
            if (value == null) {
                continue;
            }

            Method setter = propertyDescriptor.getWriteMethod();
            if (setter == null) {
                continue;
            }
            Class<?>[] params = setter.getParameterTypes();

            if (isCompatibleType(value, params[0])) {
                try {
                    setter.invoke(bean, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new AerospikeException("Bean set error " + e.getMessage());
                }
            } else {
                throw new AerospikeException("Bean value not compatible "+value+" ("+value.getClass().getName()+")");
            }
        }

        return bean;
    }

    public void updateBean(String key, Object bean) throws AerospikeException {
        if (exists(key) == false) {
            throw new AerospikeException("Bean does not exist "+key);
        }
        putBean(key, bean, RecordExistsAction.UPDATE_ONLY);
    }

    public void insertBean(String key, Object bean) throws AerospikeException {
        if (exists(key)) {
            throw new AerospikeException("Bean already exists "+key);
        }
        putBean(key, bean, RecordExistsAction.CREATE_ONLY);
    }

    private void putBean(String key, Object bean, RecordExistsAction recordExistsAction) throws AerospikeException {
        if (bean == null) {
            throw new AerospikeException("Bean is null");
        }
        if (type.isInstance(bean) == false) {
            throw new AerospikeException("Bean is not type "+type.getName());
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            throw new AerospikeException("Bean info error "+e.getMessage());
        }
        List<Bin> bins = new ArrayList<>();
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            Method getter = propertyDescriptor.getReadMethod();
            if (getter == null) {
                continue;
            }

            Object value;
            try {
                value = getter.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new AerospikeException("Bean get error "+e.getMessage());
            }
            Bin bin = new Bin(propertyDescriptor.getName(), value);
            bins.add(bin);
        }
        Key key1 = new Key(getAsNamespace().getName(), getName(), key);
        WritePolicy policy = new WritePolicy();
        policy.recordExistsAction = recordExistsAction;
        putRecord(policy, key1, bins.toArray(new Bin[bins.size()]));
    }

    private boolean isCompatibleType(Object value, Class<?> type) {
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        }
        return false;
    }

}
