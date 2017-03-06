package com.jnvarzea.taquin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * https://developer.android.com/guide/topics/ui/layout/gridview.html
 * Created by Jorge Nogueira on 28/02/17.
 */

public class PuzzlePieces extends BaseAdapter {
    private ArrayList<ImageView> puzzlePiecesViews = new ArrayList<>();
    private Bitmap image;
    private Context context;
    private int size;
    private ImageView blankView;
    private boolean solved =false;


    @Override
    public int getCount() {
        return puzzlePiecesViews.size();
    }

    @Override
    public ImageView getItem(int position) {
        return puzzlePiecesViews.get(position);
    }

    public ImageView getItem(Coordinate coordinate) {
        for(ImageView imageView:puzzlePiecesViews){
            Coordinate toTest = new Coordinate(imageView.getX(), imageView.getY(), imageView.getWidth());
            if(coordinate.Equals(toTest)){
                return imageView;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return puzzlePiecesViews.get(position);
    }

    public boolean NearMe(Coordinate a, Coordinate b) {
        //System.out.println(a.x + " " + a.y + " - " + b.x + " " + b.y );
        if (a.x + 1 == b.x && a.y == b.y) {
            return true;
        }
        if (a.x - 1 == b.x && a.y == b.y) {
            return true;
        }
        if (a.y + 1 == b.y && a.x == b.x) {
            return true;
        }
        if (a.y - 1 == b.y && a.x == b.x) {
            return true;
        }
        return false;
    }

    /**
     * It works!
     * @return true if win
     */
    public boolean Win(){
        Coordinate lastValue = new Coordinate(0,0,0);

        int v = 0;
        for (ImageView imageView:puzzlePiecesViews) {
            v++;
            Coordinate thisValue = PieceValue(imageView);
            //System.out.println(thisValue.x + "/" + thisValue.y + " - " + lastValue.x + "/" + lastValue.y);
            if(!thisValue.isBiggerThan(lastValue)){
                return false;
            }
            lastValue = thisValue;
            if(v%size==0){
                lastValue = new Coordinate(0,0,0);
            }
        }

        solved = true;

        return true;
    }

    public Coordinate PieceValue(ImageView a){
        return new Coordinate(a.getX(),a.getY(), a.getWidth()+1f);
    }

    public PuzzlePieces(Context context, String name, int size) {
        this.context = context;
        this.size = size;
        image = BitmapFactory.decodeResource(context.getResources(),
                context.getResources().getIdentifier(name, "drawable", context.getPackageName()));
        GeneratePuzzlePieces();

    }

    public PuzzlePieces(Context context, Bitmap image, int size) {
        this.context = context;
        this.size = size;
        this.image = image;
        GeneratePuzzlePieces();

    }

    public void Shuffle() {
        blankView = puzzlePiecesViews.get(size * size - 1);
        Coordinate blankViewCoordinate = new Coordinate(blankView.getX(),
                blankView.getY(), blankView.getWidth()+1f, size);
        blankView.setAlpha(0f);

        for(int i = 0; i<(100*size); i++) {
            ImageView next = getItem(blankViewCoordinate.NextMove());
            if (next != null) {
                Exchange(next, blankView);
            }
        }
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public ImageView getBlankView() {
        return blankView;
    }

    public void setBlankView(ImageView blankView) {
        this.blankView = blankView;
    }

    public void Exchange(ImageView a, ImageView b){
        Coordinate save = new Coordinate(b.getX(), b.getY(), b.getWidth()+1f);
        b.setX(a.getX());
        b.setY(a.getY());
        a.setX(save.realX);
        a.setY(save.realY);
    }


    private void GeneratePuzzlePieces() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Bitmap toAdd = Bitmap.createBitmap(image, j * (image.getWidth() / size),
                        i * (image.getWidth() / size), (image.getWidth() / size),
                        (image.getWidth() / size));

                ImageView imageView = new ImageView(context);
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(toAdd);
                imageView.setOnTouchListener(new OnTouchPuzzleListener(this));

                puzzlePiecesViews.add(imageView);
            }
        }
    }

}
