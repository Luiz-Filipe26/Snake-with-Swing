package snake;

public class Snake {
    
    public static void main(String[] args) {
        SnakeView.getInstancia().setVisible(true);
        SnakeController snakeController = SnakeController.getInstancia();
        snakeController.adicionarSnakeView();
        snakeController.adicionarCampoJogo();
        SnakeLogic snakeLogic = new SnakeLogic();
        snakeController.adicionarObserver(snakeLogic);
        snakeLogic.start();
    }
}