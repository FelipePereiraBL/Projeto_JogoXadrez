package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;


public class ChessMatch 

{
	public int turn;
	public Color currentPlayer;	
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantValnerable;
	private ChessPiece promoted;
	
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
	
	public boolean getCheckMate()
	{
		return checkMate;
	}

	public ChessPiece getEnpassantValnerable()
	{
		return enPassantValnerable;
	}
	
	public ChessPiece getPromoted()
	{
		return promoted;
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
			throw new ChessException("You cant´t put yourself in check !");
		}
		
		ChessPiece movedPiece=(ChessPiece)board.piece(target);
		
		//SpecialMove promoted
		promoted=null;
		if(movedPiece instanceof Pawn)
		{
		   if(movedPiece.getColor()==Color.WHITE && target.getRow()==0 ||movedPiece.getColor()==Color.BLACK && target.getRow()==7)
		   {
			promoted=(ChessPiece)board.piece(target);
			promoted=replacePromotedPiece("Q");
		   }
		}
		
		check= (testCheck(opponent(currentPlayer)))? true:false;
		
		if(testCheckMate(opponent(currentPlayer)))
		{
			checkMate=true;
		}
		else
		{
			nextTurn();
		}
		
		//SpecialMove en passant
		if(movedPiece instanceof Pawn && target.getRow()==sourse.getRow()-2 ||target.getRow()==sourse.getRow()+2)
		{
			enPassantValnerable=movedPiece;
		}
		else
		{
			enPassantValnerable=null;
		}
		
		return (ChessPiece)capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type)
	{
		if(promoted==null)
		{
			throw new IllegalStateException("There is no piece to be promotede !");
		}
		if(!type.equals("B")&&!type.equals("N")&&!type.equals("R")&&!type.equals("Q"))
		{
			return promoted;
		}
		
		Position pos=promoted.getChessPosition().toPosition();
		Piece p=board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece=newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type,Color color)
	{
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("Q")) return new Queen(board, color);
	    return new Rook(board, color);
	}
	
	private Piece makeMove(Position sourse,Position target)
	{
		ChessPiece p=(ChessPiece)board.removePiece(sourse);
		p.increaseMoveCount();
		Piece capturedPiece=board.removePiece(target);
		board.placePiece(p, target);
		
		if(capturedPiece!=null)
		{
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		//Specil move castling Kingside rook
		if(p instanceof King && target.getColumn()==sourse.getColumn()+2)
		{
			Position sourceT=new Position(sourse.getRow(),sourse.getColumn()+3);
			Position targetT=new Position(sourse.getRow(),sourse.getColumn()+1);
			ChessPiece rook=(ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//Specil move castling Quennside rook
		if(p instanceof King && target.getColumn()==sourse.getColumn()-2)
		{
			Position sourceT=new Position(sourse.getRow(),sourse.getColumn()-4);
			Position targetT=new Position(sourse.getRow(),sourse.getColumn()-1);
			ChessPiece rook=(ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//SpecialMove en passant
		if(p instanceof Pawn)
		{
			if(sourse.getColumn()!=target.getColumn() && capturedPiece==null)
			{
				Position pawnPoition;
				if(p.getColor()==Color.WHITE)
				{
					pawnPoition=new Position(target.getRow()+1, target.getColumn());
				}
				else
				{
					pawnPoition=new Position(target.getRow()-1, target.getColumn());
				}
				
				capturedPiece=board.removePiece(pawnPoition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	private void undoMove(Position sourse,Position target, Piece capturedPiece)
	{
		ChessPiece p=(ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, sourse);
		if(capturedPiece!=null)
		{
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		// #specialmove castling kingside rook
				if (p instanceof King && target.getColumn() == sourse.getColumn() + 2) {
					Position sourceT = new Position(sourse.getRow(), sourse.getColumn() + 3);
					Position targetT = new Position(sourse.getRow(), sourse.getColumn() + 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, sourceT);
					rook.decreaseMoveCount();
				}

				// #specialmove castling queenside rook
				if (p instanceof King && target.getColumn() == sourse.getColumn() - 2) {
					Position sourceT = new Position(sourse.getRow(), sourse.getColumn() - 4);
					Position targetT = new Position(sourse.getRow(), sourse.getColumn() - 1);
					ChessPiece rook = (ChessPiece)board.removePiece(targetT);
					board.placePiece(rook, sourceT);
					rook.decreaseMoveCount();
				}
				
				//SpecialMove en passant
				if(p instanceof Pawn)
				{
					if(sourse.getColumn()!=target.getColumn() && capturedPiece==enPassantValnerable)
					{
						ChessPiece pawn=(ChessPiece)board.removePiece(target);
						Position pawnPoition;
						if(p.getColor()==Color.WHITE)
						{
							pawnPoition=new Position(3, target.getColumn());
						}
						else
						{
							pawnPoition=new Position(4, target.getColumn());
						}
						board.placePiece(pawn, pawnPoition);
						
					}
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
			throw new ChessException("The chosen piece can´t move to target position !");
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
	
	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
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
	private boolean testCheckMate(Color color)
	{
		if(!testCheck(color))
		{
			return false;
		}
		
		List<Piece> list= piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		
		for(Piece p:list)
		{
			boolean[][] mat=p.possibleMoves();
			
			for (int i = 0; i < board.getRows(); i++) 
			{
				for (int j = 0; j < board.getColumns(); j++)
				{
					if(mat[i][j])
					{
						Position sourse=((ChessPiece)p).getChessPosition().toPosition();
						Position target=new Position(i, j);
						Piece capturedPiece=makeMove(sourse, target);
						boolean testCheck=testCheck(color);
						undoMove(sourse, target, capturedPiece);
						if(!testCheck)
						{
							return false;
						}
					}
				}				
			}
		}
		
		return true;
	}
	
	private void placeNewPiece(char column,int row,ChessPiece piece)
	{
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup()
	{
	      placeNewPiece('a', 1, new Rook(board, Color.WHITE));
	      placeNewPiece('b', 1, new Knight(board, Color.WHITE));
	      placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
	      placeNewPiece('d', 1, new Queen(board, Color.WHITE));
          placeNewPiece('e', 1, new King(board, Color.WHITE,this));
          placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
          placeNewPiece('g', 1, new Knight(board, Color.WHITE));
          placeNewPiece('h', 1, new Rook(board, Color.WHITE));
          placeNewPiece('a', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('b', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('c', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('d', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('e', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('f', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('g', 2, new Pawn(board, Color.WHITE,this));
          placeNewPiece('h', 2, new Pawn(board, Color.WHITE,this));
          
          placeNewPiece('a', 8, new Rook(board, Color.BLACK));
          placeNewPiece('b', 8, new Knight(board, Color.BLACK));
          placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
          placeNewPiece('d', 8, new Queen(board, Color.BLACK));
          placeNewPiece('e', 8, new King(board, Color.BLACK,this));
          placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
          placeNewPiece('g', 8, new Knight(board, Color.BLACK));
          placeNewPiece('h', 8, new Rook(board, Color.BLACK));
          placeNewPiece('a', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('b', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('c', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('d', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('e', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('f', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('g', 7, new Pawn(board, Color.BLACK,this));
          placeNewPiece('h', 7, new Pawn(board, Color.BLACK,this));
	}
}
