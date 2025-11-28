package com.rtech.threadly.fragments.searchFragments;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.rtech.threadly.adapters.SearchPage.SearchResultViewPagerAdapter;
import com.rtech.threadly.databinding.FragmentSearchReusltBinding;
import com.rtech.threadly.utils.ReUsableFunctions;
import com.rtech.threadly.viewmodels.SearchViewModel;


public class SearchResultFragment extends Fragment {
    FragmentSearchReusltBinding mainXml;
    SearchResultViewPagerAdapter adapter;
    SearchViewModel searchViewModel;


    public SearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       mainXml=FragmentSearchReusltBinding.inflate(inflater,container,false);
       init();
        return mainXml.getRoot();
    }

    private void init() {
        searchViewModel=new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        adapter=new SearchResultViewPagerAdapter(getChildFragmentManager());
        mainXml.viewPager.setAdapter(adapter);
        mainXml.tabLayout.setupWithViewPager(mainXml.viewPager);
        mainXml.searchBtn.setOnClickListener(this::handleSearch);
        mainXml.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
           if(hasFocus){
                   mainXml.dataHolderLayout.setVisibility(View.GONE);
           }else{
               mainXml.dataHolderLayout.setVisibility(View.VISIBLE);
           }
            }
        });
    }

    private void handleSearch(View v) {
        ReUsableFunctions.hideKeyboard((AppCompatActivity) requireActivity());
        String query=mainXml.searchEditText.getText().toString().trim();
       if(query.length()>1){
           searchViewModel.setSearching();
           searchViewModel.search(query);
           mainXml.searchEditText.setText("");
           mainXml.dataHolderLayout.setVisibility(View.VISIBLE);
       }

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