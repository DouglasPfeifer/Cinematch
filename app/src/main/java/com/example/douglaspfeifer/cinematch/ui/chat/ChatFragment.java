package com.example.douglaspfeifer.cinematch.ui.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    ArrayAdapter<String> mChatAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Layout wich will be inflated for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeScreen(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initializeScreen (View rootView) {
        // Create some dummy data for the ListView.
        String[] data = {
                "TEXT - 1",
                "TEXT - 2",
                "TEXT - 3",
                "TEXT - 4",
                "TEXT - 5",
                "TEXT - 6",
                "TEXT - 7",
                "TEXT - 8",
                "TEXT - 9",
                "TEXT - 10",
                "TEXT - 11",
                "TEXT - 12",
                "TEXT - 13",
                "TEXT - 14",
                "TEXT - 15",
                "TEXT - 16",
                "TEXT - 17",
                "TEXT - 18"
        };
        // Make the data a list
        final List<String> chatList = new ArrayList<String>(Arrays.asList(data));

        // Create a ListView adapter for the chat list
        mChatAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // This is the Activity I'm referring to
                        R.layout.list_item_chat, // This is the Layout for each item
                        R.id.listItem_nameTextView, // This is the TextView I want to be populated
                        chatList // This is the data
                );

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listView_chat);
        listView.setAdapter(mChatAdapter);

        // Listener for my listView
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent i = new Intent(getActivity(), ConversationActivity.class);
                i.putExtra("chatWith", chatList.get(position));
                startActivity(i);
            }
        });*/
    }
}