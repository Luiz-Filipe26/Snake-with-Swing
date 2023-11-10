
package snake;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SnakeController {
    
    private static SnakeController snakeController;
    private static SnakeView snakeView;
    private static DesenhoCampoJogo desenhoCampoJogo;
    
    private List<ViewObserver> observers;
    
    
    public static SnakeController getInstancia() {
        if(snakeController == null) {
            snakeController = new SnakeController();
        }
        return snakeController;
    }
    
    private SnakeController() {
        observers = new ArrayList<>();
    }
    
    public void adicionarSnakeView() {
        snakeView = SnakeView.getInstancia();
    }
    
    public void adicionarCampoJogo() {
        desenhoCampoJogo = DesenhoCampoJogo.getInstancia();
    }
    
    public int[] getValoresDimensionais() {
        int[] valores = {
            snakeView.getUnidadeLargura(),
            snakeView.getUnidadeAltura(),
            (snakeView.getJpanelLargura() - (snakeView.getXMargem() * 2)) / snakeView.getUnidadeLargura(),
            (snakeView.getJpanelAltura() - (snakeView.getYMargem() * 2)) / snakeView.getUnidadeAltura(),
            snakeView.getXMargem(),
            snakeView.getYMargem()};
        
        return valores;
    }
    
    public Graphics getGrafico() {
        return snakeView.getGrafico();
    }
    
    public BufferedImage getBuffer() {
        return snakeView.getBuffer();
    }
    
    public void notificaNovoRecordeSeFor(int pontos) {
        if (GerenciadorRecordes.getInstancia().isNovoRecorde(pontos)) {
            snakeView.notificaNovoRecorde(pontos);
        }
    }
    
    public List<List<String>> lerRecordes() {
        return GerenciadorRecordes.getInstancia().lerRecordes();
    }
    
    public void inserirRecorde(String nome, int pontos) {
        GerenciadorRecordes.getInstancia().inserirRecorde(nome, pontos);
    }
    
    public void atualizarNumMaca(int comidas) {
        snakeView.atualizarNumMaca(comidas);
    }
    
    public void novoJogoCampo() {
        desenhoCampoJogo.novoJogo();
    }
    
    public void desenharJogo(Cobrinha cobrinha, boolean crescendo, List<Point> posicoesMacaComida, List<Point> posicoesMaca) {
        desenhoCampoJogo.desenharJogo(cobrinha, crescendo, posicoesMacaComida, posicoesMaca);
    }
    
    public void perdeuOJogo() {
        snakeView.perdeuOJogo();
    }
    
    public void venceuOJogo() {
        snakeView.venceuOJogo();
    }
    
    public void adicionarObserver(ViewObserver observer) {
        observers.add(observer);
    }

    public void removerObserver(ViewObserver observer) {
        observers.remove(observer);
    }
    
    public void keyPressed(int keyCode) {
        for(ViewObserver o: observers) {
            o.keyPressed(keyCode);
        }
    }
    
    public void novoJogo(float dificuldade, boolean atrevessarBordas) {
        for(ViewObserver o: observers) {
            o.novoJogo(dificuldade, atrevessarBordas);
        }
    }
    
    public void fecharJogo() {
        for(ViewObserver o: observers) {
            o.fecharJogo();
        }
    }
    
    public void viewFechada() {
        for(ViewObserver o: observers) {
            o.viewFechada();
        }
    }
}
