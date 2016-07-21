package com.example.douglaspfeifer.cinematch.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.ui.BaseActivity;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.example.douglaspfeifer.cinematch.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    private Intent i;

    /*
     * User data
     */
    private String mLoggedUserEmail;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /*
     * Firebase
     */
    private Firebase mFirebaseUsersRef;

    /*
     * Facebook
     */
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Se o usuário já possui conta e não é a primeira vez que usa o aplicativo após a instalação
        /*if (AccessToken.getCurrentAccessToken() != null) {
            i = new Intent(this, MainActivity.class);
            startActivity(i);
        }*/

        super.onCreate(savedInstanceState);
        i = new Intent(this, MainActivity.class);
        SharedPreferences mPreferences = getSharedPreferences("settings", 0);
        if(mPreferences.getString("userEmail", null) != null)
        {
            startActivity(i);
        }

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
        getSupportActionBar().setTitle("Cinematch");
        setContentView(R.layout.activity_login);



        // Shared preferences
        sharedPref = getSharedPreferences("settings", 0);
        editor = sharedPref.edit();

        // Initialize Firebase reference
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS);

        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.button_facebookLogin);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

    }

    /*
     * Ao clicar no botão de login do Facebook, uma activity será criada
     * o resultado dessa activity irá retornar para a LoginActivity
     * este método garante que haja o botão de LogOut
     * e que os dados sejam processados para o bundle
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Let me see", "let me sing");
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
            AccessToken token = loginResult.getAccessToken();

            final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Logging....");
            dialog.show();
            Log.d("fbaccess", "Facebook AccessToken " + token.getToken());

            mFirebaseUsersRef.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.d("FBLOGIN", "The Facebook user is now authenticated with your Firebase app");

                    // Upload data to firebase
                    final Map<String, Object> userMap = new HashMap<String, Object>();

                    if(authData.getProviderData().containsKey("displayName")) {
                        editor.putString("userName", authData.getProviderData().get("displayName").toString());
                        userMap.put("name", authData.getProviderData().get("displayName").toString());
                    }
                    if(authData.getProviderData().containsKey("email")) {
                        mLoggedUserEmail = Utils.encodeEmail(authData.getProviderData().get("email").toString());
                        userMap.put("email", mLoggedUserEmail);

                        editor.putString("userEmail", mLoggedUserEmail);
                        editor.commit();
                    }
                    if(authData.getProviderData().containsKey("profileImageURL")) {
                        userMap.put("profileImageURL", authData.getProviderData().get("profileImageURL").toString());
                    }

                    /*
                    Isso nem é usado, mas se precisar de dados mais específicos, é só usar ele
                    if(authData.getProviderData().containsKey("cachedUserProfile")) {
                        objectMap = authData.getProviderData().get("cachedUserProfile");
                    }
                    */

                    mFirebaseUsersRef.child(mLoggedUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            // handle the case where the data already exists
                            mFirebaseUsersRef.child(mLoggedUserEmail).updateChildren(userMap, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError != null) {
                                        System.out.println("Data could not be saved. " + firebaseError.getMessage());
                                    }
                                    else {
                                        System.out.println("Data saved successfully.");
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"The Facebook user is now authenticated with your Firebase app",Toast.LENGTH_LONG).show();

                                        startActivity(i);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            Log.i("Nada nada nada", "nada");
                        }
                    });

                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                    Log.d("FBLOGIN", "Tthere was an error with your Firebase app");
                }
            });

        }

        @Override
        public void onCancel() {
            Log.i("Login", "Cancelled");
        }

        @Override
        public void onError(FacebookException error) {

            Log.i("Login", "Error");
        }
    }
}
