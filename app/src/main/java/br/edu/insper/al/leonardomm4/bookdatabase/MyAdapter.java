package br.edu.insper.al.leonardomm4.bookdatabase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {

    ArrayList<Item> bookList = new ArrayList<>();

    public MyAdapter(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        bookList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_view_items, null);
        TextView textView = (TextView) v.findViewById(R.id.textView);
        TextView textView2 = (TextView) v.findViewById(R.id.textView2);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        textView.setText(bookList.get(position).getbookName());
        textView2.setText(bookList.get(position).getbookAuthor());
        imageView.setImageResource(bookList.get(position).getbookImage());

        String imageFile = bookList.get(position).getbookImageFile();

        if (imageFile != null && imageFile != "") {
            Uri uri = Uri.fromFile(new File(imageFile));

            Rotator rotator = new Rotator();

            // Carrega uma imagem a partir da URI, se possível.
            Bitmap bitmap;
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                bitmap = rotator.rotateBitmap(bitmap, orientation);
            } catch (IOException exception) {
                bitmap = null;
            }
            // Se foi possível, coloca essa imagem no elemento que
            // incluímos no layout especialmente para exibi-la.
            if (bitmap != null) {
                // ImageView edit = findViewById(R.id.image_example);
                imageView.setImageBitmap(bitmap);
            }
        }
        return v;

    }
}
