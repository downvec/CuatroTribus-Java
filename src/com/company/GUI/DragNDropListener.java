package com.company.GUI;

import com.company.Main;
import com.company.Pieces.GuiPiece;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class DragNDropListener implements MouseMotionListener, MouseListener{
    private static BoardSquare[][] boardMatrix;
    private BoardSquare clickedSquare;
    private Board gameBoard;
    private List<GuiPiece> piecesOnSquare;
    private List<GuiPiece> piecesOnTheMove;

    private Boolean troopsAreBeingMobilized;
    private Boolean deployingTroops;

    private GuiPiece pieceOnDeployment;
    private static ReservesSquare currentPlayerReserves;

    /**
     * Public constructor to set the game pieces & board accordingly
     * @param board: Main board where game is happening
     */
    public DragNDropListener( Board board){
        this.gameBoard = board;
        boardMatrix = Board.boardMatrix;
        this.troopsAreBeingMobilized = false;
        this.deployingTroops = false;

        piecesOnSquare = new ArrayList<>();
        piecesOnTheMove = new ArrayList<>();
    }

    /**
     * Called when mouse clicked. Checks if click coordinates correspond to a square, and shows dialogue to mobilize if so
     * @param e: Mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();


        if (reservesClicked(clickX)){

            currentPlayerReserves = Main.currentPlayer.playerTribe.getReservesSquare();

            pieceOnDeployment = getClickedPiece(clickX,clickY);
            deployingTroops = true;


        } else { //Reserves were not clicked.
            this.clickedSquare = getClickedSquare(clickX,clickY);

            if (!troopsAreBeingMobilized) { // i.e this is the user trying to select pieces to move

                //Check if the user has power over the clicked square
                if (clickedSquare.dominantColor == Main.currentPlayer.playerTribe.getColor() && !clickedSquare.isEmpty()) {
                    this.piecesOnSquare = clickedSquare.getPieces();

                    //Creating panel to display options, and new list to contain radio buttons
                    final JPanel panel = new JPanel();
                    int listSize = this.piecesOnSquare.size();
                    JRadioButton[] pieceOptions = new JRadioButton[listSize];
                    int i = 0;

                    //Create new list of radio buttons representing available pieces and adding them to the panel
                    for (GuiPiece piece : piecesOnSquare) {
                        pieceOptions[i] = new JRadioButton(piece.toString());

                        //Adding the radiobutton to the panel
                        panel.add(pieceOptions[i]);
                        i++;
                    }

                    //Show the option pane
                    JOptionPane.showMessageDialog(this.gameBoard, panel, "Select pieces to mobilize", JOptionPane.PLAIN_MESSAGE);

                    //Getting the boolean values of the radiobuttons, and adding corresponding pieces to list of pieces being dragged
                    for (int j = 0; j < listSize; j++) {
                        if (pieceOptions[j].isSelected()) {
                            piecesOnTheMove.add(this.piecesOnSquare.get(j));
                        }
                    }

                    if (!piecesOnTheMove.isEmpty())
                        troopsAreBeingMobilized = true;

                } else { // Square clicked by user is outside of his control
                    System.out.println("You have no power over this city");
                    return;
                }
            } else { //Troops are not being mobilized
                this.clickedSquare.addPiecesToSquare(piecesOnTheMove, Main.currentPlayer.playerTribe.getColor());
                this.troopsAreBeingMobilized = false;
                piecesOnTheMove = new ArrayList<>();
                gameBoard.repaint();
            }

        }
    }

    /**
     * Called when mouse is being moved. Only acts if troopsAreBeingMobilized
     * @param e: MouseEvent
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (troopsAreBeingMobilized) {

            int x = e.getX();
            int y = e.getY();

            for (GuiPiece piece : piecesOnTheMove) {
                piece.setxPos(x);
                piece.setyPos(y);
                this.gameBoard.repaint();
            }
        }
    }

    /**
     * Called when the user is dragging pieces from staging areea into board
     * @param e: mouse event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (deployingTroops){
            pieceOnDeployment.xPos = e.getX();
            pieceOnDeployment.yPos = e.getY();
            this.gameBoard.repaint();
        }
    }

    //TODO: Fix mouseReleased to allow for free piece moving iff user is setting up
    /**
     * Method called when the mouse is released after being dragged
     * In charge of checking which board square was clicked,
     * and calling the addPieces method equipped to handle collisions
     * @param e: mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //Query squares to find which was clicked
        BoardSquare releaseSqr = this.getClickedSquare(e.getX(), e.getY());

        if (deployingTroops && !Main.currentPlayer.playerTribe.isSettingUp) {
            this.clickedSquare = getClickedSquare(e.getX(),e.getY());


            //Check if the user can buy the piece. If so, the purchase is carried out by Tribe class
            if (Main.currentPlayer.playerTribe.canBuyThisPiece(pieceOnDeployment)) {

                //Call the method for buying pieces. Changes deployment status and charges user etc...
                Main.currentPlayer.playerTribe.buyThisPiece(pieceOnDeployment);

                //TODO: If releaseSqr null, check if dropped back to reserves (not possible under rules)

                //Prompt square to accept piece
                releaseSqr.deployPiece(pieceOnDeployment, Main.currentPlayer.playerTribe.getColor());

                this.deployingTroops = false;
                pieceOnDeployment = null;

            }else { //Player cannot afford this piece
                System.out.println("You can´t afford this piece");
                Main.currentPlayer.playerTribe.getReservesSquare().returnPieceToReserves(pieceOnDeployment);
            }
            gameBoard.repaint();
        } else  if (deployingTroops && Main.currentPlayer.playerTribe.isSettingUp){ // User is setting up. Dont charge for pieces

            //Take the piece out of reserves
            Main.currentPlayer.playerTribe.setUpThisPiece(pieceOnDeployment);

            //Prompt square to accept piece
            releaseSqr.deployPiece(pieceOnDeployment, Main.currentPlayer.playerTribe.getColor());

            //Adjust game dragging logic
            this.deployingTroops = false;
            pieceOnDeployment = null;
        }
    }

    /**
     * Function to check what square was pressed by checking through matrix
     * @param clickX: x coord of the click
     * @param clickY: y coord of the click
     */
    public BoardSquare getClickedSquare(int clickX, int clickY){
        BoardSquare clickedSqr = null;

        for(int row=0; row<6; row++){
            for(int col=0; col<6; col++){
                if (boardMatrix[row][col].isClicked(clickX,clickY)){
                    clickedSqr = boardMatrix[row][col];
                    System.out.println("Square on " + clickedSqr.row + "," + clickedSqr.col + " was clicked");
                }
            }
        }
        return clickedSqr;
    }

    /**
     * Method to facilitate checking which specific piece was clicked within staging area
     * @param clickX: X coordinate click
     * @param clickY: Y coordinate click
     * @return GuiPiece that was clicked. Designed this way so null can be detected.
     */
    public GuiPiece getClickedPiece(int clickX, int clickY){
        GuiPiece selectedPiece = null ;
        for (GuiPiece piece : currentPlayerReserves.pieces){
            if (piece.isClicked(clickX,clickY)){
                selectedPiece = piece;
            }
        }
        return selectedPiece;
    }


    private boolean reservesClicked(int xCoord){
        return xCoord > 605;
    }


    /*
    Unused functions. Implemented to fulfill interface.
    */
    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }

}
