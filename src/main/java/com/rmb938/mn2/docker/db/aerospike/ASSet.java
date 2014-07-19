package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ASSet {

    private final ASNamespace asNamespace;
    private final String name;

    public ASSet(ASNamespace asNamespace, String name) {
        this.asNamespace = asNamespace;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ASNamespace getAsNamespace() {
        return asNamespace;
    }

    public boolean exists(String key) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();
        boolean exists = client.exists(null, new Key(asNamespace.getName(), name, key));
        client.close();
        return exists;
    }

    public void putRecord(WritePolicy policy, Key key, Bin... bins) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();
        client.put(policy, key, bins);
        client.close();
    }

    public Record getRecord(String key) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();
        Record record = client.get(null, new Key(asNamespace.getName(), name, key));
        client.close();
        return record;
    }

    public Map.Entry<Key, Record> getRecord(Filter... filters) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();

        Map.Entry<Key, Record> pair = null;

        Statement stmt = new Statement();
        stmt.setNamespace(asNamespace.getName());
        stmt.setSetName(name);
        stmt.setFilters(filters);

        RecordSet rs = client.query(null, stmt);
        if (rs.next()) {
            Key key = rs.getKey();
            Record record = rs.getRecord();
            pair = new AbstractMap.SimpleEntry<>(key, record);
        }
        rs.close();
        client.close();

        return pair;
    }

    public List<Map.Entry<Key, Record>> getRecords(Filter... filters) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();

        List<Map.Entry<Key, Record>> records = new ArrayList<>();

        Statement stmt = new Statement();
        stmt.setNamespace(asNamespace.getName());
        stmt.setSetName(name);
        stmt.setFilters(filters);

        RecordSet rs = client.query(null, stmt);
        while (rs.next()) {
            Key key = rs.getKey();
            Record record = rs.getRecord();
            Map.Entry<Key, Record> pair = new AbstractMap.SimpleEntry<>(key, record);
            records.add(pair);
        }
        rs.close();
        client.close();

        return records;
    }

    public void deleteRecord(String key) throws AerospikeException {
        AerospikeClient client = asNamespace.getDb().getClient();
        client.delete(null, new Key(asNamespace.getName(), name, key));
        client.close();
    }

}
