package dev.moutamid.mathtestproject.activities;

import static android.view.LayoutInflater.from;
import static dev.moutamid.mathtestproject.R.id.contactsListRecyclerView;
import static dev.moutamid.mathtestproject.R.layout.activity_listview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.ArrayList;

import dev.moutamid.mathtestproject.R;
import dev.moutamid.mathtestproject.models.SimpleContactModel;
import dev.moutamid.mathtestproject.utils.Constants;
import dev.moutamid.mathtestproject.utils.Helper;
import dev.moutamid.mathtestproject.utils.Utils;

public class ContactPickerActivity extends AppCompatActivity {
    private static final String TAG = "ContactPickerActivity";
    private Context context = ContactPickerActivity.this;

    //    List<ContactModel> contactModelArrayList;
    ArrayList<SimpleContactModel> simpleContactModelArrayList = new ArrayList<>();
    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        findViewById(R.id.skipBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Do you really want to skip finding your contact? \nIf your number is not in the list then you can skip!")
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                startActivity(new Intent(context, RegisterStudentActivity.class));
                            }
                        }).show();
            }
        });

        Utils.toast("Find your number");

        new GETCONTACTS().execute();

    }

    private ProgressDialog progressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing() && progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private class GETCONTACTS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ContactPickerActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            simpleContactModelArrayList = Helper.getContactList(ContactPickerActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            initRecyclerView();
        }
    }

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(contactsListRecyclerView);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

    }

    private class RecyclerViewAdapterMessages extends Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(activity_listview, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position1) {

            int position = holder.getAdapterPosition();

            holder.name.setText(simpleContactModelArrayList.get(position).getName());

            holder.nmbr.setText(simpleContactModelArrayList.get(position).getNumber());

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.imageView.setImageBitmap(Helper.openPhoto(
                            context,
                            Helper.getContactIDFromNumber(
                                    simpleContactModelArrayList.get(
                                            position).getNumber(), context)
                    ));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(context, RegisterStudentActivity.class)
                                    .putExtra(Constants.CONTACT_NAME, simpleContactModelArrayList.get(
                                            position).getName())
                                    .putExtra(Constants.CONTACT_NUMBER, simpleContactModelArrayList.get(
                                            position).getNumber())

                            );
                        }
                    }, 1000);

                }
            });

        }


        @Override
        public int getItemCount() {
            if (simpleContactModelArrayList == null)
                return 0;
            return simpleContactModelArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView nmbr, name;
            ImageView imageView;
            LinearLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                nmbr = v.findViewById(R.id.nmbrtextView);
                name = v.findViewById(R.id.nameTextview);
                imageView = v.findViewById(R.id.imageview);
                parentLayout = v.findViewById(R.id.parentLayout);

            }
        }

    }
}