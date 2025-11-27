package com.rtech.threadly.fragments.searchFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.rtech.threadly.R;
import com.rtech.threadly.databinding.FragmentSearchBinding;


public class SearchReusltFragment extends Fragment {
    FragmentSearchBinding mainXml;


    public SearchReusltFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mainXml=FragmentSearchBinding.inflate(inflater,container,false);
       init();
        return mainXml.getRoot();
    }

    private void init() {
    }


    private void focusAndOpenKeyboard(EditText editText) {

        editText.requestFocus();

        editText.post(() -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        focusAndOpenKeyboard(mainXml.searchEditText);
    }


}