package models;

import java.awt.*;

public class Character extends AbstractCharacter {
    private Image img;

    public Character(Image img, int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
        this.img = img;
    }

    @Override
    public void update() {
        posX += velocityX;
        posY += velocityY;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, posX, posY, width, height, null);
    }

    // Getter dan Setter untuk atribut img
    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
