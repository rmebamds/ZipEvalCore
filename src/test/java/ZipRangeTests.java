import com.rcmccormick.williamssonoma.domain.ZipRange;
import com.rcmccormick.williamssonoma.domain.ZipRangeList;
import com.rcmccormick.williamssonoma.domain.exceptions.ZipRangeInstantiationException;
import org.junit.Assert;
import org.junit.Test;

public class ZipRangeTests {

    @Test
    public void testSuccessfulConstruction() throws ZipRangeInstantiationException {
        String validInput = "[90210,95151]";
        ZipRange zipRange = new ZipRange(validInput);
        Assert.assertTrue(zipRange.getLowerBound() == 90210);
        Assert.assertTrue(zipRange.getUpperBound() == 95151);
    }

    @Test
    public void testSuccessfulConstructionLotsOfSpaces() throws ZipRangeInstantiationException {
        String validInput = "     [  90210   ,   95151    ]    ";
        ZipRange zipRange = new ZipRange(validInput);
        Assert.assertTrue(zipRange.getLowerBound() == 90210);
        Assert.assertTrue(zipRange.getUpperBound() == 95151);
    }

    @Test(expected = ZipRangeInstantiationException.class)
    public void testConstructionException1() throws ZipRangeInstantiationException {
        String garbageString = "aksfjhaskfjakjflasldfkjhalsfjkhasdfasf";
        new ZipRange(garbageString);
    }

    @Test(expected = ZipRangeInstantiationException.class)
    public void testConstructionException2() throws ZipRangeInstantiationException {
        String invalidRange = "[95226,94399]";
        new ZipRange(invalidRange);
    }

    @Test(expected = ZipRangeInstantiationException.class)
    public void testConstruction4Digit() throws ZipRangeInstantiationException {
        String pairWith4DigitLowerBound = "[1234,99999]";
        new ZipRange(pairWith4DigitLowerBound);
    }

    @Test(expected = ZipRangeInstantiationException.class)
    public void testConstruction6Digit() throws ZipRangeInstantiationException {
        String pairWith4DigitLowerBound = "[11111,123456]";
        new ZipRange(pairWith4DigitLowerBound);
    }

    @Test
    public void testSuccessfulRangeListConstruction() throws Exception {
        String theInput = "[94133,94133] [94200,94299] [94600,94699] [93200,94134]";
        ZipRangeList zipRangeList = new ZipRangeList(theInput);

        Assert.assertTrue(zipRangeList.getZipRangeList().size() == 4);
    }

    @Test(expected = Exception.class)
    public void testAlmostValidInput() throws Exception {
        String theInput = "[94133,94133] [94200,94299] [94600,94699] [93200 94134]"; //<--- missing comma on last one
        new ZipRangeList(theInput);
    }

    /**
     * Taken from my development notes inside ZipRange class:
     *
     //this range for all below test cases: [10,15]

     // (1) that: [1,20] -- all outside, lower low, higher upper
     // (2) that: [11,12] -- all inside, higher lower bound, lower upper bound
     // (3) that: [11,16] -- lower inside, outside extends - higher lower, higher upper
     // (4) that: [6,12] -- that lower low, lower upper

     //cannot be combined if that upper is lower than this lower, or that lower is higher than this upper
     // (5) that: [1,2]
     // (6) that: [20,25]
     */
    @Test
    public void testCombining() {

        ZipRange thisRange = new ZipRange("[11111,15000]");

        //Can Combine With These
        ZipRange thatRangeTestCaseAllOutside = new ZipRange("[10000,15555]");
        ZipRange thatRangeTestCaseAllInside = new ZipRange("[12222,14999]");
        ZipRange thatRangeTestCaseHigherLowerHigherUpper = new ZipRange("[12222,15555]");
        ZipRange thatRangeTestCaseLowerLowLowerUpper = new ZipRange("[10000,12222]");

        //Cannot Combine With These
        ZipRange thatLowerLowLowerUpper = new ZipRange("[10000,10002]");
        ZipRange thatHigherLowerHigherUpper = new ZipRange("[15005,15555]");
        ZipRange theNullZipRange = null;

        //Can Combine Assertions
        Assert.assertTrue(thisRange.canCombine(thatRangeTestCaseAllOutside));
        Assert.assertTrue(thisRange.canCombine(thatRangeTestCaseAllInside));
        Assert.assertTrue(thisRange.canCombine(thatRangeTestCaseHigherLowerHigherUpper));
        Assert.assertTrue(thisRange.canCombine(thatRangeTestCaseLowerLowLowerUpper));

        //Cannot combine assertions
        Assert.assertFalse(thisRange.canCombine(thatLowerLowLowerUpper));
        Assert.assertFalse(thisRange.canCombine(thatHigherLowerHigherUpper));

        //Null combine
        Assert.assertFalse(thisRange.canCombine(theNullZipRange));
    }

    @Test(expected = Exception.class)
    public void testCombineException() throws Exception {
        ZipRange thisRange = new ZipRange("[11111,15000]");
        ZipRange thatLowerLowLowerUpper = new ZipRange("[10000,10002]");

        thisRange.combine(thatLowerLowLowerUpper);
    }

    /**
     *
     //If the input = [94133,94133] [94200,94299] [94226,94399]
     //Then the output should be = [94133,94133] [94200,94399]
     * @throws Exception
     */
    @Test
    public void testCases() throws Exception {

        ZipRangeList zipRangeList = new ZipRangeList("[94133,94133] [94200,94299] [94226,94399]");
        ZipRangeList combinedAll = zipRangeList.combine();

        Assert.assertTrue(combinedAll.getZipRangeList().size() == 2);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getLowerBound() == 94133);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getUpperBound() == 94133);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getLowerBound() == 94200);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getUpperBound() == 94399);
    }

    /**
     *
     //If the input = [94133,94133] [94200,94299] [94600,94699]
     //Then the output should be = [94133,94133] [94200,94299] [94600,94699]

     * @throws Exception
     */
    @Test
    public void testCase2() throws Exception {

        ZipRangeList zipRangeList = new ZipRangeList("[94133,94133] [94200,94299] [94600,94699]");
        ZipRangeList combinedAll = zipRangeList.combine();

        Assert.assertTrue(combinedAll.getZipRangeList().size() == 3);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getLowerBound() == 94133);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getUpperBound() == 94133);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getLowerBound() == 94200);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getUpperBound() == 94299);
        Assert.assertTrue(combinedAll.getZipRangeList().get(2).getLowerBound() == 94600);
        Assert.assertTrue(combinedAll.getZipRangeList().get(2).getUpperBound() == 94699);
    }

    @Test
    public void testCase3() throws Exception {

        ZipRangeList zipRangeList = new ZipRangeList("[94133,94133] [94200,94299] [94600,94699] [93200,94134]");
        ZipRangeList combinedAll = zipRangeList.combine();

        Assert.assertTrue(combinedAll.getZipRangeList().size() == 3);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getLowerBound() == 93200);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getUpperBound() == 94134);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getLowerBound() == 94200);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getUpperBound() == 94299);
        Assert.assertTrue(combinedAll.getZipRangeList().get(2).getLowerBound() == 94600);
        Assert.assertTrue(combinedAll.getZipRangeList().get(2).getUpperBound() == 94699);
    }

    @Test
    public void testCase4() throws Exception {

        ZipRangeList zipRangeList = new ZipRangeList("[10000,10000] [10001,90000] [10002,18989] [10010,91111] [35000,91112]");
        ZipRangeList combinedAll = zipRangeList.combine();

        Assert.assertTrue(combinedAll.getZipRangeList().size() == 2);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getLowerBound() == 10000);
        Assert.assertTrue(combinedAll.getZipRangeList().get(0).getUpperBound() == 10000);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getLowerBound() == 10001);
        Assert.assertTrue(combinedAll.getZipRangeList().get(1).getUpperBound() == 91112);
    }
}
