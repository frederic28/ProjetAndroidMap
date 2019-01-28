package kristof.ccm.ccm2018goooglemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addUser extends AppCompatActivity {

    private String TAG = "Fred";
    private TextView nameSaisie;
    private TextView phoneSaisie;
    private DocumentReference userCollection;
    private FirebaseFirestore fireStoreFireBase;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        nameSaisie = findViewById (R.id.nameCreate);
        phoneSaisie = findViewById (R.id.phoneCreate);

        fireStoreFireBase = FirebaseFirestore.getInstance();
        userCollection = fireStoreFireBase.collection("user").document();


    }


    public void onClickCreateUtilisateur(View view) {

        Map<String, Object> map1 = new HashMap<>();

        map1.put("name", nameSaisie.getText().toString());
        map1.put("phoneNumber", phoneSaisie.getText().toString());


        userCollection.set(map1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent monIntent = new Intent(addUser.this, ActivitePrincipale.class);
                        Bundle b = new Bundle();
                        b.putString("name", nameSaisie.getText().toString());
                        b.putString("phoneNumber", phoneSaisie.getText().toString());
                        b.putString("userId", userCollection.getId());
                        monIntent.putExtras(b);
                        startActivity(monIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addUser.this, "User incorrect ou non connu", Toast.LENGTH_LONG).show();
                    }
                });


    }
}
