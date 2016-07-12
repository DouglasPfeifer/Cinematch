package com.example.douglaspfeifer.cinematch.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.User;
import com.example.douglaspfeifer.cinematch.ui.BaseActivity;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.example.douglaspfeifer.cinematch.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    Intent i;

    /*
     * Firebase
     */
    private Firebase mFirebaseUsersRef;
    private DataSnapshot mDataSnapshot;

    /*
     * Facebook
     */
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private TextView mInfo;
    private Bundle bundle;

    /*
     * Usuário
     */
    private User mLoggedUser;

    /*
     * Shared Preferences
     */
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Se o usuário já possui conta e não é a primeira vez que usa o aplicativo após a instalação
        /*if (AccessToken.getCurrentAccessToken() != null) {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        }*/

        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Cinematch");
        setContentView(R.layout.activity_login);

        /*
         * Inicialização de tela
         */
        initializeScreen();

        /*
         * Método que verifica se o login foi
         * um sucesso,
         * cancelado
         * ou ocorreu um erro
         */
        mLoginButton.registerCallback(mCallbackManager, new FacebookImplementation());
    }

    /*
     * Inicialização de tela
     */
    public void initializeScreen () {
        i = new Intent(this, MainActivity.class);
        sharedPref = getSharedPreferences("settings", 0);
        editor = sharedPref.edit();

        mInfo = (TextView)findViewById(R.id.info);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.button_facebookLogin);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mLoggedUser = new User();
    }

    /*
     * Ao clicar no botão de login do Facebook, uma activity será criada
     * o resultado dessa activity irá retornar para a LoginActivity
     * este método garante que haja o botão de LogOut
     * e que os dados sejam processados para o bundle
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * Se o seguinte bloco de código não for adicionado
         * `FacebookCallback` não será chamado
         */

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
     *
     */
    class FacebookImplementation implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                // Get facebook data from login
                getFacebookData(object);
                }
            });

            Bundle parameters = new Bundle();

            // Parâmetros que pediremos ao facebook
            parameters.putString("fields", "id, first_name, last_name, email, gender");
            request.setParameters(parameters);

            request.executeAsync();
        }

        @Override
        public void onCancel() {
            mInfo.setText("Login attempt canceled.");
        }

        @Override
        public void onError(FacebookException error) {
            mInfo.setText("Login attempt failed.");
        }
    }

    private Bundle getFacebookData(final JSONObject object) {

        final Map<String, Object> userMap = new HashMap<String, Object>();

        // Preciso do email antes para achar o nó do usuário na lista de usuários do firebase
        if (object.has(Constants.LOGIN_BUNDLE_EMAIL)) {
            try {
                mLoggedUser.setEmail(Utils.encodeEmail(object.getString(Constants.LOGIN_BUNDLE_EMAIL)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.putString("email", mLoggedUser.getEmail());
        }

        /**
         * Referênciando o nó de usuários no Firebase
         */
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mLoggedUser.getEmail());

        // mLoggedUser receive the data from firebase or null
        mFirebaseUsersRef.addValueEventListener(new ValueEventListener() {

            // Se o usuário já possui conta, mas é a primeira vez que usa após instalar o aplicativo
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mLoggedUser = dataSnapshot.getValue(User.class);

                    editor.putString("idFacebook", mLoggedUser.getIdFacebook());
                    editor.putString("profilePic", mLoggedUser.getProfilePic());
                    editor.putString("first_name", mLoggedUser.getFirst_name());
                    editor.putString("last_name", mLoggedUser.getLast_name());
                    editor.putString("gender", mLoggedUser.getGender());
                    editor.putFloat("rating", mLoggedUser.getRating());
                    editor.putString("description", mLoggedUser.getDescription());
                    editor.putInt("numOfRates", mLoggedUser.getNumOfRates());

                    editor.commit();

                    startActivity(i);
                } else {
                    try {
                        //put your value
                        editor.putString("idFacebook", object.getString("id"));

                        URL profilePic = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=150");
                        editor.putString("profilePic", profilePic.toString());

                        if (object.has(Constants.LOGIN_BUNDLE_FIRST_NAME)) {
                            editor.putString(Constants.LOGIN_BUNDLE_FIRST_NAME, object.getString(Constants.LOGIN_BUNDLE_FIRST_NAME));
                        }
                        if (object.has(Constants.LOGIN_BUNDLE_LAST_NAME)) {
                            editor.putString(Constants.LOGIN_BUNDLE_LAST_NAME, object.getString(Constants.LOGIN_BUNDLE_LAST_NAME));
                        }
                        if (object.has(Constants.LOGIN_BUNDLE_GENDER)) {
                            editor.putString(Constants.LOGIN_BUNDLE_GENDER, object.getString(Constants.LOGIN_BUNDLE_GENDER));
                        }
                    } catch (MalformedURLException | JSONException e) {
                        e.printStackTrace();
                    }
                    editor.putFloat("rating", 2.5f);
                    editor.putString("description", "...");
                    editor.putInt("numOfRates", 1);
                    //commits your edits
                    editor.commit();

                    userMap.put("email", sharedPref.getString("email", ""));
                    userMap.put("idFacebook", sharedPref.getString("idFacebook", ""));
                    userMap.put("profilePic", sharedPref.getString("profilePic", ""));
                    userMap.put("first_name", sharedPref.getString("first_name", ""));
                    userMap.put("last_name", sharedPref.getString("last_name", ""));
                    userMap.put("gender", sharedPref.getString("gender", ""));
                    userMap.put("rating", 2.5);
                    userMap.put("description", sharedPref.getString("description", "..."));
                    userMap.put("numOfRates", sharedPref.getInt("numOfRates", 1));

                    mFirebaseUsersRef.updateChildren(userMap, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                System.out.println("Data could not be saved. " + firebaseError.getMessage());
                            }
                            else {
                                System.out.println("Data saved successfully.");
                                startActivity(i);
                            }
                        }
                    });
                }
            }

            // Se o usuário não possui conta e é a primeira vez ou mais que usa o aplicativo
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return null;
    }
}
