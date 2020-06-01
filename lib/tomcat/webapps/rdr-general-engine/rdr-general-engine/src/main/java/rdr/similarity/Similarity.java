package rdr.similarity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by hyeon0145 on 2/21/18.
 */
public class Similarity {
    /**
     * 두 Object[] 사이의 유사도를 계산합니다. 유사도는 0부터 1의 값을 가질 수 있습니다.
     * 1에 가까울 수록 두 Object[]이 유사함을 의미합니다.
     * 지원하는 Object는 Number(Integer, Double)와 String입니다.
     *
     * @param a  비교할 첫 번째 Object[]
     * @param b  비교할 두 번째 Object[]
     */
    public static double calculate(Object[] a, Object[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Dimensions of a and b are different");
        if (a.length == 0) return 1.0;

        ArrayList<Integer> indiciesOfNull = new ArrayList<>();
        ArrayList<Integer> indiciesOfNotNull = new ArrayList<>();
        for (int index = 0; index < a.length; ++index) {
            if ((a[index] == null) || (b[index] == null)) {
                indiciesOfNull.add(index);
            } else {
                indiciesOfNotNull.add(index);
            }
        }

        Object[] notNullA = indiciesOfNotNull.stream().map(index -> a[index]).toArray();
        Object[] notNullB = indiciesOfNotNull.stream().map(index -> b[index]).toArray();
        for (int index = 0; index < notNullA.length; ++index) {
            boolean areBothNumber = (notNullA[index] instanceof Number) && (notNullB[index] instanceof Number);
            boolean areBothString = (notNullA[index] instanceof String) && (notNullB[index] instanceof String);

            if (!(areBothNumber || areBothString)) {
                throw new IllegalArgumentException("Both elements of a and b should have equivalent type");
            }
        }

        Number[] numbersOfA = Arrays.stream(notNullA)
                .filter(element -> element instanceof Number)
                .toArray(Number[]::new);
        Number[] numbersOfB = Arrays.stream(notNullB)
                .filter(element -> element instanceof Number)
                .toArray(Number[]::new);
        String[] stringsOfA = Arrays.stream(notNullA)
                .filter(element -> element instanceof String)
                .toArray(String[]::new);
        String[] stringsOfB = Arrays.stream(notNullB)
                .filter(element -> element instanceof String)
                .toArray(String[]::new);

        Boolean[] nullitiesOfA = indiciesOfNull.stream()
                .map(index -> a[index] == null)
                .toArray(Boolean[]::new);
        Boolean[] nullitiesOfB = indiciesOfNull.stream()
                .map(index -> b[index] == null)
                .toArray(Boolean[]::new);

        double similarityOfNumbers = Similarity.calculate(numbersOfA, numbersOfB);
        double similarityOfStrings = Similarity.calculate(stringsOfA, stringsOfB);
        double similarityOfNullities = Similarity.calculate(nullitiesOfA, nullitiesOfB);

        return (
                ((double)numbersOfA.length / a.length) * similarityOfNumbers
                + ((double)stringsOfA.length / a.length) * similarityOfStrings
                + ((double)nullitiesOfA.length / a.length) * similarityOfNullities
        );
    }

    /**
     * 두 double[] 사이의 유사도를 계산합니다. 유사도는 0부터 1의 값을 가질 수 있습니다.
     * 1에 가까울 수록 두 double[]이 유사함을 의미합니다.
     *
     * @param a  비교할 첫 번째 double[]
     * @param b  비교할 두 번째 double[]
     */
    public static double calculate(double[] a, double[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Dimensions of a and b are different");
        if (a.length == 0) return 1.0;

        double aDotB = IntStream.range(0, a.length).mapToDouble(index -> a[index] * b[index]).sum();

        double lengthOfA = Math.sqrt(Arrays.stream(a).map(element -> element * element).sum());
        double lengthOfB = Math.sqrt(Arrays.stream(b).map(element -> element * element).sum());

        return aDotB / (lengthOfA * lengthOfB);
    }

    public static double calculate(Number[] a, Number[] b) {
        return Similarity.calculate(
                Arrays.stream(a).mapToDouble(Number::doubleValue).toArray(),
                Arrays.stream(b).mapToDouble(Number::doubleValue).toArray()
        );
    }

    /**
     * 두 String[] 사이의 유사도를 계산합니다. 유사도는 0부터 1의 값을 가질 수 있습니다.
     * 1에 가까울 수록 두 String[]이 유사함을 의미합니다.
     *
     * @param a  비교할 첫 번째 String[]
     * @param b  비교할 두 번째 String[]
     */
    public static double calculate(String[] a, String[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Dimensions of a and b are different");
        if (a.length == 0) return 1.0;

        return (IntStream.range(0, a.length)
                .mapToDouble(index -> Similarity.calculate(a[index], b[index]))
                .sum()) / a.length;
    }

    /**
     * 두 String 사이의 유사도를 계산합니다. 유사도는 0부터 1의 값을 가질 수 있습니다.
     * 1에 가까울 수록 두 String이 유사함을 의미합니다.
     *
     * @param a  비교할 첫 번째 String
     * @param b  비교할 두 번째 String
     */
    public static double calculate(String a, String b) {
        if (a.equals(b)) return 1.0;
        if ((a.length() < 2) || (b.length() < 2)) return 0.0; // for length-1 strings

        List<String> bigramsOfA = Utils.bigrams(a);
        List<String> bigramsOfB = Utils.bigrams(b);

        // Sørensen–Dice coefficient
        return (2.0 * Utils.intersection(bigramsOfA, bigramsOfB).size()) / (bigramsOfA.size() + bigramsOfB.size());
    }

    /**
     * 두 boolean[] 사이의 유사도를 계산합니다. 유사도는 0부터 1의 값을 가질 수 있습니다.
     * 1에 가까울 수록 두 boolean[]이 유사함을 의미합니다.
     *
     * @param a  비교할 첫 번째 boolean[]
     * @param b  비교할 두 번째 boolean[]
     */
    public static double calculate(boolean[] a, boolean[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("Dimensions of a and b are different");
        if (a.length == 0) return 1.0;

        return (double)(IntStream.range(0, a.length).map(index -> (a[index] == b[index]) ? 1 : 0).sum()) / a.length;
    }

    public static double calculate(Boolean[] a, Boolean[] b) {
        boolean[] unboxedA = new boolean[a.length];
        for (int index = 0; index < a.length; ++index) unboxedA[index] = a[index];

        boolean[] unboxedB = new boolean[b.length];
        for (int index = 0; index < b.length; ++index) unboxedB[index] = b[index];

        return Similarity.calculate(unboxedA, unboxedB);
    }
}
