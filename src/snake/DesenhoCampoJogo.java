package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class DesenhoCampoJogo {
    private static DesenhoCampoJogo desenhoCampoJogo;
    private static SnakeController snakeController;
    
    private int unidadeLargura;
    private int unidadeAltura;
    private int larguraJogo;
    private int alturaJogo;
    private int xMargem;
    private int yMargem;
    
    public final Point DIREITA;
    public final Point ESQUERDA;
    public final Point CIMA;
    public final Point BAIXO;
    
    private Graphics gBuffer;
    private Graphics gBufferExterno;
    private Graphics gPanel;
    private BufferedImage bufferExterno;
    private BufferedImage buffer;
    
    private BufferedImage imagemCompleta;
    private HashMap<ArrayList<Point>, Point> mapaCauda ;
    private HashMap<Point, Point> mapaCaudaPonta;
    private HashMap<Point, Point> mapaCabeca;
    private HashMap<Point, Image> mapaImagens;
    
    public static synchronized DesenhoCampoJogo getInstancia() {
        if (desenhoCampoJogo == null) {
            desenhoCampoJogo = new DesenhoCampoJogo();
        }
        return desenhoCampoJogo;
    }
    
    private DesenhoCampoJogo() {
        
        snakeController = SnakeController.getInstancia();
        
        int[] valoresDimensionais = snakeController.getValoresDimensionais();
        unidadeLargura = valoresDimensionais[0];
        unidadeAltura = valoresDimensionais[1];
        larguraJogo = valoresDimensionais[2];
        alturaJogo = valoresDimensionais[3];
        xMargem = valoresDimensionais[4];
        yMargem = valoresDimensionais[5];
        
        DIREITA = new Point(unidadeLargura, 0);
        ESQUERDA = new Point(-unidadeLargura, 0);
        CIMA = new Point(0, -unidadeAltura);
        BAIXO = new Point(0, unidadeAltura);
        
        try {
            imagemCompleta = ImageIO.read(getClass().getResource("/snake/snake-graphics.png"));
        } catch (IOException ex) {
            Logger.getLogger(DesenhoCampoJogo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        inicializarMaps();
        inicializarDesenho();
    }
    
    private void inicializarMaps() {
        
        mapaCauda = new HashMap<>();
        Point[][] equivalenciasCauda = { 
            {DIREITA, DIREITA, new Point(1,0)},
            {CIMA, CIMA, new Point(2, 1)},
            {ESQUERDA, CIMA, new Point(0, 1)},
            {CIMA, DIREITA, new Point(0, 0)},
            {DIREITA, BAIXO, new Point(2, 0)},
            {BAIXO, ESQUERDA, new Point(2, 2)} };
        
        for (Point[] equivalencia : equivalenciasCauda) {
            ArrayList<Point> linhaPonto = new ArrayList();
            linhaPonto.add(equivalencia[0]);
            linhaPonto.add(equivalencia[1]);
            mapaCauda.put(linhaPonto, equivalencia[2]);
        }
        
        mapaCaudaPonta = new HashMap<>();
        mapaCaudaPonta.put(CIMA, new Point(3, 2));
        mapaCaudaPonta.put(DIREITA, new Point(4, 2));
        mapaCaudaPonta.put(ESQUERDA , new Point(3, 3));
        mapaCaudaPonta.put(BAIXO, new Point(4, 3));
        
        mapaCabeca = new HashMap<>();
        mapaCabeca.put(CIMA, new Point(3, 0));
        mapaCabeca.put(DIREITA, new Point(4, 0));
        mapaCabeca.put(ESQUERDA , new Point(3, 1));
        mapaCabeca.put(BAIXO, new Point(4, 1));
        
        mapaImagens = new HashMap<>();
        
        for (int x = 0; x < imagemCompleta.getWidth(); x += 64) {
            for (int y = 0; y < imagemCompleta.getHeight(); y += 64) {
                Point coordenadas = new Point(x/64, y/64);
                if(coordenadas.x==1 && coordenadas.y==2) {
                    Image imagem = imagemCompleta.getSubimage(x, y, 64, 64)
                                    .getScaledInstance(xMargem, yMargem, Image.SCALE_SMOOTH);
                    mapaImagens.put(coordenadas, imagem);
                }
                else if(coordenadas.x==0 && coordenadas.y==2) {
                    Image imagem = imagemCompleta.getSubimage(x, y+64, 64, 64)
                                    .getScaledInstance(unidadeLargura*2, unidadeAltura*2, Image.SCALE_SMOOTH);
                    mapaImagens.put(coordenadas, imagem);
                }
                else {
                    Image imagem = imagemCompleta.getSubimage(x, y, 64, 64)
                                    .getScaledInstance(unidadeLargura, unidadeAltura, Image.SCALE_SMOOTH);
                    mapaImagens.put(coordenadas, imagem);
                }
            }
        }
    }
    
    private void inicializarDesenho() {
        int largura = 2*xMargem + larguraJogo * unidadeLargura;
        int altura = 2*yMargem + alturaJogo * unidadeAltura;
        
        gPanel = snakeController.getGrafico();
        bufferExterno = snakeController.getBuffer();
        
        gBufferExterno = bufferExterno.getGraphics();
        buffer = new BufferedImage(largura-xMargem, altura-yMargem, BufferedImage.TYPE_INT_ARGB);
        gBuffer = buffer.getGraphics();

        Image imagemFundo = mapaImagens.get(new Point(1, 2));
        for(int x=0; x<largura; x+=xMargem) {
            gBufferExterno.drawImage(imagemFundo, x, 0, null);
            gBufferExterno.drawImage(imagemFundo, x, altura-yMargem, null);
        }

        for(int y=0; y<altura; y+=yMargem) {
            gBufferExterno.drawImage(imagemFundo, 0, y, null);
            gBufferExterno.drawImage(imagemFundo, largura-xMargem, y, null);
        }

        gBuffer.setColor(Color.white);
        gBuffer.fillRect(0, 0, largura- 2*xMargem, altura- 2*yMargem);
        gBufferExterno.drawImage(buffer, xMargem, yMargem, null);
    }
    
    public void novoJogo() {
        gBuffer.setColor(Color.white);
        gBuffer.fillRect(0, 0, (unidadeLargura*larguraJogo), (unidadeAltura*alturaJogo));
        gBufferExterno.drawImage(buffer, xMargem, yMargem, null);
    }
    
    public synchronized void desenharJogo(Cobrinha cobrinha, boolean crescendo, List<Point> posicoesMacaComida, List<Point> posicoesMaca) {   
        List<Point> corpoCobrinha = cobrinha.getCorpoCobrinha();
        Map<Point, Point> direcoesCobrinha = cobrinha.getDirecoesCobrinha();
        
        Point cabeca = corpoCobrinha.get(0);
        Point cauda = corpoCobrinha.get(1);
        Point caudaPosterior = corpoCobrinha.get(corpoCobrinha.size() - 2);
        Point caudaPonta = corpoCobrinha.get(corpoCobrinha.size() - 1);
        
        if (!crescendo) {
            desenhaPonto(cobrinha.getPontoRemovido());
        }
        
        if(posicoesMacaComida != null) {
            for(Point p : posicoesMacaComida) {
                desenhaPonto(p);
            }
        }
        
        if(posicoesMaca != null) {
            if(posicoesMaca.size() == 1) {
                desenhaPontoImagem(mapaImagens.get(new Point(0, 3)), posicoesMaca.get(0));
            }
            else if(posicoesMaca.size() == 4) {
                desenhaPontoImagem(mapaImagens.get(new Point(0, 2)), posicoesMaca.get(0));
            }
        }

        desenhaPontoImagem(getCabecaImagem(direcoesCobrinha.get(cabeca)), cabeca);
        
        desenhaPonto(cauda);
        desenhaPontoImagem(getCaudaImagem(
                new ArrayList(){{
                    add(direcoesCobrinha.get(cauda));
                    add(direcoesCobrinha.get(cabeca));
                }}), cauda);
        
        desenhaPonto(caudaPonta);
        desenhaPontoImagem(getCaudaPontaImagem(direcoesCobrinha.get(caudaPosterior)), caudaPonta);
        
        gBufferExterno.drawImage(buffer, xMargem, yMargem, null);
        gPanel.drawImage(bufferExterno, 0, 0, null);
    }
    
    private void desenhaPontoImagem(Image im, Point p) {
        gBuffer.drawImage(im, p.x, p.y, null);
    }
    
    private void desenhaPonto(Point p) {
        gBuffer.fillRect(p.x, p.y, unidadeLargura, unidadeAltura);
    }
    
    private Image getCabecaImagem(Point direcao) {        
        Point lugarImagem = mapaCabeca.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaImagem(List<Point> direcoes) {
        Point lugarImagem = mapaCauda.get(direcoes);
        if(lugarImagem == null) {
            lugarImagem = mapaCauda.get(new ArrayList() {{
                add(new Point(-direcoes.get(1).x, -direcoes.get(1).y));
                add(new Point(-direcoes.get(0).x, -direcoes.get(0).y));
            }});
        }
        
        return mapaImagens.get(lugarImagem);
    }
    
    private Image getCaudaPontaImagem(Point direcao) {
        Point lugarImagem = mapaCaudaPonta.get(direcao);
        return mapaImagens.get(lugarImagem);
    }
}
