package chess;

import java.util.ArrayList;
import java.util.List;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.King;
import chess.pieces.Rook;


public class ChessMatch 

{
	public int turn;
	public Color currentlayer;	
	private Board board;
	
	private List<Piece> piecesOnTheBoard=new ArrayList<>();
	private List<Piece> capturedPieces=new ArrayList<>();
	
	public ChessMatch()
	{
		board=new Board(8, 8);
		
		turn=1;
		
		currentlayer=Color.WHITE;
		initialSetup();
	}
	
	
	public int getTurn()
	{
		return turn;
	}
	
	public Color getCurrentPlayer()
	{
		return currentlayer;
	}

	public ChessPiece[][] getPieces()
	{
		ChessPiece[][]mat=new ChessPiece[board.getRows()] [board.getColumns()];
		
		for (int i = 0; i < board.getRows(); i++)
		{
			for (int j = 0; j <board.getColumns(); j++) 
			{
				mat[i][j]=(ChessPiece)board.piece(i,j);
			}		
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition soursePosition)
	{
		Position position=soursePosition.toPosition();
		
		validateSoursePosition(position);
		
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition soursePosition,ChessPosition targetPosition)
	{
		Position sourse=soursePosition.toPosition();
		Position target=targetPosition.toPosition();
		validateSoursePosition(sourse);
		validateTargetPosition(sourse,target);
		Piece capturePiece=makeMove(sourse,target);
		nextTurn();
		return (ChessPiece)capturePiece;
	}
	
	private Piece makeMove(Position sourse,Position target)
	{
		Piece p=board.removePiece(sourse);
		Piece capturePiece=board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturePiece!=null)
		{
			piecesOnTheBoard.remove(capturePiece);
			capturedPieces.add(capturePiece);
		}
		return capturePiece;
	}
	
	private void validateSoursePosition(Position position)
	{
		if(!board.thereIsAPiece(position))
		{
			throw new ChessException("There is no piece on sourse position !");
		}
		
		if(currentlayer!=((ChessPiece)board.piece(position)).getColor())
		{
			throw new ChessException("The chosen piece is not yours !");
		}
		
		if(!board.piece(position).isThereAnyPossibleMove())
		{
			throw new ChessException("There is no possible moves for the chose piece !");
		}
	}
	
	private void validateTargetPosition(Position sourse,Position target)
	{
		if(!board.piece(sourse).possibleMove(target))
		{
			throw new ChessException("The chosen piece can´t move to target position !");
		}
	}
	
	private void nextTurn()
	{
		turn++;
		currentlayer=(currentlayer==Color.WHITE)?Color.BLACK:Color.WHITE;
	}
	private void placeNewPiece(char column,int row,ChessPiece piece)
	{
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup()
	{
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
