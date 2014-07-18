package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.Record;

import java.util.ArrayList;
import java.util.List;

public class BeanProcessor {

    public <T> T toBean(Record record, Class<T> type) throws ParserException {
        return createBean(record, type);
    }

    public <T> List<T> toBeanList(Record record, Class<T> type) {
        List<T> records = new ArrayList<>();

        return records;
    }

    private <T> T createBean(Record record, Class<T> type) throws ParserException {
        T bean = newInstance(type);

        return bean;
    }

    protected <T> T newInstance(Class<T> c) throws ParserException {
        try {
            return c.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            throw new ParserException(
                    "Cannot create " + c.getName() + ": " + e.getMessage());

        }
    }

}
