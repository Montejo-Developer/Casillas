package com.example.toni.casillas2;

import android.content.Context;

/**
 * Created by Usuario on 12/11/2017.
 */
public class TileView extends android.support.v7.widget.AppCompatButton {
    // coordenadas
    public int x = 0;
    public int y = 0;
    // trama a mostrar
    private int index = 0;
    //max tramas
    private int topElements = 0;
    public TileView(Context context, int x, int y, int topElements, int index, int
            background) {
        super(context);
        this.x = x; //coordenada X
        this.y = y; //coordenada Y
        this.topElements = topElements; //max tramas
        this.index = index; //índice de trama
        this.setBackgroundResource(background);
    }
    public int getNewIndex(){
        index ++;
        //controlar si necesitamos volver a comenzar el ciclo de tramas
        if (index == topElements)index = 0;
        return index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setBackground(int background)
    {
        this.setBackgroundResource(background);
    }
}
