package kristof.ccm.ccm2018goooglemap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import javax.annotation.Nullable;

public class LaunchActivity extends AppCompatActivity {
    private EditText nameET;
    private EditText phoneNumberET;
    private FirebaseFirestore firebaseFirestore;
    private String nomDeBase = "user";
    private Boolean userExist = false;
    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        nameET = findViewById(R.id.nameET);
        phoneNumberET = findViewById(R.id.phoneNumberET);
    }

    public void connectBt(View view) {
        if(phoneNumberET.getText().toString().matches("") || nameET.getText().toString().matches("")){
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_LONG).show();
        } else {
            CollectionReference userCollection = FirebaseFirestore.getInstance().collection(nomDeBase);
            userCollection.whereEqualTo("name", nameET.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userExist = true;
                            Log.d("geo", "user exist !");
                            if(Objects.requireNonNull(document.getString("phoneNumber")).matches(phoneNumberET.getText().toString())){
                                userId = document.getId();
                                login();
                            } else {
                                Toast.makeText(LaunchActivity.this, "Le n° de tel est incorrect.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });

            if(!userExist){
                Log.d("geo", "user don't exist !");
                /*
                User user = new User(nameET.getText().toString(), phoneNumberET.getText().toString());

                userCollection
                        .add(user)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.d("LaunchActivity", "Insertion réussie");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("LaunchActivity", "Insertion ratée");
                            }
                        });
                userId = userCollection.getId();
                login();
                */
            }
        }

    }

    private void login() {
        Intent monIntent = new Intent(this, ActivitePrincipale.class);
        Bundle b = new Bundle();
        b.putString("name", nameET.getText().toString());
        b.putString("phoneNumber", phoneNumberET.getText().toString());
        b.putString("userId", userId);
        monIntent.putExtras(b);
        startActivity(monIntent);
    }
}
