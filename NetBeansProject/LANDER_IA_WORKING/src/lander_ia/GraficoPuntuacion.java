/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lander_ia;

import java.awt.Point;
import java.util.HashMap;
import org.encog.neural.networks.BasicNetwork;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Lope
 */
public class GraficoPuntuacion {
    HashMap<Integer, Float> listaPuntuaciones;
    private final Point tlCorner;
    private final Point brCorner;
    int alto, ancho;
    int lastGen = 0;
    int maxScore = 30000;
    Float puntuacion;
    Point lastPoint;
    Point point;
    
    public GraficoPuntuacion(BasicNetwork _red, Point _tlCorner, Point _brCorner){
        tlCorner = _tlCorner;
        brCorner = _brCorner;
        alto = brCorner.y - tlCorner.y -20;
        ancho = brCorner.x - tlCorner.x-20;
        listaPuntuaciones = new HashMap<>();  
    }
    
     public void AddScore(int gen, float score){
        listaPuntuaciones.put(gen, score);
        lastGen = (int) gen;
    }
    
    public void draw(Graphics grphcs){
        grphcs.setColor(Color.white);
        grphcs.setAntiAlias(false);
        grphcs.fillRect(tlCorner.x,tlCorner.y, brCorner.x - tlCorner.x, brCorner.y - tlCorner.y);
        grphcs.setColor(Color.black);
        grphcs.drawRect(tlCorner.x,tlCorner.y, brCorner.x - tlCorner.x, brCorner.y - tlCorner.y);
        grphcs.setAntiAlias(true);
        grphcs.setLineWidth(2);
        
        grphcs.drawLine(tlCorner.x+10, tlCorner.y+10, tlCorner.x+10, brCorner.y-10);
        grphcs.drawLine(tlCorner.x+10, brCorner.y-10, brCorner.x-10, brCorner.y-10);
        
        
        lastPoint = new Point( tlCorner.x+10, brCorner.y-10);
        for(int gen = 1; gen <= lastGen; gen++){
            Float puntuation = listaPuntuaciones.get(gen);
            if(puntuation != null){
                float yRatio =  puntuation/(float)maxScore;
                float xRatio =  gen/(float)lastGen; 

                point = new Point( (int)(tlCorner.x+ xRatio*ancho+10),(int)(brCorner.y-yRatio*alto-10));

                grphcs.setColor(Color.red);
                grphcs.drawLine(lastPoint.x+2,lastPoint.y+2, point.x+2, point.y+2);
                grphcs.setColor(Color.blue);
                grphcs.fillOval(point.x,point.y, 5, 5);
                grphcs.fillOval(lastPoint.x,lastPoint.y, 5, 5);
                lastPoint = point;
            }
        }
    }    
}
