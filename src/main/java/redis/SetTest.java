package redis;

import com.seovic.chronos.LoadTest;
import com.seovic.chronos.Request;
import com.seovic.chronos.feed.CollectionFeed;
import com.seovic.chronos.request.AbstractRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import redis.clients.jedis.JedisCluster;

/**
 * @author Aleks Seovic  2021.04.23
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SetTest
        extends LoadTest
    {
    private JedisCluster cluster;

    private SetTest(JedisCluster cluster)
        {
        this.cluster = cluster;
        }

    public static SetTest forCluster(JedisCluster cluster)
        {
        return new SetTest(cluster);
        }

    public SetTest withEntries(long cEntries, byte[] abValue)
        {
        List<Request> requests = LongStream.rangeClosed(1, cEntries)
                .mapToObj(key -> Long.toString(key).getBytes(StandardCharsets.UTF_8))
                .map(abKey -> new AbstractRequest()
                    {
                    public String getName()
                        {
                        return "redis.set";
                        }

                    protected Object doExecute() throws Throwable
                        {
                        cluster.set(abKey, abValue);
                        return null;
                        }
                    })
                .collect(Collectors.toList());
        withRequestFeed(new CollectionFeed(requests));
        return this;
        }
    }
