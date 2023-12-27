package my.app.projetdentaire.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import my.app.projetdentaire.MainActivity;
import my.app.projetdentaire.R;
import my.app.projetdentaire.api.RetrofitStudent;
import my.app.projetdentaire.api.StudentApi;
import my.app.projetdentaire.beans.Student;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends AppCompatActivity {

    EditText email, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the login method when the login button is clicked
                performLogin();
            }
        });
    }

    private void performLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Student object for login
        Student student = new Student();
        student.setEmail(userEmail);
        student.setPassword(userPassword);
        Log.d("student", student.getEmail());

        // Call the login API
        StudentApi loginApi = RetrofitStudent.getClient().create(StudentApi.class);
        Call<Student> call = loginApi.login(student);

        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Student loginResponse = response.body();
                    if (loginResponse != null) {
                        Toast.makeText(LoginFragment.this, "Login successful", Toast.LENGTH_SHORT).show();
                        long studentId = loginResponse.getId();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra("studentId", studentId);
                        startActivity(i);
                        LoginFragment.this.finish();

                    }
                } else {
                    // Handle unsuccessful login response (e.g., invalid email or password)
                    try {
                        String errorBody = response.errorBody().string();

                        Log.d("Error Body", errorBody);
                        Toast.makeText(LoginFragment.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                // Gérer les erreurs de connexion
                Toast.makeText(LoginFragment.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}