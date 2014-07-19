package com.rmb938.mn2.docker.db.aerospike;

import java.util.HashMap;

public class ASNamespace {

    private final String name;
    private final AerospikeDatabase db;

    private HashMap<String, ASSet> sets = new HashMap<>();

    public ASNamespace(AerospikeDatabase db, String name) {
        this.name = name;
        this.db = db;
    }

    public ASSet registerSet(ASSet asSet) {
        sets.put(asSet.getName(), asSet);
        return asSet;
    }

    public ASSet getASSet(String setName) {
        return sets.get(setName);
    }

    protected String getName() {
        return name;
    }

    protected AerospikeDatabase getDb() {
        return db;
    }
}
