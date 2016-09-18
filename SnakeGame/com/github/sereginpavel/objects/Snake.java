package com.github.sereginpavel.objects;

import com.github.sereginpavel.SnakeGame;

/**
 * Created by Павел on 13.07.2015.
 */
public class Snake
{
    SnakeGame main;
    public int direction = 3; //направление движения
    public int length = 2; //длина змейки

    public int snakeX[] = new int[main.WIDHT * main.HIGHT]; //позиция змейки на поле по Х
    public int snakeY[] = new int[main.WIDHT * main.HIGHT]; //позиция замейки на поле по Y

    //в конструктор передаются стартовые позиции на поле
    public Snake(int x0, int y0, int x1, int y1 ){
        snakeX[0] = x0;
        snakeY[0] = y0;
        snakeX[1] = x1;
        snakeY[1] = y1;
    }

    //движение змейки по игровому полю
    public void move(){

        for (int e = length; e > 0; e--)
        {
            snakeX[e] = snakeX[e -1];
            snakeY[e] = snakeY[e -1];
        }

        if (direction == 0) snakeX[0]++;
        if (direction == 1) snakeY[0]++;
        if (direction == 2) snakeX[0]--;
        if (direction == 3) snakeY[0]--;

        for (int d = length; d > 0; d--) //поедание змейки самой себя
        {
            if((snakeX[0] == snakeX[d]) & (snakeY[0] == snakeY[d]))
                length = d - 2;
        }

        if (length < 2) length = 2;

        //змейка не должна убегать за пределы экрана
        if (snakeX[0] > main.WIDHT) snakeX[0] = 0;
        if (snakeX[0] < 0) snakeX[0] = main.WIDHT -1;
        if (snakeY[0] > main.HIGHT - 1) snakeY[0] = 0;
        if (snakeY[0] < 0) snakeY[0] = main.HIGHT -1;
    }
}
