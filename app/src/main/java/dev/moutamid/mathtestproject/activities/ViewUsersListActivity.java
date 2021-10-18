package dev.moutamid.mathtestproject.activities;

import static android.view.LayoutInflater.from;
import static dev.moutamid.mathtestproject.R.id.parent;
import static dev.moutamid.mathtestproject.R.id.usersListRecyclerView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import dev.moutamid.mathtestproject.R;
import dev.moutamid.mathtestproject.models.UserModel;
import dev.moutamid.mathtestproject.utils.Constants;
import dev.moutamid.mathtestproject.utils.Helper;
import dev.moutamid.mathtestproject.utils.Utils;

public class ViewUsersListActivity extends AppCompatActivity {
    private static final String TAG = "ViewUsersListActivity";
    private Context context = ViewUsersListActivity.this;

    private ArrayList<UserModel> tasksArrayList;


    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users_list);

        tasksArrayList = Utils.getArrayList(Constants.DETAILS_LIST, UserModel.class);


        initRecyclerView();
    }

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(usersListRecyclerView);
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
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
            View view = from(parent.getContext()).inflate(R.layout.layout_item_users_list, parent, false);
            return new ViewHolderRightMessage(view);
        }

        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                      int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }


        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            int p = holder.getAdapterPosition();

            UserModel model = tasksArrayList.get(p);

            holder.firstName.setText(model.getFirstName());
            holder.lastName.setText(model.getLastName());
            holder.email.setText(model.getEmail1());
            holder.nmbr.setText(model.getNumber1());

/*
            Bitmap resizeBitmap = resize(model.getImageBitmap(),
                    100,
                    100);

            holder.imageView.setImageBitmap(resizeBitmap);
*/

            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;*/

/*
            holder.imageView.setImageBitmap(
                    decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));
*/

            int bitmapSize = Helper.StringToBitMap(Utils.getString(model.getImageBitmap())).getByteCount();

            if (bitmapSize > Constants.MAX_BITMAP_SIZE) {
                throw new RuntimeException(
                        "Canvas: trying to draw too large(" + bitmapSize + "bytes) bitmap.");
            } else {

                Glide.with(context)
                        .load(Helper.StringToBitMap(Utils.getString(model.getImageBitmap())))
//                        .load(model.getImageBitmap())
                        .apply(new RequestOptions()
                                .placeholder(R.color.gray)
                                .error(R.color.gray)
                        )
                        .into(holder.imageView);
            }


            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(context, RegisterStudentActivity.class)
                            .putExtra(Constants.PARAMS, p));

                }
            });

            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Are you sure?")
                            .setMessage("Do you really want to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    tasksArrayList.remove(p);

                                    adapter.notifyDataSetChanged();

                                    Utils.store(Constants.DETAILS_LIST, tasksArrayList);

                                    Utils.toast("Done");

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                    return false;
                }
            });

        }

        /*private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float) maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
                return image;
            } else {
                return image;
            }
        }*/

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends ViewHolder {

            TextView firstName, lastName, email, nmbr;
            ImageView imageView;
            RelativeLayout parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                firstName = v.findViewById(R.id.firstNameUserList);
                lastName = v.findViewById(R.id.lastNameUserList);
                email = v.findViewById(R.id.emailUserList);
                nmbr = v.findViewById(R.id.numberUserList);
                imageView = v.findViewById(R.id.imageviewUserList);
                parentLayout = v.findViewById(R.id.parentLayoutUserList);

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}