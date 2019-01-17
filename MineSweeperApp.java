package mineSweeper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MineSweeperApp extends Application {
	private static final int W = 1280;
	private static final int H = 680;
	private static final int TILESIZE = 25;
	private static final int XMAX= 45;
	private static final int YMAX = 22;
	private static final int XYMIN = 9;
	
	private static final int XTILES=9;
	private static final int YTILES=9;
	private static int XSIZE = (TILESIZE+2)*XTILES+2;
	private static int YSIZE = (TILESIZE+2)*YTILES+2;
	private static int INITPOSX = W/2-XSIZE/2+30;
	private static int INITPOSY = H/2-YSIZE/2+30;
	
	private static Tile[][] grid = new Tile[YTILES][XTILES];

	@Override
	public void start(Stage stage) throws Exception {
		ImageCreator.createImages();
		MainMenu menu = new MainMenu ();
		menu.start(stage);
	}
	
	public Parent createContent () {
		Pane root = new Pane ();
		root.setPrefSize(W, H);
		//System.out.println("hi");
		for (int y=0; y<YTILES; y++) {
			for (int x=0; x<XTILES; x++) {
				Tile temp = grid[y][x];
				temp = new Tile (x,y,Math.random()<=0.2);

				grid[y][x]=temp;
				root.getChildren().add(temp);
			}
		}
		
		for (int y=0; y<YTILES; y++) {
			for (int x=0; x<XTILES; x++) {
				Tile temp = grid[y][x];
				
				temp.setBombs(howManyBombs(temp));
				temp.setDisplay();
			}
		}
		return root;
	}
	
	public void setXTiles (int x) {
		XTILES = x;
	}
	
	public void setYTiles (int y) {
		YTILES = y;
	}
	
	public static class ImageCreator {
		private static Image bomb;
		private static Image flag;
		
		private static void createImages() throws Exception {
			InputStream is1 = Files.newInputStream(Paths.get("resources/images/bomb.png"));
			bomb = new Image (is1);
			is1 = Files.newInputStream(Paths.get("resources/images/flag.png"));
			flag = new Image (is1);
			is1.close();
		}
		
		private static Image getBomb () {
			return bomb;
		}
		
		private static Image getFlag () {
			return flag;
		}
	}
	
	
	private static int howManyBombs (Tile tile){
		int num = 0;
		
		if (tile.hasBomb) {
			return -1;
		}
		else {
			for (int i=-1; i <= 1; i ++) {
				int row = tile.y+i;
				for (int j = -1; j <= 1; j ++) {
					int col = tile.x+j;
					if (row>=0 && row<YTILES && col>=0 && col<XTILES) {
						Tile t = grid[row][col];
						if (t.hasBomb)
							num++;
					}
				}
			}
		}
		
		return num;
	}
	
	private static void gameOver () {
		Tile temp;
		
		for (int y=0; y<YTILES; y++) {
			for (int x=0; x<XTILES; x++) {
				temp = grid[y][x];
				temp.showBomb();
			}
		}
	}
	
	private class Tile extends StackPane {
		private int x,y;
		private int xPos, yPos;
		private boolean hasBomb;
		private int bombs = 0;
		private boolean isOpen = false;
		private boolean isFlagged = false;
		private Rectangle tile = new Rectangle (TILESIZE,TILESIZE);
		private Text display = new Text ();
		private boolean canExpand = true;
		private ImageView iv = new ImageView (ImageCreator.getFlag());
		private ImageView iv2 = new ImageView (ImageCreator.getBomb());
		
		public Tile (int x, int y, boolean hasBomb) {
			this.x = x;
			this.y = y;
			this.hasBomb = hasBomb;
		
			Light.Distant light = new Light.Distant();
			light.setAzimuth(-135.0);

			Lighting lighting = new Lighting();
			lighting.setLight(light);
			lighting.setSurfaceScale(1.2);

			tile.setFill(Color.LAWNGREEN);
		    tile.setEffect(lighting);
			tile.setArcHeight(10);
			tile.setArcWidth(10);
			
			xPos = 2+INITPOSX+x*TILESIZE;
			yPos = 2+INITPOSY+y*TILESIZE;
			
			setTranslateX(xPos);
			setTranslateY(yPos);
			
			tile.setVisible(true);
			
			iv.setFitHeight(20);
			iv.setFitWidth(20);
			iv.setVisible(false);
			
			iv2.setFitHeight(25);
			iv2.setFitWidth(25);
			iv2.setVisible(false);
			
			getChildren().addAll(tile,display,iv,iv2);
			
			this.setOnMouseClicked(event -> {
	                MouseButton button = event.getButton();
	                if(button==MouseButton.PRIMARY){
	                	if (isFlagged)
	                		unflag();
	                	open();
						if (canExpand)
							expand();
	                }else if(button==MouseButton.SECONDARY && !isFlagged){
	                    flag();
	                }else {
	                	unflag();
	                }
				
			});

		}
		
		
		private void setBombs (int n) {
			this.bombs=n;
		}
		
		private void setDisplay () {
			display.setText(String.valueOf(bombs));
			display.setFont(display.getFont().font(20));
			switch (bombs) {
				case 0: 
				break;
				case 1: display.setFill(Color.DARKBLUE);
				break;
				case 2: display.setFill(Color.DARKGREEN);
				break;
				case 3: display.setFill(Color.DARKRED);
				break;
				case 4: display.setFill(Color.DARKGOLDENROD);
				break;
				default: display.setFill(Color.DARKORCHID);
			}
			
			display.setVisible(false);
		}
		
		public void open () {
			if (isFlagged) {
				unflag();
			}
			
			if (hasBomb) {
				tile.setFill(Color.BLACK);
				gameOver();
				return;
			}
		
			if (!isOpen) {
				isOpen = true;
				tile.setFill(Color.LIGHTGREY);
				if (bombs>0) {
					display.setVisible(true);
					canExpand = false;
				}
			}
			
		}
		
		public void expand () {
			if (bombs==0) {
				for (int i=-1; i <= 1; i ++) {
					int row = y+i;
					for (int j = -1; j <= 1; j ++) {
						int col = x+j;
						if (row>=0 && row<YTILES && col>=0 && col<XTILES) {
							Tile current = grid[row][col];
							//current = grid[-1][-1];
							if (!current.isOpen&&!current.hasBomb) {
								current.open();
								if (current.canExpand)
									current.expand();
							}
						}
					}
				}
			}
		}
		
		public boolean flag() {
			if (!isFlagged) {
				isFlagged = true;
		        iv.setVisible(true);
				return true;
			}
			return false;
		}
		
		public void unflag() {
			iv.setVisible(false);
			isFlagged = false;
		}
		
		public boolean showBomb() {
			if (hasBomb){
				iv2.setVisible(true);
				if (isFlagged) {
					tile.setFill(Color.YELLOW);
					unflag();
				}
				return true;
			}
			return false;
		}
	}
	
	public static void main(String[] args) {
		launch(args);

	}

	
}

