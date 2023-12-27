package my.app.projetdentaire.adapter;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import my.app.projetdentaire.R;
import my.app.projetdentaire.beans.PW;

public class PWAdapter extends BaseAdapter {
    private List<PW> pws;
    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;
    private OnItemClickListener2 onItemClickListener2;
    private Context context;

    // Déclaration de l'interface OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(byte[] pdfBytes, String pdfFileName);
    }

    // Méthode pour définir l'instance de l'interface
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PWAdapter(List<PW> pws, Context context) {
        this.pws = pws;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener2 {
        void onItemClick(long pwId);
    }

    public void setOnItemClickListener2(OnItemClickListener2 listener) {
        this.onItemClickListener2 = listener;
    }

    public void updatePWList(List<PW> newPWs) {
        pws.clear();
        pws.addAll(newPWs);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pws.size();
    }

    @Override
    public Object getItem(int position) {
        return pws.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pws.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pw, parent, false);
        }
        ImageButton download = convertView.findViewById(R.id.download);
        ImageButton go = convertView.findViewById(R.id.go);
        TextView id = convertView.findViewById(R.id.id);
        TextView title = convertView.findViewById(R.id.title);
        TextView objectif = convertView.findViewById(R.id.objectif);

        // Ensure that the title is not null before using it
        String pdfFileName = (pws.get(position).getTitle() != null) ? pws.get(position).getTitle() + ".pdf" : "Untitled.pdf";

        // Set the listener on each item even if there is no associated PDF document
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    String encodedDocs = pws.get(position).getDocs();
                    Log.d("String ", pws.toString());
                    if (encodedDocs != null) {
                        // Décodez la chaîne encodée en base64 en bytes
                        byte[] decodedBytes = Base64.decode(encodedDocs, Base64.DEFAULT);
                        // Appelez la méthode onItemClick avec les bytes du PDF et le nom du fichier
                        onItemClickListener.onItemClick(decodedBytes, pdfFileName);
                    } else {

                        Log.d("pdf", "aucun pdf ass");
                    }
                }
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long pwId = pws.get(position).getId();
                if (onItemClickListener2 != null) {
                    onItemClickListener2.onItemClick(pwId);
                }
            }
        });

        PW pw = pws.get(position);

        id.setText(String.valueOf(pw.getId()));
        title.setText(pw.getTitle());
        objectif.setText(pw.getObjectif());

        return convertView;
    }

}
