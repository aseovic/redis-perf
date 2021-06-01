package redis;

import com.seovic.chronos.LoadTest;

import java.time.Duration;

import java.util.Random;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * @author Aleks Seovic  2021.04.22
 */
public class Main
    {
    private static final Random RND = new Random();

    public static void main(String[] args)
        {
        JedisCluster cluster = new JedisCluster(Set.of(new HostAndPort("localhost", 6000)));
        cluster.getClusterNodes().forEach((id, jedis) -> System.out.println(id + ": " + jedis));

        int cPayloadSize = 1024;
        byte[] abPayload = new byte[cPayloadSize];
        RND.nextBytes(abPayload);

        LoadTest setTest = SetTest.forCluster(cluster)
                .withEntries(100_000, abPayload)
                .withThreadCount(10);

        // warmup
        System.out.println(setTest.runFor(Duration.ofSeconds(20)));
        System.out.println(setTest.runFor(Duration.ofSeconds(60)));
        }
    }
