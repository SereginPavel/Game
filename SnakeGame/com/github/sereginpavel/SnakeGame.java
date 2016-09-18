package com.github.sereginpavel;

import com.github.sereginpavel.objects.Apple;
import com.github.sereginpavel.objects.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Павел on 13.07.2015.
 */
public class SnakeGame extends JPanel implements ActionListener
{
    public static final int SCALE = 32; //размер одной клетки.
    public static final int WIDHT = 20; //ширина(в клетках)
    public static final int HIGHT = 20; //высота(в клетках)
    public static final int SPEED = 5; //скорость движения змейки

    Apple apple = new Apple((int) (Math.random() * WIDHT),(int) (Math.random() * HIGHT));
    Snake s = new Snake(10, 10, 9, 10);
    Timer t = new Timer(1000 / SPEED, this);

    //конструктор запускает таймер
    public SnakeGame(){
        t.start();
        addKeyListener(new Keyboard());
        setFocusable(true);
    }

    //отрисовка сетки и объектов
    public void paint(Graphics g){
        g.setColor(color(15, 20, 5));
        g.fillRect(0, 0, SCALE * WIDHT, SCALE * HIGHT);
        g.setColor(color(255, 216, 0));

        for (int xx = 0; xx <= SCALE * WIDHT; xx += SCALE) {
            g.drawLine(xx, 0, xx, SCALE * HIGHT);
        }

        for (int yy = 0; yy <= SCALE * HIGHT; yy += SCALE) {
            g.drawLine(0, yy, SCALE * WIDHT, yy);
        }

        for (int e = 0; e < s.length; e++)
        {
            g.setColor(color(20, 30, 150));
            g.fillRect(s.snakeX[e]*SCALE+1, s.snakeY[e] * SCALE + 1, SCALE + 1, SCALE +1);
        }
        g.setColor(color(255, 0, 0));
        g.fillRect(apple.posX * SCALE +1, apple.posY * SCALE + 1,SCALE -1, SCALE - 1);
    }

    //установка цвета
    public Color color(int red, int green, int blue){
        return new Color(red, green, blue);
    }

    //отрисовка окна
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(SCALE * WIDHT +7, SCALE * HIGHT + 30);
        frame.setLocationRelativeTo(null);
        frame.add(new SnakeGame());
        frame.setVisible(true);
    }

    @Override //обработка событий
    public void actionPerformed(ActionEvent e)
    {
        s.move();
        //поедание яблока
        if ((s.snakeX[0] == apple.posX) & (s.snakeY[0] == apple.posY)){
            apple.setRandomPosition();
            s.length++;
        }
        //яблока не должно появляться на змейке
        for (int i = 1; i < s.length; i++)
        {
            if ((s.snakeX[i] == apple.posX) & (s.snakeY[i] == apple.posY)){
                apple.setRandomPosition();
            }
        }

        repaint();
    }

    private class Keyboard extends KeyAdapter{

        //обработка нажатия клавиш
        public void keyPressed(KeyEvent keyEv){
            int key = keyEv.getKeyCode();
            if ((key == KeyEvent.VK_RIGHT) & s.direction !=2) s.direction = 0;
            if ((key == KeyEvent.VK_DOWN) & s.direction != 3) s.direction = 1;
            if ((key == KeyEvent.VK_LEFT) & s.direction != 0) s.direction = 2;
            if ((key == KeyEvent.VK_UP) & s.direction != 1) s.direction = 3;
        }
    }
}
