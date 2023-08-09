package com.example.noteexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.sqliteexample.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter myAdapter;
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noNotesView;
    private Toolbar toolbar;
    private SearchView searchView;
    private DataBaseHelper db;
    private List<Note> noteSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerviewID);
        noNotesView = findViewById(R.id.empty_notesID);

        db = new DataBaseHelper(this);
        notesList = db.getAllNotes();
        noteSource = notesList;

        myAdapter = new NoteAdapter(this, notesList);
        //GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(myAdapter);

        toggleEmptyNotes(); // check notesList.size

        FloatingActionButton  addNote =  findViewById(R.id.floatingActionButtonID);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showNote(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        // get note
        Intent intent = getIntent();
        // state 2 (edit for note) , 1 (create new note)
        int state = intent.getIntExtra("key",0);
        if(state == 1){
            String title = intent.getStringExtra("title");
            String note = intent.getStringExtra("note");

            createNote(title, note);
        }
        else if(state == 2)
        {
            String title = intent.getStringExtra("title");
            String note = intent.getStringExtra("note");
            //int position = intent.getIntExtra("position",0);
            int id = intent.getIntExtra("id",0);
            if (!notesList.isEmpty())
                updateNote(title, note, id);
        }

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                filterNotes(text);
                return true;
            }
        });



    }//end onCreate()

    private void filterNotes(String text) {
        if (text.trim().isEmpty()){
            notesList = noteSource;
        } else {
            List<Note> temp = new ArrayList<>();
            for (Note note : noteSource){
                if (note.getTitle().toLowerCase().contains(text.toLowerCase())
                        || note.getNote().toLowerCase().contains(text.toLowerCase())
                ){
                    temp.add(note);
                }
            }
            notesList = temp;
        }
        myAdapter.filterList(notesList);
    }

    private void addNote(){
        Intent intent = new Intent(this, showNoteDetails.class);
        intent.putExtra("key", 1);
        startActivity(intent);

    }
    private void showNote(int position){
       Note note  = notesList.get(position);
       Intent intent = new Intent(this, showNoteDetails.class);

       intent.putExtra("id", note.getId());
       intent.putExtra("note", note.getNote());
       intent.putExtra("title", note.getTitle());
       intent.putExtra("time", note.getTimestamp());
       intent.putExtra("position", position);

       intent.putExtra("key",2);
       startActivity(intent);

    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String title, String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(title.trim(),note.trim());
        if (id == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving note", Toast.LENGTH_SHORT).show();
        } else {
            // get the newly inserted note from db
            Note n = db.getNote(id);

            if (n != null) {
                // adding new note to array list at 0 position
                notesList.add(0, n);
                // refreshing the list
                myAdapter.notifyDataSetChanged();
                // check notesList
                toggleEmptyNotes();
            }
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String title, String note, int id) {
        Note n = db.getNote(id);
        // updating note text
        n.setNote(note);
        n.setTitle(title);
        // updating note in db
        db.updateNotes(n);

        // refreshing the list
        //notesList.set(position, n);
        notesList = db.getAllNotes();
        noteSource = notesList;
        myAdapter.filterList(notesList);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        myAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Delete options
     */
    private void showActionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this Note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote(position);
                Toast.makeText(MainActivity.this, "Item deleted!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0
        if (myAdapter.getItemCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}//end class
