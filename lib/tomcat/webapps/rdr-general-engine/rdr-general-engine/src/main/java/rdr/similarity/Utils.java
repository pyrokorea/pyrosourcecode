package rdr.similarity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by hyeon0145 on 2/21/18.
 */
public class Utils {
    public static <T> Set<T> intersection(Collection<T> a, Collection<T> b) {
        HashSet<T> output = new HashSet<T>(a);
        output.retainAll(new HashSet<T>(b));

        return output;
    }

    public static List<String> bigrams(String text) {
        String newText = text.replaceAll("\\s+", " ").toLowerCase();

        return IntStream.range(0, newText.length() - 1)
                .mapToObj(index -> newText.substring(index, index + 2))
                .collect(Collectors.toList());
    }
}
