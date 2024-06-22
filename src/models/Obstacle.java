package models;

import java.awt.*;

public class Obstacle implements GameObject {
    private int posX, posY;
    private int width, height;
    private int velocityX;
    private Image img;
    private boolean scored;
    private int point;

    public Obstacle(int posX, int posY, int width, int height, Image img) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.img = img;
        this.velocityX = -2; // Kecepatan default untuk bergerak ke kiri
        this.scored = false;
        this.point = height;
    }

    @Override
    public void update() {
        // Logika pembaruan obstacle, misalnya memperbarui posisi berdasarkan kecepatan
        posX += velocityX;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, posX, posY, width, height, null);
    }

    // Getter dan Setter untuk atribut
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(int velocityX) {
        this.velocityX = velocityX;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Rectangle getBounds() {
        return new Rectangle(posX, posY, width, height);
    }
}
