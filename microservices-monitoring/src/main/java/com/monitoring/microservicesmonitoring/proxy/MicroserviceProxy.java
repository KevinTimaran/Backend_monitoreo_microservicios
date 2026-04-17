package com.monitoring.microservicesmonitoring.proxy;

public interface MicroserviceProxy<T> {

    T execute(String operation, Object... params);
}

