package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    private ImageView cover;
    private ImageView about;
    private TextView icon;

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
        cover = findViewById(R.id.cover);
        about = findViewById(R.id.about);
        icon = findViewById(R.id.icon);
        icon.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });



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
            rating.setText(book.getString("rating"));
            synopsis.setText(book.getString("synopsis"));
            if ((book.getString("has")).equals("true")){
                has.setText("Possui: " + "Sim");
            }
            else{
                has.setText("Possui: " + "Não");
            }

            String image = book.getString("image");
            if (image != null && image != "") {
                // Descobre a URI do último arquivo que foi criado. Aqui não usamos
                // a URI do provedor de arquivos porque o uso dele é apenas local.
                Uri uri = Uri.fromFile(new File(image));

                Rotator rotator = new Rotator();

                // Carrega uma imagem a partir da URI, se possível.
                Bitmap bitmap;
                try {
                    ExifInterface exif = new ExifInterface(uri.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    bitmap = rotator.rotateBitmap(bitmap, orientation);
                } catch (IOException exception) {
                    bitmap = null;
                }

                // Se foi possível, coloca essa imagem no elemento que
                // incluímos no layout especialmente para exibi-la.
                if (bitmap != null) {
                    // ImageView edit = findViewById(R.id.image_example);
                    cover.setImageBitmap(bitmap);
                    cover.setMinimumHeight(200);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        edit.setOnClickListener(view -> {
            Intent intent = new Intent(this, EditPage.class);
            intent.putExtra("idbook", id);
            startActivity(intent);
        });

        about.setOnClickListener(view -> {

            Intent intent = new Intent(this, About.class);
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
                Toast toast = Toast.makeText(getApplicationContext(), "Livro removido com sucesso", Toast.LENGTH_SHORT);
                toast.show();
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
