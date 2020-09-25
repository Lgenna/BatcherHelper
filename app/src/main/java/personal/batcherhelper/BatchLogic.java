package personal.batcherhelper;

import android.util.Log;
import java.util.Arrays;

public class BatchLogic {

    private static final String TAG = "BatchLogic";

    public boolean isCurve = false;
    // 5 because there are always 3 blanks and 2 CCV's on each batch,
    //  regardless of sample counts, which I guess includes 0 samples...
    public int totalTubes = 5;
    private int availableTubes = 0;

    public int[] batchSamples = new int[12];
    // waterMDL,    waterPT,    waterShared, 
    // waterTP,     waterTKN,   waterExtra,
    // soilMDL,     soilPT,     soilShared, 
    // soilTP,      soilTKN,    soilExtra

    private int[] batchQC = new int[]{
            3, 2, 0, 0, 0,
            0, 0, 0, 0, 0};
    //  blank,      CCV,        curvePoints,    waterLCS,   waterMS,
    //  waterMSD,   soilBlank,  soilLCS,        soilMS,     soilMSD

    private String[] batchSamplesNames = new String[]{
            " x Water MDL",     " x Water PT",  " x Shared Water",  " x Water TP",      " x Water TKN",
            " x Water Extra",   " x Soil MDL",  " x Soil PT",       " x Soil Shared",   " x Soil TP",
            " x soil TKN", " x soil Extra"};


    private String[] batchQCNames = new String[]{
            " x Blank",     " x CCV",           " x Curve Points",  " x Water LCS", " x Water MS",
            " x Water MSD", " x Soil Blanks",   " x Soil LCS",      " x Soil MS",   " x Soil MSD"};

    public void setTotalTubes(int totalTubes) {
        this.totalTubes = totalTubes;
        this.availableTubes = totalTubes;
    }
    
    public int[] getBatchQC() {
        return batchQC;
    }
    
    public int[] getBatchSamples() {
        return batchSamples;
    }
    
    public void setBatchSamplesValue(int index, int givenValue) {
        this.batchSamples[index] = givenValue;
    }

    public int getBatchSize() {
        return totalTubes - availableTubes;
    }

    public int getAvailableTubes() {
        return availableTubes;
    }

    public String[] getBatchSamplesNames() {
        String[] copiedArray = Arrays.copyOf(batchSamplesNames, batchSamplesNames.length);
        Arrays.setAll(copiedArray, i -> copiedArray[i].replace(" x ", ""));
        return copiedArray;
    }

    public String[] getBatchQCNames() {
        String[] copiedArray = Arrays.copyOf(batchQCNames, batchQCNames.length);
        Arrays.setAll(copiedArray, i -> copiedArray[i].replace(" x ", ""));
        return copiedArray;
    }

    public void setCurvePoints(boolean addPoints) {
        if (addPoints) {
            this.batchQC[2] = 7;
        } else {
            this.batchQC[2] = 0;
        }
    }
    
    public void resetAllFields() {
        Arrays.fill(batchQC, 0);
        Arrays.fill(batchSamples, 0);
        batchQC[0] = 3;
        batchQC[1] = 2;
    }

    /**
     * Prints the output of the batch in a squishy-human readable format.
     *
     * Mainly for test purposes as it returns a string
     */
    private String batchPrinter() {

        StringBuilder stringy = new StringBuilder();

        for(int i = 0; i < batchQC.length; i++) {
            if (batchQC[i] > 0) {
                stringy.append(batchQC[i]).append(batchQCNames[i]).append("\n");
            }
        }

        for(int i = 0; i < batchSamples.length; i++) {
            if (batchSamples[i] > 0) {
                stringy.append(batchSamples[i]).append(batchSamplesNames[i]).append("\n");
            }
        }

        stringy.append("\n");

        stringy.append("Available Tubes: ").append(availableTubes).append("\n");
        stringy.append("Tubes Needed: ").append(totalTubes - availableTubes);

        return stringy.toString();
    }

    /**
     * Finds the number of Spikes needed for TP/TKN samples using the larger of the two analysis
     *  types, works for both MS and MSD, granted that isMS is true or false accordingly
     *
     * @param tpAmount The number of TP samples
     * @param tknAmount The number of TKN samples
     * @param isMS If we are currently trying to find MS or MSD
     * @return the number of spikes needed (either MS or MSD, depending on the state of isMS)
     */
    private int findNeededSpikes(int tpAmount, int tknAmount, boolean isMS) {

        int largest = tpAmount;

        // multiply TKN by 2 because TKN needs another MS for every 10 samples, so double its "weight"
        if (isMS) tknAmount *= 2;

        if(tknAmount >= largest) largest = tknAmount;

        int totalSpikesNeeded = 0;
        for (int i = largest; i > 0; i -= 20) {
            totalSpikesNeeded++;
        }

        return totalSpikesNeeded;
    }

    /**
     * Finds Blank's and LCS's needed for soils. These have to be separate from water samples.
     *
     *       Granted, this should usually output 1 for both the blank and lcs,
     *        but there is the slim chance there might be more than 20 soils, and
     *        we don't know how to account for that, so just add another blank and lcs
     *
     * @param sampleAmount number of soil samples
     * @return returns a SoilQC object which stores the number of soil blanks and LCS's needed
     */
    private SoilQC findSoilQC (int sampleAmount) {
        int soilBlank = 0, soilLCS = 0;

        for (int i = sampleAmount; i > 0; i -= 20) {
            soilBlank++;
            soilLCS++;
        }

        SoilQC soilSample = new SoilQC();

        soilSample.setSoilBlank(soilBlank);
        soilSample.setSoilLCS(soilLCS);

        return soilSample;
    }

    public void QCMinusAvailableTubes() {

        int updatedAvailableTubes = totalTubes;
        int generalLCS = 0;
        int totalSoils = 0;
        
        // find genreal LCS's needed and even though soils are treated differently
        //  we're also including them in generalLCS, this is because of poor wording 
        //  in the SOP and we want to take things safe.
        for (int element : batchSamples) if (element > 0) generalLCS = 2;
        batchQC[3] = generalLCS;
      
        // find MS and MSD for waters
        batchQC[4] = findNeededSpikes(batchSamples[3], batchSamples[4], true);
        batchQC[5] = findNeededSpikes(batchSamples[3], batchSamples[4], false);
      
        // finds the total number of soils (ex. MDL's, TKN samples), used later
        for (int i = 6; i < batchSamples.length; i++) {
            totalSoils = batchSamples[i];
        }
        
        SoilQC returnedSoilQC = findSoilQC(totalSoils);
        batchQC[6] = returnedSoilQC.getSoilBlank();
        batchQC[7] = returnedSoilQC.getSoilLCS();

        // find MS and MSD for soils
        batchQC[8] = findNeededSpikes(batchSamples[9], batchSamples[10], true);
        batchQC[9] = findNeededSpikes(batchSamples[9], batchSamples[10], false);

        for (int value : batchQC) {
            updatedAvailableTubes -= value;
        }

        for (int batchSample : batchSamples) {
            updatedAvailableTubes -= batchSample;
        }

        updatedAvailableTubes += (batchSamples[2] * 2); // waterShared
        updatedAvailableTubes += (batchSamples[8] * 2); // soilShared

        availableTubes = updatedAvailableTubes;
    }

    /**
     * This method finds the maximum number of samples you can have for a specific sample type
     *  given (only water samples however). This uses recursion till it generates a number less
     *  or equal to the number of available tubes + stored sample amounts.
     *
     * @param maxSampleAmount the maximum number of samples you can have of that type, usually 50
     * @param isTP A check to see if the given sample type is TP or TKN
     * @return returns the maximum number of samples you can have minus needed matrix spikes and
     *  matrix spike duplicates, these are added later.
     */

    public int findMaxAllowedSample(int maxSampleAmount, boolean isTP) {
        
        int waterNeededMS, waterNeededMSD;
        int numTP = 0, numTKN = 0;
        int storedMS = batchQC[4];
        int storedMSD = batchQC[5];

        if (isTP) {
            numTP = maxSampleAmount;
        } else {
            numTKN = maxSampleAmount;
        }

        // compute the needed number of MS and MSD's for the given number of samples
        waterNeededMS = findNeededSpikes(numTP, numTKN, true);
        waterNeededMSD = findNeededSpikes(numTP, numTKN, false);

        int waterLCS = 0;

        // We have to do a special case to see if there are no LCS's added yet.
        //  They were not "officially" added yet because there are no samples, but you
        //  can't "officially" add them here because the user might cancel the
        //  numberPicker dialog window.
        if (batchQC[3] == 0) {
            waterLCS += 2;
        }

        // One beefy check of an if statement that makes a recursive call if false.
        //  Mainly checks generated values to ones already stored
        if ((waterNeededMS + waterNeededMSD + maxSampleAmount + waterLCS) <=
                availableTubes + storedMS + storedMSD) {
            return maxSampleAmount;
        } else {
            return findMaxAllowedSample(maxSampleAmount - 1, isTP);
        }
    }

    // TODO maximum soils findMaxAllowedSample
}
