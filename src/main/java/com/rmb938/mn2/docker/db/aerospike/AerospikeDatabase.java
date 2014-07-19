package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;

import javax.xml.stream.events.Namespace;
import java.util.*;

public class AerospikeDatabase {

    private final Host[] hosts;

    private HashMap<String, ASNamespace> namespaces = new HashMap<>();

    public AerospikeDatabase(Host[] hosts) {
        this.hosts = hosts;
    }

    public ASNamespace registerNamespace(ASNamespace namespace) {
        namespaces.put(namespace.getName(), namespace);
        return namespace;
    }

    public ASNamespace getASNamespace(String namespace) {
        return namespaces.get(namespace);
    }

    protected AerospikeClient getClient() throws AerospikeException {
        return new AerospikeClient(new ClientPolicy(), hosts);
    }

}
