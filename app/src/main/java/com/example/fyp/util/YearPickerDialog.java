package com.example.fyp.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.fyp.R;
import com.google.firebase.database.annotations.Nullable;

public class YearPickerDialog extends DialogFragment {
    private OnYearSelectedListener listener;

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }

    public void setOnYearSelectedListener(OnYearSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.NetworkAlertDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.year_picker_dialog, null);
        builder.setView(view);

        NumberPicker yearPicker = view.findViewById(R.id.yearPicker);

        // prevent the default keyboard from opening when clicking and holding
        EditText editText = findEditText(yearPicker);

        if (editText != null) {
            editText.setInputType(InputType.TYPE_NULL);
        }

        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        yearPicker.setMinValue(1990);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView btnOK = view.findViewById(R.id.tvOKYear);
        TextView btnCancel = view.findViewById(R.id.tvCancelYearDialog);
        TextView selectedYear = view.findViewById(R.id.SelectedYear);

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedYear.setText(String.valueOf(newVal));
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onYearSelected(yearPicker.getValue());
                }
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    private EditText findEditText(ViewGroup viewGroup) {
        EditText editText = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof EditText) {
                editText = (EditText) child;
                break;
            } else if (child instanceof ViewGroup) {
                editText = findEditText((ViewGroup) child);
                if (editText != null) {
                    break;
                }
            }
        }
        return editText;
    }

}
