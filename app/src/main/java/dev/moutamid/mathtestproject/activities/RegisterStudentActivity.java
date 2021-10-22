package dev.moutamid.mathtestproject.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.moutamid.mathtestproject.R;
import dev.moutamid.mathtestproject.databinding.ActivitySignUpBinding;
import dev.moutamid.mathtestproject.models.UserModel;
import dev.moutamid.mathtestproject.utils.Constants;
import dev.moutamid.mathtestproject.utils.Helper;
import dev.moutamid.mathtestproject.utils.Utils;

public class RegisterStudentActivity extends AppCompatActivity {
    private static final String TAG = "RegisterStudentActivity";
    private Context context = RegisterStudentActivity.this;
    Bitmap bitmap;

    private ArrayList<UserModel> userDetailsArrayList =
            Utils.getArrayList(Constants.DETAILS_LIST, UserModel.class);

    private ActivitySignUpBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        checkIfContactsIntentHasData();

        checkIfUsersListIntentHasData();

        setonclickOnAddMoreNumber();

        setonclickOnAddMoreEmail();

        doneBtnListener();

        imageClickListener();

    }

    boolean isEditing = false;
    int position;

    private void checkIfUsersListIntentHasData() {
        if (getIntent().hasExtra(Constants.PARAMS)) {
            position = getIntent().getIntExtra(Constants.PARAMS, 0);
            isEditing = true;

            UserModel model = userDetailsArrayList.get(getIntent().getIntExtra(Constants.PARAMS, 0));

            b.nameFirstEditText.setText(model.getFirstName());
            b.nameLastEditText.setText(model.getLastName());

            b.numberEditText1.setText(model.getNumber1());

            b.emailEditText1.setText(model.getEmail1());
            bitmap = Helper.StringToBitMap(Utils.getString(model.getImageBitmap()));
//            bitmap = model.getImageBitmap();
            b.imageviewSignup.setImageBitmap(bitmap);
//            b.imageviewSignup.setImageBitmap(model.getImageBitmap());

            b.emailEditText2T.setText(model.getEmail2());
            b.emailEditText3T.setText(model.getEmail3());
            b.emailEditText4T.setText(model.getEmail4());
            b.emailEditText5T.setText(model.getEmail5());
            b.emailEditText6T.setText(model.getEmail6());
            b.emailEditText7T.setText(model.getEmail7());
            b.emailEditText8T.setText(model.getEmail8());
            b.emailEditText9T.setText(model.getEmail9());
            b.emailEditText10T.setText(model.getEmail10());

            b.numberEditText2T.setText(model.getNumber2());
            b.numberEditText3T.setText(model.getNumber3());
            b.numberEditText4T.setText(model.getNumber4());
            b.numberEditText5T.setText(model.getNumber5());
            b.numberEditText6T.setText(model.getNumber6());
            b.numberEditText7T.setText(model.getNumber7());
            b.numberEditText8T.setText(model.getNumber8());
            b.numberEditText9T.setText(model.getNumber9());
            b.numberEditText10T.setText(model.getNumber10());

        }
    }

    private void checkIfContactsIntentHasData() {
        if (getIntent().hasExtra(Constants.CONTACT_NAME)) {
            String name = getIntent().getStringExtra(Constants.CONTACT_NAME);
            String number = getIntent().getStringExtra(Constants.CONTACT_NUMBER);

            String[] names = name.split(" ");

            b.nameFirstEditText.setText(names[0]);

            if (names.length > 1)
                b.nameLastEditText.setText(names[1]);

            b.numberEditText1.setText(number);

            bitmap = Helper.openPhoto(
                    context, Helper.getContactIDFromNumber(number, context));
            b.imageviewSignup.setImageBitmap(bitmap);


        }
    }

    private void imageClickListener() {
        b.imageviewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final CharSequence[] items = {"Take a photo", "Choose from internal storage", "Browse photo online"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        switch (position) {
                            case 0:
                                takePhoto();
                                break;
                            case 1:
                                chooseFromStorage();
                                break;
                            case 2:
                                showImageOnlineDialog();
                                break;

                        }

                    }
                });

                dialog = builder.create();
                dialog.show();

            }
        });
    }

    Dialog dialog;

    private void showImageOnlineDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_online_images);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.findViewById(R.id.searchImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CODE HERE

                EditText editText = dialog.findViewById(R.id.searchEdittextOnline);

                if (!editText.getText().toString().isEmpty())
                    initRecyclerView(editText.getText().toString());

            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private ArrayList<String> onlineImgsLinksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    //    private RequestQueue mRequestQueue;
    private ProgressDialog progressDialog;

    private void initRecyclerView(String searchKey) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        String url = "https://pixabay.com/api/?key=23913537-cb7ca49b7fdc3a5be562c5f00&q=" + searchKey;
//                String url = "http://api.brainshop.ai/get?bid=160533&key=HRVPYcwch6xZXykp&uid=[uid]&msg=[msg]" + message;

        // creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String botResponse = response.toString();
                Log.d(TAG, "onResponse: " + botResponse);
                Pattern p = Pattern.compile("\"previewURL\":\"(.*?)\",");

                Matcher m = p.matcher(botResponse);

                int counter = 0;

                while (m.find()) {
                    String bar = m.group(1);

                    String link = bar.replaceAll("\\\\", "");

                    Log.d(TAG, "onResponse: " + counter);
                    Log.d(TAG, "onResponse: m.group(1): " + link);
                    counter++;

                    if (counter <= 50)
                        onlineImgsLinksArrayList.add(link);
                }

                Log.d(TAG, "onResponse: size: " + onlineImgsLinksArrayList.size());

                conversationRecyclerView = dialog.findViewById(R.id.imageSearchRecyclerView);

                adapter = new RecyclerViewAdapterMessages();
                int mNoOfColumns = 3;
                //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
                conversationRecyclerView.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
                conversationRecyclerView.setHasFixedSize(true);
                conversationRecyclerView.setNestedScrollingEnabled(false);

                conversationRecyclerView.setAdapter(adapter);

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Utils.toast(error.toString());
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest);

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_image_searcher, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position1) {
            int position = holder.getAdapterPosition();

            Glide.with(context)
                    .load(onlineImgsLinksArrayList.get(position))
                    .apply(new RequestOptions()
                            .placeholder(R.color.gray)
                            .error(R.color.gray)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.title);

            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                bitmap = Glide
                                        .with(context)
                                        .asBitmap()
                                        .load(onlineImgsLinksArrayList.get(position))
                                        .addListener(new RequestListener<Bitmap>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                        b.imageviewSignup.setImageBitmap(bitmap);
                                                        dialog.dismiss();

                                                    }
                                                });
                                                return false;
                                            }
                                        })
                                        .submit()
                                        .get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            });

        }

        @Override
        public int getItemCount() {
            if (onlineImgsLinksArrayList == null)
                return 0;
            return onlineImgsLinksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            ImageView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.imageviewLayout);

            }
        }

    }

    Uri imageUri;

    public void takePhoto() {
        ImagePicker.with(RegisterStudentActivity.this)
                .cameraOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();

        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(getFilesDir(), "Pic.jpg");
//        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, 8888);*/
    }

    private void chooseFromStorage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 9999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999 && resultCode == RESULT_OK) {

            //imageUri = data.getData();
            Uri imageUri = data.getData();

/*

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");

            progressBarImageView.playAnimation();
            progressBarImageView.setVisibility(View.VISIBLE);
//            progressDialog.show();

            final StorageReference filePath = storageReference
                    .child(mAuth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
//            final StorageReference filePath = storageReference.child("sliders")
//                    .child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri photoUrl) {

//                            TextView othertxt = findViewById(R.id.othertextregistration);
//                            othertxt.setText(photoUrl.toString());

                            profileImageUrl = photoUrl.toString();

                            databaseReference.child("users")
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child("profileUrl")
                                    .setValue(profileImageUrl)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        utils.storeString(
                                                                getActivity(),
                                                                "profileUrl",
                                                                profileImageUrl
                                                        );
*/

            b.imageviewSignup.setImageURI(data.getData());

            ContentResolver contentResolver = getContentResolver();
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 8888 && resultCode == RESULT_OK) {
            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);
            ContentResolver cr = getContentResolver();
            try {
                bitmap = android.provider.MediaStore.Images.Media
                        .getBitmap(cr, selectedImage);

                b.imageviewSignup.setImageBitmap(bitmap);

            } catch (Exception e) {
                Log.e("Camera", e.toString());
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri uri = data.getData();

            b.imageviewSignup.setImageURI(data.getData());

            ContentResolver contentResolver = getContentResolver();
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    private void doneBtnListener() {
        b.doneBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null) {
                    Utils.toast("Please select an image!");
                    return;
                }

                if (b.nameFirstEditText.getText().toString().isEmpty()) {
                    Utils.toast("Please enter first name!");
                    return;
                }

                if (b.nameLastEditText.getText().toString().isEmpty()) {
                    Utils.toast("Please enter last name!");
                    return;
                }

                if (b.numberEditText1.getText().toString().isEmpty()) {
                    Utils.toast("Please enter first name!");
                    return;
                }

                if (b.emailEditText1.getText().toString().isEmpty()) {
                    Utils.toast("Please enter first name!");
                    return;
                }

                UserModel userModel = new UserModel();

                userModel.firstName = b.nameFirstEditText.getText().toString();
                userModel.lastName = b.nameLastEditText.getText().toString();
                userModel.number1 = b.numberEditText1.getText().toString();
                userModel.email1 = b.emailEditText1.getText().toString();

                userModel.imageBitmap = userModel.email1 + userModel.number1;
//Helper.StringToBitMap(Utils.getString(model.getImageBitmap()))
                Utils.store(userModel.email1 + userModel.number1, Helper.BitMapToString(bitmap));

                userModel.email2 = b.emailEditText2T.getText().toString();
                userModel.email3 = b.emailEditText3T.getText().toString();
                userModel.email4 = b.emailEditText4T.getText().toString();
                userModel.email5 = b.emailEditText5T.getText().toString();
                userModel.email6 = b.emailEditText6T.getText().toString();
                userModel.email7 = b.emailEditText7T.getText().toString();
                userModel.email8 = b.emailEditText8T.getText().toString();
                userModel.email9 = b.emailEditText9T.getText().toString();
                userModel.email10 = b.emailEditText10T.getText().toString();

                userModel.number2 = b.numberEditText2T.getText().toString();
                userModel.number3 = b.numberEditText3T.getText().toString();
                userModel.number4 = b.numberEditText4T.getText().toString();
                userModel.number5 = b.numberEditText5T.getText().toString();
                userModel.number6 = b.numberEditText6T.getText().toString();
                userModel.number7 = b.numberEditText7T.getText().toString();
                userModel.number8 = b.numberEditText8T.getText().toString();
                userModel.number9 = b.numberEditText9T.getText().toString();
                userModel.number10 = b.numberEditText10T.getText().toString();

//                ArrayList<UserModel> usersDetailsList = Utils.getArrayList(Constants.DETAILS_LIST, UserModel.class);

                if (isEditing) {
                    userDetailsArrayList.remove(position);
                    userDetailsArrayList.add(position, userModel);

                } else userDetailsArrayList.add(userModel);

                Utils.store(Constants.DETAILS_LIST, userDetailsArrayList);

                Utils.toast("Done");

                finish();
                startActivity(new Intent(context, ViewUsersListActivity.class));

            }
        });
    }

    private void setonclickOnAddMoreNumber() {
        b.addmoreNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (b.addmoreNumber.getText().toString().equals(getString(R.string.add_more))) {

                if (b.numberEditText2.getVisibility() == View.GONE) {
                    b.numberEditText2.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText3.getVisibility() == View.GONE) {
                    b.numberEditText3.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText4.getVisibility() == View.GONE) {
                    b.numberEditText4.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText5.getVisibility() == View.GONE) {
                    b.numberEditText5.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText6.getVisibility() == View.GONE) {
                    b.numberEditText6.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText7.getVisibility() == View.GONE) {
                    b.numberEditText7.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText8.getVisibility() == View.GONE) {
                    b.numberEditText8.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText9.getVisibility() == View.GONE) {
                    b.numberEditText9.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.numberEditText10.getVisibility() == View.GONE) {
                    b.numberEditText10.setVisibility(View.VISIBLE);
                    Utils.toast("You can only add max 10 numbers");

                    b.addmoreNumber.setVisibility(View.GONE);
//                        b.addmoreNumber.setText("");
                }

//                }

            }
        });
    }

    private void setonclickOnAddMoreEmail() {
        b.addmoreEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (b.addmoreEmail.getText().toString().equals(getString(R.string.add_more))) {

                if (b.emailEditText2.getVisibility() == View.GONE) {
                    b.emailEditText2.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText3.getVisibility() == View.GONE) {
                    b.emailEditText3.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText4.getVisibility() == View.GONE) {
                    b.emailEditText4.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText5.getVisibility() == View.GONE) {
                    b.emailEditText5.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText6.getVisibility() == View.GONE) {
                    b.emailEditText6.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText7.getVisibility() == View.GONE) {
                    b.emailEditText7.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText8.getVisibility() == View.GONE) {
                    b.emailEditText8.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText9.getVisibility() == View.GONE) {
                    b.emailEditText9.setVisibility(View.VISIBLE);
                    return;
                }
                if (b.emailEditText10.getVisibility() == View.GONE) {
                    b.emailEditText10.setVisibility(View.VISIBLE);

                    b.addmoreEmail.setVisibility(View.GONE);
                    Utils.toast("You can only add max 10 emails");
//                        b.addmoreNumber.setText("");
                }

//                }

            }
        });
    }
}