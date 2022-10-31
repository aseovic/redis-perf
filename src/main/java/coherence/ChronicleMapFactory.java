package coherence;

import com.tangosol.util.Binary;
import java.util.concurrent.ConcurrentMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

/**
 * @author Aleks Seovic  2022.03.10
 */
public class ChronicleMapFactory
    {
    public static ConcurrentMap<Binary, Binary> create()
        {
        return ChronicleMapBuilder
            .of(Binary.class, Binary.class)
            .name("chronicle-map")
            .averageKeySize(10)
            .averageValueSize(1024)
            .entries(100_000)
            .create();
        }
    }
