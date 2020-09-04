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
import android.widget.Toast;

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
    private Context context;

    // Keep in mind that batchLogic stores all of the arrays and their contents, this
    //  can be considered a design flaw...
    private BatchLogic batchLogic = new BatchLogic();

    // get sample names and store it because they never change, however
    //  sample amounts do, so grab those whenever needed.
    private String[] sBatchSamples = batchLogic.getBatchSamplesNames();
    private String[] sBatchQC = batchLogic.getBatchQCNames();

    private boolean needsCurve;

    private TextView vNumTubesAvailable, vNumExtra, vNumShared, vNumTP, vNumTKN,
            vNumWaterPTs, vNumSoilPTs, vNumWaterMDLs, vNumSoilMDLs, vNumTubesLeft,
            vCurrentBatchSize;
    private CheckBox vNeedsCurve;

    // recycler view adapter
    private BatchViewCustomAdapter batchViewCustomAdapter;
    
    // items for recycler View
    private ArrayList<BatchItem> mBatchItems = new ArrayList<>();
    
    
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

    /**
     *  This method makes calls to the itemAdder method for each 
     *   item that's in the batchQC and batchSamples arrays.
     *
     *  The Log.i() commands are commented out since they kindof spam the console and are not
     *   terribly necessary.
     */
    
    private void initData() {

        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();

        mBatchItems.clear(); // wipe before adding new data, easy solution to avoid duplicates

        // go through each type of item in the array
        for (int i = 0; i < batchQC.length; i++) {
            // if its value is greater than 0, add it
            if (batchQC[i] > 0) {
                itemAdder(sBatchQC[i], true, batchQC[i], i);
//                Log.i(TAG, "Added " + sBatchQC[i] + " to the ArrayList");
            }
        }

        for (int i = 1; i < batchSamples.length - 1; i++) {
            if (batchSamples[i] > 0) {
                // special case to subtract the shared samples from TP and TKN
                if (i == 6 || i == 7) {
                    itemAdder(sBatchSamples[i], false, batchSamples[i] - batchSamples[5], i);
//                    Log.i(TAG, "Added " + sBatchSamples[i] + " to the ArrayList");
                } else {
                    itemAdder(sBatchSamples[i], false, batchSamples[i], i);
//                  Log.i(TAG, "Added " + sBatchSamples[i] + " to the ArrayList");
                }
            }
        }
    }


    private void initRecyclerView() {
        RecyclerView mRecyclerView = view.findViewById(R.id.currentBatchView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        batchViewCustomAdapter = new BatchViewCustomAdapter(getContext(), mBatchItems);
        mRecyclerView.setAdapter(batchViewCustomAdapter);
    }

    /**
     * Not used but required method, it gets called whenever the value is updated from a numberPicker, eg. it 
     *  gets called whenever the user is scrolling through instead of only when the user picks a value, why
     *  you would want this, we will never know.
     */
    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) { }

    private void numberWheelDialog(int min, int max, int valueIndex, int sourceArray, TextView calledView) {
        final Dialog dialog = new Dialog(context);

        int[] localBatchSamples = batchLogic.getBatchSamples();

        // change the background to a blue underline while the numberWheel is open, if moved to within the 
        //  OnClickListener it will only turn blue when the user taps on the view, instead of it being blue
        //  while the numberWheelDialog is open
        
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
        if (valueIndex == -1) {
            // min because there was no previous value
                numberPicker.setValue(min);

                // TODO don't always set the min, set the number of totalTubes or the min if totalTubes
                //  was never set before.

        } else {
            numberPicker.setValue(localBatchSamples[valueIndex]);
        }
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        confirm.setOnClickListener(v -> {

            int confirmedValue = numberPicker.getValue();
            Log.i(TAG, "Received " + confirmedValue + " as the confirmed value.");

            // set whoever called the numberPicker to the confirmed value
            calledView.setText(String.valueOf(confirmedValue));

            // store the confirmed value within its valueIndex in the array
            switch (sourceArray) {
                // only one condition for now where a numberPicker is needed for a value
                //  that is not stored within an array, this might change later on.
                case -1:
                    batchLogic.setTotalTubes(confirmedValue);
                    Log.i(TAG, "Changed the number of totalTubes to " + confirmedValue);
                    break;
                case 0:
                    batchLogic.setBatchSamplesValue(valueIndex, confirmedValue);
                    Log.i(TAG, "Changed the number of " + sBatchSamples[valueIndex] + " to " + confirmedValue);
                    break;
                default:
                    Log.e(TAG, "ERROR - Unknown sourceArray /( " + sourceArray + " /)");
            }

            // perform logic and set the number of tubes left
            batchLogic.performLogic();
            vNumTubesLeft.setText(String.valueOf(batchLogic.getAvailableTubes()));

            // grab the new data from batchSamples and batchQC and re-add it to the mBatchSamples ArrayList
            initData();
            // notify the recycler view that there is new data
            batchViewCustomAdapter.notifyDataSetChanged();

            // change the background of the clicked back to gray
            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            // dismiss the dialog
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            dialog.dismiss();
        });
        dialog.show();
    }


    @Override
    public void onClick(View view) {

        int[] localBatchSamples = batchLogic.getBatchSamples();
        int[] localBatchQC = batchLogic.getBatchQC();
        int availableTubes = batchLogic.getAvailableTubes();
        int maxAllowedSamples;

        switch (view.getId()) {
            case R.id.numTubesAvailable:
               numberWheelDialog(localBatchQC[0] + localBatchQC[1] + 1, 50, -1,-1, vNumTubesAvailable);
                break;
            case R.id.numWaterMDLs:
                numberWheelDialog(0, availableTubes, 0, 0, vNumWaterMDLs);
                break;
//             case R.id.numSoilMDLs:
//                numberWheelDialog(0, 50, 999, vNumSoilMDLs);
//                 break;
            case R.id.numWaterPTs:
                numberWheelDialog(0, availableTubes, 1, 0, vNumWaterPTs);
                break;
//             case R.id.numSoilPTs:
//                numberWheelDialog(0, 50, 999, vNumSoilPTs);
//                 break;
            case R.id.numShared:
                // only allow the user to enter in a value IF they have both TP and TKN already
                //  entered in, this helps reduce problems when generating the max number of
                //  samples
                if (localBatchSamples[3] != 0 && localBatchSamples[4] != 0) {
                    numberWheelDialog(0, (localBatchSamples[3] + localBatchSamples[4]), 2, 0, vNumShared);
                } else {
                    Toast.makeText(context, "Add TP & TKN before shared values", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.numTP:
                maxAllowedSamples + localBatchSamples[3] = batchLogic.findMaxAllowedSample(availableTubes, true);
                if (maxAllowedSamples > 0) {
                    Log.i(TAG, "New maximum for " + sBatchSamples[3] + " is " + maxAllowedSamples);
                    numberWheelDialog(0, maxAllowedSamples, 3, 0, vNumTP);
                } else {
                    Log.i(TAG, "No new maximum for " + sBatchSamples[3] + " was found | 0 > " + maxAllowedSamples);
                }
                break;
            case R.id.numTKN:
                maxAllowedSamples + localBatchSamples[4] = batchLogic.findMaxAllowedSample(availableTubes, false);
                if (maxAllowedSamples > 0) {
                    Log.i(TAG, "New maximum for " + sBatchSamples[4] + " is " + maxAllowedSamples);
                    numberWheelDialog(0, maxAllowedSamples, 4, 0, vNumTKN);
               } else {
                    Log.i(TAG, "No new maximum for " + sBatchSamples[4] + " was found | 0 > " + maxAllowedSamples);
                }
                break;
            case R.id.numExtra:
                numberWheelDialog(0, availableTubes, 5, 0, vNumExtra);
                break;
        }
    }
}
