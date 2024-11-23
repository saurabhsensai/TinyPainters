package com.example.paint2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.*;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import yuku.ambilwarna.AmbilWarnaDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private CustomView customView;
    private int currentColor;
    private int itr =1 ;
    private ImageButton backgroundImageButton;

    private ImageButton clearButton;
    private boolean isEraserEnabled = false;
    Drawable whiteDrawable = new ColorDrawable(Color.WHITE);
    private int[] imageResources = {R.drawable.white, R.drawable.cutecat,  R.drawable.cutecat2,  R.drawable.cutecat3, R.drawable.deer , R.drawable.own, R.drawable.flower};

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton brushSizeButton = findViewById(R.id.brushSizeButton);
        customView = findViewById(R.id.customView);
        currentColor = Color.BLACK; // Default color
        customView.setColor(currentColor);
        backgroundImageButton = findViewById(R.id.backgroundImageButton);
        clearButton = findViewById(R.id.clearButton);




        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearConfirmationDialog();
            }
        });

        backgroundImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });

        ImageButton downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  downloadImage();
                                              }
                                          });

        brushSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBrushSizeDialog();
            }
        });
    }

    public void onUndoClick(View view) {
        customView.undo();
    }

    public void onRedoClick(View view) {
        customView.redo();
    }

    public void onClearClick(View view) {
        customView.clear();
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Background Image");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_selection, null);
        builder.setView(dialogView);

        GridView gridView = dialogView.findViewById(R.id.gridView);
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, imageResources);
        gridView.setAdapter(adapter);

        final AlertDialog dialog = builder.create();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedImageResource = imageResources[position];
                setBackgroundImage(selectedImageResource);
                customView.clear();
                dialog.dismiss();
            }
        });

        dialog.show();
    }



    public void onChangeColorClick(View view) {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(this, currentColor, true,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // Do nothing on cancel
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        currentColor = color;
                        customView.setColor(currentColor);
                    }
                });
        colorPickerDialog.show();
    }

    private void showBrushSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Brush Size");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(70);
        seekBar.setProgress((int) customView.getStrokeWidth());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) getResources().getDimension(R.dimen.dialog_margin);
        layoutParams.setMargins(margin, margin, margin, 0);
        seekBar.setLayoutParams(layoutParams);

        final TextView sizeTextView = new TextView(this);
        sizeTextView.setText("Size: " + seekBar.getProgress());
        sizeTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.addView(seekBar);
        layout.addView(sizeTextView);

        builder.setView(layout);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeTextView.setText("Size: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float strokeWidth = seekBar.getProgress();
                customView.setStrokeWidth(strokeWidth);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void downloadImage() {
        Bitmap bitmap = Bitmap.createBitmap(customView.getWidth(), customView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        customView.draw(canvas);

        String filename = "my_drawing"+ itr+".png";
        itr = itr +  1;
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File file = new File(directory, filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();


            MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"image/png"}, null);

            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showClearConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Clear Drawing");
        builder.setMessage("Are you sure you want to clear the drawing?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customView.clear();

            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void setBackgroundImage(int resourceId) {
        // Load the image from resources
        Bitmap backgroundImage = BitmapFactory.decodeResource(getResources(), resourceId);

        // Set the background image on the CustomView
        customView.setBackground(new BitmapDrawable(getResources(), backgroundImage));
    }

    private void toggleEraserMode() {
        isEraserEnabled = !isEraserEnabled;

        if (isEraserEnabled) {
            // Set eraser properties
            customView.setEraserMode(true);
            customView.setEraserSize(30); // Adjust the eraser size as needed
        } else {
            // Disable eraser mode
            customView.setEraserMode(false);
        }
    }

}
