package snake;

public class SnakeMain {
    
    public static void main(String[] args) {
        //Inicializa as classes de View, Controller e de desenho no campo
        SnakeView.getInstancia().setVisible(true);
        SnakeController snakeController = SnakeController.getInstancia();
        snakeController.adicionarSnakeView();
        snakeController.adicionarCampoJogo();
        SnakeLogic snakeLogic = new SnakeLogic();
        snakeController.adicionarObserver(snakeLogic);
        snakeLogic.start();
    }
}
