package personal.batcherhelper;

public class BatchLogic {

//    private int[] batchSamples, batchQC;

    public BatchLogic() {
//        this.batchSamples = batchSamples;
        //  numTubesAvailable, numWaterMDLs, numSoilMDLs, NumWaterPTs, numSoilPTs,
        //      numShared, numTP, numTKN, numExtraNeeded
//        this.batchQC = batchQC;
        // blank, ccv, curvePoints, ms, msd


    }

    int[] batchSamples = new int[]{0, 0, 0, 0, 0, 6, 23, 11, 0};
    //  numTubesAvailable, numWaterMDLs, numSoilMDLs, NumWaterPTs, numSoilPTs,
    //      numShared, numTP, numTKN, numExtraNeeded
    int[] batchQC = new int[]{3, 2, 0, 0, 0, 2};
    // blank, ccv, curvePoints, ms, msd, LCS

    public int[] getBatchSamples() {
        return batchSamples;
    }

    public void setBatchSamples(int[] batchSamples) {
        this.batchSamples = batchSamples;
    }

    public int[] getBatchQC() {
        return batchQC;
    }

    public void setBatchQC(int[] batchQC) {
        this.batchQC = batchQC;
    }

    private String batchPrinter() {

        StringBuilder stringy = new StringBuilder();

        stringy.append("3 x Blank \n");
        stringy.append("2 x CCV \n");
        stringy.append("2 x LCS \n");
        stringy.append(batchQC[3]).append(" x MS \n");
        stringy.append(batchQC[4]).append(" x MSD \n");
        stringy.append(batchSamples[5]).append(" x Shared \n");
        stringy.append(batchSamples[6] - batchSamples[5]).append(" x TP \n");
        stringy.append(batchSamples[7] - batchSamples[5]).append(" x TKN \n");

        stringy.append("\n");

        stringy.append("tubesFree: ").append(batchSamples[0]);

        return stringy.toString();
    }

    private int findExtraMS(int sampleAmount, boolean isTKN) {
        // int extraMSNeeded = ((sampleAmount % 100) / 10);
        // int sharedAndExtraMS = sampleAmount + extraMSNeeded;

        int extraMSNeeded = 0;
        int samplesPerMS = 20;

        if (isTKN) {
            samplesPerMS = 10;
        }
        if (sampleAmount > 0) {
            extraMSNeeded++;
            for (int i = sampleAmount; i > samplesPerMS; i -= samplesPerMS) {
                extraMSNeeded++;
            }
        }


//        int sampAmountAndExtraMS = extraMSNeeded + sampleAmount;

        // if (sampAmountAndExtraMS <= tubesFree) {
        //     System.out.println("Added " + sampleAmount + " samples (isTKN : " + isTKN + ") and " + extraMSNeeded + " MS's to the batch.");
        // } else {
        //     System.out.println("Not enough room for " + sampAmountAndExtraMS + " samples and " + extraMSNeeded + " MS's.");
        // }

        return extraMSNeeded;
    }


    private void workDooer() {

        if (batchSamples[5] != 0) {
            batchQC[3] += findExtraMS(batchSamples[5], true);
        } else {
            batchQC[3] += findExtraMS(batchSamples[7] - batchSamples[5], true);
        }
        batchQC[3] += findExtraMS(batchSamples[6] - batchSamples[5], false);
        batchSamples[0] -= batchQC[3];

        if (batchSamples[5] != 0) {
            batchQC[4] += findExtraMSD(batchSamples[5]);
        } else {
            batchQC[4]+= findExtraMSD(batchSamples[7] - batchSamples[5]);
        }
        batchQC[4] += findExtraMSD(batchSamples[6] - batchSamples[5]);




    }

    private int findExtraMSD(int sampleAmount) {
        int extraMSDNeeded = 0;

        for (int i = sampleAmount; i > 0; i -= 20) {
            extraMSDNeeded++;
        }


        return extraMSDNeeded;
    }

    public void runner() {
        workDooer();
        System.out.println(batchPrinter());

    }
}
