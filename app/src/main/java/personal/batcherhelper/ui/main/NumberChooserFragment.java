package personal.batcherhelper.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.widget.LinearLayout;
import android.widget.NumberPicker;

import personal.batcherhelper.R;

public class NumberChooserFragment extends Fragment implements NumberPicker.OnValueChangeListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    int mostRecentNumber;

    public static NumberChooserFragment newInstance() {
        NumberChooserFragment fragment = new NumberChooserFragment();
        System.out.println("I WAS CALLED");
        return fragment;
    }




//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//
//
//        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_number_picker, new LinearLayout(getActivity()), false);
//        Dialog builder = new Dialog(getActivity());
////        builder.setMessage("this is a message")
////                .setPositiveButton("positive", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        System.out.println("user pressed positive");
////                    }
////                })
////                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        System.out.println("user canceled");
////                    }
////                });
//        // Create the AlertDialog object and return it
//        builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        builder.setContentView(view);
//        return builder;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("I WAS CALLED2");


    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        System.out.println("I WAS CALLED3");

        View view = inflater.inflate(R.layout.fragment_number_picker, container, false);

        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(50);

        numberPicker.setOnValueChangedListener(this);

        return view;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        mostRecentNumber = i1;
    }
}