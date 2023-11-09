package snake;

public class Snake {
    
    public static void main(String[] args) {
        SnakeView.getInstancia().setVisible(true);
        SnakeController snakeModel = SnakeController.getInstancia();
        snakeModel.adicionarSnakeView();
        SnakeLogic snakeLogic = new SnakeLogic();
        snakeModel.adicionarObserver(snakeLogic);
        snakeLogic.start();
    }
}
