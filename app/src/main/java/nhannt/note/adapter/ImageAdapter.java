package nhannt.note.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import nhannt.note.R;

/**
 * Created by IceMan on 1/13/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<String> mData;
    private LayoutInflater mLayoutInflater;

    public ImageAdapter(Context mContext, ArrayList<String> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_image, null);
        ImageViewHolder holder = new ImageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position)).placeholder(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(holder.ivImage);
        holder.id = position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int id;
        ImageView ivImage;
        ImageView btDelete;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_image_item);
            btDelete = (ImageView) itemView.findViewById(R.id.bt_delete_image_item);
            btDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_delete_image_item:
                    confirmDialog(id);
                    break;
            }
        }
    }

    private void confirmDialog(final int imagePos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.warning));
        builder.setMessage(mContext.getString(R.string.delete_photo_question));
        builder.setPositiveButton(mContext.getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mData.remove(imagePos);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void refreshList(ArrayList<String> lstImage) {
        mData.clear();
        mData.addAll(lstImage);
        notifyDataSetChanged();
    }
}
