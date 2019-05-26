package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class BookPage extends AppCompatActivity {
    private TextView title;
    private TextView genre;
    private TextView author;
    private TextView has;
    private TextView synopsis;
    private TextView rating;
    private ImageView edit;
    private ImageView remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        remove = findViewById(R.id.remove);
        genre = findViewById(R.id.genre);
        author = findViewById(R.id.author);
        has = findViewById(R.id.has);
        synopsis = findViewById(R.id.synopsis);
        rating = findViewById(R.id.rating);


        int id;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = 0;
            } else {
                id = extras.getInt("idbook");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("idbook");
        }


        String json = loadData();
        try {

            JSONObject root = new JSONObject(json);
            JSONObject data = root.getJSONObject("database");
            JSONArray books = data.getJSONArray("books");
            JSONObject book = books.getJSONObject(id);
            title.setText(book.getString("name"));
            genre.setText("Categoria: " + book.getString("genre"));
            author.setText("Autor: " + book.getString("author"));
            has.setText("Possui: " + book.getString("has"));
            rating.setText("Opinião: " + book.getString("rating"));
            synopsis.setText("Descrição: " + book.getString("synopsis"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditPage.class);
            intent.putExtra("idbook", id);
            startActivity(intent);
        });


        remove.setOnClickListener(view -> {
            try {

                JSONObject root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                books.remove(id);
                data.put("books",books);
                root.put("database", data);
                saveData(root.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent =  new Intent(this, Homepage.class);
            startActivity(intent);

        });

    }

    private void saveData(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("data", s);
        editor.apply();
    }

    private String loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", 0);
        String json = sharedPreferences.getString("data", null);
        return json;
    }
}
