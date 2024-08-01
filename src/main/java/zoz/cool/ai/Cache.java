package zoz.cool.ai;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Cache {
    private final int capacity;
    private final List<Long> cache;
    private final Map<Long, Object> mapCache;

    public Cache(int capacity) {
        this.capacity = capacity;
        this.cache = new ArrayList<>();
        this.mapCache = new HashMap<>();
    }

    public Object get(Long key) {
        return mapCache.get(key);
    }

    public void put(Long key, Object value) {
        if (cache.size() >= capacity) {
            Long oldKey = cache.removeFirst();
            mapCache.remove(oldKey);
        }
        mapCache.put(key, value);
    }

    public boolean has(Long key) {
        return mapCache.containsKey(key);
    }
}
