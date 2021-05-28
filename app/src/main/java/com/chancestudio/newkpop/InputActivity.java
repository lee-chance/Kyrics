package com.chancestudio.newkpop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chancestudio.newkpop.Popup.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class InputActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private ImageView ImageView_input;
    private Uri filePath;

    private ProgressDialog progressDialog;

    LinearLayout LinearLayout_View1, LinearLayout_View2, LinearLayout_View3, LinearLayout_View4, LinearLayout_translate, LinearLayout_pronounce;
    TextInputEditText EditText_title, EditText_singer, EditText_youtube;
    EditText EditText_lyrics;
    Button Button_next, Button_previous;
    ImageView ImageView_step;

    int viewNum = 1;

    String Lyrics_all;
    String title, singer, youtube_address;
    String[] lyrics, pronounce, translate;
    EditText[] ET_pronounce;
    EditText[] ET_translate;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String id = mStore.collection("songList").document().getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        //선언
        {
            ImageView_input = (ImageView) findViewById(R.id.ImageView_input);
            EditText_title = (TextInputEditText) findViewById(R.id.EditText_title);
            EditText_singer = (TextInputEditText) findViewById(R.id.EditText_singer);
            EditText_youtube = (TextInputEditText) findViewById(R.id.EditText_youtube);
            Button_next = (Button) findViewById(R.id.Button_next);
            Button_previous = (Button) findViewById(R.id.Button_previous);
            LinearLayout_View1 = (LinearLayout) findViewById(R.id.LinearLayout_View1);
            LinearLayout_View2 = (LinearLayout) findViewById(R.id.LinearLayout_View2);
            LinearLayout_View3 = (LinearLayout) findViewById(R.id.LinearLayout_View3);
            LinearLayout_View4 = (LinearLayout) findViewById(R.id.LinearLayout_View4);
            LinearLayout_translate = (LinearLayout) findViewById(R.id.LinearLayout_translate);
            LinearLayout_pronounce = (LinearLayout) findViewById(R.id.LinearLayout_pronounce);
            ImageView_step = (ImageView) findViewById(R.id.ImageView_step);
            EditText_lyrics = (EditText) findViewById(R.id.EditText_lyrics);
        }

        ImageView_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        onClickNext();
        onClickBack();
    }

    public void onClickNext() {
        Button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (viewNum) {
                    case 1:
                        viewNum++;
                        LinearLayout_View1.setVisibility(View.GONE);
                        LinearLayout_View2.setVisibility(View.VISIBLE);
                        ImageView_step.setImageResource(R.drawable.step2);
                        Button_previous.setVisibility(View.VISIBLE);
                        Button_next.setText(R.string.next);
                        title = EditText_title.getText().toString();
                        singer = EditText_singer.getText().toString();
                        youtube_address = EditText_youtube.getText().toString();

                        int idx = youtube_address.indexOf("=");
                        youtube_address = youtube_address.substring(idx + 1);

                        break;

                    case 2:
                        Lyrics_all = EditText_lyrics.getText().toString();
                        if (Lyrics_all.length() == 0) {
                            Toast.makeText(getApplicationContext(), R.string.Empty, Toast.LENGTH_SHORT).show();
                        } else {
                            lyrics = Lyrics_all.split("\\n");
                            viewNum++;
                            LinearLayout_View2.setVisibility(View.GONE);
                            LinearLayout_View3.setVisibility(View.VISIBLE);
                            ImageView_step.setImageResource(R.drawable.step3);
                            Button_previous.setVisibility(View.VISIBLE);
                            Button_next.setText(R.string.next);

                            pronounce = new String[lyrics.length];

                            for (int i = 0; i < lyrics.length; i++) {
                                LinearLayout[] LL_pronounce = new LinearLayout[lyrics.length];
                                LL_pronounce[i] = new LinearLayout(getApplicationContext());
                                LinearLayout.LayoutParams LL_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                LL_params.setMargins(5, 5, 5, 5);
                                LL_pronounce[i].setLayoutParams(LL_params);
                                LL_pronounce[i].setOrientation(LinearLayout.VERTICAL);
                                LinearLayout_pronounce.addView(LL_pronounce[i]);

                                TextView[] TV_pronounce = new TextView[lyrics.length];
                                TV_pronounce[i] = new TextView(getApplicationContext());
                                LinearLayout.LayoutParams TV_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TV_params.setMargins(0, 0, 0, 5);
                                TV_pronounce[i].setLayoutParams(TV_params);
                                TV_pronounce[i].setText(lyrics[i]);
                                LL_pronounce[i].addView(TV_pronounce[i]);

                                ET_pronounce = new EditText[lyrics.length];
                                ET_pronounce[i] = new EditText(getApplicationContext());
                                LinearLayout.LayoutParams ET_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                ET_pronounce[i].setLayoutParams(ET_params);
                                ET_pronounce[i].setHint(R.string.write_pronounce);
                                ET_pronounce[i].setSingleLine();
                                final int finalI = i;
                                ET_pronounce[i].addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        pronounce[finalI] = editable.toString();
                                    }
                                });
                                LL_pronounce[i].addView(ET_pronounce[i]);
                            }
                        }
                        break;

                    case 3:
                        viewNum++;
                        LinearLayout_View3.setVisibility(View.GONE);
                        LinearLayout_View4.setVisibility(View.VISIBLE);
                        ImageView_step.setImageResource(R.drawable.step4);
                        Button_previous.setVisibility(View.VISIBLE);
                        Button_next.setText(R.string.complete);

                        translate = new String[lyrics.length];

                        for (int i = 0; i < lyrics.length; i++) {
                            LinearLayout[] LL_translate = new LinearLayout[lyrics.length];
                            TextView[] TV_translate = new TextView[lyrics.length];
                            ET_translate = new EditText[lyrics.length];

                            LL_translate[i] = new LinearLayout(getApplicationContext());
                            TV_translate[i] = new TextView(getApplicationContext());
                            ET_translate[i] = new EditText(getApplicationContext());

                            LinearLayout.LayoutParams LL_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            LL_params.setMargins(5, 5, 5, 5);
                            LL_translate[i].setLayoutParams(LL_params);
                            LL_translate[i].setOrientation(LinearLayout.VERTICAL);
                            LinearLayout_translate.addView(LL_translate[i]);

                            LinearLayout.LayoutParams TV_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            TV_params.setMargins(0, 0, 0, 5);
                            TV_translate[i].setLayoutParams(TV_params);
                            TV_translate[i].setText(lyrics[i]);
                            LL_translate[i].addView(TV_translate[i]);

                            LinearLayout.LayoutParams ET_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            ET_translate[i].setLayoutParams(ET_params);
                            ET_translate[i].setHint(R.string.write_translate);
                            ET_translate[i].setSingleLine();
                            final int finalI = i;
                            ET_translate[i].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    translate[finalI] = editable.toString();
                                }
                            });
                            LL_translate[i].addView(ET_translate[i]);

                        }
                        break;

                    default:

                        newFirebaseDB();
                        uploadFile();

                        Intent intent = new Intent(InputActivity.this, LoadingDialog.class);
                        startActivity(intent);

                        finish();
                }
            }
        });
    }

    public void onClickBack() {
        Button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewNum) {
                    case 4:
                        viewNum--;
                        LinearLayout_View4.setVisibility(View.GONE);
                        LinearLayout_View3.setVisibility(View.VISIBLE);
                        ImageView_step.setImageResource(R.drawable.step3);
                        Button_previous.setVisibility(View.VISIBLE);
                        Button_next.setText(R.string.next);

                        LinearLayout_translate.removeAllViews();

                        break;

                    case 3:
                        viewNum--;
                        LinearLayout_View3.setVisibility(View.GONE);
                        LinearLayout_View2.setVisibility(View.VISIBLE);
                        ImageView_step.setImageResource(R.drawable.step2);
                        Button_previous.setVisibility(View.VISIBLE);
                        Button_next.setText(R.string.next);

                        LinearLayout_pronounce.removeAllViews();

                        break;

                    case 2:
                        viewNum--;
                        LinearLayout_View2.setVisibility(View.GONE);
                        LinearLayout_View1.setVisibility(View.VISIBLE);
                        ImageView_step.setImageResource(R.drawable.step1);
                        Button_previous.setVisibility(View.INVISIBLE);
                        Button_next.setText(R.string.next);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                filePath = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    ImageView_input.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void uploadFile() {
        if (filePath != null) {
            progressDialog = new ProgressDialog(this);

            progressDialog.setTitle(R.string.uploading_title);
            progressDialog.show();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            String filename = title + "_" + singer + ".jpg";

            StorageReference storageReference = storage.getReferenceFromUrl("gs://newkpop-84c7f.appspot.com").child("images/" + filename);

            storageReference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "%" + R.string.uploading_msg);
                }
            });
        }
    }

    private void newFirebaseDB() {

        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("title", title);
        post.put("singer", singer);
        post.put("youtube_address", youtube_address);
        post.put("lyrics", ArraytoString(lyrics));
        post.put("pronounce", ArraytoString(pronounce));
        post.put("translate", ArraytoString(translate));
        post.put("timestamp", FieldValue.serverTimestamp());

        String document_id = title + "_" + singer;

        mStore.collection("songList").document(document_id).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InputActivity.this, "success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InputActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String ArraytoString(String[] values) {
        StringBuilder str = new StringBuilder();
        if (values.length > 0) {
            str.append(values[0]);
            for (int i = 1; i < values.length; i++) {
                str.append("/ ");
                str.append(values[i]);
            }
        }
        return str.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
