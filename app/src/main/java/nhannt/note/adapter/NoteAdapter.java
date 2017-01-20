package nhannt.note.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nhannt.note.R;
import nhannt.note.activity.DetailActivity;
import nhannt.note.model.Note;

/**
 * Created by IceMan on 12/30/2016.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context mContext;
    private ArrayList<Note> mData;
    private LayoutInflater mLayoutInflater;

    public NoteAdapter(Context mContext, ArrayList<Note> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_note_main, null);
        NoteViewHolder holder = new NoteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        holder.tvNoteTitle.setText(mData.get(position).getTitle());
        holder.tvNoteContent.setText(mData.get(position).getContent());
        holder.cardView.setCardBackgroundColor(mData.get(position).getColor());
        holder.pos = position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int pos;
        TextView tvNoteTitle;
        TextView tvNoteContent;
        TextView tvNoteNotifyDate;
        CardView cardView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            tvNoteContent = (TextView) itemView.findViewById(R.id.tv_content_note_item);
            tvNoteTitle = (TextView) itemView.findViewById(R.id.tv_title_note_item);
            tvNoteNotifyDate = (TextView) itemView.findViewById(R.id.tv_date_time_item);
            cardView = (CardView) itemView.findViewById(R.id.card_view_note);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(DetailActivity.KEY_IS_CREATE_NEW, false);
            intent.putExtra(DetailActivity.KEY_NOTE, mData.get(this.pos));
            mContext.startActivity(intent);
        }
    }
}
