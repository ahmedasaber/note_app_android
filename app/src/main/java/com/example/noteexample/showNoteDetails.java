package com.example.noteexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.sqliteexample.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class showNoteDetails extends AppCompatActivity {
    private EditText mNote, mTitle;
    private TextView mTime;
    private Toolbar toolbar;
    private ImageView checkCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_description);

        checkCircle = findViewById(R.id.toolbarCheckCircle);
        checkCircle.setVisibility(View.VISIBLE);
        
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notes");

        mTitle = findViewById(R.id.note_title);
        mNote = findViewById(R.id.note);
        mTime = findViewById(R.id.timeTxt);

        Intent intent = getIntent();

        // state 2 (update) , 1 (create new note)
        int state = intent.getIntExtra("key",0);
        if(state == 2) // can edit note
        {
            int id = intent.getIntExtra("id",0);
            String note = intent.getStringExtra("note");
            String title = intent.getStringExtra("title");
            String time = intent.getStringExtra("time");
            int position = intent.getIntExtra("position",0);

            mTitle.setText(title);
            mNote.setText(note);
            mTime.setVisibility(View.VISIBLE);
            mTime.setText("Edited"+formatDate(time));

            checkCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mTitle.getText().toString().trim().isEmpty()
                            && mNote.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "Error with updating note", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(showNoteDetails.this, MainActivity.class);
                        intent.putExtra("key",2);

                        intent.putExtra("id",id);
                        intent.putExtra("note", mNote.getText().toString().trim());
                        intent.putExtra("title", mTitle.getText().toString().trim());
                        intent.putExtra("position", position);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else if (state == 1) // add new note
        {
            mTime.setVisibility(View.GONE);
            checkCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mTitle.getText().toString().trim().isEmpty()
                            && mNote.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "Error with saving note", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(showNoteDetails.this, MainActivity.class);
                        intent.putExtra("key",1);

                        intent.putExtra("title", mTitle.getText().toString().trim());
                        intent.putExtra("note", mNote.getText().toString().trim());

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
