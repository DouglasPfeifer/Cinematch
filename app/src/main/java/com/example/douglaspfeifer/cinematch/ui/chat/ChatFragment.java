package com.example.douglaspfeifer.cinematch.ui.chat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    ArrayAdapter<String> mChatAdapter;
    private Firebase mFirebaseUsersRef;
    private ChatListAdapter mChatListAdapter;
    private SharedPreferences sharedPref;
    private Map groupNames;
    private List groupValueList;
    private List groupKeyList;
    private String mLoggedUserEmail;
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
    private void initializeScreen (final View rootView) {
        sharedPref = getActivity().getSharedPreferences("settings", 0);
        mLoggedUserEmail = sharedPref.getString("userEmail", null);
        // Initialize Firebase reference
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS);
        mFirebaseUsersRef.child(mLoggedUserEmail).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null) {
                    groupNames = (Map) snapshot.getValue();
                    groupValueList = new ArrayList(groupNames.keySet());
                    groupKeyList = new ArrayList(groupNames.values());
                    // Make the data into a list
                    final List<String> nameList = new ArrayList<String>(groupValueList);
                    final List<String> nodeList = new ArrayList<String>(groupKeyList);
                    // Create a ListView adapter for the chat list
                    mChatAdapter =
                            new ArrayAdapter<String>(
                                    getActivity(), // This is the Activity I'm referring to
                                    R.layout.list_item_chat, // This is the Layout for each item
                                    R.id.listItem_nameTextView, // This is the TextView I want to be populated
                                    nameList // This is the data
                            );
                    // Get a reference to the ListView, and attach this adapter to it.
                    ListView listView = (ListView) rootView.findViewById(R.id.listView_chat);
                    listView.setAdapter(mChatAdapter);
                    // Listener for my listView
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                            Intent i = new Intent(getActivity(), ConversationActivity.class);
                            i.putExtra("chatNode", nodeList.get(position));
                            i.putExtra("chatName", nameList.get(position));
                            startActivity(i);
                        }
                    });
                } else {
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}