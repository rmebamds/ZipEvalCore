import com.rcmccormick.williamssonoma.domain.ZipRange;
import com.rcmccormick.williamssonoma.domain.ZipRangeList;
import com.rcmccormick.williamssonoma.domain.exceptions.ZipRangeInstantiationException;
import org.junit.Assert;
import org.junit.Test;

public class ZipRangeTests {

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

    @Test(expected = Exception.class)
    public void testAlmostValidInput() throws Exception {
        String theInput = "[94133,94133] [94200,94299] [94600,94699] [93200 94134]"; //<--- missing comma on last one
        new ZipRangeList(theInput);
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
}
