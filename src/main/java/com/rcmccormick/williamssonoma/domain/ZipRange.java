package com.rcmccormick.williamssonoma.domain;

import com.google.common.base.Strings;
import com.rcmccormick.williamssonoma.domain.exceptions.ZipRangeInstantiationException;

import java.util.Comparator;
import java.util.regex.Pattern;

public class ZipRange {

    private Integer upperBound;
    private Integer lowerBound;

    /**
     * Valid regex for string form of zip range
     */
    private static final String VALID_STRING_INPUT_REGEX = "\\s*\\[\\s*\\d{5}\\s*,\\s*\\d{5}\\s*\\]\\s*";
    private static final Pattern VALID_STRING_PATTERN = Pattern.compile(VALID_STRING_INPUT_REGEX);

    public static Comparator<ZipRange> LOWER_BOUND_COMPARATOR = new Comparator<ZipRange>() {
        public int compare(ZipRange zipRange1, ZipRange zipRange2) {
            return zipRange1.getLowerBound().compareTo(zipRange2.getLowerBound());
        }
    };

    /**
     * @param stringInput - in the format described by VALID_STRING_INPUT_REGEX
     * @throws ZipRangeInstantiationException
     */
    public ZipRange(String stringInput) throws ZipRangeInstantiationException {
        if(Strings.isNullOrEmpty(stringInput) || !VALID_STRING_PATTERN.matcher(stringInput.trim()).matches()) {
            throw new ZipRangeInstantiationException("Invalid String formatted input to ZipRange constructor");
        }
        stringInput = stringInput.replaceAll("\\]|\\[|\\s*","");
        String[] strParts = stringInput.split(",");

        assert(strParts.length == 2);

        Integer lower = Integer.valueOf(strParts[0]);
        Integer upper = Integer.valueOf(strParts[1]);

        if(lower > upper) {
            throw new ZipRangeInstantiationException("Invalid input to ZipRange constructor");
        }
        this.upperBound = upper;
        this.lowerBound = lower;
    }

    /**
     * Copy Constructor
     * @param zipRange
     * @throws ZipRangeInstantiationException
     */
    public ZipRange(ZipRange zipRange) throws ZipRangeInstantiationException {
        if(zipRange.getLowerBound() == null || zipRange.getUpperBound() == null || zipRange.getUpperBound() < zipRange.getLowerBound()) {
            throw new ZipRangeInstantiationException("Invalid input to ZipRange copy constructor");
        }
        this.lowerBound = zipRange.getLowerBound();
        this.upperBound = zipRange.getUpperBound();
    }

    /**
     *
     * @param lower
     * @param upper
     * @throws ZipRangeInstantiationException
     */
    public ZipRange(Integer lower, Integer upper) throws ZipRangeInstantiationException {

        if(lower == null || upper == null || lower > upper) {
            throw new ZipRangeInstantiationException("Invalid input to ZipRange constructor");
        }
        this.upperBound = upper;
        this.lowerBound = lower;
    }

    /**
     * Testing for the case that thatRange canCombine this range
     * see tests for enumeration of cases
     * @param thatRange
     * @return
     */
    public boolean canCombine(ZipRange thatRange) {
        return thatRange != null && thatRange.getUpperBound() >= this.getLowerBound() && thatRange.getLowerBound() <= this.getUpperBound();
    }

    /**
     * Combines ranges that are contained within each other
     * Expectation is that you call containedWithin first before you call this
     * @param thatRange
     * @throws Exception
     */
    public ZipRange combine(ZipRange thatRange) throws Exception {
        if(!this.canCombine(thatRange)) {
            throw new Exception("Invalid argument to combine, range not contained inside instance");
        }

        return new ZipRange(Math.min(this.getLowerBound(), thatRange.getLowerBound()), Math.max(this.getUpperBound(), thatRange.getUpperBound()));
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Integer upperBound) {
        this.upperBound = upperBound;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Integer lowerBound) {
        this.lowerBound = lowerBound;
    }

    public static String getValidStringInputRegex() {
        return VALID_STRING_INPUT_REGEX;
    }

    @Override
    public String toString() {
        return "[" + this.getLowerBound() + "," + this.getUpperBound() + "]";
    }
}
