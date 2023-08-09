package com.example.noteexample;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sqliteexample.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {
    private List<Note> note_list;
    private Context context;

    public NoteAdapter(Context context,List<Note> note_list) {
        this.context = context;
        this.note_list = note_list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_content, parent, false);
        return new MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = note_list.get(position);

        if(note.getNote().isEmpty()){
            holder.noteTxt.setVisibility(View.GONE);
        }else{
            holder.noteTxt.setVisibility(View.VISIBLE);
            holder.noteTxt.setText(note.getNote());
        }

        if(note.getTitle().isEmpty()){
            holder.titleTxt.setVisibility(View.GONE);
        }else{
            holder.titleTxt.setVisibility(View.VISIBLE);
            holder.titleTxt.setText(note.getTitle());
        }

        // Formatting and displaying timestamp
        //holder.time.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return note_list.size();
    }

    public void filterList(List<Note> filteredList){
        note_list = filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noteTxt,titleTxt;
        public MyViewHolder(View itemView) {
            super(itemView);
            noteTxt = itemView.findViewById(R.id.note_id);
            titleTxt = itemView.findViewById(R.id.title_id);
        }
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    String formatDate(String dataStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date data = fmt.parse(dataStr);
            SimpleDateFormat fmt_out = new SimpleDateFormat(" MMM d, yyyy");
            return fmt_out.format(data);
        }catch (ParseException e) {

        }
        return "";
    }
}
