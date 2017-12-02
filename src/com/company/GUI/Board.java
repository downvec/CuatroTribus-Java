package com.company.GUI;

import com.company.Pieces.GuiPiece;
import com.company.Pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel{
    private JFrame fram;

    private Image imgBackground;
    private List<GuiPiece> visiblePieces = new ArrayList<>();
    private BoardSquare[][] boardMatrix = new BoardSquare[5][5];

    /*
    Board main constructor. Called to create main game board, and to set up the initial game positions
     */
    public Board(){
        //Create new Drag and Drop listener
        DragNDropListener mouseListener = new DragNDropListener(visiblePieces,this);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);

        initGraphics();
        setupNewGame();
    }

    /*
    Method inherited from JFrame. Called when drawing new images or refreshing screen
     */
    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(this.imgBackground,0,0,null);

        for (GuiPiece curPiece: visiblePieces){
            g.drawImage(curPiece.getIcon(),curPiece.getxPos(),curPiece.getyPos(),null);
        }
    }

    /*
    Method to initialize graphic components i.e background image
     */
    private void initGraphics(){
        this.setLayout(null);

        //Drawing background
        this.imgBackground = new ImageIcon(getClass().getClassLoader().getResource("com/company/images/Board.png")).getImage();


        //Create application frame
        //FIXME: Window is drawn slightly smaller than the actual size of background image.
        fram = new JFrame();
        fram.setVisible(true);
        fram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fram.add(this);
        fram.setSize(imgBackground.getWidth(null),imgBackground.getHeight(null));

        //Creating a white horse
        //FIXME: This is just for testing purposes.
        createAndAddPiece(Piece.colors.WHITE, Piece.types.HORSE, 2, true,1,1);

    }

    /*
    Creates 36 new BoardSquare objects and places them on the board matrix.
     */
    private void setupNewGame(){
        //Create 36 new boardsquares, and place them in the board matrix
        for( int row=0; row<6 ;row++){
            for (int col=0; col<6; col++){
                boardMatrix[row][col] = new BoardSquare(row,col);
            }
        }
    }

    /*
    Creates and paints a new piece based on parameters.
     */
    private void createAndAddPiece(Piece.colors color, Piece.types type, int power, boolean isDeployed, int x, int y) {
        Image pieceIcon = getIconForPiece(color,type);

        GuiPiece newPiece = new GuiPiece(color,type,power,isDeployed,x,y,pieceIcon);
        this.visiblePieces.add(newPiece);

        //Calling repaint to update screen
        fram.repaint();
    }

    /*
    Gets the icon for a specific piece using color and type, making use of naming conventions for icons
     */
    private Image getIconForPiece(Piece.colors pieceColor, Piece.types pieceType){
        String pathName = "";

        switch (pieceColor) {
            case RED:
                pathName += "r_";
                break;
            case BLUE:
                pathName += "b_";
                break;
            case GREEN:
                pathName += "g_";
                break;
            case WHITE:
                pathName += "w_";
                break;
        }

        switch (pieceType) {
            case HORSE:
                pathName += "Horse.png";
                break;
            case INFANTRY:
                pathName += "Infant.png";
                break;
            case ARTILLERY:
                pathName += "Artillery.png";
                break;
        }

        URL iconUrl = getClass().getClassLoader().getResource("com/company/images/" + pathName);
        return new ImageIcon(iconUrl).getImage();

        }

    /*
    Recieves a piece, and determines in which row / col it is, and changes its parameters to match
    & Adds the piece to the corresponding square´s list of pieces.
     */
    public void parseXYCoords(GuiPiece piece){
        int x = piece.getxPos();
        int y = piece.getyPos();

        if (x > 0 && x < 100){
            piece.setColumn(0);
        } else if (x < 200){
            piece.setColumn(1);
        }else if (x < 300){
            piece.setColumn(2);
        }else if (x < 400){
            piece.setColumn(3);
        }else if (x < 500){
            piece.setColumn(4);
        }else if (x < 600){
            piece.setColumn(5);
        }

        if (y > 0 && y < 100){
            piece.setRow(0);
        } else if (y < 200){
            piece.setRow(1);
        }else if (y < 300){
            piece.setRow(2);
        }else if (y < 400){
            piece.setRow(3);
        }else if (y < 500){
            piece.setRow(4);
        }else if (y < 600){
            piece.setRow(5);
        }

        //Adds the piece to BoardSquare´s piece list.
        boardMatrix[piece.getRow()][piece.getColumn()].addPieceToList(piece);
    }

    /*
    Checks row / col parameters of the piece, and centers it into the square
    FIXME: Needs tweaking to center pieces correctly
     */
    public void centerPieceToSquare(GuiPiece piece){
        int pieceCol = piece.getColumn();
        int pieceRow = piece.getRow();

        switch (pieceCol){
            case 0:
                piece.setxPos(10);
                break;
            case 1:
                piece.setxPos(110);
                break;
            case 2:
                piece.setxPos(210);
                break;
            case 3:
                piece.setxPos(310);
                break;
            case 4:
                piece.setxPos(410);
                break;
            case 5:
                piece.setxPos(510);
                break;
        }

        switch (pieceRow){
            case 0:
                piece.setyPos(10);
                break;
            case 1:
                piece.setyPos(110);
                break;
            case 2:
                piece.setyPos(210);
                break;
            case 3:
                piece.setyPos(310);
                break;
            case 4:
                piece.setyPos(410);
                break;
            case 5:
                piece.setyPos(510);
                break;
        }

        System.out.println("I am in column " + pieceCol + " and row " + pieceRow);
    }


    }
