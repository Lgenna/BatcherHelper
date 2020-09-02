package personal.batcherhelper.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import personal.batcherhelper.BatchItem;
import personal.batcherhelper.BatchItemNames;
import personal.batcherhelper.BatchLogic;
import personal.batcherhelper.BatchViewCustomAdapter;
import personal.batcherhelper.R;

public class TP_TKNBatchFragment extends Fragment implements View.OnClickListener, NumberPicker.OnValueChangeListener{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "TP_TKNBatchFragment";

    private View view;
    Context context;

    BatchLogic batchLogic = new BatchLogic();

    // get sample names and store it because it never changes, sample amounts do however
    String[] sBatchSamples = batchLogic.getBatchSamplesNames();
    String[] sBatchQC = batchLogic.getBatchQCNames();

    boolean needsCurve;

    TextView vNumTubesAvailable, vNumExtra, vNumShared, vNumTP, vNumTKN,
            vNumWaterPTs, vNumSoilPTs, vNumWaterMDLs, vNumSoilMDLs, vNumTubesLeft,
            vCurrentBatchSize;
    CheckBox vNeedsCurve;

    // items for recycler View
    ArrayList<BatchItem> mBatchItems = new ArrayList<>();

    public static TP_TKNBatchFragment newInstance() {
        TP_TKNBatchFragment fragment = new TP_TKNBatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }



    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tp_tkn_batch, container, false);

        batchLogic.performLogic();
//        int[] localBatchSamples = batchLogic.getBatchSamples();

        vNumTubesAvailable = view.findViewById(R.id.numTubesAvailable);
        vNumTubesAvailable.setOnClickListener(this); // calling onClick() method
        vNumExtra = view.findViewById(R.id.numExtra);
        vNumExtra.setOnClickListener(this);
        vNumShared = view.findViewById(R.id.numShared);
        vNumShared.setOnClickListener(this);
        vNumTP = view.findViewById(R.id.numTP);
        vNumTP.setOnClickListener(this);
        vNumTKN = view.findViewById(R.id.numTKN);
        vNumTKN.setOnClickListener(this);
        vNumWaterPTs = view.findViewById(R.id.numWaterPTs);
        vNumWaterPTs.setOnClickListener(this);
        vNumSoilPTs = view.findViewById(R.id.numSoilPTs);
        vNumSoilPTs.setOnClickListener(this);
        vNumWaterMDLs = view.findViewById(R.id.numWaterMDLs);
        vNumWaterMDLs.setOnClickListener(this);
        vNumSoilMDLs = view.findViewById(R.id.numSoilMDLs);
        vNumSoilMDLs.setOnClickListener(this);
        vNeedsCurve = view.findViewById(R.id.isCurveNeeded);
        vNeedsCurve.setOnClickListener(this);
        vNumTubesLeft = view.findViewById(R.id.numOfTubesLeft);
        vCurrentBatchSize = view.findViewById(R.id.currentBatchSizeNumber);

        vCurrentBatchSize.setText(String.valueOf(batchLogic.getBatchSize()));

        initData();
        initRecyclerView();

//        getBatchSize();

        return view;
    }

    private void itemAdder(String itemName, boolean isQC, int itemCount, int indexInArray) {
        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();

        BatchItem item = new BatchItem();

        item.setmItem(itemName);
        item.setmIsQC(isQC);
        item.setmCount(itemCount);
        item.setmIndexInArray(indexInArray);

        mBatchItems.add(item);
    }

    private void initData() {

        // this adds items to the sample and qc arrayLists which get added to the recycler
        //  view...

        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();

        mBatchItems.clear(); // wipe before adding new data, crude and easy solution

        for (int i = 0; i < batchQC.length; i++) {
            // go through each type of item in the array
            if (batchQC[i] > 0) {
                // if its value is greater than 0, add it
                // it should... SHOULD, avoid adding the same value twice... should...

                itemAdder(sBatchQC[i], true, batchQC[i], i);
//                Log.i(TAG, "Added " + sBatchQC[i] + " to the ArrayList");
            }
        }

        for (int i = 1; i < batchSamples.length - 1; i++) {
            // go through each type of item in the array
            if (batchSamples[i] > 0) {
                // if its value is greater than 0, add it
                // it should... SHOULD, avoid adding the same value twice... should...

                if (i == 6 || i == 7) { // special case to subtract the shared samples from TP and TKN
                    itemAdder(sBatchSamples[i], false, batchSamples[i] - batchSamples[5], i);
//                    Log.i(TAG, "Added " + sBatchQC[i] + " to the ArrayList");
                } else {
                    itemAdder(sBatchSamples[i], false, batchSamples[i], i);
//                  Log.i(TAG, "Added " + sBatchQC[i] + " to the ArrayList");
                }
            }
        }
    }

    BatchViewCustomAdapter batchViewCustomAdapter;

    private void initRecyclerView() {
        RecyclerView mRecyclerView = view.findViewById(R.id.currentBatchView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        batchViewCustomAdapter = new BatchViewCustomAdapter(getContext(), mBatchItems);
        mRecyclerView.setAdapter(batchViewCustomAdapter);
    }


    // is onValueChange required within the class? If yes, what does it do?
//    int mostRecentNumber;

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//        mostRecentNumber = i1;
    }

    private void numberWheelDialog(int min, int max, int valueIndex, TextView calledView) {
        final Dialog dialog = new Dialog(context);

        int[] localBatchSamples = batchLogic.getBatchSamples();

        calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_blue_underline));
        dialog.setContentView(R.layout.fragment_number_picker);
        // add confirm and cancel buttons
        Button confirm = dialog.findViewById(R.id.confirm);
        Button cancel = dialog.findViewById(R.id.cancel);
        // make it so the only way to close the view it tapping on the "cancel" button
        dialog.setCancelable(false);
        final NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        // set the min value to the minimum number of samples you can use
        numberPicker.setMinValue(min);
        // set the max to the maximum number of samples you can have without going over the number of tubes
        numberPicker.setMaxValue(max);
        // set the current value of the number wheel to the last stored number
        numberPicker.setValue(localBatchSamples[valueIndex]);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        confirm.setOnClickListener(v -> {

            int confirmedValue = numberPicker.getValue();
            Log.i(TAG, "Received " + confirmedValue + " as the confirmed value.");

            // set whoever called the numberPicker to the confirmed value
            calledView.setText(String.valueOf(confirmedValue));

            // store the confirmed value within its valueIndex in the array
            batchLogic.setBatchSamplesValue(valueIndex, confirmedValue);
            Log.i(TAG, "Changed the number of " + sBatchSamples[valueIndex] + " to " + confirmedValue);

            // perform logic and set the number of tubes left
            batchLogic.performLogic();
            vNumTubesLeft.setText(String.valueOf(batchLogic.getAvailableTubes()));

            initData();
            // notify the recycler view that there is new data
            batchViewCustomAdapter.notifyDataSetChanged();

            // change the background of the clicked back to gray
            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            // dismiss the dialog
            dialog.dismiss();
        });
        dialog.show();

    }


    @Override
    public void onClick(View view) {

        int[] localBatchSamples = batchLogic.getBatchSamples();
        int availableTubes = batchLogic.getAvailableTubes();
        int maxAllowedSamples;

        switch (view.getId()) {
            case R.id.numTubesAvailable:
//                numberWheelDialog(0, 50, 999, vNumTubesAvailable);
                break;
            case R.id.numWaterMDLs:
                numberWheelDialog(0, availableTubes, 0, vNumWaterMDLs);
                break;
            case R.id.numSoilMDLs:
//                numberWheelDialog(0, 50, 999, vNumSoilMDLs);
                break;
            case R.id.numWaterPTs:
                numberWheelDialog(0, availableTubes, 1, vNumWaterPTs);
                break;
            case R.id.numSoilPTs:
//                numberWheelDialog(0, 50, 999, vNumSoilPTs);
                break;
            case R.id.numShared:
                // only allow the user to enter in a value IF they have both TP and TKN already
                //  entered in, this helps reduce problems when generating the max number of
                //  samples
                if (localBatchSamples[3] != 0 && localBatchSamples[4] != 0) {
                    numberWheelDialog(0, (localBatchSamples[3] + localBatchSamples[4]), 2, vNumShared);
                }
                break;
            case R.id.numTP:

                // TODO sometimes causes the value generated to be less than 0, which is a problem


                maxAllowedSamples = batchLogic.findMaxAllowedSample(availableTubes, true);
                Log.i(TAG, "New maximum for " + sBatchSamples[3] + " is " + maxAllowedSamples);
                numberWheelDialog(0, maxAllowedSamples, 3, vNumTP);
                break;
            case R.id.numTKN:
                maxAllowedSamples = batchLogic.findMaxAllowedSample(availableTubes, false);
                Log.i(TAG, "New maximum for " + sBatchSamples[4] + " is " + maxAllowedSamples);
                numberWheelDialog(0, maxAllowedSamples, 4, vNumTKN);
                break;
            case R.id.numExtra:
                numberWheelDialog(0, availableTubes, 5, vNumExtra);
                break;
        }
    }

}