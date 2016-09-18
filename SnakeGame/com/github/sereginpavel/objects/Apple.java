package com.github.sereginpavel.objects;


import com.github.sereginpavel.SnakeGame;

/**
 * Created by Павел on 13.07.2015.
 */
public class Apple
{
    SnakeGame main;
    public int posX; //позиция для яблока по X
    public int posY; //позиция для яблока по Y

    //стартовая позиция отображения яблока
    public Apple(int startX, int startY){
        posX = startX;
        posY = startY;
    }

    //рандомное отображение яблока
    public void setRandomPosition(){
        posX = (int) (Math.random() * main.WIDHT );
        posY = (int) (Math.random() * main.HIGHT );
    }
}
