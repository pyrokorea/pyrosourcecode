package rdr.similarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by hyeon0145 on 8/27/17.
 */
public class BodyVector {
    private double bmi;
    private double fat;
    private double muscle;
    private List others;

    public BodyVector(double bmi, double fat, double muscle, List others) {
        this.bmi = bmi;
        this.fat = fat;
        this.muscle = muscle;
        this.others = others;
    }

    public BodyVector(double bmi, double fat, double muscle) {
        this(bmi, fat, muscle, new ArrayList());
    }

    public double getBmi() {
        return this.bmi;
    }

    public double getFat() {
        return this.fat;
    }

    public double getMuscle() {
        return this.muscle;
    }

    public List getOthers() { return this.others; }

    public Object[] toArray() {
        return Stream.concat(
                Stream.of(this.bmi, this.fat, this.muscle),
                others.stream()
        ).toArray();
    }
}
