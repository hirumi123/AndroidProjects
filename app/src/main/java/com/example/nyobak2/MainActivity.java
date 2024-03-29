package com.example.nyobak2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    ArrayList<UserModel> listUsers;
    SharedPreferences sharedPreferences;

    public static final String userpref = "userpref";
    public static final String userName = "username";
    public static final String userPass = "password";
    public static final String userKey = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button loginBtn = (Button) findViewById(R.id.button_login);
        EditText usernameField = (EditText) findViewById(R.id.username_login);
        TextView teksdaftar = (TextView) findViewById(R.id.new_accounts);
        EditText passwordField = (EditText) findViewById(R.id.password_login);

        sharedPreferences = getSharedPreferences(userpref, Context.MODE_PRIVATE);

        UpdateCredentials();

        if (!CheckIsLoggedIn()) {
            startActivity(new Intent(MainActivity.this, Dashboard.class));
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logUser = usernameField.getText().toString();
                String logPass = passwordField.getText().toString();

                if (logUser.isEmpty()) {
                    usernameField.setError("Harap masukkan username anda!");
                }
                else if (logPass.isEmpty()) {
                    passwordField.setError("Harap masukkan password anda!");
                }
                else {
                    boolean tmpIsLoggedIn = false;
                    for (UserModel user: listUsers) {
                        if (logUser.equals(user.getName()) && logPass.equals(user.getPassword())) {
                            Toast.makeText(MainActivity.this, "Proses Berhasil", Toast.LENGTH_SHORT).show();
                            tmpIsLoggedIn = true;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(userName, logUser);
                            editor.putString(userPass, logPass);
                            editor.putString(userKey, user.getKey());
                            editor.commit();

                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                            finish();
                        }
                    }

                    if (!tmpIsLoggedIn) {
                        Toast.makeText(MainActivity.this, "Username atau password anda salah", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        teksdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean CheckIsLoggedIn() {
        String loginUser = sharedPreferences.getString(userName, "");
        // true, if empty. false, if it has a value
        return loginUser.isEmpty();
    }

    private void UpdateCredentials() {
        database.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    UserModel user = item.getValue(UserModel.class);
                    user.setKey(item.getKey());

                    listUsers.add(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}