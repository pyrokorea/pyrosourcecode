package rdr.similarity;

public class BodyDistance {
    private static final double ALLOWABLE_DIFFERENCE = 1.0;
    /**
     * 두 BodyVector 사이의 거리를 계산합니다. 거리는 0부터 Double.POSITIVE_INFINITY의 값을 가질 수 있습니다.
     * 0에 가까울 수록 두 BodyVector가 유사함을 의미합니다.
     *
     * @param a  비교할 첫 번째 BodyVector
     * @param b  비교할 두 번째 BodyVector
     */
    public static double calculate(BodyVector a, BodyVector b) {
        if (!BodyDistance.needsToCalculate(a, b)) return Double.POSITIVE_INFINITY;

        return Similarity.calculate(a.toArray(), b.toArray());
    }


    /**
     * 두 BodyVector의 거리 계산 필요 여부를 반환합니다.
     * BMI, 체지방률, 근육량의 차이가 ALLOWABLE_DIFFERENCE를 초과할 경우 거리 계산이 필요하지 않습니다.
     *
     * @param a  비교할 첫 번째 BodyVector
     * @param b  비교할 두 번째 BodyVector
     */
    private static boolean needsToCalculate(BodyVector a, BodyVector b) {
        double difference = Math.abs(a.getBmi() - b.getBmi());
        if (difference > ALLOWABLE_DIFFERENCE) return false;

        difference = Math.abs(a.getFat() - b.getFat());
        if (difference > ALLOWABLE_DIFFERENCE) return false;

        difference = Math.abs(a.getMuscle() - b.getMuscle());
        if (difference > ALLOWABLE_DIFFERENCE) return false;

        return true;
    }
}
