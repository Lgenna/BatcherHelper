package personal.batcherhelper;

import java.util.Arrays;

public class BatchLogic {

    // 1 MSD can supply up to 20 Water TP and 20 Water TKN,
    //  soils are entirely separate from water but follow the same conditions.

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

//    public int waterShared = 1;
//    public int waterMDL = 1;
//    public int waterPT = 0;
//    public int waterTP = 20;
//    public int waterTKN = 12;
//    public int waterExtra = 0;
//
//    public int soilShared = 0;
//    public int soilMDL = 0;
//    public int soilPT = 0;
//    public int soilTP = 0;
//    public int soilTKN = 0;
//    public int soilExtra = 0;

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

    /*
        Prints the output of the batch in a squishy-human readable format.
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

    /*
       Finds the number of MS's needed for TP/TKN samples using the larger
        of the two analysis types
    */
    private int findExtraMS(int tpAmount, int tknAmount) {

        boolean isTKN = false;
        int largest = tpAmount;

        // multiply TKN by 2 because TKN needs another MS for every 10 samples so double its weight
        if((tknAmount * 2) >= tpAmount) {
            isTKN = true;
            largest = tknAmount * 2;
        }

        System.out.println("largest : " + largest);

        // 20 TP samples per MS
        // 10 TKN samples per MS
        int samplesPerMS = 20;
//        if (isTKN) samplesPerMS = 10;

        int totalMSNeeded = 0;
        if (largest > 0) {
            totalMSNeeded++;
            for (int i = largest; i > samplesPerMS; i -= samplesPerMS) {
                totalMSNeeded++;
            }
        }

        return totalMSNeeded;
    }

    /*
       Finds the number of MSD's needed for TP/TKN samples using the larger
        of the two analysis types
    */
    private int findExtraMSD(int tpAmount, int tknAmount) {

        int largest = tpAmount;
        if (tknAmount >= largest) largest = tknAmount;

        int totalMSDNeeded = 0;
        for (int i = largest; i > 0; i -= 20) {
            totalMSDNeeded++;
        }

        return totalMSDNeeded;
    }

    /*
      Finds Blank's and LCS's needed for soils. These have to be separate from water samples.

      Granted, this should usually output 1 for both the blank and lcs,
       but there is the slim chance there might be more than 20 soils, and
       we dont know how to account for that, so just add another blank and lcs
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




    /*
      Adds everything together prior to adding data to arrays to ensure everything is
       less than the total number of tubes available.
    */
    public int performLogic() {

        // running total of added values
        int runningTotal = 0;
        int availableTubes = totalTubes;

        // local pre-calculated variables
        int waterTpMinusShared = batchSamples[3] - batchSamples[2];
        int waterTknMinusShared = batchSamples[4] - batchSamples[2];

        // check to see if there is a curve needed
        int curvePoints = 0;
        if (isCurve) {
             curvePoints = 7;
        }

        // finds MS and MSD needed for waters
        int waterNeededMS = findExtraMS(waterTpMinusShared, waterTknMinusShared);
        int waterNeededMSD = findExtraMSD(waterTpMinusShared, waterTknMinusShared);



        // finds the total number of soils, used later
        int totalSoils = 0;
        for (int i = 6; i < batchSamples.length; i++) {
            totalSoils = batchSamples[i];
        }

        // get soil QC
        SoilQC returnedSoilQC = findSoilQC(totalSoils);
        int soilBlank = returnedSoilQC.getSoilBlank();
        int soilLCS = returnedSoilQC.getSoilLCS();

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

//        System.out.println("runningTotal : " + runningTotal);

        if ((totalTubes - runningTotal) >= 0) {

            batchQC[2] = curvePoints;

            batchQC[4] = waterNeededMS;
            batchQC[5] = waterNeededMSD;

            batchQC[6] = soilBlank;
            batchQC[7] = soilLCS;
//            batchQC[8] = soilNeededMS;
//            batchQC[9] = soilNeededMSD;

            // subtract from totalTubes (batchSamples[0])

            for (int value : batchQC) {
                availableTubes -= value;
            }

            for (int i = 1; i < batchSamples.length; i++) {
                availableTubes -= batchSamples[i];
            }

            return availableTubes;

        } else {
            System.out.println("Not enough room | Need : " + runningTotal + " > Have : " + availableTubes);
            return -1;
        }


    }
}
