package com.monitoring.microservicesmonitoring.proxy;

public interface ExecutableService<T> {

    T execute(String operation, Object... params);
}

