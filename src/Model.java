
public class Model {
	private boolean whiteTurn = true;
	private boolean pieceSelected = false;
	private boolean isOver = false;
	private boolean whiteKingMoved = false;
	private boolean blackKingMoved = false;
	private boolean blackRookAMoved = false;
	private boolean blackRookHMoved = false;
	private boolean whiteRookAMoved = false;
	private boolean whiteRookHMoved = false;
	private boolean enPassantAvailable = false;
	private int enPassantColumn;
	
	
	public int getEnPassantColumn() {
		return enPassantColumn;
	}

	public void setEnPassantColumn(int enPassantColumn) {
		this.enPassantColumn = enPassantColumn;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public boolean getEnPassantAvailable() {
		return enPassantAvailable;
	}

	public void setEnPassantAvailable(boolean enPassantAvailable) {
		this.enPassantAvailable = enPassantAvailable;
	}
	
	public boolean getBlackRookAMoved() {
		return blackRookAMoved;
	}

	public void setBlackRookAMoved(boolean blackRookAMoved) {
		this.blackRookAMoved = blackRookAMoved;
	}

	public boolean getBlackRookHMoved() {
		return blackRookHMoved;
	}

	public void setBlackRookHMoved(boolean blackRookHMoved) {
		this.blackRookHMoved = blackRookHMoved;
	}

	public boolean getWhiteRookAMoved() {
		return whiteRookAMoved;
	}

	public void setWhiteRookAMoved(boolean whiteRookAMoved) {
		this.whiteRookAMoved = whiteRookAMoved;
	}

	public boolean getWhiteRookHMoved() {
		return whiteRookHMoved;
	}

	public void setWhiteRookHMoved(boolean whiteRookHMoved) {
		this.whiteRookHMoved = whiteRookHMoved;
	}

	public boolean getWhiteTurn() {
		return whiteTurn;
	}

	public void setWhiteTurn(boolean whiteTurn) {
		this.whiteTurn = whiteTurn;
	}

	public boolean getPieceSelected() {
		return pieceSelected;
	}

	public void setPieceSelected(boolean pieceSelected) {
		this.pieceSelected = pieceSelected;
	}

	public boolean getIsOver() {
		return isOver;
	}

	public void setIsOver(boolean isOver) {
		this.isOver = isOver;
	}
	
	public boolean getWhiteKingMoved() {
		return whiteKingMoved;
	}

	public void setWhiteKingMoved(boolean moved) {
		this.whiteKingMoved = moved;
	}
	
	public boolean getBlackKingMoved() {
		return blackKingMoved;
	}

	public void setBlackKingMoved(boolean moved) {
		this.blackKingMoved = moved;
	}
}
