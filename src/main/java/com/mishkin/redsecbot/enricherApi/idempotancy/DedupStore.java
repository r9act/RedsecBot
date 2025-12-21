package com.mishkin.redsecbot.enricherApi.idempotancy;

/**
 * @author a.mishkin
 */
public interface DedupStore {

    /**
     * @return true если мы событие обработано первый раз
     */
    boolean claim(String correlationId);
}
