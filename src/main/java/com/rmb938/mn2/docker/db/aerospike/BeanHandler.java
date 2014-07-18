package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.Record;


public class BeanHandler<T> implements Handler<T> {

    public static BasicRecordProcessor defaultConvert = new BasicRecordProcessor();

    private final Class<T> type;
    private final BasicRecordProcessor convert;

    public BeanHandler(Class<T> type) {
        this.type = type;
        convert = BeanHandler.defaultConvert;
    }

    @Override
    public T handle(AerospikeDatabase database, String query) throws ParserException {
        Record record = null;
        T bean = convert.toBean(record, type);
        return bean;
    }

}
