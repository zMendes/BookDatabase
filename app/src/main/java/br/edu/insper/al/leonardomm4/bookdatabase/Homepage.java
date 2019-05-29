package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
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
    private ImageView sortAuthor;

    private ImageView about;
    private ImageView add;
    private CheckBox checkBox;

    private Boolean sorted;

    private int picture = R.drawable.ic_launcher_background;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        gridView = findViewById(R.id.gv);

        sortName = findViewById(R.id.sortName);
        sortName.setOnClickListener(view -> {

            ArrayList<JSONObject> list = new ArrayList<>();

            String json = loadData();
            JSONObject root;
            try {
                root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                for (int i=0;i < books.length();i++){
                    list.add(books.getJSONObject(i));
                }
                Collections.sort(list, (jsonObject, t1) -> {
                    int compare = 0;
                    try {
                        compare =jsonObject.getString("name").compareTo(t1.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return compare;
                });
                JSONArray Sorted = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Sorted.put(list.get(i));
                }

                data.put("books",Sorted);
                root.put("database", data);
                saveData(root.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RefreshList();


        });

        sortAuthor = findViewById(R.id.sortAuthor);
        sortAuthor.setOnClickListener(view -> {

            ArrayList<JSONObject> list = new ArrayList<>();

            String json = loadData();
            JSONObject root;
            try {
                root = new JSONObject(json);
                JSONObject data = root.getJSONObject("database");
                JSONArray books = data.getJSONArray("books");
                for (int i=0;i < books.length();i++){
                    list.add(books.getJSONObject(i));
                }
                Collections.sort(list, (jsonObject, t1) -> {
                    int compare = 0;
                    try {
                        compare =jsonObject.getString("author").compareTo(t1.getString("author"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return compare;
                });
                JSONArray Sorted = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Sorted.put(list.get(i));
                }

                data.put("books",Sorted);
                root.put("database", data);
                saveData(root.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RefreshList();


        });



        about = findViewById(R.id.about);
        add = findViewById(R.id.add);

        checkBox = findViewById(R.id.checkbox_owned);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }



        about.setOnClickListener(view -> {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        });

        add.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddPage.class);
            startActivity(intent);
        });

        RefreshList();


        gridView.setOnItemClickListener((parent, view, position, id) -> {
            goToPage(position);
            System.out.println(bookList);
            System.out.println(position);
        });}

    protected void RefreshList() {

        bookList =new ArrayList<>();


        String json_f =loadData();
        if (json_f == null){
        String json = loadJSON();
        saveData(json);
        json_f = loadData();}

        else {
            json_f = loadData() ;
        }

        LinkedList<StringBuilder> builders = new LinkedList<>();

        try {

            JSONObject root = new JSONObject(json_f);
            JSONObject data =  root.getJSONObject("database");

            JSONArray books = data.getJSONArray("books");

            for(int i=0; i<books.length(); i++){
                //int i2 = 2;
                builders.add(new StringBuilder());
                JSONObject book = books.getJSONObject(i);
                builders.get(i).append(book.getString("name"));
                builders.get(i).append("\n");
                builders.get(i).append(book.getString("author"));
                String image = book.optString("image");



                if (checkBox.isChecked()) {
                    if (book.getBoolean("has")) {
                        Item item = new Item(builders.get(i).toString(), picture, i);
                        item.setbookImageString(image);
                        bookList.add(item);
                    }
                } else {
                    Item item = new Item(builders.get(i).toString(), picture, i);
                    item.setbookImageString(image);
                    bookList.add(item);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyAdapter myAdapter = new MyAdapter(this,R.layout.grid_view_items,bookList);
        gridView.setAdapter(myAdapter);

    }


    public boolean OnCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.gv);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                bookList.getFilter().filter(s);  //O erro ocorre por ele ser ligado ao ArrayAdapter
                return false;
            }
        });
        return OnCreateOptionsMenu(menu);
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

    private void saveData(String s){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("data",s);
        editor.apply();
    }

    private String loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",0);
        String json = sharedPreferences.getString("data", null);
        return json;
    }
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_owned:
                if (checked){
                    RefreshList();
                }
                // Do something
            else
                RefreshList();// Do something else
                break;

        }
    }

    }
