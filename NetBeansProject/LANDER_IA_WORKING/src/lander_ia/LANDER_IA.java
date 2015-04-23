/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lander_ia;

import java.awt.Point;
import java.util.ArrayList;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.MLMethod;
import org.encog.ml.MLResettable;
import org.encog.ml.MethodFactory;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.FeedForwardPattern;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author Lope
 */
public class LANDER_IA extends BasicGame{
    SpriteSheet fondo;
    SpriteSheet fondo2;
    Nave nave;
    GraficoRNA grafico;
    GraficoPuntuacion grafico2;
    MLTrain train;
    int epoch = 1;
    int flagSpeed = 0;
    boolean flagNoRender = false;
    ArrayList<Float> listaPuntuaciones = new ArrayList<>();
    
    @Override
    public void keyPressed(int key, char c){
        if(c == '1') 
            flagSpeed = 1;
        if(c == '2')
            flagSpeed = 2;
        if(c == '3')
            flagNoRender = !flagNoRender;
    }
    
    public static BasicNetwork createNetwork(){
        FeedForwardPattern pattern = new FeedForwardPattern();
        pattern.setInputNeurons(3);
        pattern.addHiddenLayer(5);
        pattern.setOutputNeurons(1);
        pattern.setActivationFunction(new ActivationTANH());
        BasicNetwork network = (BasicNetwork)pattern.generate();
        network.reset();
        return network;
    }
    
    public LANDER_IA(){
        super("Lunar Lander IA");
    }
    
    public void rondaEntrenamiento(){
                double error = train.getError();
                int generaciones  = 0;
                
                while(error == train.getError() && generaciones < 20 ){
                    train.iteration();
                    System.out.println("Epoch #" + epoch + " Score:" + train.getError());
                    epoch++;
                    generaciones++;
                }
    }
    
    public static void main(String[] args) {
        try{
            AppGameContainer app = new AppGameContainer(new LANDER_IA());
            app.setDisplayMode(600, 530, false);
            app.setMaximumLogicUpdateInterval(60);			
            app.setTargetFrameRate(60);
            app.setAlwaysRender(true);
            app.setVSync(true);
            app.start();
        }
        catch (SlickException e){}
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        fondo =  new SpriteSheet("data/FondoLander.png",304,667);
        fondo2 =  new SpriteSheet("data/fondo2.png",304,667);

        train = new MLMethodGeneticAlgorithm(new MethodFactory(){
        @Override
        public MLMethod factor() {
                final BasicNetwork result = createNetwork();
                ((MLResettable)result).reset();
                return result;
            }
        },new PilotScore(),500);

        //train.finishTraining();

        System.out.println("\nHow the winning network landed:");
        BasicNetwork network = (BasicNetwork)train.getMethod();
        NeuralPilot pilot = new NeuralPilot(network,true);
        System.out.println(pilot.scorePilot());

        nave = new Nave(network);
        grafico = new GraficoRNA(network, new Point(305,0), new Point(600,296));
        grafico2 = new GraficoPuntuacion(network, new Point(305,368), new Point(600,529));
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        if(nave.isFlying() || nave.explosionFlag)
            nave.update();
        else{
            this.rondaEntrenamiento(); // Grafica puntuacion
            listaPuntuaciones.add((float) train.getError());
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {}
            
            grafico2.AddScore(epoch, (float)train.getError());
            nave = new Nave((BasicNetwork) train.getMethod());
            grafico = new GraficoRNA((BasicNetwork) train.getMethod(), new Point(305,0), new Point(600,296));
        }
        
        if(flagSpeed != 0){
            if(flagSpeed == 1){
                gc.setMaximumLogicUpdateInterval(30);			
                gc.setTargetFrameRate(60);
                gc.setAlwaysRender(true);
                gc.setVSync(true);
                flagSpeed = 0;
            }else{
                gc.setMaximumLogicUpdateInterval(0);			
                gc.setTargetFrameRate(2000);
                gc.setAlwaysRender(false);
                gc.setVSync(false);
                flagSpeed = 0;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics grphcs) throws SlickException {
        if(!flagNoRender){
            fondo.draw(0,-120);
            nave.draw();
            fondo2.draw(0,-120);
            grafico.draw(grphcs);
            grafico2.draw(grphcs);
        }
        
        grphcs.setAntiAlias(false);
        grphcs.setColor(Color.white);
        grphcs.fillRect(306, 298, 293, 70);
        
        grphcs.setColor(Color.black);
        grphcs.drawString( "Score: "+train.getError()+" Generacion: "+epoch, 320, 340 );
        grphcs.drawString("Fuel: "+nave.sim.getFuel()+" Velocidad: "+(int)nave.sim.getVelocity(), 320, 320);
                
        grphcs.setColor(Color.red);
        if(nave.thrust)
            grphcs.drawString("THRUST!",426, 200);
    }
}
