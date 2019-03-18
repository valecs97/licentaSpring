package ro.vitoc.licenta.core.cache;

import ro.vitoc.licenta.core.model.SimpleProject;

public interface CacheManager<T> {
    void cacheValue(T T);
    T checkCache(T T);
}
