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

        runTests(session, abValue, "test");             // LocalCache
        runTests(session, abValue, "safe-test");        // SafeHashMap
        runTests(session, abValue, "concurrent-test");  // ConcurrentHashMap
        runTests(session, abValue, "rj-test");          // RamJournal
        runTests(session, abValue, "fj-test");          // FlashJournal
        runTests(session, abValue, "cm-test");          // ChronicleMap

        Coherence.closeAll();
        }

    private static void runTests(Session session, byte[] abValue, String sCacheName)
        {
        var test = session.getCache(sCacheName);
        test.truncate();
        System.out.println("Testing: " + test.getCacheName());

        var putTest = PutTest.forCache(test)
                .withName("NamedCache.put")
                .withKeys(LongStream.rangeClosed(1, 100_000).boxed())
                .withValue(abValue)
                .withRequest(NamedCache::put)
                .withThreadCount(10);

        var getTest = GetTest.forCache(test)
                .withName("NamedCache.get")
                .withKeys(LongStream.rangeClosed(1, 100_000).boxed())
                .withRequest(NamedCache::get)
                .withThreadCount(10);

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        getTest.runFor(Duration.ofSeconds(30));
        System.out.println(getTest.runFor(Duration.ofSeconds(60)));

        getTest = getTest
                .withName("AsyncNamedCache.get")
                .withRequest((cache, key) -> cache.async(OrderBy.none()).invoke(key, CacheProcessors.get()));

        getTest.runFor(Duration.ofSeconds(30));
        System.out.println(getTest.runFor(Duration.ofSeconds(60)));

        test.truncate();
        putTest = putTest
                .withName("NamedCache.putAll")
                .withRequest((cache, key, value) -> cache.putAll(Collections.singletonMap(key, value)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        test.truncate();
        putTest = putTest
                .withName("NamedCache.invoke")
                .withRequest((cache, key, value) -> cache.invoke(key, CacheProcessors.put(value, 0L)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));

        test.truncate();
        putTest = putTest
                .withName("AsyncNamedCache.put")
                .withRequest((cache, key, value) -> cache.async(OrderBy.none()).invoke(key, CacheProcessors.put(value, 0L)));

        putTest.runFor(Duration.ofSeconds(30));
        System.out.println(putTest.runFor(Duration.ofSeconds(60)));
        test.truncate();
        }
    }
