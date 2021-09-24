package com.ziloka.ProxyBroker.services;

import com.google.gson.Gson;
import com.ziloka.ProxyBroker.services.models.LookupResult;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyThread implements Runnable {

    private static final Logger logger = LogManager.getLogger(ProxyCollector.class);

    final ConcurrentHashMap<String, LookupResult> onlineProxies;
    String proxy;
    String host;
    int port;
    String types;
    String lvl;
    ProxyChecker proxyChecker;
    ProxyLookup proxyLookup;

    /**
     * @param onlineProxies - Online Proxy Hashmap
     * @param proxy - Proxy syntax host:port
     * @param types - Proxy types
     * @param lvl - Proxy anonymity level
     */
    public ProxyThread(PoolingHttpClientConnectionManager poolingConnManager, Gson gson, ConcurrentHashMap<String, LookupResult> onlineProxies, String proxy, String types, String lvl) {
        this.onlineProxies = onlineProxies;
        this.proxy = proxy;
        this.host = proxy.split(":")[0];
        this.port = Integer.parseInt(proxy.split(":")[1]);
        this.types = types;
        this.lvl = lvl;
        this.proxyChecker = new ProxyChecker(this.onlineProxies, this.proxy, types);
        this.proxyLookup = new ProxyLookup(poolingConnManager, gson,  this.host);
    }

    /**
     * Method executed when thread is executed
     */
    public void run(){
        try {
            if(this.proxyChecker.check()){
                this.onlineProxies.put(this.proxy, this.proxyLookup.getInfo());
            }
        } catch (IOException e) {
            // Don't print anything
            e.printStackTrace();
        }
    }

}
