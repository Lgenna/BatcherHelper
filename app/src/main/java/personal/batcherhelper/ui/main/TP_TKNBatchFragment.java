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
import java.util.Objects;

import personal.batcherhelper.BatchItem;
import personal.batcherhelper.BatchItemNames;
import personal.batcherhelper.BatchLogic;
import personal.batcherhelper.BatchViewCustomAdapter;
import personal.batcherhelper.R;

public class TP_TKNBatchFragment extends Fragment implements View.OnClickListener, NumberPicker.OnValueChangeListener{

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "TP_TKNBatchFragment";

//    private PageViewModel pageViewModel;
    private View view;
    Context context;

    BatchLogic batchLogic = new BatchLogic();

    public static TP_TKNBatchFragment newInstance() {
        TP_TKNBatchFragment fragment = new TP_TKNBatchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    TextView iCurrentBatchSize;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tp_tkn_batch, container, false);


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

//        final TextView textView = root.findViewById(R.id.section_label);
//        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        initData();
        initRecyclerView();

        getBatchSize();

//        batchLogic.runner();

        return view;
    }

    ArrayList<BatchItem> mBatchItems = new ArrayList<>();
//    ArrayList<BatchItemNames> mBatchItemNames = new ArrayList<>();


    private void itemAdder(String itemName, boolean isQC, int itemCount, int indexInArray) {

        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();
        ArrayList<String> batchItemNames = new ArrayList<String>();

        BatchItemNames batchItemName = new BatchItemNames();
        batchItemName.setmItem(itemName);

        for (BatchItem item : mBatchItems) {
            batchItemNames.add(item.getmItem());
        }

        if (!batchItemNames.contains(itemName)) { // item was not present

            System.out.println("item name not found : " + itemName);
            BatchItem item = new BatchItem();

            item.setmItem(itemName);
            item.setmIsQC(isQC);
            item.setmCount(itemCount);
            item.setmIndexInArray(indexInArray);

            mBatchItems.add(item);

//            mBatchItemNames.add(batchItemName);
        } else { // item was present, now time to update it
            int position = batchItemNames.indexOf(itemName);
            BatchItem updateThis = mBatchItems.get(position);

            if (updateThis.getmIsQC()) {
                updateThis.setmCount(batchQC[indexInArray]);
            } else {
                updateThis.setmCount(batchSamples[indexInArray]);
            }

            mBatchItems.set(position, updateThis);
        }
    }

    private void getBatchSize() {
        int batchSize = 0;

        for (BatchItem item : mBatchItems ) {
            batchSize += item.getmCount();
        }
        iCurrentBatchSize = view.findViewById(R.id.currentBatchSizeNumber);
        iCurrentBatchSize.setText(String.valueOf(batchSize));
    }



    String[] sBatchSamples = new String[]{"WhatDidYouDo", "Water MDL", "Soil MDL"
            , "Water PT", "Soil PT", "Shared", "TP", "TKN", "Extra Needed"};
    String[] sBatchQC = new String[]{"Blank", "CCV", "Curve Points", "MS", "MSD", "LCS"};

    boolean needsCurve;

    TextView vNumTubesAvailable, vNumExtra, vNumShared, vNumTP, vNumTKN,
            vNumWaterPTs, vNumSoilPTs, vNumWaterMDLs, vNumSoilMDLs;
    CheckBox vNeedsCurve;

    private void initData() {

        // this adds items to the sample and qc arrayLists which get added to the recycler
        //  view...

        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();

        for (int i = 0; i < batchQC.length; i++) {
            // go through each type of item in the array
            if (batchQC[i] > 0) {
                // if its value is greater than 0, add it
                // it should... SHOULD, avoid adding the same value twice... should...

                itemAdder(sBatchQC[i], true, batchQC[i], i);
                System.out.println("Added " + sBatchQC[i] + " to the ArrayList");
            }
        }

        for (int i = 1; i < batchSamples.length - 1; i++) {
            // go through each type of item in the array
            if (batchSamples[i] > 0) {
                // if its value is greater than 0, add it
                // it should... SHOULD, avoid adding the same value twice... should...

                if (i == 6 || i == 7) { // special case to subtract the shared samples from TP and TKN
                    itemAdder(sBatchSamples[i], false, batchSamples[i] - batchSamples[5], i);
                    System.out.println("Added " + sBatchSamples[i] + " to the ArrayList");
                } else {
                    itemAdder(sBatchSamples[i], false, batchSamples[i], i);
                    System.out.println("Added " + sBatchSamples[i] + " to the ArrayList");
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

    int mostRecentNumber;

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        mostRecentNumber = i1;
    }

    private void numberWheelDialog(int min, int max, int valueIndex, TextView calledView) {


        int[] batchQC = batchLogic.getBatchQC();
        int[] batchSamples = batchLogic.getBatchSamples();
        final Dialog dialog = new Dialog(context);

        calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_blue_underline));
        dialog.setContentView(R.layout.fragment_number_picker);
        dialog.setCancelable(false);
        Button confirm = dialog.findViewById(R.id.confirm);
        Button cancel = dialog.findViewById(R.id.cancel);
        final NumberPicker numberPicker = dialog.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(batchSamples[valueIndex]);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        confirm.setOnClickListener(v -> {

            int confirmedValue = numberPicker.getValue();

            Log.i(TAG, "Received " + confirmedValue + " as the confirmed value.");

            calledView.setText(String.valueOf(confirmedValue));

            batchSamples[valueIndex] = confirmedValue;

            batchLogic.runner();
            initData();
            batchViewCustomAdapter.notifyDataSetChanged();

            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            calledView.setBackground(ContextCompat.getDrawable(context, R.drawable.light_gray_underline));
            dialog.dismiss(); // dismiss the dialog
        });
        dialog.show();
    }

//    private void computeSamples() {
//        int numShared = batchSamples[5];
//        int numMS = batchQC[3];
//
//        while (numShared > 0) {
//            numMS++;
//            numShared -= 10;
//        }
//
//
//
//
//        System.out.println("numShared: " + numShared);
//        System.out.println("numMS: " + numMS);
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.numTubesAvailable:
                numberWheelDialog(0, 50, 0, vNumTubesAvailable);
                break;
            case R.id.numWaterMDLs:
                numberWheelDialog(0, 50, 1, vNumWaterMDLs);
                break;
            case R.id.numSoilMDLs:
                numberWheelDialog(0, 50, 2, vNumSoilMDLs);
                break;
            case R.id.numWaterPTs:
                numberWheelDialog(0, 50, 3, vNumWaterPTs);
                break;
            case R.id.numSoilPTs:
                numberWheelDialog(0, 50, 4, vNumSoilPTs);
                break;
            case R.id.numShared:
                numberWheelDialog(0, 50, 5, vNumShared);
                break;

            case R.id.numTP:
                numberWheelDialog(0, 50, 6, vNumTP);
//                numTP = Integer.parseInt(vNumTP.getText().toString());
//                itemAdder("TP", false, numTP);
                break;

            case R.id.numTKN:
                numberWheelDialog(0, 50, 7, vNumTKN);
                break;
            case R.id.numExtra:
                numberWheelDialog(0, 50, 8, vNumExtra);
                break;
        }
    }

}