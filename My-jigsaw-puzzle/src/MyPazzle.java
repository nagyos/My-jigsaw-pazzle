import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import java.io.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.geometry.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

class Pazzle extends ImageView {
    private int number;
    private boolean correctFlg = false;

    public Pazzle(Image img, int num) {
        setImage(img);
        this.number = num;
    }

    public ImageView getImageView() {
        return this;
    }

    public int getNumber() {
        return this.number;
    }

    public void setFlg(boolean b) {
        this.correctFlg = b;
    }

    public boolean getFlg() {
        return this.correctFlg;
    }

}

public class MyPazzle extends Application {
    Scene titleScene, gameScene;
    private int width = 3, height = 3;
    private int tw = width, th = height;
    private int correctCount = 0;
    private double magRatio = 0.5;
    final String[] names = new String[] { "BGMを流す", "正解音を出す", "下絵を表示する" };
    final CheckBox[] cbs = new CheckBox[names.length];
    Image img = new Image("image/sample.png"), simg;
    ImageView imageview = new ImageView(img);
    private double origSizeX, origSizeY, sSizeX, sSizeY;
    ArrayList<ArrayList<Pazzle>> images = new ArrayList<ArrayList<Pazzle>>();
    Pane pane = new Pane(), p = new Pane();
    BorderPane bp = new BorderPane();
    FlowPane fp = new FlowPane(), ffpp = new FlowPane();
    TilePane tp = new TilePane(Orientation.VERTICAL);
    HBox hb = new HBox(), hbTop = new HBox(), sizehb = new HBox();
    VBox vb = new VBox();
    AnchorPane ap = new AnchorPane();
    Group group;
    Rectangle rect;
    ArrayList<Line> line = new ArrayList<Line>();
    Label lb = new Label("残り" + String.valueOf(width * height - correctCount) + "ピース"), timeLabel = new Label("");
    Text fileText;
    Pazzle pazzle;
    MediaPlayer correctMp = new MediaPlayer(new Media(new File("sounds/correct.mp3").toURI().toString())),
            bgm = new MediaPlayer(new Media(new File("sounds/bgm.mp3").toURI().toString()));
    private boolean correctMpFlg = true, startFlg = false;
    double startTime = 0;
    File fl = new File("image/sample.png");
    ImageView view;
    Button startBtn, backBtn, fileButton, btn, playBtn;
    Alert alert = new Alert(AlertType.INFORMATION, "", ButtonType.CLOSE),
            bgmAlert = new Alert(AlertType.INFORMATION, "", ButtonType.CLOSE),
            checkStopAlert = new Alert(AlertType.WARNING, "", ButtonType.NO, ButtonType.YES);
    ChoiceBox<Integer> choiceM = new ChoiceBox<Integer>(
            FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
            choiceN = new ChoiceBox<Integer>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

    EventHandler<MouseEvent> mouseDragDetected = (event) -> this.mouseDetected(event);
    EventHandler<MouseDragEvent> mouseReleased = (event) -> this.mouseReleased(event);
    EventHandler<MouseDragEvent> mouseDrag = (event) -> this.mouseDrag(event);
    EventHandler<MouseEvent> gameStart = (event) -> this.gameStart();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("m × nパズル");
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        Scene webScene = new Scene(browser);
        Stage webStage = new Stage();
        webStage.setScene(webScene);
        backBtn = new Button("タイトルに戻る");
        Label title = new Label("My Pazzle (m × nパズル)");
        title.setFont(Font.font("Serif", 48));
        startBtn = new Button("ゲームを始める");
        startBtn.setFont(Font.font("Serif", 24));
        Text aboutBGM = new Text("使用している音源について >");
        aboutBGM.setOnMouseEntered(e -> aboutBGM.setFill(Color.ORANGE));
        aboutBGM.setOnMouseExited(e -> aboutBGM.setFill(Color.BLACK));
        bgmAlert.setTitle("使用している音源について");
        Text bgms[] = new Text[] { new Text("(BGM)musmus :"), new Text("(正解音)Otologic :") };
        Hyperlink[] bgmLinks = new Hyperlink[] { new Hyperlink("http://musmus.main.jp/"),
                new Hyperlink("https://otologic.jp/") };

        bgmAlert.setHeaderText("使用させて頂いている音源");

        HBox[] bgmhb = new HBox[bgms.length];

        for (int i = 0; i < bgms.length; i++) {
            final String url = bgmLinks[i].getText();
            bgmLinks[i].setOnMouseClicked(ActionEvent -> {
                webStage.show();
                webEngine.load(url);
                System.out.println(url);
            });
            bgmhb[i] = new HBox();
            bgmhb[i].getChildren().addAll(bgms[i], bgmLinks[i]);
        }

        VBox bgmvb = new VBox();
        bgmvb.getChildren().addAll(bgmhb);
        bgmAlert.getDialogPane().contentProperty().set(bgmvb);

        aboutBGM.setOnMouseClicked(ActionEvent -> {
            bgmAlert.showAndWait();
        });
        VBox titleVBox = new VBox(title, startBtn, aboutBGM);
        aboutBGM.setFont(Font.font("Serif", 12));
        titleVBox.setAlignment(Pos.CENTER);
        titleVBox.setPadding(new Insets(10, 10, 10, 10));
        titleVBox.setMargin(title, new Insets(20, 20, 20, 20));
        titleVBox.setMargin(aboutBGM, new Insets(20, 20, 20, 20));

        choiceM.setValue(3);
        choiceN.setValue(3);

        alert.setTitle("おめでとうございます");
        alert.setHeaderText("パズル完成です!");
        // alert.setContentText("パズル完成です!");
        alert.setGraphic(new ImageView(new Image("image/cat.png")));
        bgm.setCycleCount(MediaPlayer.INDEFINITE);
        bgm.play();

        playBtn = new Button("スタート");
        // playBtn.setMaxWidth(Double.MAX_VALUE);
        playBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        playBtn.setFont(Font.font("Serif", 24));
        playBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, gameStart);

        choiceM.setOnAction((ActionEvent) -> {
            th = choiceM.getSelectionModel().getSelectedItem();
            playBtn.setVisible(true);
            hbTop.setBackground(null);
        });
        choiceN.setOnAction((ActionEvent) -> {
            tw = choiceN.getSelectionModel().getSelectedItem();
            playBtn.setVisible(true);
            hbTop.setBackground(null);
        });
        sizehb.getChildren().addAll(new Text("縦 ="), choiceM, new Text(",横  ="), choiceN);
        sizehb.setAlignment(Pos.CENTER);
        for (int i = 0; i < cbs.length; i++) {
            cbs[i] = new CheckBox(names[i]);
            cbs[i].setSelected(true);
            if (i == 2) {
                cbs[i].setSelected(false);
            }
        }
        cbs[0].setOnAction((ActionEvent) -> {
            if (cbs[0].isSelected()) {
                bgm.play();
            } else {
                bgm.stop();
            }
        });
        cbs[1].setOnAction((ActionEvent) -> {
            if (cbs[1].isSelected()) {
                correctMpFlg = true;
            } else {
                correctMpFlg = false;
            }
        });
        cbs[2].setOnAction((ActionEvent) -> {
            if (cbs[2].isSelected()) {
                imageview.toFront();
            } else {
                imageview.toBack();
            }
        });
        hb.getChildren().addAll(cbs);
        hb.setAlignment(Pos.CENTER);
        p.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        btn = new Button("完成図を隠す");
        btn.setOnAction((ActionEvent) -> {
            if (btn.getText().equals("完成図を隠す")) {
                btn.setText("完成図を表示する");
                view.setOpacity(0.0);
            } else {
                btn.setText("完成図を隠す");
                view.setOpacity(1.0);
            }
        });

        this.initialize();

        pane.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, mouseDrag);
        p.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, mouseDrag);

        timeLabel.setText(String.valueOf(System.currentTimeMillis() / 1000));

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setResizable(true);
        fileText = new Text("");
        fileButton = new Button("画像ファイルを選択");
        fileButton.setOnAction((ActionEvent) -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Open Resource File");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"));
            File file = null;
            file = fc.showOpenDialog(primaryStage);
            try {
                if (fl != null) {
                    FileInputStream fis = new FileInputStream(file); // FileInputStreamをインスタンス化
                    Image newImage = new Image(fis); // Imageをインスタンス化
                    double ratio = 1;
                    // 画像サイズが大きい時→縮尺，画像サイズが小さい時→拡大
                    if (newImage.getHeight() > primaryScreenBounds.getHeight() / 2
                            || newImage.getWidth() > primaryScreenBounds.getWidth() / 3) {
                        ratio = Math.min((primaryScreenBounds.getHeight() / 2) / newImage.getHeight(),
                                (primaryScreenBounds.getWidth() / 3) / newImage.getWidth());
                    } else {
                        ratio = Math.max((primaryScreenBounds.getHeight() / 2) / newImage.getHeight(),
                                (primaryScreenBounds.getWidth() / 3) / newImage.getWidth());
                    }

                    removeHandler();
                    fl = file;
                    img = newImage;
                    imageview = new ImageView(img);
                    setMagRatio(ratio);
                    initialize();
                    playBtn.setVisible(true);
                    hbTop.setBackground(null);
                }
            } catch (Exception e) {
                System.out.println("画像インスタンス生成エラー発生");
            }

        });
        vb.setAlignment(Pos.TOP_CENTER);

        vb.setPadding(new Insets(10, 10, 10, 10));
        hbTop.getChildren().addAll(fileButton, backBtn, fileText);
        hbTop.setAlignment(Pos.CENTER_LEFT);
        bp.setTop(hbTop);
        bp.setLeft(pane);
        bp.setCenter(p);
        bp.setRight(vb);

        titleScene = new Scene(titleVBox);
        gameScene = new Scene(bp);

        startBtn.setOnAction((ActionEvent) -> {
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.setScene(gameScene);
            primaryStage.show();
        });

        checkStopAlert.setTitle("タイトルに戻る");
        checkStopAlert.setHeaderText("本当にゲームを終了しますか?");
        checkStopAlert.setContentText(null);
        backBtn.setOnAction((ActionEvent) -> {
            if (startFlg) {
                if (checkStopAlert.showAndWait().get() == ButtonType.YES) {
                    removeHandler();
                    initialize();
                    playBtn.setVisible(true);
                } else {
                    return;
                }
            }
            fileText.setText(null);
            hbTop.setBackground(null);
            primaryStage.setFullScreen(false);
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setWidth(primaryScreenBounds.getWidth());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.setScene(titleScene);
            primaryStage.show();
        });
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    private void gameStart() {
        startFlg = true;
        fileText.setText(null);
        removeHandler();
        width = tw;
        height = th;
        initialize();
        addHandler();
        startTime = System.currentTimeMillis();
        hbTop.setBackground(new Background(new BackgroundFill(Color.GREENYELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void addHandler() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                images.get(i).get(j).addEventHandler(MouseEvent.DRAG_DETECTED, mouseDragDetected);
                images.get(i).get(j).addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, mouseReleased);
            }
        }
    }

    private void removeHandler() {
        playBtn.setVisible(false);
        pane.getChildren().removeAll(imageview, group);
        for (int i = 0; i < height; i++) {
            pane.getChildren().removeAll(images.get(i));
            p.getChildren().removeAll(images.get(i));
        }
        vb.getChildren().removeAll(view, lb, btn, hb, sizehb, playBtn);
    }

    private void initialize() {

        cbs[2].setSelected(false);
        correctCount = 0;
        lb.setText("残り" + String.valueOf(width * height - correctCount) + "ピース");
        setImg(magRatio);
        try {
            simg = new Image(fl.toURI().toURL().toString(), img.getWidth() * magRatio, img.getHeight() * magRatio,
                    false,
                    false);
        } catch (Exception e) {
            System.out.println("画像読み込みに失敗");
        }
        view = new ImageView(simg);
        imageview = new ImageView(simg);
        setLine();
        vb.getChildren().addAll(view, lb, btn, hb, sizehb, playBtn);
        vb.setMargin(hb, new Insets(10, 10, 10, 10));
        vb.setMargin(playBtn, new Insets(15, 15, 15, 15));
        pane.getChildren().addAll(imageview, group);
    }

    // ピースを置くマス
    // マス上の分割線
    private void setLine() {
        rect = new Rectangle(0, 0, img.getWidth() * magRatio, img.getHeight() * magRatio);
        rect.setFill(Color.GRAY);

        group = new Group();
        line.clear();
        int m = height - 1, n = width - 1;
        if (m == 0)
            m = 1;
        // if(n==1)
        for (int i = 0; i < m + n; i++) {
            if (i / m < 1) {
                line.add(new Line(0 + 1, (i + 1) * sSizeY + 1, sSizeX * width - 1, (i + 1) * sSizeY + 1));
            } else {
                line.add(new Line(((i + 1) - m) * sSizeX + 1, 0 + 1, ((i + 1) - m) * sSizeX - 1,
                        sSizeY * height - 1));
            }
            line.get(i).setStrokeWidth(2.0);
            line.get(i).setStroke(Color.BLACK);
            line.get(i).toFront();
        }
        group.getChildren().add(rect);
        group.getChildren().addAll(line);
    }

    // 中央にピースを配置する
    private void setImg(double imgRatio) {
        imageview.setOpacity(0.1);
        imageview.toFront();
        origSizeX = img.getWidth() / width;
        origSizeY = img.getHeight() / height;
        sSizeX = origSizeX * imgRatio;
        sSizeY = origSizeY * imgRatio;

        images.clear();
        boolean[][] posFlg = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            ArrayList<Pazzle> array = new ArrayList<Pazzle>();
            for (int j = 0; j < width; j++) {
                array.add(
                        new Pazzle(
                                new WritableImage(img.getPixelReader(), (int) (j * origSizeX),
                                        (int) (i * origSizeY),
                                        (int) origSizeX,
                                        (int) origSizeY),
                                i * width + j));
                array.get(j).setScaleX(imgRatio);
                array.get(j).setScaleY(imgRatio);
                posFlg[i][j] = true;
            }
            images.add(array);
            p.getChildren().addAll(images.get(i));
        }

        int count = 0;
        // 中央（緑）に配置
        while (count != width * height) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            if (posFlg[y][x]) {
                posFlg[y][x] = false;
                count++;
                images.get(y).get(x).toFront();
                images.get(y).get(x).setTranslateY(sSizeY * (count % height) + 10 * (count % height));
            }
        }

    }

    private void setMagRatio(double ratio) {
        this.magRatio = ratio;
    }

    private void mouseDetected(MouseEvent e) {
        // アクティブサークルの設定
        pazzle = (Pazzle) e.getSource();
        if (pazzle.getFlg()) {
            return;
        }
        correctMp.stop();
        gameScene.setCursor(Cursor.CLOSED_HAND);
        pazzle.toFront();
        pazzle.startFullDrag();
        e.consume();
    }

    // private void mouseReleased(MouseDragEvent e) {
    private void mouseReleased(MouseEvent e) {
        System.out.println("mouse released");
        // // サークルの色とマウスの形状を元に戻す
        gameScene.setCursor(Cursor.DEFAULT);

        int x = (int) (e.getSceneX() / sSizeX);
        int y = (int) (e.getSceneY() / sSizeY);
        System.out.println("release  " + e.getX() + " " + e.getY());
        System.out.println("release scene " + e.getSceneX() + " " + e.getSceneY());
        System.out.println(pazzle.getTranslateX() + " " + pazzle.getTranslateY());
        // System.out.println(e.getSceneX() + " " + e.getSceneY());
        // System.out.println("x:" + x + " y:" + y + "isContain:" +
        // pane.getChildren().contains(pazzle));
        if (pazzle.getNumber() == y * width + x && pane.getChildren().contains(pazzle)) {
            correctCount++;
            lb.setText("残り" + String.valueOf(width * height - correctCount) + "ピース");
            pazzle.setFlg(true);
            pazzle.removeEventHandler(MouseEvent.DRAG_DETECTED, mouseDragDetected);
            pazzle.removeEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, mouseReleased);
            correctMp.stop();
            if (correctMpFlg) {
                correctMp.play();
            }

            int prePazzleX = (int) (pazzle.getTranslateX());
            int prePazzleY = (int) (pazzle.getTranslateY());
            int preMouseSceneX = (int) (e.getSceneX());
            int preMouseSceneY = (int) (e.getSceneY());
            /*
             * TODO: 差分調整
             */
            System.out.println("正解");
            System.out.println("sSizeX:" + sSizeX + " sSizeY:" + sSizeY);
            System.out.println("x:" + x + " y:" + y);
            System.out.println(prePazzleX + " " + prePazzleY);
            System.out.println(preMouseSceneX + " " + preMouseSceneY);
            System.out.println("pazzle側の(0,0): " + (preMouseSceneX - prePazzleX) + " " + (preMouseSceneY - prePazzleY));
            System.out.println((x * sSizeX - (preMouseSceneX - prePazzleX) / 2) + " "
                    + (y * sSizeY - (preMouseSceneY - prePazzleY - 27) / 2));
            pazzle.setTranslateX(x * sSizeX - (preMouseSceneX - prePazzleX) / 2);
            pazzle.setTranslateY(y * sSizeY - (preMouseSceneY - prePazzleY - 27) / 2);

            if (correctCount == width * height) {
                correctCount = 0;
                lb.setText("完成です!");
                alert.setContentText(
                        "かかった時間は" + String.valueOf((int) (System.currentTimeMillis() - startTime) / 1000) + "秒です");
                startFlg = false;
                alert.showAndWait();
                playBtn.setVisible(true);
                hbTop.setBackground(null);
            }
        }
        e.consume();

    }

    private void mouseDrag(MouseEvent e) {
        Pane p = (Pane) e.getSource();
        if (p.getLayoutBounds().contains(e.getX(), e.getY()) == false) {
            return;
        }
        if (p.getChildren().contains(pazzle) == false) {
            p.getChildren().add(pazzle);
        }
        pazzle.setTranslateX(e.getX() - origSizeX / 2);
        pazzle.setTranslateY(e.getY() - origSizeY / 2);
        System.out.println("drag  " + e.getX() + " " + e.getY());
        // System.out.println("drag " + e.getSceneX() + " " + e.getSceneY());

    }

}
