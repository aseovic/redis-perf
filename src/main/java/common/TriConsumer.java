package common;

/**
 * @author Aleks Seovic  2021.05.21
 */
@FunctionalInterface
public interface TriConsumer<T1, T2, T3>
    {
    void accept(T1 first, T2 second, T3 third);
    }
