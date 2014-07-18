package com.rmb938.mn2.docker.db.aerospike;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;

public class AerospikeDatabase {

    private Host[] hosts;

    public AerospikeDatabase(Host[] hosts) throws AerospikeException {
        this.hosts = hosts;
    }

    private AerospikeClient getClient() throws AerospikeException {
        return new AerospikeClient(new ClientPolicy(), hosts);
    }

}
