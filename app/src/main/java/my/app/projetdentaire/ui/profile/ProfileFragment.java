package my.app.projetdentaire.ui.profile;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONTEXT_INCLUDE_CODE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

import my.app.projetdentaire.MainActivity;
import my.app.projetdentaire.R;
import my.app.projetdentaire.api.RetrofitStudent;
import my.app.projetdentaire.api.StudentApi;
import my.app.projetdentaire.beans.Student;
import my.app.projetdentaire.databinding.FragmentProfileBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Bitmap selectedBitmap;
    private ImageButton editphoto;
    private ImageView image;
    private Button editprofile;
    Student student;
    private EditText emaill, usernamee, firstNamee, lastNamee, numberr;
    private TextView mail, name;
    private long studentId;

    private FragmentProfileBinding binding;


    public static ProfileFragment newInstance(long studentId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putLong("studentId", studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editphoto = root.findViewById(R.id.editphoto);
        image = root.findViewById(R.id.image);
        editprofile = root.findViewById(R.id.editprofile);
        emaill = root.findViewById(R.id.emaill);
        usernamee = root.findViewById(R.id.usernamee);
        firstNamee = root.findViewById(R.id.firstNamee);
        lastNamee = root.findViewById(R.id.lastNamee);
        numberr = root.findViewById(R.id.numberr);
        mail = root.findViewById(R.id.mail);
        name = root.findViewById(R.id.name);

        editphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });
        fetchStudent();

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateprofile();
            }
        });

        return root;
    }


    public void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CONTEXT_INCLUDE_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTEXT_INCLUDE_CODE && resultCode == RESULT_OK && data != null) {
            image.setVisibility(View.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                selectedBitmap = bitmap;
                image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchStudent() {
        StudentApi studentApi = RetrofitStudent.getClient().create(StudentApi.class);
        MainActivity mainActivity = (MainActivity) requireActivity();

        studentId = mainActivity.getStudentId();

        Call<Student> call = studentApi.getStudentById(studentId);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    // Handle successful login response
                    student = response.body();
                    Log.d("response valide", student.getEmail());
                    if (student != null) {
                        // Student exists, populate the form
                        populateFormWithStudentData();

                        //Toast.makeText(requireActivity(), "Fetch data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("response invalide", response.toString());
                    // Handle unsuccessful login response
                    Toast.makeText(requireActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.d("erreur", t.toString());
                // Handle failure (e.g., network issues)
                Toast.makeText(requireActivity(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // La permission a été accordée
        } else {
            // La permission a été refusée
            Toast.makeText(requireContext(), "Permission refusée pour accéder au stockage externe", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateFormWithStudentData() {

        emaill.setText(student.getEmail());
        firstNamee.setText(student.getFirst_name());
        lastNamee.setText(student.getLast_name());
        numberr.setText(student.getNumber());
        usernamee.setText(student.getUserName());
        mail.setText(student.getEmail());
        name.setText(student.getFirst_name() + " " + student.getLast_name());

        if (student != null && student.getPhoto() != null) {
            byte[] imageData = Base64.decode(student.getPhoto().substring(student.getPhoto().indexOf(",") + 1), Base64.DEFAULT);
            image.setImageBitmap(convertByteArrayToBitmap(imageData));
        }

    }

    private Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    public void updateprofile() {

        MainActivity mainActivity = (MainActivity) requireActivity();
        long id = mainActivity.getStudentId();

        String usernam = usernamee.getText().toString();
        String fn = firstNamee.getText().toString();
        String ln = lastNamee.getText().toString();
        String emai = emaill.getText().toString();
        String num = numberr.getText().toString();

        Student newStudent = new Student(id, fn, ln, emai, num, usernam);

        // Compresser l'image uniquement si selectedBitmap n'est pas null
        if (selectedBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            newStudent.setPhoto(encodedImage);
        } else {
            // Si selectedBitmap est null, utilisez l'image existante du student
            if (student != null && student.getPhoto() != null) {
                newStudent.setPhoto(student.getPhoto());
            }
        }

        StudentApi studentApi = RetrofitStudent.getClient().create(StudentApi.class);
        Call<Void> call = studentApi.updateStudent(studentId, newStudent);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Mettre à jour les valeurs des EditText avec les nouvelles informations
                    Toast.makeText(requireActivity(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    // Gérer les cas où la mise à jour a échoué
                    Toast.makeText(requireActivity(), "Échec de la mise à jour du profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Gérer les erreurs lors de la connexion au serveur
                Toast.makeText(requireActivity(), "Erreur lors de la mise à jour du profil: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}