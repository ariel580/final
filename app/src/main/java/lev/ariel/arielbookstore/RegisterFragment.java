package lev.ariel.arielbookstore;

import android.annotation.SuppressLint;
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

// This fragment handles user registration using Firebase Authentication
public class RegisterFragment extends Fragment {

    // Firebase authentication object
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the registration screen layout
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Link UI elements (EditText + Button) from the layout
        EditText firstNameRegET = view.findViewById(R.id.editTextFirstNameReg);
        EditText lastNameRegET = view.findViewById(R.id.editTextLastNameReg);
        EditText emailRegET = view.findViewById(R.id.editTextEmailReg);
        EditText passwordRegET = view.findViewById(R.id.editTextPasswordReg);
        Button RegisterBtn = view.findViewById(R.id.buttonRegister);

        // Get instance of Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Handle registration when the Register button is clicked
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Read user input from EditText fields
                String email = emailRegET.getText().toString().trim();
                String password = passwordRegET.getText().toString().trim();
                String fname = firstNameRegET.getText().toString();
                String lname = lastNameRegET.getText().toString();

                // Try to create a new user account with email and password using Firebase
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // If registration is successful, go to BuyOrSellActivity
                                Intent intent = new Intent(getActivity(), BuyOrSellActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If registration fails, show error message
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view; // Return the view to display this fragment
    }
}
