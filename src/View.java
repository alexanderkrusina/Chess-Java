import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class View extends JFrame{
	private JButton [][] boardSpots = new JButton [8][8];
	private JButton selectedButton;
	private Icon selectedIcon;
	private int selRow, selCol, newRow, newCol;
	private Icon whitePawn, whiteKnight, whiteRook, whiteBishop, whiteQueen, whiteKing;
	private Icon blackPawn, blackKnight, blackRook, blackBishop, blackQueen, blackKing;
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	public View() {
		setLayout(new BorderLayout());

		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				getBoardSpots()[i][j] = new JButton();
				if((i+j)%2==0) 
					getBoardSpots()[i][j].setBackground(Color.white);
				else
					getBoardSpots()[i][j].setBackground(Color.LIGHT_GRAY);
			}
		}
		add(createBoard(), BorderLayout.CENTER);
		makeIcons();
		setInitialState();

		setLocation(600,150);
		setSize(700,700);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	////////////////////////////////////////////////
	////////////////////////////////////////////////

	public int getRow(JButton spot) {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(spot==boardSpots[i][j]) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getCol(JButton spot) {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(spot==boardSpots[i][j]) {
					return j;
				}
			}
		}
		return -1;
	}
	
	public void setListeners(ActionListener l) {
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				getBoardSpots()[i][j].addActionListener(l);;
			}
		}
	}
	
	public void setInitialState() {
		for(int i=0;i<8;i++) {
			getBoardSpots()[1][i].setIcon(blackPawn);
			getBoardSpots()[6][i].setIcon(whitePawn);
		}
		getBoardSpots()[0][0].setIcon(blackRook);
		getBoardSpots()[0][1].setIcon(blackKnight);
		getBoardSpots()[0][2].setIcon(blackBishop);
		getBoardSpots()[0][3].setIcon(blackQueen);
		getBoardSpots()[0][4].setIcon(blackKing);
		getBoardSpots()[0][5].setIcon(blackBishop);
		getBoardSpots()[0][6].setIcon(blackKnight);
		getBoardSpots()[0][7].setIcon(blackRook);
		getBoardSpots()[7][0].setIcon(whiteRook);
		getBoardSpots()[7][1].setIcon(whiteKnight);
		getBoardSpots()[7][2].setIcon(whiteBishop);
		getBoardSpots()[7][3].setIcon(whiteQueen);
		getBoardSpots()[7][4].setIcon(whiteKing);
		getBoardSpots()[7][5].setIcon(whiteBishop);
		getBoardSpots()[7][6].setIcon(whiteKnight);
		getBoardSpots()[7][7].setIcon(whiteRook);
	}
	
	public void makeIcons() {
		whitePawn = new ImageIcon((new ImageIcon("../icons/whitePawn.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackPawn = new ImageIcon((new ImageIcon("../icons/blackPawn.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		whiteKnight = new ImageIcon((new ImageIcon("../icons/whiteKnight.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackKnight = new ImageIcon((new ImageIcon("../icons/blackKnight.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		whiteBishop = new ImageIcon((new ImageIcon("../icons/whiteBishop.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackBishop = new ImageIcon((new ImageIcon("../icons/blackBishop.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		whiteRook = new ImageIcon((new ImageIcon("../icons/whiteRook.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackRook = new ImageIcon((new ImageIcon("../icons/blackRook.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		whiteKing = new ImageIcon((new ImageIcon("../icons/whiteKing.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackKing = new ImageIcon((new ImageIcon("../icons/blackKing.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		whiteQueen = new ImageIcon((new ImageIcon("../icons/whiteQueen.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
		blackQueen = new ImageIcon((new ImageIcon("../icons/blackQueen.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));
	}
	
	public boolean isEndOfBoard(JButton spot) {
		for(int i=0;i<8;i++) {
			if(spot == getBoardSpots()[7][i] || spot == getBoardSpots()[0][i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isWhitePiece(JButton spot) {
		if(spot.getIcon() == whitePawn || spot.getIcon() == whiteKnight || 
				spot.getIcon() == whiteBishop || spot.getIcon() == whiteRook || 
				spot.getIcon() == whiteKing || spot.getIcon() == whiteQueen) {
			return true;
		}
		return false;
	}
	
	public boolean isBlackPiece(JButton spot) {
		if(spot.getIcon() == blackPawn || spot.getIcon() == blackKnight || 
				spot.getIcon() == blackBishop || spot.getIcon() == blackRook || 
				spot.getIcon() == blackKing || spot.getIcon() == blackQueen) {
			return true;
		}
		return false;
	}
	
	
	public JPanel createBoard() {
		JPanel board = new JPanel();
		board.setLayout(new BorderLayout());

		JPanel squares = new JPanel();
		squares.setLayout(new GridLayout(8,8));

		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				squares.add(getBoardSpots()[i][j]);
			}
		}

		board.add(squares, BorderLayout.CENTER);
		board.add(makeLetters(), BorderLayout.NORTH);
		board.add(makeLetters(), BorderLayout.SOUTH);
		board.add(makeNumbers(), BorderLayout.WEST);
		board.add(makeNumbers(), BorderLayout.EAST);

		return board;
	}

	public JPanel makeLetters() {
		JPanel letters = new JPanel();
		letters.setLayout(new GridLayout(1,10));
		letters.add(new Label(""));
		letters.add(new Label("A"));
		letters.add(new Label("B"));
		letters.add(new Label("C"));
		letters.add(new Label("D"));
		letters.add(new Label("E"));
		letters.add(new Label("F"));
		letters.add(new Label("G"));
		letters.add(new Label("H"));
		letters.setBorder(new EmptyBorder(5,5,10,10));
		return letters;
	}

	public JPanel makeNumbers() {
		JPanel numbers = new JPanel();
		numbers.setLayout(new GridLayout(8,1));
		for(int i=8;i>0;i--) {
			numbers.add(new Label(Integer.toString(i)));
		}
		numbers.setBorder(new EmptyBorder(5,10,5,5));
		return numbers;
	}

	public JButton getSelectedButton() {
		return selectedButton;
	}

	public void setSelectedButton(JButton selectedButton) {
		this.selectedButton = selectedButton;
	}

	public Icon getSelectedIcon() {
		return selectedIcon;
	}

	public void setSelectedIcon(Icon selectedIcon) {
		this.selectedIcon = selectedIcon;
	}

	public JButton [][] getBoardSpots() {
		return boardSpots;
	}

	public void setBoardSpots(JButton [][] boardSpots) {
		this.boardSpots = boardSpots;
	}

	public int[] whiteKingSpot() {
		int[] spot = new int[2];
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(boardSpots[i][j].getIcon() == getWhiteKing()) {
					spot[0] = i;
					spot[1] = j;
					return spot;
				}
			}
		}
		return null;
	}
	
	public int[] blackKingSpot() {
		int[] spot = new int[2];
		for(int i=0;i<8;i++) {
			for(int j=0;j<8;j++) {
				if(boardSpots[i][j].getIcon() == getBlackKing()) {
					spot[0] = i;
					spot[1] = j;
					return spot;
				}
			}
		}
		return null;
	}

	public Icon getBlackKing() {
		return blackKing;
	}

	public void setBlackKing(Icon blackKing) {
		this.blackKing = blackKing;
	}

	public Icon getWhiteKing() {
		return whiteKing;
	}

	public void setWhiteKing(Icon whiteKing) {
		this.whiteKing = whiteKing;
	}
	
	public Icon getBlackPawn() {
		return blackPawn;
	}

	public Icon getWhitePawn() {
		return whitePawn;
	}
	
	public Icon getBlackRook() {
		return blackRook;
	}

	public Icon getWhiteRook() {
		return whiteRook;
	}
	
	public Icon getBlackKnight() {
		return blackKnight;
	}

	public Icon getWhiteKnight() {
		return whiteKnight;
	}
	
	public Icon getBlackBishop() {
		return blackBishop;
	}

	public Icon getWhiteBishop() {
		return whiteBishop;
	}
	
	public Icon getBlackQueen() {
		return blackQueen;
	}

	public Icon getWhiteQueen() {
		return whiteQueen;
	}
}
