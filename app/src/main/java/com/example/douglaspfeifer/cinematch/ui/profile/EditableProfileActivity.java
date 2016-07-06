package com.example.douglaspfeifer.cinematch.ui.profile;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.User;
import com.example.douglaspfeifer.cinematch.ui.BaseActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditableProfileActivity extends BaseActivity {

    private EditText descriptionEditText;
    private TextView charactersRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editable_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display the button inside the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Instantiate the views
        descriptionEditText = (EditText) findViewById(R.id.description_editText);
        charactersRemaining = (TextView) findViewById(R.id.charactersRemaining_textView);

        // Characters remaining at descriptionEditText
        descriptionEditText.addTextChangedListener(mTextEditorWatcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editable_profile, menu);
        return true;
    }

    // This method makes the return button on the action bar works | "it just works" - Toddy Howard.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.complete_changes:
                completeChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // This method is a copy-paste from
    // http://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside/28939113#28939113
    // It should make the EditText disappear when a touch outside occurs
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    // This method is a copy-paste from
    // http://stackoverflow.com/questions/3013791/live-character-count-for-edittext
    // It should show the counter for the characters remaining at descriptionEditText
    final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current remaining length
            charactersRemaining.setText(String.valueOf(300-s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    // Method called when the user finish editing
    public void completeChanges () {

        // Get the reference to the userList node in Firebase
        Firebase userListRef = new Firebase(Constants.FIREBASE_URL).child("userList");

        // Get the string the the user entered into the descriptionEditText
        String userEnteredDescription = descriptionEditText.getText().toString();

        // TWO WAYS OF GENERATING THE DATA FOR FIREBASE
        // -----FIRST WAY-----
        // Convert the POJO into a Map (THIS WILL ERASE EVERYTHING THAT HAS A NULL VALUE):
        // New user object
        // User user = new User();
        // Make the user changes
        // user.setAge(24);
        // user.setDescription(userEnteredDescription);
        // Map<String, Object> userMap = new ObjectMapper().convertValue(user, Map.class);
        // -----SECOND WAY-----
        // Create a HashMap and adding some attributes to it
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("description", userEnteredDescription);

        // Change the data on Firebase
        // Go to the child node of the root node
        // This will create the node for you if it doesn't already exist
        // Then using the setValue menu it will set value the node
        // The updateChildren simply changes the value without deleting everything
        userListRef.child("user1").updateChildren(userMap);
    }
}
