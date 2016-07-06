package com.example.douglaspfeifer.cinematch.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Parcelable;
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
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    /*
     * Facebook
     */
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private TextView mInfo;
    private Bundle bundle;

    /*
     * Firebase
     */
    private Firebase mFirebaseUsersRef;

    /*
     * Usuário
     */
    private User mLoggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_login);

        /**
         * Referênciando o nó de usuários no Firebase
         */
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS);

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
        mInfo = (TextView)findViewById(R.id.info);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.button_facebookLogin);
        mLoginButton.setReadPermissions(Arrays.asList("email", "user_birthday", "public_profile"));
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

            String accessToken = loginResult.getAccessToken().getToken();

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    // Get facebook data from login
                    Bundle bFacebookData = getFacebookData(object);

                }
            });

            Bundle parameters = new Bundle();

            // Parâmetros que pediremos ao facebook
            parameters.putString("fields", "id, first_name, last_name, email, gender, birthday");
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

    private Bundle getFacebookData(JSONObject object) {

        // Mapa usado para colocar o usuário dentro e depois mandar para o firebase
        Map<String, Object> userMap = new HashMap<String, Object>();

        try {
            bundle = new Bundle();

            mLoggedUser.setFacebookId(object.getString("id"));

            try {
                URL profilePic = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=200&height=150");
                mLoggedUser.setProfilePic(profilePic);
                userMap.put("profilePic", mLoggedUser.getProfilePic().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            userMap.put("idFacebook", mLoggedUser.getFacebookId());

            if (object.has(Constants.LOGIN_BUNDLE_FIRST_NAME)) {
                mLoggedUser.setFirstName(object.getString(Constants.LOGIN_BUNDLE_FIRST_NAME));
                userMap.put(Constants.LOGIN_BUNDLE_FIRST_NAME, mLoggedUser.getFirstName());
            }
            if (object.has(Constants.LOGIN_BUNDLE_LAST_NAME)) {
                mLoggedUser.setLastName(object.getString(Constants.LOGIN_BUNDLE_LAST_NAME));
                userMap.put(Constants.LOGIN_BUNDLE_LAST_NAME, mLoggedUser.getLastName());
            }
            if (object.has(Constants.LOGIN_BUNDLE_EMAIL)) {
                mLoggedUser.setEmail(Utils.encodeEmail(object.getString(Constants.LOGIN_BUNDLE_EMAIL)));
                userMap.put(Constants.LOGIN_BUNDLE_EMAIL, mLoggedUser.getEmail());
            }
            if (object.has(Constants.LOGIN_BUNDLE_GENDER)) {
                mLoggedUser.setGender(object.getString(Constants.LOGIN_BUNDLE_GENDER));
                userMap.put(Constants.LOGIN_BUNDLE_GENDER, mLoggedUser.getGender());
            }
            if (object.has(Constants.LOGIN_BUNDLE_BIRTHDAY)) {
                mLoggedUser.setBirthday(object.getString(Constants.LOGIN_BUNDLE_BIRTHDAY));
                userMap.put(Constants.LOGIN_BUNDLE_BIRTHDAY, mLoggedUser.getBirthday());
            }

            userMap.put("description", "...");

            mFirebaseUsersRef.child(mLoggedUser.getEmail()).updateChildren(userMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                        completeLogin();
                    }
                }
            });

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Troca de tela
    private void completeLogin () {
        Intent i = new Intent(this, MainActivity.class);
        Bundle b = new  Bundle();
        b.putParcelable("loggedUser", mLoggedUser);
        i.putExtras(b);
        startActivity(i);
    }
}
