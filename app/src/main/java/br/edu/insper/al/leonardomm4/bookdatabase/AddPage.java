package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddPage extends AppCompatActivity {

    private TextView title;
    private EditText genre;
    private EditText author;
    private EditText has;
    private EditText synopsis;
    private EditText rating;
    private ImageView edit;
    private ImageView confirm;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        author = findViewById(R.id.author);
        genre = findViewById(R.id.genre);
        has = findViewById(R.id.has);
        synopsis = findViewById(R.id.synopsis);
        rating = findViewById(R.id.rating);
        confirm = findViewById(R.id.confirm);
        icon = findViewById(R.id.icon);
        icon.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });

        title.setHint("digite aqui");
        genre.setHint("digite aqui");
        author.setHint("digite aqui");
        has.setHint("digite aqui");
        rating.setHint("digite aqui");
        synopsis.setHint("digite aqui");



        confirm.setOnClickListener(view -> {
            String json = loadData();
            if (title.getText().toString().isEmpty()|| author.getText().toString().isEmpty()|| genre.getText().toString().isEmpty()
                    || has.getText().toString().isEmpty() || rating.getText().toString().isEmpty() || synopsis.getText().toString().isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(), "Complete os itens restantes",Toast.LENGTH_LONG);
                toast.show();
            }
            else {
            try {
                JSONObject root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                JSONObject book = new JSONObject();


                book.put("name", title.getText().toString());
                book.put("author", author.getText().toString());
                book.put("genre", genre.getText().toString());
                book.put("has", has.getText().toString());
                book.put("synopsis", synopsis.getText().toString());
                book.put("rating", rating.getText().toString());


                books.put(books.length(),book);
                data.put("books",books);
                root.put("database", data);
                saveData(root.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
            Toast toast = Toast.makeText(getApplicationContext(), "Livro adicionado com sucesso", Toast.LENGTH_SHORT);
            toast.show();}

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
