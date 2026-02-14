import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
public class Main extends Application{
    //Particle demoParticle=new Particle(400+350, 300, -1, 20, 200,0,0);
    List<Particle> particles=new ArrayList<>();
    Particle BlackHole=new Particle(400,300,0,0,1000_0,0,0);
    double EventHorizon=40;
    public void start(Stage stage){
        Canvas canvas=new Canvas(800,600);
        GraphicsContext gc=canvas.getGraphicsContext2D();
        Pane root=new Pane(canvas);
        Scene scene=new Scene(root);
        stage.setTitle("New Black Hole Simulator");
        stage.setScene(scene);
        stage.show();
        int count=100;
        double spawnRadius=250;

        for(int i=0;i<count;i++){
            double angle = Math.random()*2*Math.PI;
            double r=spawnRadius+Math.random()*30;
            double x=BlackHole.x+r*Math.cos(angle);
            double y=BlackHole.y+r*Math.sin(angle);
            double speed=Math.sqrt(BlackHole.mass/r);
            double vx=-speed * Math.sin(angle);
            double vy=speed*Math.cos(angle);
            particles.add(new Particle(x,y,vx,vy,100,0,0));
        }
        new AnimationTimer() {
            long lastTime=0;
            public void handle(long now){
                if(lastTime==0){
                    lastTime=now;
                    return;
                }
                double dt=(now - lastTime) / 1_000_000_000.0;
                lastTime=now;
                update(dt);
                //collisionHandle(demoParticle,demoParticle.x, demoParticle.y);
                render(gc);
            }
        }.start();
    }
    void update(double dt) {

    for (Particle p : particles) {
        if (!p.alive) continue;
        p.resetAcceleration();
    }

    // Black hole gravity
    for (Particle p : particles) {
        if (!p.alive) continue;
        p.applyGravity(BlackHole,dt);
    }

    // Particle-particle gravity
    for (int i = 0; i < particles.size(); i++) {

        Particle a = particles.get(i);
        if (!a.alive) continue;

        for (int j = i + 1; j < particles.size(); j++) {

            Particle b = particles.get(j);
            if (!b.alive) continue;

            a.applyGravity(b,dt);
            b.applyGravity(a,dt);
        }
    }

    // Integrate motion
    for (Particle p : particles) {
        if (!p.alive) continue;
        p.update(dt);
    }
}

    
    


    boolean collisionHandle(Particle particle,double x,double y){
        if(particle.x>=800 || particle.x<=0){
            if(particle.x>=800){
                particle.x=800;
            }
            else{
                particle.x=0;
            }
            particle.vx=-(particle.vx);
            return true;
        }
        if(particle.y>=600 || particle.y<=0){
             if(particle.y>=600){
                particle.y=600;
            }
            else{
                particle.y=0;
            }
            particle.vy=-(particle.vy);
            return true;
        }
        return false;
    }
    void render(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,800,600);
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(BlackHole.x-15,BlackHole.y-15,40,40);
        gc.setFill(Color.WHITE);
        for(Particle p:particles){
            if(!p.alive)return;
            gc.fillOval(p.x,p.y,4,4);
        }

    }
    public static void main(String [] args){
        launch(args);
    }
}
class Particle{
    double x;
    double y;
    double vx;
    double vy;
    double mass;
    double ax;
    double ay;
    boolean alive=true;
    Particle(double x,double y,double vx,double vy,double mass,double ax,double ay){
        this.x=x;
        this.y=y;
        this.vx=vx;
        this.vy=vy;
        this.mass=mass;
        this.ax=ax;
        this.ay=ay;
    }
    void resetAcceleration(){
        ax=0;
        ay=0;
    }
    public void applyGravity(Particle other,double dt){
        double dx=other.x-x;
        double dy=other.y-y;
        double distanceSquared=dx*dx+dy*dy;
        double distance=Math.sqrt(distanceSquared);
        double softening=5;
        double G=1.0;
        if(distance < 1e-3)return;
        double force=G*other.mass/(distanceSquared+softening*softening);
        ax+=force*dx/distance;
        ay+=force*dy/distance;
    }
    void update(double dt) {
    x += vx * dt + 0.5 * ax * dt * dt;
    vx += ax * dt;

    y += vy * dt + 0.5 * ay * dt * dt;
    vy += ay * dt;
}

}