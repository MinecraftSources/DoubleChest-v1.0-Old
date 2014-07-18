package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.Record;

import java.util.List;

public class BasicRecordProcessor implements RecordProcessor {

    private static final BeanProcessor defaultConvert = new BeanProcessor();

    private final BeanProcessor convert;

    public BasicRecordProcessor() {
        this.convert = BasicRecordProcessor.defaultConvert;
    }

    @Override
    public <T> T toBean(Record record, Class<T> type) throws ParserException {
        return convert.toBean(record, type);
    }

    @Override
    public <T> List<T> toBeanList(Record record, Class<T> type) throws ParserException {
        return convert.toBeanList(record, type);
    }

}
