package com.outsmart.outsmartpower.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.network.WifiListFragment;

import org.w3c.dom.Text;

/**
 * Created by Rene Moise on 2/25/2017.
 */

public class GetPasswordDialog extends DialogFragment {
    private TextView passwordTV;
    private EditText passwordET;
    private Button passwordBTN;

    public String text;

    public Context context;

    public GetPasswordDialog(){

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.get_passwords_dialog,container, false);

        passwordTV = (TextView) rootView.findViewById(R.id.password_tv);
        passwordET = (EditText) rootView.findViewById(R.id.password_et);
        passwordBTN = (Button) rootView.findViewById(R.id.password_btn);

        passwordBTN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onPasswordButtonClicked fragment = (onPasswordButtonClicked) getTargetFragment();
                fragment.onFinishedEnteringPassword(passwordET.getText().toString());
                dismiss();
            }
        });
        return rootView;
    }

    public interface onPasswordButtonClicked{
        void onFinishedEnteringPassword(String password);
    }
}
