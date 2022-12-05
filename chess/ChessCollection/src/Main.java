import javax.swing.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
            System.out.println("欢迎使用本游戏，请选择你想玩的游戏");
            System.out.println("1: 象棋  2:五子棋 3:退出");
            Scanner sc = new Scanner(System.in);
            int i = sc.nextInt();
            switch (i){
                case 1 :new GameFrame();break;
                case 2 :
                    MyFrame myFrame = new MyFrame();
                    myFrame.setVisible(true); // 设置窗口为可见
                    myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口关闭操作属性
                    break;
                case 3 :  System.exit(0);
            }




    }
}