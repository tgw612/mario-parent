package com.mario.redis.config.sentinel;

public class RedisConfigBean {

    private String masterName;

    private Set<String> sentinelNodes;

    private JedisPoolConfig masterPoolConfig;

    private Integer database;

    private String password;

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public Set<String> getSentinelNodes() {
        return sentinelNodes;
    }

    public void setSentinelNodes(Set<String> sentinelNodes) {
        this.sentinelNodes = sentinelNodes;
    }

    public JedisPoolConfig getMasterPoolConfig() {
        return masterPoolConfig;
    }

    public void setMasterPoolConfig(JedisPoolConfig masterPoolConfig) {
        this.masterPoolConfig = masterPoolConfig;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
