package my.app.projetdentaire.ui.pw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.app.projetdentaire.MainActivity;
import my.app.projetdentaire.R;
import my.app.projetdentaire.adapter.PWAdapter;
import my.app.projetdentaire.api.RetrofitStudent;
import my.app.projetdentaire.api.StudentApi;
import my.app.projetdentaire.beans.PW;
import my.app.projetdentaire.databinding.FragmentPwBinding;
import my.app.projetdentaire.ui.sendtp.SendPWActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PwFragment extends Fragment {

    private FragmentPwBinding binding;

    private List<PW> pws = new ArrayList<>();

    private ListView listView;

    private ImageButton download, go;

    private PWAdapter adapterPw;
    private long studentId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPwBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.listpws);
        adapterPw = new PWAdapter(pws, getContext());
        listView.setAdapter(adapterPw);
        download = root.findViewById(R.id.download);
        go = root.findViewById(R.id.go);

        adapterPw.setOnItemClickListener(new PWAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(byte[] pdfBytes, String pdfFileName) {
                downloadPdf(pdfBytes, pdfFileName);
            }
        });

        adapterPw.setOnItemClickListener2(new PWAdapter.OnItemClickListener2() {
            @Override
            public void onItemClick(long pwId) {
                // Logique à effectuer lors du clic sur le bouton "btngo"
                launchAnotherActivity(pwId);
            }
        });

        listView.setAdapter(adapterPw);

        fetchpw();

        return root;
    }

    private void launchAnotherActivity(long pwId) {
        Intent intent = new Intent(requireContext(), SendPWActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("pwId", pwId);
        startActivity(intent);
    }

    public void fetchpw() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        studentId = mainActivity.getStudentId();

        StudentApi studentApi = RetrofitStudent.getClient().create(StudentApi.class);
        Call<List<PW>> call = studentApi.getPW(studentId);
        call.enqueue(new Callback<List<PW>>() {
            @Override
            public void onResponse(Call<List<PW>> call, Response<List<PW>> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    List<PW> pwList = response.body();
                    Log.d("pw", pwList.toString());

                    if (pwList != null) {
                        // Update the pws list and notify the adapter
                        pws.clear();
                        pws.addAll(pwList);
                        adapterPw.notifyDataSetChanged();

                    }
                } else {
                    Log.d("response", response.toString());
                    // Handle unsuccessful response
                    Toast.makeText(requireActivity(), "err1", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PW>> call, Throwable t) {
                Toast.makeText(requireActivity(), "Inerrrr2", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void downloadPdf(byte[] pdfBytes, String pdfFileName) {
        // Écrivez les bytes du PDF dans un fichier temporaire
        File tempFile = new File(requireContext().getExternalCacheDir(), pdfFileName);
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(pdfBytes);
            fos.close();

            // Créez une Uri à partir du fichier temporaire
            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", tempFile);

            // Créez l'intent pour ouvrir le fichier avec l'application par défaut du système
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Lancez l'activité avec l'intent
            startActivity(intent);
        } catch (IOException e) {
            // Gérez les erreurs d'écriture de fichier ici
            e.printStackTrace();

        }
    }

    private Uri createUriFromBytes(byte[] pdfBytes, String pdfFileName) {
        try {
            // Écrivez les bytes du PDF dans un fichier temporaire
            File tempFile = File.createTempFile("temp_pdf", ".pdf", requireContext().getExternalCacheDir());
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(pdfBytes);
            fos.close();

            // Retournez l'URI du fichier temporaire
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérez les erreurs d'écriture de fichier ici
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}