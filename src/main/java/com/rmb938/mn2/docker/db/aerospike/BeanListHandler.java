package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.Record;

import java.util.List;


public class BeanListHandler<T> implements Handler<List<T>> {

    private final Class<T> type;
    private final BasicRecordProcessor convert;

    public BeanListHandler(Class<T> type) {
        this.type = type;
        convert = BeanHandler.defaultConvert;
    }

    @Override
    public List<T> handle(AerospikeDatabase database, String query) throws ParserException {
        Record record = null;
        return convert.toBeanList(record, type);
    }

}
