package mineSweeper;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;


public class MainMenu extends Application {

	private GameMenu gameMenu;
	private Stage primaryStage;
	private final int W = 1280;
	private final int H = 680;
	
	public void start(Stage arg0) throws Exception {
		Pane root1 = new Pane ();
		root1.setPrefSize(1280, 680);
		InputStream is = Files.newInputStream(Paths.get("resources/images/cover picture.jpg"));
		Image img = new Image(is);
		is.close();
		
		
		ImageView imgView = new ImageView (img);
		Label label = new Label ("Press any key to continue");
		label.setTranslateX(500);
		label.setTranslateY(500);
		label.setFont(label.getFont().font(25));
		label.setTextFill(Color.WHITE);
		
		gameMenu = new GameMenu ();
		gameMenu.setVisible(false);
		
		root1.getChildren().addAll(imgView,label,gameMenu);
		
		Scene scene1 = new Scene (root1);
		
		scene1.setOnKeyPressed(event ->{
			FadeTransition fade = new FadeTransition (Duration.seconds(0.5),gameMenu);
			fade.setToValue(0);
			fade.setToValue(1);
			
			label.setVisible(false);
			gameMenu.setVisible(true);
			fade.play();
		});
		
		primaryStage = new Stage ();
		primaryStage.setScene(scene1);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch (args);
	}
	
	public class GameMenu extends Parent{
		
		public GameMenu() {
			HBox menu0 = new HBox (350);
			
			menu0.setTranslateX(150);
			menu0.setTranslateY(500);
			
			MenuButton newGame = new MenuButton ("New Game", 300, 100);
			newGame.setOnMouseClicked(event ->{
				SizeMenu sizeMenu = new SizeMenu ();
				getChildren().add(sizeMenu);
				MineSweeperApp mineSweeper = new MineSweeperApp();
				Scene scene = new Scene(mineSweeper.createContent());
				primaryStage.setScene(scene);
			});
			MenuButton exit = new MenuButton ("Exit", 300, 100);
			exit.setOnMouseClicked(event -> {
				System.exit(0);
			});
			
			menu0.getChildren().addAll(newGame, exit);
			
			Rectangle background = new Rectangle (1280, 680);
			background.setOpacity(0.4);
			background.setFill(Color.AZURE);
			
			getChildren().addAll(background,menu0);
		}
		
	}
	
	public static class SizeMenu extends Parent{
		private Rectangle box;
		private Label prompt;
		private TextField row;
		private TextField col;
		
		public SizeMenu() {
			box = new Rectangle (500,375);
			box.setFill(Color.BLACK);
			box.setOpacity(0.4);
			box.setEffect(new GaussianBlur(3.5));
			box.setTranslateX(420);
			box.setTranslateY(182);
			getChildren().add(box);
			
			prompt = new Label ("Enter Game Size");
			prompt.setTranslateX(425);
			prompt.setTranslateY(187);
		}
	}
	
	public static class MenuButton extends StackPane{
		private Text text;
		
		public MenuButton (String name, int x, int y) {
			text = new Text (name);
			text.setFont(text.getFont().font(null,FontWeight.BOLD,FontPosture.REGULAR,30));
			text.setFill(Color.BLACK);
			
			Rectangle button = new Rectangle (x,y);
			button.setOpacity(0.6);
			button.setFill(Color.WHITE);
			button.setEffect(new GaussianBlur(3.5));
			
			setAlignment(Pos.CENTER);
			//setRotate(-0.5);
			getChildren().addAll(button, text);
			
			this.setOnMouseEntered(event -> {
				button.setTranslateX(10);
				text.setTranslateX(10);
				button.setFill(Color.GREY);
				text.setFill(Color.YELLOW);
			});
			
			this.setOnMouseExited(event -> {
				button.setTranslateX(0);
				text.setTranslateX(0);
				button.setFill(Color.WHITE);
				text.setFill(Color.BLACK);
			});
			
			DropShadow drop = new DropShadow (50, Color.DARKGREY);
			drop.setInput(new Glow());
			
			this.setOnMousePressed(event ->{
				setEffect(drop);
			});
			
			this.setOnMouseReleased(event ->{
				setEffect(null);
			});
		}
	}
}

