
package snake;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cobrinha {
    private List<Point> cobrinha;
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
    
    public List<Point> getCobrinha() {
        return cobrinha;
    }
    
    public Map<Point, Point> getDirecoesCobrinha() {
        return direcoesCobrinha;
    }
    
    public Point getPontoRemovido() {
        return pontoRemovido;
    }
    
    public int getTamanho() {
        return cobrinha.size();
    }
    
    public boolean moverCobrinha(Point direcao, boolean atravessarBordas, boolean crescer) {
        Point novaCabeca = new Point(cobrinha.get(0).x + direcao.x, cobrinha.get(0).y + direcao.y);
        if (cobrinha.contains(novaCabeca)) {
            return false;
        } else if (atravessouBorda(novaCabeca)) {
            if (!atravessarBordas) {
                return false;
            }
            novaCabeca = novaDirecaoAlemDaBorda(novaCabeca);
        }
        cobrinha.add(0, novaCabeca);
        direcoesCobrinha.put(novaCabeca, direcao);
        if(!crescer) {
            pontoRemovido = cobrinha.remove(cobrinha.size()-1);
            direcoesCobrinha.remove(pontoRemovido);
        }
        return true;
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
        cobrinha = new ArrayList();
        cobrinha.add(new Point(unidadeLargura * larguraJogo / 2, unidadeAltura * alturaJogo / 2));
        cobrinha.add(new Point(cobrinha.get(0).x - unidadeLargura, unidadeAltura * alturaJogo / 2));
        cobrinha.add(new Point(cobrinha.get(1).x - unidadeLargura, unidadeAltura * alturaJogo / 2));
        
        direcoesCobrinha = new HashMap();
        direcoesCobrinha.put(cobrinha.get(0), DIREITA);
        direcoesCobrinha.put(cobrinha.get(1), DIREITA);
        direcoesCobrinha.put(cobrinha.get(2), DIREITA);
    }
}
