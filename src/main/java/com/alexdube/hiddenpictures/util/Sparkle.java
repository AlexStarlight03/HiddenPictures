package com.alexdube.hiddenpictures.util;

import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;


public class Sparkle {
    // position x, y d'un seul sparkle, ça vélocité x et vélocité y ainsi que son radius.
    // Faire une initialisation de chaque sparkle avec Math.random() permet aux particules de bouger de façon unique
    //et d'avoir des tailles diverses.
    double x, y, vx, vy, r;
    double speed;
    double acceleration;
    double angle;
    Color c;

    public Sparkle(double centerX, double centerY) {
        x = centerX;
        y = centerY;
        angle = Math.random() * 2 * Math.PI;
        speed = 0.005 + Math.random() * 0.1;
        acceleration = 0.001 + Math.random() * 0.003;
        vx = Math.cos(angle) * speed;
        vy = Math.sin(angle) * speed;
        r = 1 + Math.random() * 3;
        c = Color.hsb(Math.random() * 360, 1, 1, 0.9);

    }

    private void update() {
        speed += acceleration;
        vx = Math.cos(angle) * speed;
        vy = Math.sin(angle) * speed;
        x += vx;
        y += vy;
    }

    public static void sparkleAnimation(ImageView foundImg) {
        AnchorPane parent = (AnchorPane) foundImg.getParent();

        Bounds bounds = foundImg.localToScene(foundImg.getBoundsInLocal());
        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();

        double paneW = parent.getWidth();
        double paneH = parent.getHeight();

        Canvas canvas = new Canvas(paneW, paneH);
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        GraphicsContext g = canvas.getGraphicsContext2D();

        parent.getChildren().add(canvas);

        int n = 180;
        Sparkle[] sparks = new Sparkle[n];
        for (int i = 0; i < n; i++){
            sparks[i] = new Sparkle(centerX, centerY);
        }

        new AnimationTimer() {
            private long timerStart = -1;

            @Override
            public void handle (long now) {
                if (timerStart < 0) {
                    timerStart = now;
                }
                double animationTime = (now - timerStart) / 1_000_000_000.0;
                if (animationTime > 1.2) {
                    this.stop();
                    parent.getChildren().remove(canvas);
                    return;
                }

                g.clearRect(0, 0, paneW, paneH);

                for (Sparkle s: sparks){
                    s.update();
                    g.setFill(s.c);
                    g.fillOval(s.x, s.y, s.r * 2, s.r * 2);
                }
            }
        }.start();

    }

}

