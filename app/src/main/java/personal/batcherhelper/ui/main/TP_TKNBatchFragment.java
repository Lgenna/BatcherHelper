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

import personal.batcherhelper.BatchItem;
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

    private TextView[] textViewList;

    private TextView    vNumTubesAvailable, vWaterMDL,  vWaterPT,   vWaterShared,   vWaterTP,   vWaterTKN,  vWaterExtra,
                        vNumTubesLeft,      vSoilMDL,   vSoilPT,    vSoilShared,    vSoilTP,    vSoilTKN,   vSoilExtra,
                        vCurrentBatchSize;

    private CheckBox vNeedsCurve;
    private ImageButton vResetAllFields;

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

        vWaterMDL = view.findViewById(R.id.water_mdl);
        vWaterMDL.setOnClickListener(this);
        vWaterPT = view.findViewById(R.id.water_pt);
        vWaterPT.setOnClickListener(this);
        vWaterShared = view.findViewById(R.id.water_shared);
        vWaterShared.setOnClickListener(this);
        vWaterTP = view.findViewById(R.id.water_tp);
        vWaterTP.setOnClickListener(this);
        vWaterTKN = view.findViewById(R.id.water_tkn);
        vWaterTKN.setOnClickListener(this);
        vWaterExtra = view.findViewById(R.id.water_extra);
        vWaterExtra.setOnClickListener(this);

        vSoilMDL = view.findViewById(R.id.soil_mdl);
        vSoilMDL.setOnClickListener(this);
        vSoilPT = view.findViewById(R.id.soil_pt);
        vSoilPT.setOnClickListener(this);
        vSoilShared = view.findViewById(R.id.soil_shared);
        vSoilShared.setOnClickListener(this);
        vSoilTP = view.findViewById(R.id.soil_tp);
        vSoilTP.setOnClickListener(this);
        vSoilTKN = view.findViewById(R.id.soil_tkn);
        vSoilTKN.setOnClickListener(this);
        vSoilExtra = view.findViewById(R.id.soil_extra);
        vSoilExtra.setOnClickListener(this);


        vNeedsCurve = view.findViewById(R.id.isCurveNeeded);
        vNeedsCurve.setOnClickListener(this);
        vNumTubesLeft = view.findViewById(R.id.tubes_left);
        vCurrentBatchSize = view.findViewById(R.id.current_batch_size);
        vResetAllFields = view.findViewById(R.id.resetAllFields);
        vResetAllFields.setOnClickListener(this);

        textViewList = new TextView[] {vNumTubesAvailable,  vWaterMDL,  vWaterPT,   vWaterShared,   vWaterTP,   vWaterTKN,  vWaterExtra,
                                                            vSoilMDL,   vSoilPT,    vSoilShared,    vSoilTP,    vSoilTKN,   vSoilExtra  };

        updateData();
        initRecyclerView();

        return view;
    }
    
    public void updateData() {
        batchLogic.performLogic();
        vCurrentBatchSize.setText(String.valueOf(batchLogic.getBatchSize()));
        vNumTubesLeft.setText(String.valueOf(batchLogic.getAvailableTubes()));
        initData();
    }
    
    private void itemAdder(String itemName, int itemCount, int indexInArray) {

        BatchItem item = new BatchItem();

        item.setmItem(itemName);
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
                itemAdder(sBatchQC[i], batchQC[i], i);
//                Log.i(TAG, "Added " + sBatchQC[i] + " to the ArrayList");
            }
        }

        for (int i = 1; i < batchSamples.length - 1; i++) {
            if (batchSamples[i] > 0) {
                // special case to subtract the shared samples from TP and TKN
                if (i == 6 || i == 7) {
                    itemAdder(sBatchSamples[i], batchSamples[i] - batchSamples[5], i);
//                    Log.i(TAG, "Added " + sBatchSamples[i] + " to the ArrayList");
                } else {
                    itemAdder(sBatchSamples[i], batchSamples[i], i);
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
            // grab the new data from batchSamples and batchQC and re-add it to the mBatchSamples ArrayList
            updateData();
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

    /**
     * Separate method to check if availableTubes has been set to reduce duplicated code, also spits
     *  some info to the user stating that they should set the number of available tubes
     * @return returns true or false depending on if availableTubes was set
     */

    private boolean availableTubesSet() {
        if (vNumTubesAvailable.getText().equals("")) {
            // was never set yet
            Log.w(TAG, "AvailableTubes was not set yet, skipping onClick call");
            ShowToast(context, "Set # tubes available first");
            return false;
        }
        // was set
        return true;
    }


    @Override
    public void onClick(View view) {

        int[] localBatchSamples = batchLogic.getBatchSamples();
        int[] localBatchQC = batchLogic.getBatchQC();
        int availableTubes = batchLogic.getAvailableTubes();
        int maxAllowedSamples;

        switch (view.getId()) {
            case R.id.isCurveNeeded:
                if (availableTubesSet()) {
                    // check to see the status of "checked"
                    if (vNeedsCurve.isChecked()) {
                        batchLogic.performLogic();
                        // check to see if there are enough available tubes
                        if (availableTubes - 7 >= 0) {
                            // then add the 7 curve points needed
                            batchLogic.setCurvePoints(true);
                            Log.i(TAG, "Set 7 curve points.");
                            
                            updateData();
                            batchViewCustomAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Not enough room for the 7 curve points needed.");
                            ShowToast(context, "Not enough available tubes");
                            vNeedsCurve.setChecked(false);
                        }
                    } else {
                        // was unchecked, remove added curvePoints if present
                        if (localBatchQC[2] > 0) {
                            Log.w(TAG, "Removed 7 curve points.");
                            batchLogic.setCurvePoints(false);

                            updateData();
                            batchViewCustomAdapter.notifyDataSetChanged();
                        }

                    }
                } else {
                    vNeedsCurve.setChecked(false);
                }
                break;
            case R.id.numTubesAvailable:
                // add all samples and QC together and set that as the minimum value
                int totalQC = 0;
                int totalSample = 0;
                for (int QC : localBatchQC) {
                    totalQC += QC;
                }
                for (int sample : localBatchSamples) {
                    totalSample += sample;
                }
               
                // if there happens to be no samples, set the minimum to one higher than QC needed
                //  because a batch technically needs one sample, mainly to prevent big brain time
                //  when you run a batch with no samples
                if (totalSample == 0) totalSample += 1;
                
                // this will crash the program if totalQC + totalSample is greater than the maximum
                // which there shouldn't be a case where it is?
                
                // max of 50 because that's how many we can hold on a tray, if you can hold more,
                //  good for you.
                numberWheelDialog(totalQC + totalSample, 50, -1,-1, vNumTubesAvailable);
                break;
            case R.id.water_mdl:
//                numberWheelDialog(0, availableTubes, 0, 0, vNumWaterMDLs);
                break;
             case R.id.soil_mdl:
//                numberWheelDialog(0, 50, 999, vNumSoilMDLs);
                 break;
            case R.id.water_pt:
//                numberWheelDialog(0, availableTubes, 1, 0, vNumWaterPTs);
                break;
             case R.id.soil_pt:
//                numberWheelDialog(0, 50, 999, vNumSoilPTs);
                 break;
            case R.id.water_shared:
                if (availableTubesSet()) {
                    // only allow the user to enter in a value IF they have both TP and TKN already
                    //  entered in, this helps reduce problems when generating the max number of
                    //  samples
                    if (localBatchSamples[3] != 0 && localBatchSamples[4] != 0) {
                        // we want the smallest because there cant be more than the smallest number of
                        // samples shared
                        int smallest = localBatchSamples[3];
                        if (smallest > localBatchSamples[4]) {
                            smallest = localBatchSamples[4];
                        }

                        numberWheelDialog(0, smallest, 2, 0, vWaterShared);
                    } else {
                        numberWheelDialog(0, availableTubes, 2, 0, vWaterShared);
                    }
                }
                break;
            case R.id.water_tp:

                // TODO onClickListener doesn't do anything if min == max (if 0 == 0) when clicked on
                //  make it toast or present some useful information

                if (availableTubesSet()) {
                    maxAllowedSamples = localBatchSamples[3] + batchLogic.findMaxAllowedSample(availableTubes, true);
                    if (maxAllowedSamples > 0) {
                        Log.i(TAG, "New maximum for " + sBatchSamples[3] + " is " + maxAllowedSamples);
                        numberWheelDialog(localBatchSamples[2], maxAllowedSamples, 3, 0, vWaterTP);
                    } else {
                        Log.i(TAG, "No new maximum for " + sBatchSamples[3] + " was found | 0 > " + maxAllowedSamples);
                    }
                }
                break;
            case R.id.water_tkn:
                if (availableTubesSet()) {
                    maxAllowedSamples = localBatchSamples[4] + batchLogic.findMaxAllowedSample(availableTubes, false);
                    if (maxAllowedSamples > 0) {
                        Log.i(TAG, "New maximum for " + sBatchSamples[4] + " is " + maxAllowedSamples);
                        numberWheelDialog(localBatchSamples[2], maxAllowedSamples, 4, 0, vWaterTKN);
                    } else {
                        Log.i(TAG, "No new maximum for " + sBatchSamples[4] + " was found | 0 > " + maxAllowedSamples);
                    }
                }
                break;
            case R.id.water_extra:
                if (availableTubesSet()) {
                    numberWheelDialog(0, availableTubes + localBatchSamples[5], 5, 0, vWaterExtra);
                }
                break;
            case R.id.resetAllFields:
                ShowToast(context, "All fields reset");
                Log.i(TAG, "Resetting all fields");
                batchLogic.resetAllFields();
                mBatchItems.clear();
                batchViewCustomAdapter.notifyDataSetChanged();
                updateData();
                // Now time for the not-so-fun part of updating each text view
                //  ... so we make it fun by throwing it through a loop!
                for (TextView item : textViewList) {
                    item.setText("");
                }
                break;
            default:
                Log.i(TAG, "Unknown button pressed: " + view.getId());
                break;
        }
    }
    public void ShowToast(Context context, String info) {
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        TextView toastText = toastView.findViewById(android.R.id.message);
        toastText.setTextColor(ContextCompat.getColor(context, R.color.white));
        toastView.setBackgroundResource(R.drawable.toast_item_border);
        toast.show();
    }
}
