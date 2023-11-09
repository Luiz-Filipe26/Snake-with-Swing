
package snake;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SnakeController {
    
    private static SnakeController sc;
    private static SnakeView view;
    
    private List<ViewObserver> observers;
    
    
    public static SnakeController getInstancia() {
        if(sc == null) {
            sc = new SnakeController();
        }
        return sc;
    }
    
    private SnakeController() {
        observers = new ArrayList<>();
    }
    
    public int[] getValoresDimensionais() {
        int[] valores = new int[4];
        valores[0] = view.getUnidadeLargura();
        valores[1] = view.getUnidadeAltura();
        valores[2] = (view.getJpanelLargura() - (view.getXMargem() * 2)) / valores[0];
        valores[3] = (view.getJpanelAltura() - (view.getYMargem() * 2)) / valores[1];
        
        return valores;
    }
    
    public void adicionarSnakeView() {
        view = SnakeView.getInstancia();
    }
    
    public void notificaNovoRecordeSeFor(int pontos) {
        if (GerenciadorRecordes.getInstancia().isNovoRecorde(pontos)) {
            view.notificaNovoRecorde(pontos);
        }
    }
    
    public List<List<String>> lerRecordes() {
        return GerenciadorRecordes.getInstancia().lerRecordes();
    }
    
    public void inserirRecorde(String nome, int pontos) {
        GerenciadorRecordes.getInstancia().inserirRecorde(nome, pontos);
    }
    
    public void atualizarNumMaca(int comidas) {
        view.atualizarNumMaca(comidas);
    }
    
    public void desenharJogo(Cobrinha cobrinhaObj, boolean apagarPonto, Point posMaca, boolean apagarMaca, boolean macaGrande) {
        view.desenharJogo(cobrinhaObj, apagarPonto, posMaca, apagarMaca, macaGrande);
    }
    
    public void perdeuOJogo() {
        view.perdeuOJogo();
    }
    
    public void venceuOJogo() {
        view.venceuOJogo();
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
