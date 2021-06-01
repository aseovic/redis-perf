package coherence;

import com.seovic.chronos.LoadTest;
import com.seovic.chronos.coherence.PutRequest;
import com.seovic.chronos.feed.CollectionFeed;

import com.tangosol.net.NamedCache;

import common.TriConsumer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Aleks Seovic  2021.04.23
 */
@SuppressWarnings({"rawtypes"})
public class PutTest extends LoadTest<PutTest>
    {
    private final NamedCache cache;

    private Set<?> keys;
    private Object value;
    
    PutTest(NamedCache cache)
        {
        this.cache = cache;
        }

    public static PutTest forCache(NamedCache cache)
        {
        return new PutTest(cache);
        }

    public PutTest withKeys(Set<?> keys)
        {
        this.keys = keys;
        return this;
        }

    public PutTest withKeys(Stream<?> keys)
        {
        this.keys = keys.collect(Collectors.toSet());
        return this;
        }

    public PutTest withValue(Object value)
        {
        this.value = value;
        return this;
        }

    public PutTest withRequest(TriConsumer<NamedCache, Object, Object> request)
        {
        List<PutRequest> requests = keys.stream()
                .map(key -> new PutRequest(cache, key, value)
                    {
                    protected Object doExecute() throws Throwable
                        {
                        request.accept(cache, key, value);
                        return null;
                        }
                    })
                .collect(Collectors.toList());
        withRequestFeed(new CollectionFeed(true, requests));
        return this;
        }
    }
