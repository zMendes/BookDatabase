package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Homepage extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<Item> bookList;


    private ImageView sortName;
    private TextView icon;
    private ImageView about;
    private ImageView add;
    private ImageView searchicon;
    private CheckBox checkBox;

    private TextView nautor;
    private TextView nlivros;

    private EditText searchbar;
    private String searchkey;

    private int picture = R.drawable.cover;

    private Boolean sorted;

    private Boolean searchview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        gridView = findViewById(R.id.gv);
        nautor = findViewById(R.id.nautores);
        nlivros = findViewById(R.id.nlivros);

        searchicon = findViewById(R.id.search_icon);
        searchbar = findViewById(R.id.search_enter);

        gridView = findViewById(R.id.gv);

        icon = findViewById(R.id.icon);
        icon.setOnClickListener(view -> {
            Intent intent = new Intent(this, Homepage.class);
            startActivity(intent);
        });


        sorted = false;

        searchkey = null;
        searchview = false;

        sortName = findViewById(R.id.sortName);
        sortName.setOnClickListener(view -> {
            if (sorted) {
                sorted = false;
                Toast toast = Toast.makeText(getApplicationContext(), "Sorting by: title", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                sorted = true;
                Toast toast = Toast.makeText(getApplicationContext(), "Sorting by: author", Toast.LENGTH_SHORT);
                toast.show();
            }
            RefreshList();

        });

        about = findViewById(R.id.about);
        add = findViewById(R.id.add);

        checkBox = findViewById(R.id.checkbox_owned);
        checkBox.setChecked(false);

        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }

        searchicon.setOnClickListener(view -> {
            if (!searchview) {
                searchview = true;
                searchbar.setVisibility(View.VISIBLE);
            } else {
                searchview = false;
                searchbar.setVisibility(View.GONE);
            }
        });

        about.setOnClickListener(view -> {

            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        });

        add.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddPage.class);
            startActivity(intent);
        });


        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchkey = searchbar.getText().toString().toLowerCase();
                RefreshList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        RefreshList();


        countBooks();


        gridView.setOnItemClickListener((parent, view, position, id) -> goToPage(bookList.get(position).getbookId()));
    }


    protected void RefreshList() {

        bookList = new ArrayList<>();


        String json_f = loadData();
        if (json_f == null) {
            String json = loadJSON();
            saveData(json);
            json_f = loadData();
        } else {
            json_f = loadData();
        }
        /* json_f = loadJSON();

        saveData(json_f);*/


        try {

            JSONObject root = new JSONObject(json_f);
            JSONObject data = root.getJSONObject("database");

            JSONArray books = data.getJSONArray("books");

            for (int i = 0; i < books.length(); i++) {
                JSONObject book = books.getJSONObject(i);


                int picture = R.drawable.cover;
                if (searchkey != null) {

                    if ((book.getString("name").toLowerCase().contains(searchkey)) || (book.getString("author").toLowerCase().contains(searchkey))) {
                        parseBook(book);
                    }
                } else {
                    parseBook(book);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortList(bookList);

        MyAdapter myAdapter = new MyAdapter(this, R.layout.grid_view_items, bookList);
        gridView.setAdapter(myAdapter);

    }

    public void countBooks() {
        int nbooks = 0;
        int nauthors = 0;


        ArrayList<String> listauthors = new ArrayList<>();


        String json_f = loadData();
        if (json_f == null) {
            String json = loadJSON();
            saveData(json);
            json_f = loadData();
        } else {
            json_f = loadData();
        }

        try {
            JSONObject root = new JSONObject(json_f);
            JSONObject data = root.getJSONObject("database");

            JSONArray livros = data.getJSONArray("books");


            for (int i = 0; i < livros.length(); i++) {
                nbooks++;
                JSONObject book = livros.getJSONObject(i);
                String aut = book.getString("author");

                if (!(listauthors.contains(aut))) {
                    nauthors++;
                    listauthors.add(aut);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nlivros.setText("Livros: " + nbooks);
        nautor.setText("Autores: " + nauthors);
    }


    public void parseBook(JSONObject book) {
        try {
            String bookName = book.getString("name");
            String bookAuthor = book.getString("author");
            String imageFile = book.optString("image");
            int id = book.getInt("id");
            if (checkBox.isChecked()) {
                if (book.getBoolean("has")) {
                    Item item = new Item(bookName, bookAuthor, picture, id);
                    item.setbookImageFile(imageFile);
                    bookList.add(item);
                }
            } else {
                Item item = new Item(bookName, bookAuthor, picture, id);
                item.setbookImageFile(imageFile);
                bookList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goToPage(int id) {
        Intent intent = new Intent(this, BookPage.class);
        intent.putExtra("idbook", id);
        startActivity(intent);
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

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox_owned:
                if (checked) {
                    RefreshList();
                }
                // Do something
                else
                    RefreshList();// Do something else
                break;

        }
    }

    public void sortList(ArrayList<Item> list) {
        if (sorted) {
            Collections.sort(list, (item, item2) -> {

                int compare = item.getbookName().compareTo(item2.getbookName());

                return compare;

            });
        } else {
            Collections.sort(list, (item, item2) -> {

                int compare = item.getbookAuthor().compareTo(item2.getbookAuthor());

                return compare;
            });

        }
    }
}