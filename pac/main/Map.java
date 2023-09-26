package pac.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Map extends JPanel {
    private static final Random RANDOM = new Random();
    private final int HUMAN_DOT = 1;
    private final int AI_DOT = 2;
    private final int EMPTY_DOT = 0;
    private final int DOT_PADDING = 5;
    private int fieldSizeY = 3;
    private int fieldSizeX = 3;
    private char[][] field;
    private int panelWidth;
    private int panelHeight;
    private int cellHeight;
    private int cellWidth;
    private int gameOverType;
    private boolean isGameOver;
    private boolean isInitialized;
    private static final int STATE_DRAW = 0;
    private static final int STATE_WIN_HUMAN = 1;
    private static final int STATE_WIN_AI = 2;

    private static final String MSG_WIN_HUMAN = "Победил игрок!";
    private static final String MSG_WIN_AI = "Победил копьютер!";
    private static final String MSG_DRAW = "Ничья!";



    Map(){
        addMouseListener((new MouseAdapter() { // слушатель на мышку
            @Override
            public void mouseReleased(MouseEvent e) { // важно когда пользователь отпустит мышку
                update(e);
            }
        }));
        isInitialized = false;
    }

    private void update(MouseEvent e) {
        if (isGameOver || !isInitialized) return;
        int cellX = e.getX()/cellWidth;
        int cellY = e.getY()/cellHeight;
        if (!isValidCell(cellX, cellY) || !isEmptyCell(cellX, cellY)) return;
        field[cellX][cellY] = HUMAN_DOT;
        System.out.printf("x = %d, y = %d\n", cellX, cellY);
        repaint(); // клик отрисовка
        if (checkEndGame(HUMAN_DOT, STATE_WIN_HUMAN)) return;
        aiTurn();
        repaint();
        if (checkEndGame(AI_DOT, STATE_WIN_AI)) return;
    }

    private boolean checkEndGame(int dot, int gameOverType) {
        if (checkWin(dot)) {
            this.gameOverType = gameOverType;
            isGameOver = true;
            repaint();
            return true;
        }
        if (isMapFull()) {
            this.gameOverType = STATE_DRAW;
            isGameOver = true;
            repaint();
            return true;
        }
        return false;

    }

    void startNewGame(int mode, int fSzX, int fSzY, int wLen) {
        System.out.printf("Mode: %d;\nSize: x=%d, y=%d;\nWin Length: %d", mode, fSzX, fSzY, wLen);
        initMap();
        isGameOver = false;
        isInitialized = true;
        repaint(); // просим панель перерисоваться, если вдруг прописовка неполная после инциализации
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
        }

    private void render(Graphics g) {
        if (!isInitialized) return;
        this.panelWidth = getWidth();
        this.panelHeight = getHeight();
        cellHeight = panelHeight / 3;
        cellWidth = panelHeight / 3;

        g.setColor(Color.BLACK);
        for (int h = 0; h < 3; h++) {
            int y = h * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }
        for (int w = 0; w < 3; w++) {
            int x = w * cellHeight;
            g.drawLine(x, 0 , x, panelHeight);
        }

        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == EMPTY_DOT) continue;
                if (field[i][j] == HUMAN_DOT) {
                    g.setColor(Color.BLUE);
                    g.fillOval(i * cellHeight + DOT_PADDING,
                            j * cellHeight + DOT_PADDING,
                            cellWidth - DOT_PADDING * 2,
                            cellHeight - DOT_PADDING * 2);
                } else if (field[i][j] == AI_DOT) {
                    g.setColor(new Color(0xff0000));
                    g.fillOval(i * cellHeight + DOT_PADDING,
                            j * cellHeight + DOT_PADDING,
                            cellWidth - DOT_PADDING * 2,
                            cellHeight - DOT_PADDING * 2);
                } else {
                    throw new RuntimeException("Unexpected value " + field[i][j] + " int cell: x=" + i + " y=" + j);
                }
            }
        }
        if (isGameOver) {
            showMessageGameOver(g);
        }
    }

    private void showMessageGameOver(Graphics g){
        g.setColor((Color.DARK_GRAY));
        g.fillRect(0,200, getWidth(),70);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Times new roman", Font.BOLD, 48));
        switch (gameOverType) {
            case 0:
                g.drawString(MSG_DRAW, 180, getHeight() / 2); break;
            case 2:
                g.drawString(MSG_WIN_AI, 20, getHeight() / 2); break;
            case 1:
                g.drawString(MSG_WIN_HUMAN, 70, getHeight() / 2); break;
            default:
                throw new RuntimeException("Unexpected gameOver state: " + gameOverType);
        }
    }


    private void initMap(){
        fieldSizeY = 3;
        fieldSizeX = 3;
        field = new char[fieldSizeX][fieldSizeY];
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                field[i][j] = EMPTY_DOT;
            }
        }
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    private boolean isEmptyCell (int x, int y) {
        return field[x][y] == EMPTY_DOT;
    }

    private void aiTurn (){
        int x, y;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isEmptyCell(x,y));
        field[x][y] = AI_DOT;
        repaint();
    }

    private boolean checkWin(int c) {
        if (field[0][0] == c && field[0][1] == c && field[0][2] == c) return true;
        if (field[1][0] == c && field[1][1] == c && field[1][2] == c) return true;
        if (field[2][0] == c && field[2][1] == c && field[2][2] == c) return true;

        if (field[0][0] == c && field[1][0] == c && field[2][0] == c) return true;
        if (field[0][1] == c && field[1][1] == c && field[2][1] == c) return true;
        if (field[0][0] == c && field[1][2] == c && field[2][2] == c) return true;

        if (field[0][0] == c && field[1][1] == c && field[2][2] == c) return true;
        if (field[0][2] == c && field[1][1] == c && field[2][0] == c) return true;
        return false;
    }

    private boolean isMapFull (){
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                if (field[i][j] == EMPTY_DOT) return false;
            }
        }
        return true;
    }
}
