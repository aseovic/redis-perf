package coherence;

import com.tangosol.internal.util.processor.CacheProcessors;

import com.tangosol.net.AsyncNamedMap.OrderBy;
import com.tangosol.net.Coherence;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Session;

import java.time.Duration;

import java.util.Collections;
import java.util.Random;
import java.util.stream.LongStream;

/**
 * @author Aleks Seovic  2021.04.23
 */
@SuppressWarnings("unchecked")
public class Main
    {
    private static final Random RND = new Random();

    public static void main(String[] args)
        {
        Coherence.clusterMember().start().join();
        Session session = Coherence.getInstance().getSession();

        int cPayloadSize = 1024;
        byte[] abValue = new byte[cPayloadSize];
        RND.nextBytes(abValue);

        var test = session.getCache("test");

        var putTest = PutTest.forCache(test)
                .withName("NamedCache.put")
                .withKeys(LongStream.rangeClosed(1, 100_000).boxed())
                .withValue(abValue)
                .withRequest(NamedCache::put)
                .withThreadCount(10);

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        putTest = putTest
                .withName("NamedCache.putAll")
                .withRequest((cache, key, value) -> cache.putAll(Collections.singletonMap(key, value)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        putTest = putTest
                .withName("NamedCache.invoke")
                .withRequest((cache, key, value) -> cache.invoke(key, CacheProcessors.put(value, 0L)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        putTest = putTest
                .withName("AsyncNamedCache.put")
                .withRequest((cache, key, value) -> cache.async(OrderBy.none()).invoke(key, CacheProcessors.put(value, 0L)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        Coherence.closeAll();
        }
    }
