package com.outsmart.outsmartpower.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.outsmart.outsmartpower.SmartOutlet;
import com.outsmart.outsmartpower.R;
import com.outsmart.outsmartpower.managers.SmartOutletManager;

/**
 * Created by Rene Moise on 3/26/2017.
 */

/**
 * This class is a listFragment displays the list to the user. The user may choose to select the
 * item or choose to cancel. If he selects the item, the item selected is returned to the caller.
 * If he chooses to cancel, the effect is simlar to the backpressed. He is returned to the previous
 * page.
 */
public class GetClickedItemListFragment extends ListFragment {

    //When this button is clicked the fragment is popped off the stack thus returning back to the
    //previous fragment.
    Button cancelButton;

    //This adapter is passed to the listFragment to be displayed. The content is gotten from the
    //the OutsmartManager
    ArrayAdapter<SmartOutlet> adapter;

    //Outsmart Manager is needed to get the list of available outsmart.
    SmartOutletManager outsmartManager = SmartOutletManager.getInstance();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initialize the cancel button.
        cancelButton = (Button) getActivity().findViewById(R.id.cancelListBTN);

        //Set listener.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Initialize the arrayAdapter.
        adapter = new ArrayAdapter<SmartOutlet>(getActivity(),
                android.R.layout.simple_list_item_1,outsmartManager.getSmartOutletList());
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_list, container,false);
        return view;
    }

    public interface OnReceivedClickedListItem {
        void receiveClickedItem(int chosenOutsmart);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OnReceivedClickedListItem receivedPosition = UIManager.getInstance();
        if(receivedPosition != null){
            UIManager.getInstance().receiveClickedItem(position);
        }
    }
}
