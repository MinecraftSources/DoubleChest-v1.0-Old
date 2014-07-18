package com.rmb938.mn2.docker.db.aerospike;


import java.util.List;

public interface Handler<T> {

    public abstract T handle(AerospikeDatabase database, String query) throws ParserException;
}
