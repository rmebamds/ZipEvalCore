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

    private List<ZipRange> zipRangeList = new ArrayList<>();

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
            this.zipRangeList.sort(ZipRange.LOWER_BOUND_COMPARATOR);
        }
        catch(Exception e) {
            throw new Exception("Invalid input to ZipRangeList constructor");
        }
    }

    /**
     * Combine all internal ZipRanges into the equivalence class of zip ranges
     *  not destructive or mutative
     * @return - new copy of resulting list which is subset of this.zipRangeList
     */
    public ZipRangeList combine() throws Exception {
        
        final ZipRangeList returnZipRangeList = new ZipRangeList();

        ZipRange lastCombined = null;
        for(int i = 0 ; i < this.zipRangeList.size() ; i++) {
            if(i + 1 < this.zipRangeList.size()) {
                final ZipRange thisRange = lastCombined == null ? this.zipRangeList.get(i) : lastCombined;
                final ZipRange thatRange = this.zipRangeList.get(i + 1);
                if(thisRange.canCombine(thatRange)) {
                    try {
                        lastCombined = thisRange.combine(thatRange);
                    }
                    catch(Exception e) {
                        throw new Exception("Failure to combine range , ranges incompatible");
                    }
                }
                else { //cannot combine adjacent ZipRanges

                    if(lastCombined != null) { //we had already started a chain of combining
                        returnZipRangeList.getZipRangeList().add(new ZipRange(lastCombined));
                    }
                    else { //just add the current index
                        returnZipRangeList.getZipRangeList().add(new ZipRange(thisRange));
                    }
                    //reset last combined
                    lastCombined = null;
                }
            }
            else { //didn't combine on last iteration, add last ZipRange to list as is
                if(lastCombined == null) {
                    returnZipRangeList.getZipRangeList().add(new ZipRange(this.zipRangeList.get(i)));
                }
            }
        }

        if(lastCombined != null) { //hasn't been added yet if last act was to combine a range, add into return list
            returnZipRangeList.getZipRangeList().add(new ZipRange(lastCombined));
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
