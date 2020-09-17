package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.King;
import chess.pieces.Rook;


public class ChessMatch 

{
	public int turn;
	public Color currentPlayer;	
	private Board board;
	private boolean check;
	
	private List<Piece> piecesOnTheBoard=new ArrayList<>();
	private List<Piece> capturedPieces=new ArrayList<>();
	
	public ChessMatch()
	{
		board=new Board(8, 8);
		
		turn=1;
		
		currentPlayer=Color.WHITE;
		initialSetup();
	}
	
	
	public int getTurn()
	{
		return turn;
	}
	
	public Color getCurrentPlayer()
	{
		return currentPlayer;
	}
	
	public boolean getCheck()
	{
		return check;
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
		Piece capturedPiece=makeMove(sourse,target);
		
		if(testCheck(currentPlayer))
		{
			undoMove(sourse, target, capturedPiece);
			throw new ChessException("You cant�t put yourself in check !");
		}
		
		check= (testCheck(opponent(currentPlayer)))? true:false;
		
		nextTurn();
		return (ChessPiece)capturedPiece;
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
	
	private void undoMove(Position sourse,Position target, Piece capturedPiece)
	{
		Piece p=board.removePiece(target);
		board.placePiece(p, sourse);
		if(capturedPiece!=null)
		{
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSoursePosition(Position position)
	{
		if(!board.thereIsAPiece(position))
		{
			throw new ChessException("There is no piece on sourse position !");
		}
		
		if(currentPlayer!=((ChessPiece)board.piece(position)).getColor())
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
			throw new ChessException("The chosen piece can�t move to target position !");
		}
	}
	
	private void nextTurn()
	{
		turn++;
		currentPlayer=(currentPlayer==Color.WHITE)?Color.BLACK:Color.WHITE;
	}
	
	private Color opponent(Color color)
	{
		return (color==color.WHITE)?color.BLACK:color.WHITE;
	}
	
	private ChessPiece king(Color color)
	{
		List<Piece>list=piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor()==color).collect(Collectors.toList());
		
		for(Piece p:list)
		{
			if(p instanceof King)
			{
				return (ChessPiece)p;
			}
		}
		
		throw new IllegalStateException("There is no "+color+" King on the board !");
	}
	
	private boolean testCheck(Color color)
	{
		Position kingPosition=king(color).getChessPosition().toPosition();
		
		List<Piece> opponentPiece=piecesOnTheBoard.stream().filter(x->((ChessPiece)x).getColor()==opponent(color)).collect(Collectors.toList());
		
		for(Piece p:opponentPiece)
		{
			boolean[][] mat=p.possibleMoves();
			
			if(mat[kingPosition.getRow()][kingPosition.getColumn()])
			{
				return true;
			}
		}
		
		return false;
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
