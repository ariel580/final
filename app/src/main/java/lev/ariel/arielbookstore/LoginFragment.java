package lev.ariel.arielbookstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        EditText emailLoginEt = view.findViewById(R.id.editTextEmailLogin);
        EditText passwordLogInEt = view.findViewById(R.id.editTextPasswordLogin);
        Button loginBtn = view.findViewById(R.id.buttonLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = emailLoginEt.getText().toString();
                String password = passwordLogInEt.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                    Toast.makeText(getContext(), "email and/or password can't be empty", Toast.LENGTH_LONG).show();
                else if (!validatePassword() || !validateUsername()) {
                    Toast.makeText(getContext(), "username and/or password not valid", Toast.LENGTH_LONG).show();

                }
                else{
                    checkUser();}}
                public boolean validateUsername(){//יצירת פונקציה הבודקת האם הערך שהכניס ל username לא ריק
                    String val = emailLoginEt.getText().toString();//מכניס למשתנה את המידע שהכניס המשתמש במסך
                    if(val.isEmpty()){//תנאי לבדוק אם הכניס מידע ריק
                        emailLoginEt.setError("email cannot be empty");//מוציא הודעת שגיאה למסך ואומר לו שהערך לא יכול ליהיות ריק
                        return false;
                }
                else{
                        emailLoginEt.setError(null);//אין שגיאה
                        return true;
                }
            }
            public Boolean validatePassword(){//יצירת פונקציה הבודקת האם הערך שהכניס לסיסמה לא ריק
                String val = passwordLogInEt.getText().toString();//מכניס למשתנה את המידע שהכניס המשתמש במסך
                if(val.isEmpty()){//תנאי לבדוק אם הכניס מידע ריק
                    passwordLogInEt.setError("password cannot be empty");//מוציא הודעת שגיאה למסך ואומר לו שהערך לא יכול ליהיות ריק
                    return false;
                }
                else{
                    passwordLogInEt.setError(null);//אין שגיאה
                    return true;
                }
            }
            public void checkUser(){
                String userUsername = emailLoginEt.getText().toString().trim();//מכניס למשתנה את המידע שהכניס המשתמש במסך
                String userPassword = passwordLogInEt.getText().toString().trim();//מכניס למשתנה את המידע שהכניס המשתמש במסך
                auth.signInWithEmailAndPassword(userUsername, userPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {//הפעולה בעזרת הפונקציה authentication של firebase שולפת את הנתונים של המשמשים בdatabase ובודק האם הנתונים שהכניס המשתמש למסך נמצאים בdatabase. בנוסף הוא שם מאזים להאם הפעולה תצליח או לא תצליח
                    @Override
                    public void onSuccess(AuthResult authResult) {//מה יקרה כאשר הפעולה תצליח והנתונים שהכניס המשתמש כן נמצאו בdatabase
                        Toast.makeText(getContext(), "good", Toast.LENGTH_SHORT).show();//מדפיס הודעה למסך שעבד
                        Intent intent = new Intent(getContext(),BuyOrSellActivity.class);//פונקציה המעבירה את המשתמש לעמוד main
                        startActivity(intent);//קיראה לפונקציה
                    }
                }).addOnFailureListener(new OnFailureListener() {//מאזין למקרה שלא נמצאו הנתונים שהכניס המשתמש בdatabase
                    @Override
                    public void onFailure(@NonNull Exception e) {//מה יקרה כאשר הפעולה לא תצליח והנתונים שהכניס המשתמש לא נמצאו בdatabase
                        Toast.makeText(getContext(), "not-good", Toast.LENGTH_SHORT).show();//מדפיס הודעת שגיאה למסך של המשתמש
                    }
                });
                //else
              //  {
                  //  String message = FirebaseUtil.LoginToDb(email, password);
                  //  Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                 //   if(message.equals("login successfully"))
                   // {
                      //  Intent intent = new Intent(getActivity(),BuyOrSellActivity.class);
                     //   startActivity(intent);
                   // }
             //   }

            }
        });

        return view;
    }
}