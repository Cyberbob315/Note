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
import nhannt.note.utils.Constant;
import nhannt.note.utils.DateTimeUtils;

/**
 * Created by IceMan on 12/30/2016.
 *
 * An adapter extended RecyclerView.Adapter to show Note on a RecyclerView
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
        View view = mLayoutInflater.inflate(R.layout.item_note_main, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        holder.tvNoteTitle.setText(mData.get(position).getTitle());
        holder.tvNoteContent.setText(mData.get(position).getContent());
        holder.cardView.setCardBackgroundColor(mData.get(position).getColor());
        holder.tvNoteDate.setText(DateTimeUtils.getDateStrFromMilliseconds(mData.get(position).getCreatedDate(), "yyyy-MM-dd"));
        holder.pos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int pos;
        TextView tvNoteTitle;
        TextView tvNoteContent;
        TextView tvNoteDate;
        CardView cardView;

        public NoteViewHolder(View itemView) {
            super(itemView);
            tvNoteContent = (TextView) itemView.findViewById(R.id.tv_content_note_item);
            tvNoteTitle = (TextView) itemView.findViewById(R.id.tv_title_note_item);
            tvNoteDate = (TextView) itemView.findViewById(R.id.tv_date_time_item);
            cardView = (CardView) itemView.findViewById(R.id.card_view_note);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(Constant.KEY_LIST_NOTE, mData);
            intent.putExtra(Constant.KEY_NOTE_POSITION, pos);
            mContext.startActivity(intent);
        }
    }
}
