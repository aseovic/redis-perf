package coherence;

import com.seovic.chronos.LoadTest;
import com.seovic.chronos.coherence.GetRequest;
import com.seovic.chronos.coherence.PutRequest;
import com.seovic.chronos.feed.CollectionFeed;
import com.tangosol.net.NamedCache;
import common.TriConsumer;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Aleks Seovic  2021.04.23
 */
@SuppressWarnings({"rawtypes"})
public class GetTest
        extends LoadTest<GetTest>
    {
    private final NamedCache cache;

    private Set<?> keys;
    
    GetTest(NamedCache cache)
        {
        this.cache = cache;
        }

    public static GetTest forCache(NamedCache cache)
        {
        return new GetTest(cache);
        }

    public GetTest withKeys(Set<?> keys)
        {
        this.keys = keys;
        return this;
        }

    public GetTest withKeys(Stream<?> keys)
        {
        this.keys = keys.collect(Collectors.toSet());
        return this;
        }

    public GetTest withRequest(BiConsumer<NamedCache, Object> request)
        {
        List<GetRequest> requests = keys.stream()
                .map(key -> new GetRequest(cache, key)
                    {
                    protected Object doExecute() throws Throwable
                        {
                        request.accept(cache, key);
                        return null;
                        }
                    })
                .collect(Collectors.toList());
        withRequestFeed(new CollectionFeed(true, requests));
        return this;
        }
    }
