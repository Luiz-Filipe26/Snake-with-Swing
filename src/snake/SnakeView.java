package snake;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Luiz
 */
public class SnakeView extends javax.swing.JFrame {
    
    private static SnakeView view;
    private SnakeController snakeController;
    
    private int xMargem;
    private int yMargem;
    private int unidadeLargura;
    private int unidadeAltura;
    private int largura;
    private int altura;
    private int tamanhoFonte;
    private int dificuldade;
    private int pontos;
    private boolean jogoFechado;
    public final Point DIREITA;
    public final Point ESQUERDA;
    public final Point CIMA;
    public final Point BAIXO;
    private Graphics gBuffer;
    private Graphics gBufferExterno;
    private Graphics gPanel;
    private HashSet<Integer> direcoesValidas;
    private Queue<Point> direcao = new LinkedList();
    private BufferedImage imagemCompleta;
    private HashMap<ArrayList<Point>, Point> mapaCauda ;
    private HashMap<Point, Point> mapaCaudaPonta;
    private HashMap<Point, Point> mapaCabeca;
    private HashMap<Point, Image> mapaImagens;
    private BufferedImage buffer;
    private BufferedImage bufferExterno;
    
    public static SnakeView getInstancia() {
        if(view == null) {
            view = new SnakeView();
        }
        return view;
    }
    /**
     * Creates new form View
     */
    private SnakeView() {
        snakeController = SnakeController.getInstancia();
        xMargem = 20;
        yMargem = 20;
        unidadeLargura = 30;
        unidadeAltura = 30;
        DIREITA = new Point(unidadeLargura, 0);
        ESQUERDA = new Point(-unidadeLargura, 0);
        CIMA = new Point(0, -unidadeAltura);
        BAIXO = new Point(0, unidadeAltura);
        tamanhoFonte = 16;
        jogoFechado = true;
        dificuldade = 5;
        direcao.add(new Point(30, 0));
        
        try {
            imagemCompleta = ImageIO.read(getClass().getResource("/snake/snake-graphics.png"));
        } catch (IOException ex) {
            Logger.getLogger(SnakeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComponents();
        inicializarMaps();
        inicializarDesenho();
        requestFocus();
        
        jButtonFecharJogo.setVisible(false);
        jButtonSalvarRecorde.setVisible(false);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keycode = e.getKeyCode();
                if(direcoesValidas.contains(keycode)) {
                    snakeController.keyPressed(keycode);
                }
            }
        });
    }
    
    public void inicializarMaps() {
        direcoesValidas = new HashSet();
        int[] direcoesValidasVet = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                                    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D};
        for (int d : direcoesValidasVet) {
            direcoesValidas.add(d);
        }
        
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
    
    public void inicializarDesenho() {
        largura = jPanelGrafico.getWidth();
        altura = jPanelGrafico.getHeight();
        int larguraSobrando = (largura -2 * xMargem) % unidadeLargura;
        int alturaSobrando = (altura -2 * yMargem) % unidadeAltura;
        if(larguraSobrando != 0 || alturaSobrando != 0) {
            largura -= larguraSobrando;
            altura -= alturaSobrando;
            jPanelGrafico.setPreferredSize(new Dimension(largura, altura));
            jPanelGrafico.setSize(new Dimension(largura, altura));
        }
        
        bufferExterno = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        gBufferExterno = bufferExterno.getGraphics();
        buffer = new BufferedImage(largura-xMargem, altura-yMargem, BufferedImage.TYPE_INT_ARGB);
        gBuffer = buffer.getGraphics();
        gPanel = jPanelGrafico.getGraphics();

        Image imagemFundo = mapaImagens.get(new Point(1, 2));
        for(int x=0; x<largura; x+=xMargem) {
            gBufferExterno.drawImage(imagemFundo, x, 0, view);
            gBufferExterno.drawImage(imagemFundo, x, altura-yMargem, view);
        }

        for(int y=0; y<altura; y+=yMargem) {
            gBufferExterno.drawImage(imagemFundo, 0, y, view);
            gBufferExterno.drawImage(imagemFundo, largura-xMargem, y, view);
        }

        gBuffer.setColor(Color.white);
        gBuffer.fillRect(0, 0, largura- 2*xMargem, altura- 2*yMargem);
        gBufferExterno.drawImage(buffer, xMargem, yMargem, view);
    }

    public int getUnidadeLargura() {
        return unidadeLargura;
    }

    public int getUnidadeAltura() {
        return unidadeAltura;
    }

    public int getXMargem() {
        return xMargem;
    }

    public int getYMargem() {
        return yMargem;
    }
    
    public int getJpanelLargura() {
        return jPanelGrafico.getWidth();
    }
    
    public int getJpanelAltura() {
        return jPanelGrafico.getHeight();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelGrafico = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bufferExterno != null) {
                    g.drawImage(bufferExterno, 0, 0, this);
                }
            }
        };
        jLabelMensagem = new javax.swing.JLabel();
        jButtonNovoJogo = new javax.swing.JButton();
        jButtonFecharJogo = new javax.swing.JButton();
        jSliderDificuldade = new javax.swing.JSlider();
        jLabelDificuldade = new javax.swing.JLabel();
        jCheckBoxAtravessarBordas = new javax.swing.JCheckBox();
        jTextFieldNome = new javax.swing.JTextField();
        jButtonSalvarRecorde = new javax.swing.JButton();
        jButtonMostrarRecordes = new javax.swing.JButton();
        jLabelNome = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SNAKE GAME");
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                formMouseExited(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelGrafico.setBackground(new java.awt.Color(255, 255, 255));
        jPanelGrafico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelGrafico.setPreferredSize(new java.awt.Dimension(520, 280));
        jPanelGrafico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPanelGraficoMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout jPanelGraficoLayout = new javax.swing.GroupLayout(jPanelGrafico);
        jPanelGrafico.setLayout(jPanelGraficoLayout);
        jPanelGraficoLayout.setHorizontalGroup(
            jPanelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 518, Short.MAX_VALUE)
        );
        jPanelGraficoLayout.setVerticalGroup(
            jPanelGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        jLabelMensagem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButtonNovoJogo.setText("NOVO JOGO");
        jButtonNovoJogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovoJogoActionPerformed(evt);
            }
        });

        jButtonFecharJogo.setText("FECHAR JOGO");
        jButtonFecharJogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFecharJogoActionPerformed(evt);
            }
        });

        jSliderDificuldade.setMaximum(10);
        jSliderDificuldade.setMinimum(1);
        jSliderDificuldade.setMinorTickSpacing(1);
        jSliderDificuldade.setPaintTicks(true);
        jSliderDificuldade.setValue(5);
        jSliderDificuldade.setName(""); // NOI18N
        jSliderDificuldade.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderDificuldadeStateChanged(evt);
            }
        });

        jLabelDificuldade.setText("Dificuldade: 5");

        jCheckBoxAtravessarBordas.setText("Atravessar Bordas");

        jTextFieldNome.setText("Anonimo");

        jButtonSalvarRecorde.setText("Salvar Recorde");
        jButtonSalvarRecorde.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvarRecordeActionPerformed(evt);
            }
        });

        jButtonMostrarRecordes.setText("Mostrar Recordes");
        jButtonMostrarRecordes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMostrarRecordesActionPerformed(evt);
            }
        });

        jLabelNome.setText("Nome:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMensagem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(137, 137, 137)
                                .addComponent(jSliderDificuldade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDificuldade))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(184, 184, 184)
                                .addComponent(jButtonNovoJogo)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonFecharJogo))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addComponent(jCheckBoxAtravessarBordas))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jLabelNome, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonSalvarRecorde)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonMostrarRecordes)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovoJogo)
                    .addComponent(jButtonFecharJogo))
                .addGap(27, 27, 27)
                .addComponent(jCheckBoxAtravessarBordas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jSliderDificuldade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelDificuldade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSalvarRecorde)
                    .addComponent(jButtonMostrarRecordes)
                    .addComponent(jLabelNome, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(125, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNovoJogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovoJogoActionPerformed
        requestFocus();
        if(jogoFechado) {
            jogoFechado = false;
            jLabelMensagem.setText("");
            jButtonFecharJogo.setVisible(true);
            jButtonNovoJogo.setVisible(false);
            pontos = 0;
            jButtonSalvarRecorde.setVisible(false);
            
            gBuffer.setColor(Color.white);
            gBuffer.fillRect(0, 0, largura- 2*xMargem, altura- 2*yMargem);
            gBufferExterno.drawImage(buffer, xMargem, yMargem, this);
            
            snakeController.novoJogo(dificuldade, jCheckBoxAtravessarBordas.isSelected());
        }
    }//GEN-LAST:event_jButtonNovoJogoActionPerformed

    private void jButtonFecharJogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFecharJogoActionPerformed
        jogoFechado = true;
        snakeController.fecharJogo();
        jButtonFecharJogo.setVisible(false);
        jButtonNovoJogo.setVisible(true);
    }//GEN-LAST:event_jButtonFecharJogoActionPerformed

    private void jSliderDificuldadeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderDificuldadeStateChanged
        dificuldade = jSliderDificuldade.getValue();
        jLabelDificuldade.setText("Dificuldade: " + dificuldade);
    }//GEN-LAST:event_jSliderDificuldadeStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        snakeController.viewFechada();
    }//GEN-LAST:event_formWindowClosing

    private void jPanelGraficoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelGraficoMouseEntered
        requestFocus();
    }//GEN-LAST:event_jPanelGraficoMouseEntered

    private void formMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseExited
        requestFocus();
    }//GEN-LAST:event_formMouseExited

    private void jButtonSalvarRecordeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvarRecordeActionPerformed
        snakeController.inserirRecorde(jTextFieldNome.getText(), pontos);
        jButtonSalvarRecorde.setVisible(false);
        pontos = 0;
    }//GEN-LAST:event_jButtonSalvarRecordeActionPerformed

    private void jButtonMostrarRecordesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMostrarRecordesActionPerformed
        RecordesView recordesView = new RecordesView();
        recordesView.setVisible(true);
    }//GEN-LAST:event_jButtonMostrarRecordesActionPerformed
    
    public void notificaNovoRecorde(int pontos) {
        this.pontos = pontos;
        jLabelMensagem.setText("Novo recorde de " + pontos + " pontos!");
        jButtonSalvarRecorde.setVisible(true);
    }
    
    public void perdeuOJogo() {
        jogoFechado = true;
        snakeController.fecharJogo();
        jButtonFecharJogo.setVisible(false);
        jButtonNovoJogo.setVisible(true);
        jLabelMensagem.setText("Você perdeu! " + jLabelMensagem.getText());
    }
    
    public void venceuOJogo() {
        jLabelMensagem.setText("Você venceu!");
        snakeController.fecharJogo();
        jButtonFecharJogo.setVisible(false);
        jButtonNovoJogo.setVisible(true);
    }
    
    public void atualizarNumMaca(int comidas) {
        jLabelMensagem.setText("Maçãs comidas: " + comidas);
    }
    
    public synchronized void desenharJogo(Cobrinha cobrinhaObj, boolean crescendo, Point posMaca, boolean apagarMaca, boolean macaGrande) {   
        List<Point> cobrinha = cobrinhaObj.getCobrinha();
        Map<Point, Point> direcoesCobrinha = cobrinhaObj.getDirecoesCobrinha();
        
        Point cabeca = cobrinha.get(0);
        Point cauda = cobrinha.get(1);
        Point caudaPosterior = cobrinha.get(cobrinha.size() - 2);
        Point caudaPonta = cobrinha.get(cobrinha.size() - 1);
        
        if (!crescendo) {
            desenhaPonto(cobrinhaObj.getPontoRemovido());
        }

        if (apagarMaca) {
            desenhaPonto(cobrinhaObj.getPontoRemovido());
            desenhaPonto(posMaca);
            if(macaGrande) {
                desenhaPonto(new Point(posMaca.x + unidadeLargura, posMaca.y));
                desenhaPonto(new Point(posMaca.x, posMaca.y + unidadeAltura));
                desenhaPonto(new Point(posMaca.x + unidadeLargura, posMaca.y + unidadeAltura));
            }
        } else {
            desenhaPontoImagem(mapaImagens.get(new Point(0, 3 - (macaGrande ? 1 : 0))), posMaca);
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
        
        gBufferExterno.drawImage(buffer, xMargem, yMargem, this);
        gPanel.drawImage(bufferExterno, 0, 0, this);
    }
    
    private void desenhaPontoImagem(Image im, Point p) {
        gBuffer.drawImage(im, p.x, p.y, this);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonFecharJogo;
    private javax.swing.JButton jButtonMostrarRecordes;
    private javax.swing.JButton jButtonNovoJogo;
    private javax.swing.JButton jButtonSalvarRecorde;
    private javax.swing.JCheckBox jCheckBoxAtravessarBordas;
    private javax.swing.JLabel jLabelDificuldade;
    private javax.swing.JLabel jLabelMensagem;
    private javax.swing.JLabel jLabelNome;
    private javax.swing.JPanel jPanelGrafico;
    private javax.swing.JSlider jSliderDificuldade;
    private javax.swing.JTextField jTextFieldNome;
    // End of variables declaration//GEN-END:variables
}
