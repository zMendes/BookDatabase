package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);
        title = findViewById(R.id.title);
        genre = findViewById(R.id.genre);
        author = findViewById(R.id.author);
        has = findViewById(R.id.has);
        synopsis = findViewById(R.id.synopsis);
        rating = findViewById(R.id.rating);


        String json = loadJSON();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray jArr = obj.getJSONArray("books");
            JSONObject book = jArr.getJSONObject(0);
            title.setText(book.getString("name"));
            genre.setText("Categoria: " + book.getString("genre"));
            author.setText("Autor: " + book.getString("author"));
            has.setText("Possui: " + book.getString("has"));
            rating.setText("Opinião: " + book.getString("rating"));
            synopsis.setText("Descrição: " + book.getString("synopsis"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSON() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
