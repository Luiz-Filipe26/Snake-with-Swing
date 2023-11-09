package snake;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class SnakeLogic extends Thread implements ViewObserver {

    private final SnakeController snakeController;
    private Queue<Point> direcoesTeclado = new LinkedList();
    private Cobrinha cobrinha;
    private final Object lock;
    
    private final int unidadeLargura;
    private final int unidadeAltura;
    private final int larguraJogo;
    private final int alturaJogo;
    private final int areaJogo;
    
    public final Point DIREITA;
    public final Point ESQUERDA;
    public final Point CIMA;
    public final Point BAIXO;
    
    private final float taxaAumento;
    private final float VELOCIDADE_PADRAO;
    private final float DIFICULDADE_PADRAO;
    private final float NIVEIS_SUBIDOS_EM_UM_JOGO;
    private final float DIFERENCA_VELOCIDADE_POR_DIFICULDADE;
    private float dificuldade;
    
    private boolean atravessarBordas;
    private boolean jogoFechado;
    private boolean novoJogo;
    private boolean viewFechada;
    
    private Random random = new Random();

    public SnakeLogic() {
        lock = new Object();
        SnakeView.getInstancia();
        snakeController = SnakeController.getInstancia();
        int[] valoresDimensionais = snakeController.getValoresDimensionais();
        unidadeLargura = valoresDimensionais[0];
        unidadeAltura = valoresDimensionais[1];
        larguraJogo = valoresDimensionais[2];
        alturaJogo = valoresDimensionais[3];
        jogoFechado = true;
        novoJogo = false;
        viewFechada = false;
        areaJogo = larguraJogo * alturaJogo;
        NIVEIS_SUBIDOS_EM_UM_JOGO = 3;
        DIFERENCA_VELOCIDADE_POR_DIFICULDADE = 0.5f;
        taxaAumento = DIFERENCA_VELOCIDADE_POR_DIFICULDADE * NIVEIS_SUBIDOS_EM_UM_JOGO / areaJogo;
        VELOCIDADE_PADRAO = 5;
        DIFICULDADE_PADRAO = 5;
        
        DIREITA = new Point(unidadeLargura, 0);
        ESQUERDA = new Point(-unidadeLargura, 0);
        CIMA = new Point(0, -unidadeAltura);
        BAIXO = new Point(0, unidadeAltura);
    }

    @Override
    public void run() {

        while (!viewFechada) {

            esperarNovoJogo();
            
            int pontos = gameLoop();
            
            if (cobrinha.getTamanho() == areaJogo) {
                snakeController.venceuOJogo();
            }
            
            snakeController.notificaNovoRecordeSeFor(pontos);
        }
    }
    
    private int gameLoop() {
        float velocidade = VELOCIDADE_PADRAO + DIFERENCA_VELOCIDADE_POR_DIFICULDADE * (dificuldade - DIFICULDADE_PADRAO);
        long tempoEspera = (long) (1000 / velocidade);

        boolean comeu = false;
        boolean macaGrande = false;
        int qtdCrescer = 0;
        int pontos = 0;


        cobrinha = new Cobrinha();
        Point direcaoAtual = DIREITA;

        List<Point> posicoesMaca = gerarPosicaoMaca(cobrinha.getCobrinha(), false);
        snakeController.atualizarNumMaca(0);

        while (!jogoFechado && cobrinha.getTamanho() < areaJogo) {
            direcaoAtual = obterDirecaoTeclado(direcaoAtual);

            if(!cobrinha.moverCobrinha(direcaoAtual, atravessarBordas, qtdCrescer > 0)) {
                snakeController.perdeuOJogo();
                break;
            }

            if (qtdCrescer >0) {
                qtdCrescer--;
            }

            if (comeu) {
                comeu = false;
                macaGrande = random.nextInt(5) == 0;
                posicoesMaca = gerarPosicaoMaca(cobrinha.getCobrinha(), macaGrande);
            }

            if (posicoesMaca.contains(cobrinha.getCobrinha().get(0))) {
                comeu = true;
                pontos += macaGrande ? 4 : 1;
                qtdCrescer += macaGrande ? 4 : 1;
                snakeController.atualizarNumMaca(pontos);
            }

            snakeController.desenharJogo(cobrinha, qtdCrescer > 0, posicoesMaca.get(0), comeu, macaGrande);
            try {
                if(comeu) {
                    velocidade += taxaAumento;
                    tempoEspera = (long) (1000 / velocidade);
                }
                Thread.sleep(tempoEspera);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(SnakeLogic.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        return pontos;
    }
    
    private Point obterDirecaoTeclado(Point direcaoAtual) {
        if(direcaoAtual == null) {
            return DIREITA;
        }
        
        if (direcoesTeclado.isEmpty()) {
            return direcaoAtual;
        }
        Point direcaoTeclado = direcoesTeclado.poll();

        if (direcaoAtual.x == -direcaoTeclado.x && direcaoAtual.y == -direcaoTeclado.y) {
            return direcaoAtual;
        }
        
        return direcaoTeclado;
    }

    private void esperarNovoJogo() {
        synchronized (lock) {
            while (!novoJogo) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {

                }
            }
        }
        novoJogo = false;
    }

    private List<Point> gerarPosicaoMaca(List<Point> cobrinha, boolean macaGrande) {
        Point posMaca = new Point();
        List<Point> posicoesMaca;

        do {
            posMaca.x = unidadeLargura * random.nextInt(larguraJogo - (macaGrande ? 1 : 0));
            posMaca.y = unidadeAltura * random.nextInt(alturaJogo - (macaGrande ? 1 : 0));

            posicoesMaca = new ArrayList<>() {
                {
                    add(posMaca);
                    if (macaGrande) {
                        add(new Point(posMaca.x + unidadeLargura, posMaca.y));
                        add(new Point(posMaca.x, posMaca.y + unidadeAltura));
                        add(new Point(posMaca.x + unidadeLargura, posMaca.y + unidadeAltura));
                    }
                }
            };
        } while (verificarColisaoComCobrinha(posicoesMaca, cobrinha));

        return posicoesMaca;
    }

    private boolean verificarColisaoComCobrinha(List<Point> posicoesMaca, List<Point> cobrinha) {
        for (Point pm : posicoesMaca) {
            if (cobrinha.contains(pm) && !cobrinha.get(0).equals(pm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_W ->
                direcoesTeclado.offer(CIMA);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S ->
                direcoesTeclado.offer(BAIXO);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A ->
                direcoesTeclado.offer(ESQUERDA);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D ->
                direcoesTeclado.offer(DIREITA);
        }
        if (direcoesTeclado.size() == 3) {
            direcoesTeclado.poll();
        }
    }

    @Override
    public void novoJogo(float dificuldade, boolean atravessarBordas) {
        direcoesTeclado.clear();
        direcoesTeclado.offer(new Point(unidadeLargura, 0));
        jogoFechado = false;
        this.dificuldade = dificuldade;
        this.atravessarBordas = atravessarBordas;
        novoJogo = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void fecharJogo() {
        jogoFechado = true;
    }

    @Override
    public void viewFechada() {
        viewFechada = true;
    }
}