package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class EditPage extends AppCompatActivity {

    private EditText title;
    private EditText genre;
    private EditText author;
    private EditText has;
    private EditText synopsis;
    private EditText rating;
    private ImageView edit;
    private ImageView confirm;
    private String lastPath;
    private static final int REQUEST_TAKE_PHOTO = 1;

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

        String json = loadData();
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
            String image = book.getString("image");
            if (image != null && image != "") {
                // Descobre a URI do último arquivo que foi criado. Aqui não usamos
                // a URI do provedor de arquivos porque o uso dele é apenas local.
                Uri uri = Uri.fromFile(new File(image));

                // Carrega uma imagem a partir da URI, se possível.
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException exception) {
                    bitmap = null;
                }

                // Se foi possível, coloca essa imagem no elemento que
                // incluímos no layout especialmente para exibi-la.
                if (bitmap != null) {
                    // ImageView edit = findViewById(R.id.image_example);
                    edit.setImageBitmap(bitmap);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        edit.setOnClickListener(view -> {
            // Intent intent = new Intent(this, EditPage.class);
            // intent.putExtra("idbook", id);
            // startActivity(intent);

            // Constrói uma Intent que corresponde ao pedido de "tirar foto".
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Se não existe no celular uma Activity que aceite esse Intent,
            // não podemos fazer nada. Parece um caso meio absurdo, mas o
            // usuário pode ter desativado a câmera por algum motivo.
            if (intent.resolveActivity(getPackageManager()) == null) {
                return;
            }

            // A ideia é criar um arquivo e compartilhar esse arquivo com o
            // aplicativo que receba a Intent, para que ele possa gravar a
            // foto nele. Por isso precisamos de um provedor de arquivos.

            // Escolhemos para guardar o arquivo a pasta padrão para fotos.
            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Esse método createTempFile é especial: além de criar o arquivo,
            // também garante que o nome desse arquivo vai ser único na pasta.
            File file;
            try {
                file = File.createTempFile("image", ".jpg", directory);
            } catch (IOException exception) {
                file = null;
            }

            // Se o arquivo não pôde ser criado, não podemos fazer nada.
            if (file == null) {
                return;
            }

            // Guarda o caminho do arquivo no atributo.
            lastPath = file.getAbsolutePath();

            // O arquivo será passado para o aplicativo de câmera através de
            // uma URI criada pelo provedor de arquivos. Atenção: o nome de
            // pacote no parâmetro "authority" deve ser EXATAMENTE IGUAL no
            // nome no arquivo de configuração manifests/AndroidManifest.xml.
            Uri uri = FileProvider.getUriForFile(
                    this,
                    "br.edu.insper.al.leonardomm4.bookdatabase.fileprovider",
                    file
            );

            // Anexa a URI na Intent, para que o aplicativo de câmera a receba.
            // Essa é uma maneira comum de uma Activity passar dados para outra.
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            // Dispara a Intent. Lembrando que quem vai receber essa Intent não
            // é este aplicativo, mas algum outro que saiba recebê-la. No caso,
            // estamos esperando que o aplicativo de câmera faça isso.
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        });

        confirm.setOnClickListener(view -> {
            try {
                JSONObject root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                JSONObject book = books.getJSONObject(id);

                book.put("name", title.getText().toString());
                book.put("genre", genre.getText().toString());
                book.put("author", author.getText().toString());
                book.put("has", has.getText().toString());
                book.put("rating", rating.getText().toString());
                book.put("synopsis", synopsis.getText().toString());
                book.put("image", lastPath);

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

    // Quando usamos o método startActivityForResult para disparar uma Intent,
    // esse método onActivityResult é chamado quando a Activity que recebeu
    // a Intent terminou de receber e obteve algum resultado. Neste caso,
    // quando o aplicativo de câmera tirou a foto e guardou no arquivo.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Confirma que de fato é o resultado da Intent de "tirar foto"
        // e que de fato a Activity que recebeu a Intent teve resultado.
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            // Descobre a URI do último arquivo que foi criado. Aqui não usamos
            // a URI do provedor de arquivos porque o uso dele é apenas local.
            Uri uri = Uri.fromFile(new File(lastPath));

            // Carrega uma imagem a partir da URI, se possível.
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException exception) {
                bitmap = null;
            }

            // Se foi possível, coloca essa imagem no elemento que
            // incluímos no layout especialmente para exibi-la.
            if (bitmap != null) {
                // ImageView edit = findViewById(R.id.image_example);
                edit.setImageBitmap(bitmap);
            }
        }
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
