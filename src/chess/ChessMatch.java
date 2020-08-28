package chess;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.King;
import chess.pieces.Rook;


public class ChessMatch 
{
	private Board board;
	
	public ChessMatch()
	{
		board=new Board(8, 8);
		
		initialSetup();
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
		return (ChessPiece)capturePiece;
	}
	
	private Piece makeMove(Position sourse,Position target)
	{
		Piece p=board.removePiece(sourse);
		Piece capturePiece=board.removePiece(target);
		board.placePiece(p, target);
		return capturePiece;
	}
	
	private void validateSoursePosition(Position position)
	{
		if(!board.thereIsAPiece(position))
		{
			throw new ChessException("There is no piece on sourse position !");
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
	
	private void placeNewPiece(char column,int row,ChessPiece piece)
	{
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
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
