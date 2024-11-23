package com.example.paint2;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private int[] imageResources;

    public ImageAdapter(Context context, int[] imageResources) {
        this.context = context;
        this.imageResources = imageResources;
    }

    @Override
    public int getCount() {
        return imageResources.length;
    }

    @Override
    public Object getItem(int position) {
        return imageResources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Adjust the size as needed
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imageResources[position]);
        return imageView;
    }
}
