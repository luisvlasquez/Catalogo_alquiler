package com.example.alquiler_articulos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateArticleFragment extends Fragment {
    private static final int RESULT_OK =-1 ;
    private View view;
    private DatabaseReference RootReference, myDta;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private Button BtnCreate;
    private EditText ArticleName, ArticleDesc, Price;
    private RecyclerView recyclerView;
    private String currentArticleId, currentUserId, Image, Email;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Articles> articlesArrayList = new ArrayList<>();
    private Button BtnUpload;
    private StorageReference myStorage;
    private static final int GALERY_INTENT = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.create_articles, container, false);
        BtnUpload = view.findViewById(R.id.btnUploadImg);
        firebaseAuth = FirebaseAuth.getInstance();
        RootReference = FirebaseDatabase.getInstance().getReference();
        currentArticleId = RootReference.push().getKey();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        Email=firebaseAuth.getCurrentUser().getEmail();
        myStorage = FirebaseStorage.getInstance().getReference();
        BtnCreate = view.findViewById(R.id.btnCreateArticle);
        ArticleName = view.findViewById(R.id.TxtArticleName);
        ArticleDesc = view.findViewById(R.id.TxtArticleDesc);
        Price = view.findViewById(R.id.TextPrice);
        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// * es para que tome todas las extenciones
                startActivityForResult(intent, GALERY_INTENT);
            }
        });
       return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_INTENT && resultCode == RESULT_OK) {
            final Uri uri = data.getData();
            final StorageReference filepath = myStorage.child("fotos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (ArticleName.getText().length() == 0 || ArticleDesc.getText().length() == 0 || Price.getText().length() == 0) {
                                Alerter.create(getActivity()).setText(R.string.empty)
                                        .setTitle("Error").setBackgroundColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_format_list)
                                        .enableVibration(true)
                                        .setDismissable(true).show();
                                Toast.makeText(getContext(), R.string.empty, Toast.LENGTH_LONG).show();
                            } else {
                                Alerter.create(getActivity())
                                        .setTitle(R.string.upload_img)
                                        .setText(R.string.upload_img_s)
                                        .setIcon(R.drawable.ic_image)
                                        .setBackgroundColorRes(R.color.purble_black)
                                        .enableVibration(true)
                                        .setDismissable(true)
                                        .enableProgress(true)
                                        .show();
                                final String IdOn, NameOn, DescriptionOn, UserIdOn, EmailOn;
                                Double PriceOn;
                                String url = uri.toString();
                                IdOn = currentArticleId;
                                NameOn = ArticleName.getText().toString();
                                DescriptionOn = ArticleDesc.getText().toString();
                                PriceOn = Double.parseDouble(Price.getText().toString());
                                UserIdOn = currentUserId;
                                EmailOn=Email;
                                Map<String, Object> ArticleData = new HashMap<>();
                                ArticleData.put("IdArticle", IdOn);
                                ArticleData.put("Name", NameOn);
                                ArticleData.put("Description", DescriptionOn);
                                ArticleData.put("Price", PriceOn);
                                ArticleData.put("Image", url);
                                ArticleData.put("UserId", UserIdOn);
                                ArticleData.put("EmailCurrentUser",EmailOn);
                                RootReference.child("Articles").push().setValue(ArticleData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), R.string.create_and_upload, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), R.string.upload_error, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}

