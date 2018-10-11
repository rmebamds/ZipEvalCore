package com.rcmccormick.williamssonoma.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipRangeList {

    private List<ZipRange> zipRangeList = new ArrayList<>();

    private ZipRangeList() {}

    /**
     * Take input directly from command line and create ZipRange
     * @param commandArgs
     * @throws Exception
     */
    public ZipRangeList(String[] commandArgs) throws Exception {
        this(Joiner.on(" ").skipNulls().join(commandArgs));
    }

    /**
     * Take stringed input of the entire range and instantiate
     * @param stringedInput
     * @throws Exception
     */
    public ZipRangeList(final String stringedInput) throws Exception {
        try {

            final String regexFormatForZipRangeList = "(\\s*" + ZipRange.getValidStringInputRegex() + "\\s*)+";
            if(Strings.isNullOrEmpty(stringedInput) || !Pattern.compile(regexFormatForZipRangeList).matcher(stringedInput).matches()) {
                throw new Exception("Invalid format for ZipRangeList stringed input: " + stringedInput);
            }

            final Matcher zipRangeMatcher = Pattern.compile(ZipRange.getValidStringInputRegex()).matcher(stringedInput);

            while (zipRangeMatcher.find()) {
                this.zipRangeList.add(new ZipRange(zipRangeMatcher.group()));
            }

            //sorting guarantees we only have to combine once, instead of all permutations
            this.zipRangeList.sort(ZipRange.LOWER_BOUND_COMPARATOR);
        }
        catch(Exception e) {
            throw new Exception("Invalid input to ZipRangeList constructor");
        }
    }

    /**
     * Combine all internal ZipRanges into the equivalence class of condensed zip ranges, if possible
     *  not destructive or mutative.
     * @return - new copy of resulting list which is subset of this.zipRangeList
     */
    public ZipRangeList combine() throws Exception {

        final ZipRangeList returnZipRangeList = new ZipRangeList();

        final PeekingIterator<ZipRange> zipRangeIterator = Iterators.peekingIterator(zipRangeList.iterator());

        ZipRange chainedCombinationOfZipRanges = null;
        while(zipRangeIterator.hasNext()) {

            ZipRange thisRange = zipRangeIterator.next();
            final ZipRange thatRange = zipRangeIterator.hasNext() ? zipRangeIterator.peek() : null;

            if(chainedCombinationOfZipRanges != null) {
                thisRange = chainedCombinationOfZipRanges;
            }

            if(thisRange.canCombine(thatRange)) {
                chainedCombinationOfZipRanges = thisRange.combine(thatRange);
            }
            else { //cannot combine adjacent ZipRanges -- add the product of combined ranges, or just this if no combined range
                if(chainedCombinationOfZipRanges != null) {
                    returnZipRangeList.getZipRangeList().add(new ZipRange(chainedCombinationOfZipRanges));
                    chainedCombinationOfZipRanges = null;
                }
                else {
                    returnZipRangeList.getZipRangeList().add(new ZipRange(thisRange));
                }
            }
        }

        return returnZipRangeList;
    }

    public List<ZipRange> getZipRangeList() {
        return zipRangeList;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").skipNulls().join(this.getZipRangeList());
    }
}
