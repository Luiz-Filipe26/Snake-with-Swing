
package snake;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cobrinha {
    private List<Point> corpoCobrinha;
    private Map<Point, Point> direcoesCobrinha;
    
    private final int unidadeLargura;
    private final int unidadeAltura;
    private final int larguraJogo;
    private final int alturaJogo;
    
    public final Point DIREITA;
    public final Point ESQUERDA;
    public final Point CIMA;
    public final Point BAIXO;
    
    private Point pontoRemovido;
    private Point direcaoPontoRemovido;
    
    public Cobrinha() {
        direcoesCobrinha = new HashMap();
        
        int[] valoresDimensionais = SnakeController.getInstancia().getValoresDimensionais();
        unidadeLargura = valoresDimensionais[0];
        unidadeAltura = valoresDimensionais[1];
        larguraJogo = valoresDimensionais[2];
        alturaJogo = valoresDimensionais[3];
        
        DIREITA = new Point(unidadeLargura, 0);
        ESQUERDA = new Point(-unidadeLargura, 0);
        CIMA = new Point(0, -unidadeAltura);
        BAIXO = new Point(0, unidadeAltura);
        
        criarCobrinhaInicial();
    }
    
    public List<Point> getCorpoCobrinha() {
        return corpoCobrinha;
    }
    
    public Map<Point, Point> getDirecoesCobrinha() {
        return direcoesCobrinha;
    }
    
    public Point getPontoRemovido() {
        return pontoRemovido;
    }
    
    public int getTamanho() {
        return corpoCobrinha.size();
    }
    
    public boolean moverCobrinha(Point direcao, boolean atravessarBordas) {
        Point novaCabeca = new Point(corpoCobrinha.get(0).x + direcao.x, corpoCobrinha.get(0).y + direcao.y);
        if (corpoCobrinha.contains(novaCabeca)) {
            return false;
        } else if (atravessouBorda(novaCabeca)) {
            if (!atravessarBordas) {
                return false;
            }
            novaCabeca = novaDirecaoAlemDaBorda(novaCabeca);
        }
        corpoCobrinha.add(0, novaCabeca);
        direcoesCobrinha.put(novaCabeca, direcao);
        
        pontoRemovido = corpoCobrinha.remove(corpoCobrinha.size()-1);
        direcaoPontoRemovido = direcoesCobrinha.remove(pontoRemovido);
        
        return true;
    }
    
    public void crescerCobrinha() {
        if(pontoRemovido != null) {
            corpoCobrinha.add(pontoRemovido);
            direcoesCobrinha.put(pontoRemovido, direcaoPontoRemovido);
            pontoRemovido = null;
            direcaoPontoRemovido = null;
        }
    }
    
    private boolean atravessouBorda(Point p) {
        return p.x < 0 || p.x > unidadeLargura * (larguraJogo - 1) || p.y < 0 || p.y > unidadeAltura * (alturaJogo - 1);
    }
    
    private Point novaDirecaoAlemDaBorda(Point p) {
        p.x = (p.x + unidadeLargura * larguraJogo) % (unidadeLargura * larguraJogo);
        p.y = (p.y + unidadeAltura * alturaJogo) % (unidadeAltura * alturaJogo);
        return p;
    }
    
    private void criarCobrinhaInicial() {
        corpoCobrinha = new ArrayList();
        corpoCobrinha.add(new Point(unidadeLargura * larguraJogo / 2, unidadeAltura * alturaJogo / 2));
        corpoCobrinha.add(new Point(corpoCobrinha.get(0).x - unidadeLargura, corpoCobrinha.get(0).y));
        corpoCobrinha.add(new Point(corpoCobrinha.get(1).x - unidadeLargura, corpoCobrinha.get(0).y));
        
        direcoesCobrinha = new HashMap();
        direcoesCobrinha.put(corpoCobrinha.get(0), DIREITA);
        direcoesCobrinha.put(corpoCobrinha.get(1), DIREITA);
        direcoesCobrinha.put(corpoCobrinha.get(2), DIREITA);
    }
}
