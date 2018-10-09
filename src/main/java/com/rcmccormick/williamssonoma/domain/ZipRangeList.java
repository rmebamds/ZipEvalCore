package com.rcmccormick.williamssonoma.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.rcmccormick.williamssonoma.domain.exceptions.ZipRangeInstantiationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipRangeList {

    private List<ZipRange> zipRangeList = new ArrayList<ZipRange>();

    public ZipRangeList() {}

    /**
     * Copy Constructor
     * @param zipRangeList
     */
    public ZipRangeList(ZipRangeList zipRangeList) {
        for(ZipRange zr : zipRangeList.getZipRangeList()) {
            this.getZipRangeList().add(new ZipRange(zr));
        }
    }

    /**
     *
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
    public ZipRangeList(String stringedInput) throws Exception {
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
            Collections.sort(this.zipRangeList, ZipRange.LOWER_BOUND_COMPARATOR);
        }
        catch(Exception e) {
            throw new Exception("Invalid input to ZipRangeList constructor");
        }
    }

    /**
     * Combine all internal ZipRanges into the equivalence class of zip ranges
     * is not destructive
     * @return - new copy of resulting list which is subset of this.zipRangeList
     */
    public ZipRangeList combine() {
        final ZipRangeList newZipRangeList = new ZipRangeList();

        ZipRangeList copiedList = new ZipRangeList(this);

        ZipRange lastCombined = null;
        for(int i = 0 ; i < this.zipRangeList.size() ; i++) {
            if(i + 1 < this.zipRangeList.size()) {
                ZipRange thisRange = lastCombined == null ? copiedList.getZipRangeList().get(i) : lastCombined;
                ZipRange thatRange = copiedList.getZipRangeList().get(i + 1);
                if(thisRange.containedWithin(thatRange)) {
                    try {
                        lastCombined = thisRange.combine(thatRange);
                    }
                    catch(Exception e) {

                    }
                }
                else {
                    if(lastCombined != null) {
                        newZipRangeList.getZipRangeList().add(new ZipRange(lastCombined));
                    }
                    else {
                        newZipRangeList.getZipRangeList().add(new ZipRange(thisRange));
                    }
                    lastCombined = null;
                }
            }
            else {
                if(lastCombined == null) {
                    newZipRangeList.getZipRangeList().add(new ZipRange(copiedList.getZipRangeList().get(i)));
                }
            }
        }

        if(lastCombined != null) {
            newZipRangeList.getZipRangeList().add(new ZipRange(lastCombined));
        }

        return newZipRangeList;
    }

    public List<ZipRange> getZipRangeList() {
        return zipRangeList;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").skipNulls().join(this.getZipRangeList());
    }
}
