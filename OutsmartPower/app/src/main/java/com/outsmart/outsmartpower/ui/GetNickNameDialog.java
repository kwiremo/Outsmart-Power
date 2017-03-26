package com.outsmart.outsmartpower.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.Support.ParentActivity;

/**
 * Created by Rene Moise on 3/25/2017.
 */

public class GetNickNameDialog extends DialogFragment {
    //    This will represent the widget on the screen that the user can type any input
    private EditText userInput;

    //This is the button that the user clicks to close the window and add the adjacency specified.
    private Button saveButton;

    //This button cancels the dialog without entering an adjacency
    private Button cancelButton;

    //This interface provides a connection back to the main Activity (remember that the
    // DialogFragment is actually running on a different Activity thread)
    public interface onInputButtonClicked{
        void onFinishedEditDialog(String ipAddress);
    }


    //Default constructor
    public GetNickNameDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout xml file you built.
        View rootView = inflater.inflate(R.layout.string_user_input, container,false);

        //Set the title
        getDialog().setTitle("Get Adjaceny Addresses");

        //Connect the widgets on-screen to our local fields.
        userInput = (EditText) rootView.findViewById(R.id.userInputET);
        saveButton = (Button) rootView.findViewById(R.id.saveInputBTN);
        cancelButton = (Button) rootView.findViewById(R.id.cancelDialogBTN);

        //Handle addAdjacency Button
        saveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                //To get the Activity object in your Dialog class you use the fact that the main
                // activity is now also an AdjacencyPairListener (because it implements
                // that interface)
                onInputButtonClicked activity =
                        (onInputButtonClicked) ParentActivity.getParentActivity();

                activity.onFinishedEditDialog(
                        userInput.getText().toString());
                dismiss();

            }
        });

        //Handle Cancel Button.
        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return rootView;
    }

    //call the superclass’s constructor, passing the savedInstanceState.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
