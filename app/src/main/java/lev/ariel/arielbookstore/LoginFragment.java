package lev.ariel.arielbookstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// This fragment handles user login using Firebase Authentication
public class LoginFragment extends Fragment {

    FirebaseAuth auth; // Firebase authentication object

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the login fragment layout
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance();

        // Get references to the email and password input fields and login button
        EditText emailLoginEt = view.findViewById(R.id.editTextEmailLogin);
        EditText passwordLogInEt = view.findViewById(R.id.editTextPasswordLogin);
        Button loginBtn = view.findViewById(R.id.buttonLogin);

        // Set the login button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailLoginEt.getText().toString();
                String password = passwordLogInEt.getText().toString();

                // Validate email and password fields
                 if (!validatePassword() || !validateUsername()) {
                    Toast.makeText(getContext(), "username and/or password not valid", Toast.LENGTH_LONG).show();
                }

                // If valid, try to log in
                else {
                    checkUser();
                }
            }

            // Validate that the email field is not empty
            public boolean validateUsername() {
                String val = emailLoginEt.getText().toString();
                if (val.isEmpty()) {
                    emailLoginEt.setError("email cannot be empty");
                    return false;
                } else {
                    emailLoginEt.setError(null); // No error
                    return true;
                }
            }

            // Validate that the password field is not empty
            public Boolean validatePassword() {
                String val = passwordLogInEt.getText().toString();
                if (val.isEmpty()) {
                    passwordLogInEt.setError("password cannot be empty");
                    return false;
                } else {
                    passwordLogInEt.setError(null); // No error
                    return true;
                }
            }

            // Attempt to sign in the user using Firebase Authentication
            public void checkUser() {
                String userUsername = emailLoginEt.getText().toString().trim();
                String userPassword = passwordLogInEt.getText().toString().trim();

                auth.signInWithEmailAndPassword(userUsername, userPassword)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // If login is successful, navigate to BuyOrSellActivity
                                Intent intent = new Intent(getContext(), BuyOrSellActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Get the error message from the exception
                                String message = e.getMessage();

                                // Check if the message is not null before inspecting it
                                if (message != null) {

                                    // If Firebase says the user doesn't exist
                                    if (message.contains("There is no user record")) {
                                        Toast.makeText(getContext(), "No account with this email was found", Toast.LENGTH_LONG).show();

                                        // If the password is wrong for the existing user
                                    } else if (message.contains("The password is invalid")) {
                                        Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_LONG).show();

                                        // If the email format is not valid (e.g. missing @ or .com)
                                    } else if (message.contains("badly formatted")) {
                                        Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_LONG).show();

                                        // Generic fallback for other errors, print the message for debugging
                                    } else {
                                        Toast.makeText(getContext(), "incorrect password", Toast.LENGTH_LONG).show();
                                    }

                                    // If the message is null, just show a generic error
                                } else {
                                    Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                ;
            }
        });

        return view; // Return the view for this fragment
    }
}
