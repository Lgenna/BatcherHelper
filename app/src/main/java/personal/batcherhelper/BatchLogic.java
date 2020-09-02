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
    public int totalTubes = 50;
    private int availableTubes = totalTubes;


    public int[] batchSamples = new int[] {
            waterMDL,               waterPT,                waterShared,
            waterTP - waterShared,  waterTKN - waterShared, waterExtra,
            soilMDL,                soilPT,                 soilShared,
            soilTP - soilShared,    soilTKN - soilShared,   soilExtra};

    private int[] batchQC = new int[]{
            3, 2, 0, 2, 0,
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
        int runningTotal = 0;
//        int availableTubes = totalTubes;

        // local pre-calculated variables
        int waterTpMinusShared = batchSamples[3] - batchSamples[2];
        int waterTknMinusShared = batchSamples[4] - batchSamples[2];

        // check to see if there is a curve needed
        int curvePoints = 0;
        if (isCurve) {
            curvePoints = 7;
        }

        // finds MS and MSD needed for waters
        int waterNeededMS = findNeededSpikes(batchSamples[3], batchSamples[4], true);
        int waterNeededMSD = findNeededSpikes(batchSamples[3], batchSamples[4], false);

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
        for (int element : batchQC) {
            runningTotal += element;
        }


        // add the rest of the computed values
        runningTotal += waterTpMinusShared + waterTknMinusShared;
        runningTotal += batchSamples[2]; // waterShared
        runningTotal += curvePoints;
//        runningTotal += (waterNeededMS + waterNeededMSD);
//        runningTotal += (soilBlank + soilLCS);
//        runningTotal += (soilNeededMS + soilNeededMSD);

        // now check to see if all of that is greater than 0, otherwise something went wrong and
        // there are now negative tubes available
        if ((totalTubes - runningTotal) >= 0) {

            batchQC[2] = curvePoints;

            batchQC[4] = waterNeededMS;
            batchQC[5] = waterNeededMSD;

//            batchQC[6] = soilBlank;
//            batchQC[7] = soilLCS;
//            batchQC[8] = soilNeededMS;
//            batchQC[9] = soilNeededMSD;

            for (int value : batchQC) {
                availableTubes -= value;
            }

            for (int i = 1; i < batchSamples.length; i++) {
                if (i < 3 || i > 4) {
                    availableTubes -= batchSamples[i];
                } else {
                    availableTubes -= (batchSamples[i] - batchSamples[2]);
                }
            }

//            setAvailableTubes(availableTubes);

//            return availableTubes;

        } else {
            Log.i(TAG, "Not enough room | Need : " + runningTotal + " > Have : " + availableTubes);
//            return -999;
        }
    }

    /**
     * Still a work in progress but theoretically this finds the maximum number of samples you can
     *  have for a specific sample type given. This uses recursion till it generates a number less
     *  or equal to the number of available tubes.
     *
     * @param maxSampleAmount the maximum number of samples you can have of that type, usually 50
     * @param isTP temporary check to see if the given sample type is TP or TKN
     * @return returns the maximum number of samples you can have minus needed matrix spikes and
     *  matrix spike duplicates
     */

    public int findMaxAllowedSample(int maxSampleAmount, boolean isTP) {
        int waterNeededMS, waterNeededMSD;
        int numTP = 0, numTKN = 0;

        if (isTP) {
            numTP = maxSampleAmount;
        } else {
            numTKN = maxSampleAmount;
        }

        waterNeededMS = findNeededSpikes(numTP, numTKN, true);
        waterNeededMSD = findNeededSpikes(numTP, numTKN, false);

        if ((waterNeededMS + waterNeededMSD + maxSampleAmount) <= availableTubes) {
            return maxSampleAmount;
        } else {
            return findMaxAllowedSample(maxSampleAmount - 1, isTP);
        }
    }

    // TODO maximum soils
}