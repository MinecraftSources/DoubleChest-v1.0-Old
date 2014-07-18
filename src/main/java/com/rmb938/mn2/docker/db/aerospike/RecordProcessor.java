package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.Record;

import java.util.List;

public interface RecordProcessor {

    public <T> T toBean(Record record, Class<T> type) throws ParserException;

    public <T> List<T> toBeanList(Record record, Class<T> type) throws ParserException;
}
