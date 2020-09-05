package personal.batcherhelper;

import android.util.Log;
import java.util.Arrays;

public class BatchLogic {

    private static final String TAG = "BatchLogic";

    /**
     * These public ints can be set anywhere within the program, however it is not friendly
     *  towards general "for" loops or other general methods, therefore we edit the array
     *  directly (even though the array was designed to be a final resting place for sample
     *  amounts and the int's were temporary values used to check if they can fit before being
     *  put into the array, but that's besides the point)
     */

    public int waterShared = 0;
    public int waterMDL = 0;
    public int waterPT = 0;
    public int waterTP = 0;
    public int waterTKN = 0;
    public int waterExtra = 0;

    public int soilShared = 0;
    public int soilMDL = 0;
    public int soilPT = 0;
    public int soilTP = 0;
    public int soilTKN = 0;
    public int soilExtra = 0;

    public boolean isCurve = false;
    public int totalTubes = 0;
    private int availableTubes = 0;


    public int[] batchSamples = new int[] {
            waterMDL,               waterPT,                waterShared,
            waterTP - waterShared,  waterTKN - waterShared, waterExtra,
            soilMDL,                soilPT,                 soilShared,
            soilTP - soilShared,    soilTKN - soilShared,   soilExtra};

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
    
    public int[] getBatchSamples() {
        return batchSamples;
    }

    public void setBatchSamplesValue(int index, int givenValue) {
        this.batchSamples[index] = givenValue;
    }

    public void setBatchSamples(int[] batchSamples) {
        this.batchSamples = batchSamples;
    }

    public int getBatchSize() {
        return totalTubes - availableTubes;
    }

    public int[] getBatchQC() {
        return batchQC;
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

    /**
     * Prints the output of the batch in a squishy-human readable format.
     *
     * Mainly for test purposes as it is designed to print only to the console
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

        // multiply TKN by 2 because TKN needs another MS for every 10 samples, so double its weight
        if (isMS) {
            tknAmount *= 2;
        }

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

    /**
     Adds everything together before finalizing just as a precautionary measure
     */

    public void performLogic() {

        // running total of added values
//        int runningTotal = 0;

        // local pre-calculated variables
//        int waterTpMinusShared = batchSamples[3] - batchSamples[2];
//        int waterTknMinusShared = batchSamples[4] - batchSamples[2];

        // check to see if there is a curve needed
//        if (isCurve) {
//            batchQC[2] = 7;
//        }

        // finds MS and MSD needed for waters


        int waterLCS = 0;

        // add LCS's for waters and soils if present
        if (batchSamples[3] + batchSamples[4] > 0) {

            // TODO add rest of checks to see if water / soil samples are present
            //  keep in mind that PT's, MDL's, and Extras are treated differently than samples

            waterLCS += 2;
        }

        batchQC[3] = waterLCS;

        // finds the total number of soils, used later
        int totalSoils = 0;
        for (int i = 6; i < batchSamples.length; i++) {
            totalSoils = batchSamples[i];
        }

        // get soil QC
        SoilQC returnedSoilQC = findSoilQC(totalSoils);
//        int soilBlank = returnedSoilQC.getSoilBlank();
//        int soilLCS = returnedSoilQC.getSoilLCS();

        // finds MS and MSD needed for soils
//        int soilNeededMS = findExtraMS(soilTP, soilTKN); //  CHANGE TO ARRAY CALLS
//        int soilNeededMSD = findExtraMSD(soilTP, soilTKN);//  CHANGE TO ARRAY CALLS

        // add up everything in the batchQC array
//        for (int element : batchQC) {
//            runningTotal += element;
//        }


        // add the rest of the computed values
//        runningTotal += waterTpMinusShared + waterTknMinusShared;
//        runningTotal += batchSamples[2]; // waterShared
//        runningTotal += curvePoints;
//        runningTotal += waterLCS;
//        runningTotal += (waterNeededMS + waterNeededMSD);
//        runningTotal += (soilBlank + soilLCS);
//        runningTotal += (soilNeededMS + soilNeededMSD);

        // now check to see if all of that is greater than 0, otherwise something went wrong and
        // there are now negative tubes available
//        if ((totalTubes - runningTotal) >= 0) {

            int updatedAvailableTubes = totalTubes;

            batchQC[4] = findNeededSpikes(batchSamples[3], batchSamples[4], true);
            batchQC[5] = findNeededSpikes(batchSamples[3], batchSamples[4], false);

            for (int value : batchQC) {
                updatedAvailableTubes -= value;
            }

            for (int batchSample : batchSamples) {
                updatedAvailableTubes -= batchSample;
            }

            updatedAvailableTubes += (batchSamples[2] * 2); // waterShared
            updatedAvailableTubes += (batchSamples[8] * 2); // soilShared

            availableTubes = updatedAvailableTubes;

//            setAvailableTubes(availableTubes);

//            return availableTubes;

//        } else {
//            Log.i(TAG, "Not enough room | Need : " + runningTotal + " > Have : " + newAvailableTubes);
////            return -999;

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
