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


public class RegisterFragment extends Fragment {
    FirebaseAuth auth;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        EditText firstNameRegET = view.findViewById(R.id.editTextFirstNameReg);
        EditText lastNameRegET = view.findViewById(R.id.editTextLastNameReg);
        EditText emailRegET = view.findViewById(R.id.editTextEmailReg);
        EditText passwordRegET = view.findViewById(R.id.editTextPasswordReg);
        Button RegisterBtn = view.findViewById(R.id.buttonRegister);
        auth = FirebaseAuth.getInstance();
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailRegET.getText().toString().trim();
                String password = passwordRegET.getText().toString().trim();
                String fname = firstNameRegET.getText().toString();//הכנסת הקלט מהמשתמש למשתנה
                String lname = lastNameRegET.getText().toString();

                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {//יוצר בעזרת הפונקציה auhentucation של Firebase משתמש בdatabase בעל אימייל וסיסמה,בנוסף הוא שם מאזין להאם הפעולה תצליח ותכשל
                    @Override
                    public void onSuccess(AuthResult authResult) {//מה קורה כאשר השמירה של המשתמש החדש בdatabase עובד
                        Intent intent = new Intent(getActivity(),BuyOrSellActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {//מאזין להאם השמירה של המשתמש החדש בdata base לא עובדת
                    @Override
                    public void onFailure(@NonNull Exception e) {//מה קורה כאשר השמירה של המשתמש החדש בdatabase לא עובד
                        Toast.makeText(getContext(), "not valid email", Toast.LENGTH_SHORT).show();//מוציא הודעת שגיאה למשתמש על המסך
                    }
                });
            }
        });

 return view;   }
}