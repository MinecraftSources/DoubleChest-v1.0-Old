package com.rmb938.mn2.docker.db.etcd;

import java.util.List;

public class EtcdNode {
    public String key;
    public long createdIndex;
    public long modifiedIndex;
    public String value;

    // For TTL keys
    public String expiration;
    public Integer ttl;

    // For listings
    public boolean dir;
    public List<EtcdNode> nodes;

    @Override
    public String toString() {
        return EtcdClient.format(this);
    }
}
