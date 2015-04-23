/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lander_ia;

import java.awt.Point;
import org.encog.neural.networks.BasicNetwork;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Lope
 */
public class GraficoRNA {
    public BasicNetwork red;
    Point tlCorner, brCorner;
    int lmargin = 20;
    int oldLmargin = 20;
    int tmargin = 40;
    int space, oldspace;
    
    public GraficoRNA(BasicNetwork _red, Point _tlCorner, Point _brCorner){
        red = _red;
        tlCorner = _tlCorner;
        brCorner = _brCorner; 
    }
    
    public void draw(Graphics grphcs){
        grphcs.setColor(Color.white);
        grphcs.setAntiAlias(false);
        grphcs.fillRect(tlCorner.x,tlCorner.y, brCorner.x - tlCorner.x, brCorner.y - tlCorner.y);
        grphcs.setColor(Color.black);
        grphcs.drawRect(tlCorner.x,tlCorner.y, brCorner.x - tlCorner.x, brCorner.y - tlCorner.y);
        grphcs.setAntiAlias(true);
        grphcs.setLineWidth(2);

        for(int i = 0; i<red.getLayerCount(); i++){
           oldLmargin = lmargin; oldspace = space;
           space = (brCorner.x - tlCorner.x-30)/(red.getLayerNeuronCount(i));
           lmargin = (int) ((brCorner.x - tlCorner.x)/2f - (red.getLayerNeuronCount(i)*space)/2)+(space/2)-5;
           
           for(int n = 0; n < red.getLayerNeuronCount(i); n++){
              
               grphcs.setColor(Color.blue);
               grphcs.fillOval(lmargin + tlCorner.x + n*space,tmargin + tlCorner.y + i*70, 15, 15);
               grphcs.setColor(Color.black);
               grphcs.drawOval(lmargin + tlCorner.x + n*space,tmargin + tlCorner.y + i*70, 15, 15);
               
               for(int j = 0; i>0 && j<red.getLayerNeuronCount(i-1); j++){ 
                   int peso = (int)(red.getWeight(i-1, j, n)*255);
                   grphcs.setColor(new Color(255,0,0,peso));
                   grphcs.drawLine(lmargin + tlCorner.x + n*space+8,tmargin + tlCorner.y + i*70-1,
                          oldLmargin + tlCorner.x + j*oldspace+8, tmargin + tlCorner.y + (i-1)*70+17);
               }
           }
        }
    }
}
