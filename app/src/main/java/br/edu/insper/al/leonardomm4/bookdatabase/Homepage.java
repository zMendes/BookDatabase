package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Homepage extends AppCompatActivity {

    private Button book1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        book1 = findViewById(R.id.book1);

        book1.setOnClickListener(view -> {
            Intent intent = new Intent(this, BookPage.class);
            startActivity(intent);
        });
    }
}
