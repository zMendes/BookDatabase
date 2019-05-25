package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddBook extends AppCompatActivity {

    private TextView title;
    private EditText genre;
    private EditText author;
    private EditText has;
    private EditText synopsis;
    private EditText rating;
    private ImageView edit;
    private ImageView confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        genre = findViewById(R.id.genre);
        author = findViewById(R.id.author);
        has = findViewById(R.id.has);
        synopsis = findViewById(R.id.synopsis);
        rating = findViewById(R.id.rating);
        confirm = findViewById(R.id.confirm);

        title.setHint("digite aqui");
        genre.setHint("digite aqui");
        author.setHint("digite aqui");
        has.setHint("digite aqui");
        rating.setHint("digite aqui");
        synopsis.setHint("digite aqui");



        confirm.setOnClickListener(view -> {
            String json = loadData();
            try {
                JSONObject root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                JSONObject book = books.getJSONObject(books.length()+1);

                book.put("genre", genre.getText().toString());
                book.put("author", author.getText().toString());
                book.put("has", has.getText().toString());
                book.put("rating", rating.getText().toString());
                book.put("synopsis", synopsis.getText().toString());

                data.put("books",books);
                root.put("database", data);
                saveData(root.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, Homepage.class);
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
