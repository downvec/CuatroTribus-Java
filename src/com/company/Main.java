package com.company;

import com.company.GUI.Board;
import com.company.Logic.LogicEngine;
import com.company.Pieces.Piece;
import com.company.Tribes.Tribe;

public class Main {
    static Board mainBoard;
    public static Player currentPlayer;
    public static LogicEngine gameLogicEngine;


    /*
    TODO: Show brand banner on splashscreen
    TODO: Complete class for players, and find a way to wrap gameplay around that.
    TODO: Implement game turns logic
    TODO: Call recalculate reserves every time the turn ends.
    TODO: Implement PAC recollection along with turn handling.

    //All piece objects are created when tribal army is created on Tribe constructor
    //Board matrix initialized when Board is created.
     */

    public static void main(String[] args) throws Exception {

        //Creating a single static LogicEngine object, and passing the same reference to all objects that require it
        gameLogicEngine = new LogicEngine();

        //Creating tribes
        //TODO: This is temporal. Tribes should have indiidual classes, cause different abilities.
        Tribe tribuBlanca = new Tribe(colors.GREEN,gameLogicEngine);

        //Creating a user Player
        Player jugador1 = new Player(tribuBlanca,"Alonso");

        currentPlayer = jugador1;

        mainBoard = new Board();


    }
}
