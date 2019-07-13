import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Controller {
	private View view;
	private Model model;
	private JButton oldSpot;

	public Controller(View v, Model m) {
		view = v;
		model = m;
		view.setListeners(new ButtonListener());
	}

	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isWhitePiece = view.isWhitePiece((JButton)e.getSource());

			if(model.getIsOver()) {
				//JOptionPane.showMessageDialog(null, "The game is over!", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(!model.getPieceSelected()) {
				oldSpot = (JButton) e.getSource();
				if(((JButton)e.getSource()).getIcon()!= null) {
					if(model.getWhiteTurn() && !isWhitePiece) {
						JOptionPane.showMessageDialog(null, "This is not your piece!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else if(!model.getWhiteTurn() && isWhitePiece) {
						JOptionPane.showMessageDialog(null, "This is not your piece!", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						view.setSelectedButton((JButton)e.getSource());
						view.setSelectedIcon(((JButton)e.getSource()).getIcon());
						model.setPieceSelected(true);
					}
				}
			}
			else { // if a piece IS selected
				makeMove((JButton)e.getSource());
			}
		}
	}

	public void makeMove(JButton newSpot) {
		Icon newSpotIcon = newSpot.getIcon();

		// can't move on top of own piece
		if(model.getWhiteTurn()) {
			if(view.isWhitePiece(newSpot)) {
				JOptionPane.showMessageDialog(null, "Illegal move!", "Error", JOptionPane.ERROR_MESSAGE);
				model.setPieceSelected(false);
				return;
			}
		}
		else {
			if(view.isBlackPiece(newSpot)) {
				JOptionPane.showMessageDialog(null, "Illegal move!", "Error", JOptionPane.ERROR_MESSAGE);
				model.setPieceSelected(false);
				return;
			}
		}

		// check if move is legal
		if(!checkMovePiece(newSpot)) {
			JOptionPane.showMessageDialog(null, "Illegal move!", "Error", JOptionPane.ERROR_MESSAGE);
			model.setPieceSelected(false);
			return;
		}

		// if move is legal
		newSpot.setIcon(view.getSelectedIcon());
		view.getSelectedButton().setIcon(null);
		model.setPieceSelected(false);

		// check if in check after move
		if(checkCheck()) {
			oldSpot.setIcon(view.getSelectedIcon());
			newSpot.setIcon(newSpotIcon);
			JOptionPane.showMessageDialog(null, "Illegal move!", "Error", JOptionPane.ERROR_MESSAGE);
			//model.setPieceSelected(false);
			return;
		}

		if((view.getSelectedIcon()==view.getWhitePawn() || view.getSelectedIcon()==view.getBlackPawn()) && view.isEndOfBoard(newSpot)) {
			changePawnToPiece(newSpot);
		}


		// Taking if en passant is enabled
		if(model.getEnPassantAvailable()) {
			if(model.getWhiteTurn()) {
				if(view.getRow(newSpot) == 2 && view.getCol(newSpot) == model.getEnPassantColumn()) {
					view.getBoardSpots()[3][model.getEnPassantColumn()].setIcon(null);
				}
			}
			else { // black turn
				if(view.getRow(newSpot) == 5 && view.getCol(newSpot) == model.getEnPassantColumn()) {
					view.getBoardSpots()[4][model.getEnPassantColumn()].setIcon(null);
				}
			}
		}

		// resetting en passant
		model.setEnPassantAvailable(false);

		// if everything is good - other player's turn
		model.setWhiteTurn(!model.getWhiteTurn());

		// Enabling en passant if pawn moved 2 spots
		if(view.getSelectedIcon() == view.getWhitePawn()) {
			if(view.getRow(oldSpot) == 6 && view.getRow(newSpot) == 4) {
				model.setEnPassantAvailable(true);
				model.setEnPassantColumn(view.getCol(oldSpot));
			}
		}
		else if(view.getSelectedIcon() == view.getBlackPawn()) {
			if(view.getRow(oldSpot) == 1 && view.getRow(newSpot) == 3) {
				model.setEnPassantAvailable(true);
				model.setEnPassantColumn(view.getCol(oldSpot));
			}
		}

		// If in check - check if it's possible to get out
		if(checkCheck()) {
			if(!canEscape()) {
				model.setIsOver(true);
				String winner;
				if(model.getWhiteTurn()) {
					winner = "Black";
				}
				else {
					winner = "White";
				}
				JOptionPane.showMessageDialog(null, winner + " has won the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}


	/**
	 * Called after a turn ends if the next player is in check.
	 * Checks whether it's possible for the player to escape check.
	 * This is done by checking every possible move for all of the players remaining pieces 
	 * and checking if it is in check after every possible move or not
	 * @return true if it's possible to escape, false otherwise.
	 */
	public boolean canEscape() {
		// Check every spot on the board and select any pieces
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				oldSpot = view.getBoardSpots()[i][j];
				if(model.getWhiteTurn()) { // White turn
					if(view.isWhitePiece(oldSpot)) {
						if(checkEverySpot(i,j)) {
							return true;
						}
					}
				}
				else { // black turn
					if(view.isBlackPiece(view.getBoardSpots()[i][j])) {
						if(view.isBlackPiece(oldSpot)) {
							if(checkEverySpot(i,j)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the player is in check
	 * @return true if the player is in check, false otherwise
	 */
	public boolean checkCheck() {
		if(model.getWhiteTurn()) {
			int[] kingSpot = view.whiteKingSpot();
			int kingRow = kingSpot[0];
			int kingCol = kingSpot[1];

			// knight
			int[][] knightSpots = { // [0-7][0-1]
					{kingRow+1,kingCol+2},{kingRow+2,kingCol+1},{kingRow-1,kingCol+2},{kingRow-2,kingCol+1},
					{kingRow+1,kingCol-2},{kingRow+2,kingCol-1},{kingRow-1,kingCol-2},{kingRow-2,kingCol-1}
			};
			for(int i=0;i<8;i++) {
				if(knightSpots[i][0] > -1 && knightSpots[i][0] < 8 && knightSpots[i][1] > -1 && knightSpots[i][1] < 8) { // spot is on the board
					if(view.getBoardSpots()[knightSpots[i][0]][knightSpots[i][1]].getIcon() == view.getBlackKnight()) {
						return true;
					}
				}
			}

			// rook or queen
			for(int i=kingRow+1;i<8;i++) {
				if(view.getBoardSpots()[i][kingCol].getIcon()!=null) {
					if(view.getBoardSpots()[i][kingCol].getIcon() == view.getBlackRook() || view.getBoardSpots()[i][kingCol].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingRow-1;i>-1;i--) {
				if(view.getBoardSpots()[i][kingCol].getIcon()!=null) {
					if(view.getBoardSpots()[i][kingCol].getIcon() == view.getBlackRook() || view.getBoardSpots()[i][kingCol].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingCol+1;i<8;i++) {
				if(view.getBoardSpots()[kingRow][i].getIcon()!=null) {
					if(view.getBoardSpots()[kingRow][i].getIcon() == view.getBlackRook() || view.getBoardSpots()[kingRow][i].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingCol-1;i>-1;i--) {
				if(view.getBoardSpots()[kingRow][i].getIcon()!=null) {
					if(view.getBoardSpots()[kingRow][i].getIcon() == view.getBlackRook() || view.getBoardSpots()[kingRow][i].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
			}

			// bishop or queen

			// Down and right
			int i = kingRow+1;
			int j = kingCol+1;
			while(i < 8 && j < 8) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getBlackBishop() || view.getBoardSpots()[i][j].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
				i++;
				j++;
			}

			// Down and left
			i = kingRow+1;
			j = kingCol-1;
			while(i < 8 && j > -1) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getBlackBishop() || view.getBoardSpots()[i][j].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
				i++;
				j--;
			}

			// Up and left
			i = kingRow-1;
			j = kingCol-1;
			while(i > -1 && j > -1) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getBlackBishop() || view.getBoardSpots()[i][j].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
				i--;
				j--;
			}

			// Up and right
			i = kingRow-1;
			j = kingCol+1;
			while(i > -1 && j < 8) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getBlackBishop() || view.getBoardSpots()[i][j].getIcon() == view.getBlackQueen()) {
						return true;
					}
					break;
				}
				i--;
				j++;
			}


			// pawn
			if(kingCol > 0) { // Checking to ensure the king isn't on the edge of the board in order to avoid invalid indices
				if(view.getBoardSpots()[kingRow-1][kingCol-1].getIcon() == view.getBlackPawn()) {
					return true;
				}	
			}
			if(kingCol < 7) { // Checking to ensure the king isn't on the edge of the board in order to avoid invalid indices
				if(view.getBoardSpots()[kingRow-1][kingCol+1].getIcon() == view.getBlackPawn()) {
					return true;
				}
			}

			// king
			int blackKingRow = view.blackKingSpot()[0];
			int blackKingCol = view.blackKingSpot()[1];
			if(Math.abs(kingRow - blackKingRow) < 2 && Math.abs(kingCol - blackKingCol) < 2) {
				return true;
			}

			// otherwise
			return false;
		}
		else { // if black turn
			int[] kingSpot = view.blackKingSpot();
			int kingRow = kingSpot[0];
			int kingCol = kingSpot[1];

			// knight
			int[][] knightSpots = { // [0-7][0-1]
					{kingRow+1,kingCol+2},{kingRow+2,kingCol+1},{kingRow-1,kingCol+2},{kingRow-2,kingCol+1},
					{kingRow+1,kingCol-2},{kingRow+2,kingCol-1},{kingRow-1,kingCol-2},{kingRow-2,kingCol-1}
			};
			for(int i=0;i<8;i++) {
				if(knightSpots[i][0] > -1 && knightSpots[i][0] < 8 && knightSpots[i][1] > -1 && knightSpots[i][1] < 8) { // spot is on the board
					if(view.getBoardSpots()[knightSpots[i][0]][knightSpots[i][1]].getIcon() == view.getWhiteKnight()) {
						return true;
					}
				}
			}

			// rook or queen
			for(int i=kingRow+1;i<8;i++) {
				if(view.getBoardSpots()[i][kingCol].getIcon()!=null) {
					if(view.getBoardSpots()[i][kingCol].getIcon() == view.getWhiteRook() || view.getBoardSpots()[i][kingCol].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingRow-1;i>-1;i--) {
				if(view.getBoardSpots()[i][kingCol].getIcon()!=null) {
					if(view.getBoardSpots()[i][kingCol].getIcon() == view.getWhiteRook() || view.getBoardSpots()[i][kingCol].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingCol+1;i<8;i++) {
				if(view.getBoardSpots()[kingRow][i].getIcon()!=null) {
					if(view.getBoardSpots()[kingRow][i].getIcon() == view.getWhiteRook() || view.getBoardSpots()[kingRow][i].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
			}
			for(int i=kingCol-1;i>-1;i--) {
				if(view.getBoardSpots()[kingRow][i].getIcon()!=null) {
					if(view.getBoardSpots()[kingRow][i].getIcon() == view.getWhiteRook() || view.getBoardSpots()[kingRow][i].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
			}

			// bishop or queen
			// Down and right
			int i = kingRow+1;
			int j = kingCol+1;
			while(i < 8 && j < 8) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getWhiteBishop() || view.getBoardSpots()[i][j].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
				i++;
				j++;
			}

			// Down and left
			i = kingRow+1;
			j = kingCol-1;
			while(i < 8 && j > -1) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getWhiteBishop() || view.getBoardSpots()[i][j].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
				i++;
				j--;
			}

			// Up and left
			i = kingRow-1;
			j = kingCol-1;
			while(i > -1 && j > -1) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getWhiteBishop() || view.getBoardSpots()[i][j].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
				i--;
				j--;
			}

			// Up and right
			i = kingRow-1;
			j = kingCol+1;
			while(i > -1 && j < 8) {
				if(view.getBoardSpots()[i][j].getIcon()!=null) {
					if(view.getBoardSpots()[i][j].getIcon() == view.getWhiteBishop() || view.getBoardSpots()[i][j].getIcon() == view.getWhiteQueen()) {
						return true;
					}
					break;
				}
				i--;
				j++;
			}


			// pawn
			if(kingCol > 0) { // Checking to ensure the king isn't on the edge of the board in order to avoid invalid indices
				if(view.getBoardSpots()[kingRow+1][kingCol-1].getIcon() == view.getWhitePawn()) {
					return true;
				}	
			}
			if(kingCol < 7) { // Checking to ensure the king isn't on the edge of the board in order to avoid invalid indices
				if(view.getBoardSpots()[kingRow+1][kingCol+1].getIcon() == view.getWhitePawn()) {
					return true;
				}
			}

			// king
			int whiteKingRow = view.whiteKingSpot()[0];
			int whiteKingCol = view.whiteKingSpot()[1];
			if(Math.abs(kingRow - whiteKingRow) < 2 && Math.abs(kingCol - whiteKingCol) < 2) {
				return true;
			}

			// otherwise
			return false;
		}
	}

	public boolean checkMovePiece(JButton newSpot) {
		int newRow = view.getRow(newSpot);
		int newCol = view.getCol(newSpot);
		int oldRow = view.getRow(oldSpot);
		int oldCol = view.getCol(oldSpot);
		int rowDif = newRow - oldRow;
		int colDif = newCol - oldCol;

		// can't move on top of own piece
		if(model.getWhiteTurn()) {
			if(view.isWhitePiece(newSpot)) {
				model.setPieceSelected(false);
				return false;
			}
		}
		else {
			if(view.isBlackPiece(newSpot)) {
				model.setPieceSelected(false);
				return false;
			}
		}

		// KING //////////////////////////
		//////////////////////////////////
		if(oldSpot.getIcon()==view.getBlackKing() || oldSpot.getIcon()==view.getWhiteKing()) {

			if(model.getWhiteTurn() && !model.getWhiteKingMoved()) { // White turn and able to castle
				if(newRow == 7) { 
					if(newCol == 2 && view.getBoardSpots()[7][3].getIcon() == null && !model.getWhiteRookAMoved()) {
						if(!checkCheck()) { // No check on current position
							view.getBoardSpots()[7][3].setIcon(view.getWhiteKing());
							view.getBoardSpots()[7][4].setIcon(null);
							if(!checkCheck()) { // check check on position it moves through
								view.getBoardSpots()[7][2].setIcon(view.getWhiteKing());
								view.getBoardSpots()[7][3].setIcon(null);
								if(!checkCheck()) {
									view.getBoardSpots()[7][0].setIcon(null);
									view.getBoardSpots()[7][3].setIcon(view.getWhiteRook());
									model.setWhiteKingMoved(true); // need to check check for new spot and spot going through first
									model.setWhiteRookAMoved(true);
									return true;
								}
								else { // position it moves to puts it into check - reset king
									view.getBoardSpots()[7][2].setIcon(null);
									view.getBoardSpots()[7][4].setIcon(view.getWhiteKing());
								}
							}
							else { // position it moves through puts it into check - reset king
								view.getBoardSpots()[7][3].setIcon(null);
								view.getBoardSpots()[7][4].setIcon(view.getWhiteKing());
							}
						}
					}
					else if(newCol == 6 && view.getBoardSpots()[7][5].getIcon() == null && !model.getWhiteRookHMoved()) {
						if(!checkCheck()) { // No check on current position
							view.getBoardSpots()[7][5].setIcon(view.getWhiteKing());
							view.getBoardSpots()[7][4].setIcon(null);
							if(!checkCheck()) { // check check on position it moves through
								view.getBoardSpots()[7][6].setIcon(view.getWhiteKing());
								view.getBoardSpots()[7][5].setIcon(null);
								if(!checkCheck()) {
									view.getBoardSpots()[7][7].setIcon(null);
									view.getBoardSpots()[7][5].setIcon(view.getWhiteRook());
									model.setWhiteKingMoved(true); // need to check check for new spot and spot going through first
									model.setWhiteRookHMoved(true);
									return true;
								}
								else { // position it moves to puts it into check - reset king
									view.getBoardSpots()[7][6].setIcon(null);
									view.getBoardSpots()[7][4].setIcon(view.getWhiteKing());
								}
							}
							else { // position it moves through puts it into check - reset king
								view.getBoardSpots()[7][5].setIcon(null);
								view.getBoardSpots()[7][4].setIcon(view.getWhiteKing());
							}
						}
					}
				}
			}
			else if(!model.getWhiteTurn() && !model.getBlackKingMoved()) { // Black turn and able to castle
				if(newRow == 0) {
					if(newCol == 2 && view.getBoardSpots()[0][3].getIcon() == null && !model.getBlackRookAMoved()) {
						if(!checkCheck()) { // No check on current position
							view.getBoardSpots()[0][3].setIcon(view.getBlackKing());
							view.getBoardSpots()[0][4].setIcon(null);
							if(!checkCheck()) { // check check on position it moves through
								view.getBoardSpots()[0][2].setIcon(view.getBlackKing());
								view.getBoardSpots()[0][3].setIcon(null);
								if(!checkCheck()) {
									view.getBoardSpots()[0][0].setIcon(null);
									view.getBoardSpots()[0][3].setIcon(view.getBlackRook());
									model.setBlackKingMoved(true); // need to check check for new spot and spot going through first
									model.setBlackRookAMoved(true);
									return true;
								}
								else { // position it moves to puts it into check - reset king
									view.getBoardSpots()[0][2].setIcon(null);
									view.getBoardSpots()[0][4].setIcon(view.getBlackKing());
								}
							}
							else { // position it moves through puts it into check - reset king
								view.getBoardSpots()[0][3].setIcon(null);
								view.getBoardSpots()[0][4].setIcon(view.getBlackKing());
							}
						}
					}
					else if(newCol == 6 && view.getBoardSpots()[0][5].getIcon() == null && !model.getBlackRookHMoved()) {
						if(!checkCheck()) { // No check on current position
							view.getBoardSpots()[0][5].setIcon(view.getBlackKing());
							view.getBoardSpots()[0][4].setIcon(null);
							if(!checkCheck()) { // check check on position it moves through
								view.getBoardSpots()[0][6].setIcon(view.getBlackKing());
								view.getBoardSpots()[0][5].setIcon(null);
								if(!checkCheck()) {
									view.getBoardSpots()[0][7].setIcon(null);
									view.getBoardSpots()[0][5].setIcon(view.getBlackRook());
									model.setBlackKingMoved(true); // need to check check for new spot and spot going through first
									model.setBlackRookHMoved(true);
									return true;
								}
								else { // position it moves to puts it into check - reset king
									view.getBoardSpots()[0][6].setIcon(null);
									view.getBoardSpots()[0][4].setIcon(view.getBlackKing());
								}
							}
							else { // position it moves through puts it into check - reset king
								view.getBoardSpots()[0][5].setIcon(null);
								view.getBoardSpots()[0][4].setIcon(view.getBlackKing());
							}
						}
					} 
				}
			}


			if(Math.abs(newRow - oldRow) > 1 || Math.abs(newCol - oldCol) > 1) {
				return false;
			}
			// Set king moved flag to true to prevent castling in future turns
			if(model.getWhiteTurn()) {
				model.setWhiteKingMoved(true);
			}
			else {
				model.setBlackKingMoved(true);
			}
			return true;
		}

		// ROOK ////////////////////////////////
		////////////////////////////////////////
		if(oldSpot.getIcon()==view.getBlackRook() || oldSpot.getIcon()==view.getWhiteRook()) {
			int i = 1;
			if(Math.abs(newRow - oldRow) > 0 && Math.abs(newCol - oldCol) > 0) {
				return false;
			}

			if(rowDif > 0) {
				while(i<Math.abs(rowDif)) {
					if(view.getBoardSpots()[oldRow+i][oldCol].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else if(rowDif < 0) {
				while(i<Math.abs(rowDif)) {
					if(view.getBoardSpots()[oldRow-i][oldCol].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else if(colDif > 0) {
				while(i<Math.abs(colDif)) {
					if(view.getBoardSpots()[oldRow][oldCol+i].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else { // colDif < 0
				while(i<Math.abs(colDif)) {
					if(view.getBoardSpots()[oldRow][oldCol-i].getIcon() != null) {
						return false;
					}
					i++;
				}
			}

			// If move is allowed - set rook status to moved
			if(oldRow == 0 && oldCol == 0) {
				model.setBlackRookAMoved(true);
			}
			else if(oldRow == 0 && oldCol == 7) {
				model.setBlackRookHMoved(true);
			}
			else if(oldRow == 7 && oldCol == 0) {
				model.setWhiteRookAMoved(true);
			}
			else if(oldRow == 7 && oldCol == 7){
				model.setWhiteRookHMoved(true);
			}
			return true;
		}

		// KNIGHT /////////////////////
		///////////////////////////////
		if(oldSpot.getIcon()==view.getBlackKnight() || oldSpot.getIcon()==view.getWhiteKnight()) {
			if(Math.abs(newRow - oldRow) == 1 && Math.abs(newCol - oldCol) == 2) {
				return true;
			}
			else if(Math.abs(newRow - oldRow) == 2 && Math.abs(newCol - oldCol) == 1) {
				return true;
			}
			return false;
		}

		// BISHOP //////////////////////
		////////////////////////////////
		if(oldSpot.getIcon()==view.getBlackBishop() || oldSpot.getIcon()==view.getWhiteBishop()) {
			// moving diagonal
			int i = 1;
			if(Math.abs(rowDif) == Math.abs(colDif)) {

				// moving down right
				if(rowDif > 0 && colDif > 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow+i][oldCol+i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving up right
				if(rowDif > 0 && colDif < 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow+i][oldCol-i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving up left
				if(rowDif < 0 && colDif < 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow-i][oldCol-i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving down left
				if(rowDif < 0 && colDif > 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow-i][oldCol+i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				return true;
			}
		}


		// QUEEN /////////////////////////////
		//////////////////////////////////////
		if(oldSpot.getIcon()==view.getBlackQueen() || oldSpot.getIcon()==view.getWhiteQueen()) {
			// moving diagonal
			int i = 1;
			if(Math.abs(rowDif) == Math.abs(colDif)) {

				// moving down right
				if(rowDif > 0 && colDif > 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow+i][oldCol+i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving up right
				if(rowDif > 0 && colDif < 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow+i][oldCol-i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving up left
				if(rowDif < 0 && colDif < 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow-i][oldCol-i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				// moving down left
				if(rowDif < 0 && colDif > 0) {
					while(i<Math.abs(rowDif)) {
						if(view.getBoardSpots()[oldRow-i][oldCol+i].getIcon() != null) {
							return false;
						}
						i++;
					}
				}

				return true;
			}
			// moving horizontal/vertical
			else if(Math.abs(newRow - oldRow) > 0 && Math.abs(newCol - oldCol) > 0) {
				return false;
			}

			if(rowDif > 0) {
				while(i<Math.abs(rowDif)) {
					if(view.getBoardSpots()[oldRow+i][oldCol].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else if(rowDif < 0) {
				while(i<Math.abs(rowDif)) {
					if(view.getBoardSpots()[oldRow-i][oldCol].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else if(colDif > 0) {
				while(i<Math.abs(colDif)) {
					if(view.getBoardSpots()[oldRow][oldCol+i].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			else { // colDif < 0
				while(i<Math.abs(colDif)) {
					if(view.getBoardSpots()[oldRow][oldCol-i].getIcon() != null) {
						return false;
					}
					i++;
				}
			}
			return true;
		}

		// BLACK PAWN //////////////////////////////
		////////////////////////////////////////////
		if(oldSpot.getIcon()==view.getBlackPawn()){
			if(Math.abs(newCol - oldCol) == 1 && newRow == oldRow + 1) { // Taking a piece - moving 1 row forward and 1 column sideways
				if(view.isWhitePiece(newSpot)) { // Can only move this way if there is a white piece there
					return true;
				}
			}
			if(model.getEnPassantAvailable()) {
				if(view.getCol(newSpot) == model.getEnPassantColumn() && newRow == oldRow + 1 && Math.abs(newCol - oldCol) == 1) {
					return true;
				}
			}
			if(newCol != oldCol) { // If you can't take a piece, you can't move sideways
				return false;
			}
			if(oldRow == 1 && newRow == 2) { 
				if(newSpot.getIcon()==null) {
					return true;
				}
			}
			else if(oldRow == 1 && newRow == 3) {
				if(newSpot.getIcon()==null && view.getBoardSpots()[2][oldCol].getIcon()==null) {
					return true;
				}
			}
			else {
				if(newRow == oldRow + 1) {
					if(newSpot.getIcon()==null) {
						return true;
					}
				}
			}
			return false;
		}

		// WHITE PAWN ////////////////////////////////////////
		//////////////////////////////////////////////////////
		if(oldSpot.getIcon()==view.getWhitePawn()){
			if(Math.abs(newCol - oldCol) == 1 && newRow == oldRow - 1) { // Taking a piece - moving 1 row forward and 1 column sideways
				if(view.isBlackPiece(newSpot)) { // Can only move this way if there is a black piece there
					return true;
				}
			}
			if(model.getEnPassantAvailable()) {
				if(view.getCol(newSpot) == model.getEnPassantColumn() && newRow == oldRow - 1 && Math.abs(newCol - oldCol) == 1) {
					return true;
				}
			}
			if(newCol != oldCol) {
				return false;
			}
			if(oldRow == 6 && newRow == 5) {
				if(newSpot.getIcon()==null) {
					return true;
				}
			}
			else if(oldRow == 6 && newRow == 4) {
				if(newSpot.getIcon()==null && view.getBoardSpots()[5][oldCol].getIcon()==null) {
					return true;
				}
			}
			else {
				if(newRow == oldRow - 1) {
					if(newSpot.getIcon() == null) {
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Helper function to set an icon at a spot
	 * @param row - row to set icon at
	 * @param col - column to set icon at
	 * @param icon - icon to set
	 */
	public void setIconMove(int row, int col, Icon icon) {
		view.getBoardSpots()[row][col].setIcon(icon);
	}

	/**
	 * Helper method called by canEscape
	 * Checks every spot on the board to see if the piece at the given spot can move there.
	 * Checking to see if it is possible to escape from check.
	 * @param i - row of the piece to check moves for
	 * @param j - column of the piece to check moves for
	 * @return - true if a move made by the piece at the given spot can break the check
	 */
	public boolean checkEverySpot(int i, int j) {
		// check every spot on the board
		for(int k=0;k<8;k++) {
			for(int l=0;l<8;l++) {
				// If the move is valid
				if(checkMovePiece(view.getBoardSpots()[k][l])) {
					Icon oldIcon = oldSpot.getIcon();
					Icon newSpotIcon = view.getBoardSpots()[k][l].getIcon();
					setIconMove(i,j,null);
					setIconMove(k,l,oldIcon);
					if(!checkCheck()) {
						setIconMove(i,j,oldIcon);
						setIconMove(k,l,newSpotIcon);
						return true;
					}
					setIconMove(i,j,oldIcon);
					setIconMove(k,l,newSpotIcon);
				}
			}
		}
		return false;
	}

	public void changePawnToPiece(JButton spot) {
		String[] choices = { "Rook", "Knight", "Bishop", "Queen"};
		String input = (String) JOptionPane.showInputDialog(null, "Select a piece:",
				"", JOptionPane.QUESTION_MESSAGE, null, 
				choices,
				choices[0]); 
		if(model.getWhiteTurn()) {
			if(input.equals("Rook")) {
				spot.setIcon(view.getWhiteRook());
			}
			else if(input.equals("Knight")) {
				spot.setIcon(view.getWhiteKnight());
			}
			else if(input.equals("Bishop")) {
				spot.setIcon(view.getWhiteBishop());
			}
			else { // queen
				spot.setIcon(view.getWhiteQueen());
			}
		}
		else { // black turn
			if(input.equals("Rook")) {
				spot.setIcon(view.getBlackRook());
			}
			else if(input.equals("Knight")) {
				spot.setIcon(view.getBlackKnight());
			}
			else if(input.equals("Bishop")) {
				spot.setIcon(view.getBlackBishop());
			}
			else { // queen
				spot.setIcon(view.getBlackQueen());
			}
		}
	}
}
