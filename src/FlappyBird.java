import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener { //inheritance of flappy class to jpanel
    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;


    //bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y= birdY;
        int width= birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x= pipeX;
        int y= pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed= false;

       Pipe(Image image){
        this.img= image;
       } 
    }

   //game logic
    Bird bird;
    int velocityX = -4; //moves pipe to left speed 
    int velocityY = 0;
    int gravity =1;

    ArrayList<Pipe> pipes;
    Random random = new Random();



    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver=false; 
    double score = 0;

    FlappyBird() { //constructor created
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        //setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this); //added to check key functions

        //load image
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage(); 
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage(); 
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage(); 
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage(); 
       
        //bird 
        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>(); 

         //placepipetimer
        placePipesTimer = new Timer(1500,new ActionListener() {
            @Override
            public  void actionPerformed(ActionEvent e){
                placePipes();;
            }
        });

        placePipesTimer.start();

        //game timer
        gameLoop= new Timer(1000/60, this);//1000/60=16.6
        gameLoop.start();


    }

    public void placePipes(){
        //(0-1) * pipeHeight/2
        int randomPipeY =(int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);  
        
        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y +pipeHeight+ openingSpace;
        pipes.add(bottomPipe);
    
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g); //invokes the function from j panel
        draw(g);
        }

        public void draw(Graphics g){
            //background
            g.drawImage(backgroundImage, 0, 0, boardWidth,boardHeight,null);
            //bird
            g.drawImage(bird.img, bird.x, bird.y, bird.width,bird.height,null); 

            //pipes
            for(int i=0;i<pipes.size();i++){
                Pipe pipe= pipes.get(i);
                g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
            }

            //score
            g.setColor(Color.white);
            g.setFont(new Font("Ariel",Font.PLAIN,32));
            if(gameOver){
                g.drawString("Game Over: " +String.valueOf((int) score), 10, 35);             
            }
            else{
                g.drawString(String.valueOf((int) score), 10, 35);
            }
        }

        public void move() {
            //update birds position
            velocityY += gravity;
            bird.y += velocityY;
            bird.y = Math.max(bird.y, 0);
            
            //pipe
            for(int i=0;i<pipes.size();i++){
                Pipe pipe = pipes.get(i);
                pipe.x += velocityX;

                if(!pipe.passed && bird.x > pipe.x + pipe.width){
                    pipe.passed = true; 
                    score += 0.5;

                }

                if(collision(bird, pipe)){
                    gameOver = true;
                }
            }

            if (bird.y >boardHeight){
                gameOver=true;

            }
        }

         public boolean collision(Bird a, Pipe b) {
            return a.x < b.x + b.width && //a topleft corner doesnt reach b right corner
                   a.x + a.width > b.x && //a topright corner passes b top left corner
                   a.y < b.y + b.height && //a topleft corner doesnt reach b bottomleft corner
                   a.y + a.height > b.y ; //a bottomleft corner passes b topleft corner

         }

        @Override
        public void actionPerformed(ActionEvent e) {
            move();
            repaint();
            if(gameOver) {
                placePipesTimer.stop();
                gameLoop.stop();
            }
        }


        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE ){
                velocityY = -9;
                if (gameOver) {
                    //reset conditions and restart the game
                    bird.y = birdY;
                    velocityY = 0;
                    pipes.clear();;
                    score=0;
                    gameOver = false;
                    gameLoop.start();
                    placePipesTimer.start();
                }

            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
           
        }

        @Override
        public void keyReleased(KeyEvent e) {
            
        }
}
