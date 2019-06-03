package br.edu.insper.al.leonardomm4.bookdatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class AddPage extends AppCompatActivity {

    private TextView title;
    private EditText genre;
    private EditText author;
    private EditText has;
    private EditText synopsis;
    private EditText rating;
    private ImageView edit;
    private ImageView confirm;
    private String lastPath;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 101;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

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

        edit.setOnClickListener(view -> {
            final CharSequence[] options = { "Tirar Foto", "Escolher da Galeria","Cancelar" };
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPage.this);
            builder.setTitle("Escolher Imagem");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Tirar Foto"))
                    {
                        if (ContextCompat.checkSelfPermission(AddPage.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            if (ActivityCompat.shouldShowRequestPermissionRationale(AddPage.this,
                                    Manifest.permission.CAMERA)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                            } else {
                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions(AddPage.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        PERMISSION_REQUEST_CAMERA);
                            }
                        } else {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getPackageManager()) == null) {
                                return;
                            }
                            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                            File file;
                            try {
                                file = File.createTempFile("image", ".jpg", directory);
                            } catch (IOException exception) {
                                file = null;
                            }
                            if (file == null) {
                                return;
                            }

                            // Guarda o caminho do arquivo no atributo.
                            lastPath = file.getAbsolutePath();

                            Uri uri = FileProvider.getUriForFile(
                                    AddPage.this,
                                    "br.edu.insper.al.leonardomm4.bookdatabase.fileprovider",
                                    file
                            );
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, CAMERA_REQUEST_CODE);
                        }
                    }
                    else if (options[item].equals("Escolher da Galeria"))
                    {
                        if (ContextCompat.checkSelfPermission(AddPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            if (ActivityCompat.shouldShowRequestPermissionRationale(AddPage.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.
                            } else {
                                // No explanation needed; request the permission
                                ActivityCompat.requestPermissions(AddPage.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        } else {
                            Intent intent=new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, GALLERY_REQUEST_CODE);
                        }
                    }
                    else if (options[item].equals("Cancelar")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        });


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
                book.put("image", lastPath);



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


    // Quando usamos o método startActivityForResult para disparar uma Intent,
    // esse método onActivityResult é chamado quando a Activity que recebeu
    // a Intent terminou de receber e obteve algum resultado. Neste caso,
    // quando o aplicativo de câmera tirou a foto e guardou no arquivo.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Confirma que de fato é o resultado da Intent de "tirar foto"
        // e que de fato a Activity que recebeu a Intent teve resultado.
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

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

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            lastPath = picturePath;
            edit.setImageBitmap(thumbnail);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


}
