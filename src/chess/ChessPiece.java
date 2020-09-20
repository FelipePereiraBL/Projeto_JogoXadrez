package chess;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;

public abstract class ChessPiece extends Piece
{
	private Color color;
	private int moveCont;

	public ChessPiece(Board board, Color color)
	{
		super(board);
		this.color = color;
	}

	public ChessPosition getChessPosition()
	{
		return ChessPosition.fromPosition(position);
	}
	
	public Color getColor() 
	{
		return color;
	}
	
	public int getMoveCount()
	{
		return moveCont;
	}
	
	public void increaseMoveCount()
	{
		moveCont++;
	}
	public void decreaseMoveCount()
	{
		moveCont--;
	}
	
	protected boolean isThereOpponentPiece(Position position)
	{
		ChessPiece p=(ChessPiece)getBoard().piece(position);
		return p!=null && p.getColor()!=color;
	}
}
