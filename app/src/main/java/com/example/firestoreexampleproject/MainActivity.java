package com.example.firestoreexampleproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.lang.UCharacterEnums;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.net.NoRouteToHostException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private DocumentReference NoteRef = db.collection("NoteBook").document("My first Notebook");
    private CollectionReference notebookRef = db.collection("NoteBook");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextTitle=findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if(error!= null)
            {return;

            }
                String data = "";
                for(QueryDocumentSnapshot documentSnapshot : value )
                {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    String documentid =note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();
                    data += "Document Id : "+ documentid +"\nTitle : "+ title + "\nDescription : "+desc + "\n\n";
                }
                textViewData.setText(data);

            }
        });

    }

    public void addNote(View v)
    {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        Note note = new Note(title,description);
        notebookRef.add(note);


    }
    public void updateDescription(View view) {
    String description = editTextDescription.getText().toString();
    Map<String ,Object> note = new HashMap<>();
    note.put(KEY_DESCRIPTION,description);
    NoteRef.set(note , SetOptions.merge());
//    NoteRef.update(note);
    }

    public void deleteDescription(View view) {
        NoteRef.update(KEY_DESCRIPTION,FieldValue.delete());
    }

    public void deleteNote(View view) {
        NoteRef.delete();
    }
    public void loadNote(View v) {
         notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
             @Override
             public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                 String data = "";
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots )
                {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    String documentid =note.getDocumentId();
                    String title = note.getTitle();
                    String desc = note.getDescription();
                    data += "Document Id : "+ documentid +"\nTitle : "+ title + "\nDescription : "+desc + "\n";
                }
                textViewData.setText(data);
             }
         })
         .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {

                     }
                 });
    }


}