package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class EditPage extends AppCompatActivity {

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
        setContentView(R.layout.activity_edit_page);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        genre = findViewById(R.id.genre);
        author = findViewById(R.id.author);
        has = findViewById(R.id.has);
        synopsis = findViewById(R.id.synopsis);
        rating = findViewById(R.id.rating);
        confirm = findViewById(R.id.confirm);


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

        String json = loadJSON();
        try {

            JSONObject root = new JSONObject(json);
            JSONObject data = root.getJSONObject("database");
            JSONArray books = data.getJSONArray("books");
            JSONObject book = books.getJSONObject(id);

            title.setText(book.getString("name"));
            genre.setText(book.getString("genre"));
            author.setText(book.getString("author"));
            has.setText(book.getString("has"));
            rating.setText(book.getString("rating"));
            synopsis.setText(book.getString("synopsis"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditPage.class);
            intent.putExtra("idbook", id);
            startActivity(intent);
        });

        confirm.setOnClickListener(view -> {
            try {
                JSONObject root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                JSONObject book = books.getJSONObject(id);

                book.put("genre", genre.getText().toString());
                book.put("author", author.getText().toString());
                book.put("has", has.getText().toString());
                book.put("rating", rating.getText().toString());
                book.put("synopsis", synopsis.getText().toString());

                data.put("books",books);
                root.put("database", data);
                try (FileWriter file = new FileWriter("R.raw.data"))
                    {
                        file.write(root.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }
        public String loadJSON () {
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
