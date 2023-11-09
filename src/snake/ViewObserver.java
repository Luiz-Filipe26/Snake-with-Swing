
package snake;

/**
 *
 * @author Luiz
 */
public interface ViewObserver {
    void keyPressed(int keyCode);
    void novoJogo(float dificuldade, boolean atrevessarBordas);
    void fecharJogo();
    void viewFechada();
}
