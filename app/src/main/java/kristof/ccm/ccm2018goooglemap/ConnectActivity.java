package kristof.ccm.ccm2018goooglemap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class ConnectActivity extends AppCompatActivity {

    private String TAG = "Fred";
    private DocumentReference userCollection;
    private CollectionReference userDocuments;
    private TextView nameSaisie;
    private TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        FirebaseFirestore fireStoreFireBase =  FirebaseFirestore.getInstance();

        nameSaisie = findViewById (R.id.name);
        phone = findViewById (R.id.phone);

        userCollection = fireStoreFireBase.collection("user").document();
        userDocuments = fireStoreFireBase.collection("user");


    }

    public void onClickSeConnecter(View view) {

        userDocuments.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    String name = doc.get("name").toString();
                    String number = doc.get("phoneNumber").toString();
                    if (number != null && name != null && name.equals(nameSaisie.getText().toString()) && number.equals(phone.getText().toString())) {
                        Intent monIntent = new Intent(ConnectActivity.this, ActivitePrincipale.class);
                        Bundle b = new Bundle();
                        b.putString("name", nameSaisie.getText().toString());
                        b.putString("phoneNumber", phone.getText().toString());
                        b.putString("userId", doc.getId());
                        monIntent.putExtras(b);
                        startActivity(monIntent);
                    } else {

                        Toast.makeText(ConnectActivity.this, "User incorrect ou non connu", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void onClickCreerCompte(View view) {
        Intent monIntent = new Intent(this, addUser.class);
        startActivity(monIntent);
    }
}
