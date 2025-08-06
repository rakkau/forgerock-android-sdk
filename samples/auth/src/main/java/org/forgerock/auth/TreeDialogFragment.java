/*
 * Copyright (c) 2020 ForgeRock. All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package org.forgerock.auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.forgerock.android.auth.FRListener;
import org.forgerock.android.auth.Listener;

/**
 * Reference implementation of handing Advice with {@link DialogFragment}
 */
public class TreeDialogFragment extends DialogFragment {

    private MainActivity listener;
    private static final String ARG_JOURNEY_NAME = "journey_name";

    public static TreeDialogFragment newInstance() {
        return new TreeDialogFragment();
    }
    
    public static TreeDialogFragment newInstance(String journeyName) {
        TreeDialogFragment fragment = new TreeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JOURNEY_NAME, journeyName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tree, container, false);
        
        TextView treeName = view.findViewById(R.id.treeName);
        
        // Set dynamic journey name if provided
        if (getArguments() != null && getArguments().containsKey(ARG_JOURNEY_NAME)) {
            String journeyName = getArguments().getString(ARG_JOURNEY_NAME);
            treeName.setText(journeyName);
        }
        
        Button start = view.findViewById(R.id.start);
        start.setOnClickListener(v -> {
            dismiss();
            listener.launchTree(treeName.getText().toString());
        });
        Button launchBrowser = view.findViewById(R.id.launchBrowser);
        launchBrowser.setOnClickListener(v -> {
            dismiss();
            listener.launchBrowser();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)  {
            listener = (MainActivity) context;
        }
    }
}
