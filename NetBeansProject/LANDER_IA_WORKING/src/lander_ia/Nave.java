/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lander_ia;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.newdawn.slick.Animation;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Lope
 */
public class Nave {
    LanderSimulator sim;
    BasicNetwork red;
    Animation animacion;
    Animation animacionFuego;
    Animation explosion;
    boolean thrust;
    boolean explosionFlag;
    int x,y;
    
    private NormalizedField fuelStats;
    private NormalizedField altitudeStats;
    private NormalizedField velocityStats;
    
    public Nave(BasicNetwork _red){
        fuelStats = new NormalizedField(NormalizationAction.Normalize, "fuel", 200, 0, -0.9, 0.9);
        altitudeStats = new NormalizedField(NormalizationAction.Normalize, "altitude", 10000, 0, -0.9, 0.9);
        velocityStats = new NormalizedField(NormalizationAction.Normalize, "velocity", LanderSimulator.TERMINAL_VELOCITY, -LanderSimulator.TERMINAL_VELOCITY, -0.9, 0.9);

        explosionFlag = true;
        red = _red;
        sim = new LanderSimulator();
        SpriteSheet spriteNave = null;
        SpriteSheet spriteFuego = null;
        SpriteSheet spriteExplosion = null;
        try{
            spriteNave =  new SpriteSheet("data/nave.bmp",52,39);
            spriteFuego =  new SpriteSheet("data/fuego.bmp",21,19);
            spriteExplosion = new SpriteSheet("data/explosion.bmp",75,179);
        }catch(Exception e){}
        animacion = new Animation(spriteNave,100);
        animacionFuego = new Animation(spriteFuego,200);
        explosion = new Animation(spriteExplosion,50); 
        explosion.stopAt(23);
       
        x = 130;
    }
    
    public void update(){
            MLData input = new BasicMLData(3);
            input.setData(0, this.fuelStats.normalize(sim.getFuel()));
            input.setData(1, this.altitudeStats.normalize(sim.getAltitude()));
            input.setData(2, this.velocityStats.normalize(sim.getVelocity()));
            MLData output = this.red.compute(input);
            double value = output.getData(0);
            
            y = (int) (480 - 500*(sim.getAltitude()/10000));

            thrust = value > 0;
		
            if(sim.flying())
                sim.turn(thrust);
    }
    public void draw(){
        animacion.draw(x,y);
        if(thrust && sim.flying() && (sim.getFuel() > 0)) animacionFuego.draw(x+20,y+32);
        
        if(!sim.flying()){
            
            if(sim.getVelocity() < -2)
            explosion.draw(x-13,y-125);    
            animacion.stop();
        
            if(explosion.isStopped() || sim.getVelocity()>= -2)
                explosionFlag = false;  
        }
    }
    
    public boolean isFlying(){
        return sim.flying();
    }   
}
