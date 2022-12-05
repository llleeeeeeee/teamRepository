/*
象棋
*/
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;


public class GamePanel extends JPanel implements MouseListener,MouseMotionListener,ActionListener
{
    private static final long serialVersionUID = 1353029267562430095L;

    /** 游戏逻辑 */
    private GameLogic gameLogic;

    /** 网格行数 */
    final int gridRows = 10;

    /** 网格列数 */
    final int gridColumns = 9;

    /** 网格尺寸 */
    final int gridSize = 52;

    /** 网格宽度 */
    final int gridsWidth = gridSize * (gridColumns - 1);

    /** 网格高度 */
    final int gridsHeight = gridSize * (gridRows - 1);

    /** 网格左上角X坐标 */
    final int gridsLeftX = 42;

    /** 网格左上角Y坐标 */
    final int gridsTopY = 42;

    /** 象棋面板（要分层，否则棋盘标签压棋子标签） */
    private JLayeredPane panelChess;

    /** 棋盘标签 */
    JLabel labelChessBorad;

    /** 棋盘图片 */
    private ImageIcon imageIconChessBoard;

    /** 棋盘状态信息（-1->无棋，其他数字->棋子信息数组的下标） */
    int[][] chessBoradState = new int[gridRows][gridColumns];

    /** 棋子尺寸 */
    final int chessSize = 44;

    /** 棋子标签 */
    JLabel[] labelChess = new JLabel[32];

    /**
     * 棋子信息数组<br>
     * index -> 棋子索引<br>
     * color -> 棋子颜色（0-黑棋，255-红棋）<br>
     * type -> 棋子类型（rook、horse、elephant、guard、king、cannon、soldier）<br>
     * name -> 棋子名字（黑车、黑马、黑象、黑士、黑将、黑炮、黑卒、红兵、红炮、红车、红马、红相、红仕、红帅）<br>
     * number -> 棋子小标号（如卒1卒2中的1与2）<br>
     * direction -> 棋子方向（T-上方，B-下方）<br>
     * oldOldRow -> 棋子大上次行位置<br>
     * oldOldColumn -> 棋子大上次列位置<br>
     * oldRow -> 棋子上次行位置<br>
     * oldColumn -> 棋子上次列位置<br>
     * newRow -> 棋子本次行位置<br>
     * newColumn -> 棋子本次列位置<br>
     * dead -> 棋子是否处于死亡状态（T-死亡，F-活着）<br>
     * oldEatIndex -> 上次被其吃棋子下标<br>
     * eatIndex -> 本次被其吃棋子下标<br>
     */
    @SuppressWarnings("unchecked")	//数组不支持泛型
            Map<String,String>[] mapChess = new Map[32];

    /** 棋子图片 */
    private ImageIcon[] imageIconChess = new ImageIcon[14];

    /** 红棋标识 */
    final int REDCHESS = 255;

    /** 黑棋标识 */
    final int BLACKCHESS = 0;

    /** 对战方式（0-人机对战，1-人人对战） */
    int fightType ;

    /** 先手选择（1-玩家先手，2-电脑先手） */
    int playFirst ;

    /** 红黑选择（255-玩家执红，0-玩家执黑） */
    int chessColor ;

    /** 电脑棋子颜色 */
    int computerChess = -1 ;

    /** 玩家棋子颜色 */
    int playerChess = -1 ;

    /** 红棋悔棋数 */
    int redUndoNum = 30;

    /** 黑棋悔棋数 */
    int blackUndoNum = 30;

    /** 全部下棋信息 */
    List<Map<String,String>> listChess = new ArrayList<Map<String,String>>();

    /** 移动线路图信息 */
    List<Map<String,Integer>> listMove = new ArrayList<Map<String,Integer>>();

    /** 组合框控件 */
    private JComboBox<String> jcb_fightType,jcb_playFirst,jcb_chessColor;

    /** 按钮控件 */
    private JButton jb_new,jb_undo,jb_surrender;

    /** 标签控件 */
    JLabel jlb_blackUndoText,jlb_blackStateText,jlb_redUndoText,jlb_redStateText;

    /** Logo图片 */
    private ImageIcon imageIconLogo;

    /** 是否第一次点击 */
    boolean isFirstClick = true;

    /** 第一次点击棋子 */
    Map<String,String> firstClickChess = null;

    /**
     * 落子指示器<br>
     * row -> 行坐标<br>
     * column -> 列坐标<br>
     * show -> 是否显示（0-不显示，1-显示）<br>
     * color -> 颜色（0-黑，255-红）<br>
     */
    Map<String,Integer> mapPointerChess = new HashMap<String,Integer>();

    /**
     * 移动指示器<br>
     * row -> 行坐标<br>
     * column -> 列坐标<br>
     * show -> 是否显示（0-不显示，1-显示）<br>
     * color -> 颜色（-1-默认，0-黑，255-红）<br>
     */
    Map<String,Integer> mapPointerMove = new HashMap<String,Integer>();

    /** 判断游戏是否结束（true-结束，false-未结束） */
    boolean isGameOver = true;

    /**
     * 功能：构造函数<br>
     */
    public GamePanel()
    {
        //与主窗口大小保持一致，并设置背景色（去掉菜单高度）
        this.setSize(666,560);
        this.setLayout(null);

        //设置象棋面板
        this.panelChess = new JLayeredPane();
        this.panelChess.setBounds(0,0,504,558);
        this.panelChess.setLayout(null);
        this.add(this.panelChess);

        //加载图片
        this.loadImage();

        //设置棋盘背景图片
        this.labelChessBorad = new JLabel();
        this.labelChessBorad.setBounds(0,0,this.panelChess.getWidth(),this.panelChess.getHeight());
        this.labelChessBorad.setIcon(this.imageIconChessBoard);
        this.labelChessBorad.addMouseListener(this);
        this.labelChessBorad.addMouseMotionListener(this);
        this.panelChess.add(this.labelChessBorad,JLayeredPane.DEFAULT_LAYER);	//最底层

        //建立棋子标签
        this.createChess();

        //右边功能区布局
        this.option();

        //游戏逻辑
        this.gameLogic = new GameLogic(this);

        //初始化游戏
        this.initGame();
    }

    /**
     * 功能：加载图片<br>
     * 备注：考虑Jar包问题，所以图片路径用URL格式<br>
     */
    private void loadImage()
    {
        try
        {
            //棋盘图片
            this.imageIconChessBoard = new ImageIcon(new ImageIcon(this.getClass().getResource("main.gif")).getImage().getScaledInstance(this.panelChess.getWidth(),this.panelChess.getHeight(),Image.SCALE_SMOOTH));	//缩放图片来适应标签大小
            //棋子图片
           /*for(int i=0;i<14;i++)
            {
                this.imageIconChess[i] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess1.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));	//缩放图片来适应标签大小
            }
           */
            //黑将
            this.imageIconChess[0] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess1.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑士
            this.imageIconChess[1] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess2.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑马
            this.imageIconChess[2] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess3.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑象
            this.imageIconChess[3] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess4.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑车
            this.imageIconChess[4] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess5.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑炮
            this.imageIconChess[5] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess6.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //黑卒
            this.imageIconChess[6] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess7.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红帅
            this.imageIconChess[7] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess8.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红士
            this.imageIconChess[8] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess9.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红马
            this.imageIconChess[9] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess10.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红象
            this.imageIconChess[10] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess11.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红车
            this.imageIconChess[11] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess12.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红炮
            this.imageIconChess[12] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess13.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));
            //红卒
            this.imageIconChess[13] = new ImageIcon(new ImageIcon(this.getClass().getResource("chess14.gif")).getImage().getScaledInstance(this.chessSize,this.chessSize,Image.SCALE_SMOOTH));

            //Logo图片
            this.imageIconLogo = new ImageIcon(new ImageIcon(this.getClass().getResource("/resource/chess/logo.png")).getImage().getScaledInstance(100,50,Image.SCALE_SMOOTH));	//缩放图片来适应标签大小
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 功能：初始化游戏<br>
     */
    private void initGame()
    {
        //重新设置参数
        this.isGameOver = true;

        //清空下棋与移动线路图列表
        this.listChess.clear();
        this.listMove.clear();

        //指示器初始化
        this.mapPointerChess.put("row",-1);
        this.mapPointerChess.put("column",-1);
        this.mapPointerChess.put("show",0);
        this.mapPointerChess.put("color",-1);
        this.mapPointerMove.put("row",-1);
        this.mapPointerMove.put("column",-1);
        this.mapPointerMove.put("show",0);
        this.mapPointerMove.put("color",-1);

        //对战方式
        if("人人对战".equals(this.jcb_fightType.getSelectedItem().toString()))
        {
            this.fightType = 1;
        }
        else
        {
            this.fightType = 0;
        }

        //先手选择
        if("电脑先手".equals(this.jcb_playFirst.getSelectedItem().toString()))
        {
            this.playFirst = 2;
        }
        else
        {
            this.playFirst = 1;
        }

        //红黑选择
        if("玩家执黑".equals(this.jcb_chessColor.getSelectedItem().toString()))
        {
            this.chessColor = this.BLACKCHESS;
        }
        else
        {
            this.chessColor = this.REDCHESS;
        }

        //电脑与玩家棋子颜色
        if(this.fightType == 0)
        {
            if(this.chessColor == this.BLACKCHESS)
            {
                this.playerChess = this.BLACKCHESS;
                this.computerChess = this.REDCHESS;
            }
            else
            {
                this.playerChess = this.REDCHESS;
                this.computerChess = this.BLACKCHESS;
            }
        }

        //悔棋数初始化
        this.redUndoNum = 30;
        this.blackUndoNum = 30;

        //设置控件状态
        this.setComponentState(false);

        //初始化棋子（默认我方棋子在下方）
        this.initChess();
    }

    /**
     * 功能：布局棋子<br>
     */
    private void initChess()
    {
        //先按默认设置棋子信息（玩家执红：红方在下，黑方在上）
        for(int index=0;index<this.mapChess.length;index++)
        {
            this.mapChess[index].put("index",Integer.toString(index));
            this.mapChess[index].put("oldOldRow","-1");
            this.mapChess[index].put("oldOldColumn","-1");
            this.mapChess[index].put("oldRow","-1");
            this.mapChess[index].put("oldColumn","-1");
            this.mapChess[index].put("dead","F");
            this.mapChess[index].put("oldEatIndex","-1");
            this.mapChess[index].put("eatIndex","-1");
            if(index < 9)			//黑车马象士将士象马车
            {
                this.mapChess[index].put("direction","T");	//上方
                this.mapChess[index].put("newRow","0");
                this.mapChess[index].put("newColumn",Integer.toString(index));
            }
            else if(index == 9)		//黑炮1
            {
                this.mapChess[index].put("direction","T");
                this.mapChess[index].put("newRow","2");
                this.mapChess[index].put("newColumn","1");
            }
            else if(index == 10)	//黑炮2
            {
                this.mapChess[index].put("direction","T");
                this.mapChess[index].put("newRow","2");
                this.mapChess[index].put("newColumn","7");
            }
            else if(index > 10 && index < 16)	//黑卒n
            {
                this.mapChess[index].put("direction","T");
                this.mapChess[index].put("newRow","3");
                this.mapChess[index].put("newColumn",Integer.toString(2 * index - 22));
            }
            else if(index >= 16 && index < 21)	//红兵n
            {
                this.mapChess[index].put("direction","B");	//下方
                this.mapChess[index].put("newRow","6");
                this.mapChess[index].put("newColumn",Integer.toString(2 * index - 32));
            }
            else if(index == 21)		//红炮1
            {
                this.mapChess[index].put("direction","B");
                this.mapChess[index].put("newRow","7");
                this.mapChess[index].put("newColumn","1");
            }
            else if(index == 22)		//红炮2
            {
                this.mapChess[index].put("direction","B");
                this.mapChess[index].put("newRow","7");
                this.mapChess[index].put("newColumn","7");
            }
            else if(index > 22 && index < 32)	//红车马相仕帅仕相马车
            {
                this.mapChess[index].put("direction","B");
                this.mapChess[index].put("newRow","9");
                this.mapChess[index].put("newColumn",Integer.toString(index - 23));
            }
        }

        //如果玩家执黑则坐标反过来
        if(this.chessColor == this.BLACKCHESS)
        {
            //棋子信息对调（行变abs(9-行)，列不变）
            for(int index=0;index<this.mapChess.length;index++)
            {
                int row = Integer.parseInt(this.mapChess[index].get("newRow"));
                this.mapChess[index].put("newRow",Integer.toString(Math.abs(9 - row)));
                if("T".equals(this.mapChess[index].get("direction")))
                {
                    this.mapChess[index].put("direction","B");
                }
                else
                {
                    this.mapChess[index].put("direction","T");
                }
            }
        }

        //清空棋盘状态信息
        for(int row=0;row<this.chessBoradState.length;row++)
        {
            for(int column=0;column<this.chessBoradState[0].length;column++)
            {
                this.chessBoradState[row][column] = -1;
            }
        }
        //再根据棋子状态信息设置棋盘状态信息
        for(int index=0;index<this.mapChess.length;index++)
        {
            int row = Integer.parseInt(this.mapChess[index].get("newRow"));
            int column = Integer.parseInt(this.mapChess[index].get("newColumn"));
            this.chessBoradState[row][column] = index;
        }

        //重新布局棋子（X->列，Y->行）
        for(int index=0;index<this.mapChess.length;index++)
        {
            int row = Integer.parseInt(this.mapChess[index].get("newRow"));
            int column = Integer.parseInt(this.mapChess[index].get("newColumn"));
            this.labelChess[index].setBounds(this.gridsLeftX + column * this.gridSize - this.chessSize/2,this.gridsTopY + row * this.gridSize  - this.chessSize/2,this.chessSize,this.chessSize);
        }

    }

    /**
     * 功能：设置控件状态<br>
     * 参数：true-新开局；false-未开局<br>
     */
    public void setComponentState(boolean _flag)
    {
        if(_flag)	//新游戏已经开始了
        {
            this.jcb_fightType.setEnabled(false);
            this.jcb_playFirst.setEnabled(false);
            this.jcb_chessColor.setEnabled(false);
            this.jb_new.setEnabled(false);
            this.jb_undo.setEnabled(true);
            this.jb_surrender.setEnabled(true);
        }
        else	//新游戏还未开始
        {
            this.jcb_fightType.setEnabled(true);
            this.jcb_playFirst.setEnabled(true);
            this.jcb_chessColor.setEnabled(true);
            this.jb_new.setEnabled(true);
            this.jb_undo.setEnabled(false);
            this.jb_surrender.setEnabled(false);
        }
    }

    /**
     * 功能：建立棋子标签<br>
     */
    private void createChess()
    {
        for(int index=0;index<this.labelChess.length;index++)
        {
            this.labelChess[index] = new JLabel();
            this.labelChess[index].setName(Integer.toString(index));
            this.mapChess[index] = new HashMap<String,String>();
            if(index == 0)			//黑车1
            {
                this.labelChess[index].setIcon(this.imageIconChess[4]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","rook");
                this.mapChess[index].put("name","黑车");
                this.mapChess[index].put("number","1");
            }
            else if(index == 8)		//黑车2
            {
                this.labelChess[index].setIcon(this.imageIconChess[4]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","rook");
                this.mapChess[index].put("name","黑车");
                this.mapChess[index].put("number","2");
            }
            else if(index == 1)		//黑马1
            {
                this.labelChess[index].setIcon(this.imageIconChess[3]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","horse");
                this.mapChess[index].put("name","黑马");
                this.mapChess[index].put("number","1");
            }
            else if(index == 7)		//黑马2
            {
                this.labelChess[index].setIcon(this.imageIconChess[3]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","horse");
                this.mapChess[index].put("name","黑马");
                this.mapChess[index].put("number","2");
            }
            else if(index == 2)		//黑象1
            {
                this.labelChess[index].setIcon(this.imageIconChess[2]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","elephant");
                this.mapChess[index].put("name","黑象");
                this.mapChess[index].put("number","1");
            }
            else if(index == 6)		//黑象2
            {
                this.labelChess[index].setIcon(this.imageIconChess[2]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","elephant");
                this.mapChess[index].put("name","黑象");
                this.mapChess[index].put("number","2");
            }
            else if(index == 3)		//黑士1
            {
                this.labelChess[index].setIcon(this.imageIconChess[1]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","guard");
                this.mapChess[index].put("name","黑士");
                this.mapChess[index].put("number","1");
            }
            else if(index == 5)		//黑士2
            {
                this.labelChess[index].setIcon(this.imageIconChess[1]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","guard");
                this.mapChess[index].put("name","黑士");
                this.mapChess[index].put("number","2");
            }
            else if(index == 4)		//黑将
            {
                this.labelChess[index].setIcon(this.imageIconChess[0]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","king");
                this.mapChess[index].put("name","黑将");
                this.mapChess[index].put("number","");
            }
            else if(index == 9 || index == 10)		//黑炮n
            {
                this.labelChess[index].setIcon(this.imageIconChess[5]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","cannon");
                this.mapChess[index].put("name","黑炮");
                this.mapChess[index].put("number",Integer.toString(index - 8));
            }
            else if(index > 10 && index < 16)	//黑卒n
            {
                this.labelChess[index].setIcon(this.imageIconChess[6]);
                this.mapChess[index].put("color","0");
                this.mapChess[index].put("type","soldier");
                this.mapChess[index].put("name","黑卒");
                this.mapChess[index].put("number",Integer.toString(index - 10));
            }
            else if(index >= 16 && index < 21)	//红兵n
            {
                this.labelChess[index].setIcon(this.imageIconChess[13]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","soldier");
                this.mapChess[index].put("name","红兵");
                this.mapChess[index].put("number",Integer.toString(index - 15));
            }
            else if(index == 21 || index == 22)		//红炮n
            {
                this.labelChess[index].setIcon(this.imageIconChess[12]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","cannon");
                this.mapChess[index].put("name","红炮");
                this.mapChess[index].put("number",Integer.toString(index - 20));
            }
            else if(index == 23)		//红车1
            {
                this.labelChess[index].setIcon(this.imageIconChess[11]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","rook");
                this.mapChess[index].put("name","红车");
                this.mapChess[index].put("number","1");
            }
            else if(index == 31)		//红车2
            {
                this.labelChess[index].setIcon(this.imageIconChess[11]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","rook");
                this.mapChess[index].put("name","红车");
                this.mapChess[index].put("number","2");
            }
            else if(index == 24)		//红马1
            {
                this.labelChess[index].setIcon(this.imageIconChess[10]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","horse");
                this.mapChess[index].put("name","红马");
                this.mapChess[index].put("number","1");
            }
            else if(index == 30)		//红马2
            {
                this.labelChess[index].setIcon(this.imageIconChess[10]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","horse");
                this.mapChess[index].put("name","红马");
                this.mapChess[index].put("number","2");
            }
            else if(index == 25)		//红相1
            {
                this.labelChess[index].setIcon(this.imageIconChess[9]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","elephant");
                this.mapChess[index].put("name","红相");
                this.mapChess[index].put("number","1");
            }
            else if(index == 29)		//红相2
            {
                this.labelChess[index].setIcon(this.imageIconChess[9]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","elephant");
                this.mapChess[index].put("name","红相");
                this.mapChess[index].put("number","2");
            }
            else if(index == 26)		//红仕1
            {
                this.labelChess[index].setIcon(this.imageIconChess[8]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","guard");
                this.mapChess[index].put("name","红仕");
                this.mapChess[index].put("number","1");
            }
            else if(index == 28)		//红仕2
            {
                this.labelChess[index].setIcon(this.imageIconChess[8]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","guard");
                this.mapChess[index].put("name","红仕");
                this.mapChess[index].put("number","2");
            }
            else if(index == 27)		//红帅
            {
                this.labelChess[index].setIcon(this.imageIconChess[7]);
                this.mapChess[index].put("color","255");
                this.mapChess[index].put("type","king");
                this.mapChess[index].put("name","红帅");
                this.mapChess[index].put("number","");
            }
            this.labelChess[index].addMouseListener(this);
            this.labelChess[index].addMouseMotionListener(this);
            this.panelChess.add(this.labelChess[index],JLayeredPane.DRAG_LAYER);	//最高层
        }

    }

    /**
     * 功能：右边功能区布局<br>
     */
    private void option()
    {
        //logo图片
        JLabel labelLogo = new JLabel(this.imageIconLogo);
        labelLogo.setBounds(this.panelChess.getWidth() + 20,4,100,50);
        this.add(labelLogo);
        //对战方式
        JLabel jlb_fightType = new JLabel("对战方式：");
        jlb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        jlb_fightType.setBounds(this.panelChess.getWidth() + 22,60,100,24);
        this.add(jlb_fightType);
        this.jcb_fightType = new JComboBox<String>(new String[]{"人机对战","人人对战"});
        this.jcb_fightType.setBackground(Color.WHITE);
        this.jcb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jcb_fightType.setBounds(this.panelChess.getWidth() + 22,90,100,24);
        this.add(this.jcb_fightType);
        //谁先手
        JLabel jlb_playFirst = new JLabel("先手选择：");
        jlb_playFirst.setBounds(this.panelChess.getWidth() + 22,120,100,24);
        jlb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.add(jlb_playFirst);
        this.jcb_playFirst = new JComboBox<String>(new String[]{"玩家先手","电脑先手"});
        this.jcb_playFirst.setBackground(Color.WHITE);
        this.jcb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jcb_playFirst.setBounds(this.panelChess.getWidth() + 22,150,100,24);
        this.add(this.jcb_playFirst);
        //谁执红
        JLabel jlb_chessColor = new JLabel("红黑选择：");
        jlb_chessColor.setBounds(this.panelChess.getWidth() + 22,180,100,24);
        jlb_chessColor.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.add(jlb_chessColor);
        this.jcb_chessColor = new JComboBox<String>(new String[]{"玩家执红","玩家执黑"});
        this.jcb_chessColor.setBackground(Color.WHITE);
        this.jcb_chessColor.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jcb_chessColor.setBounds(this.panelChess.getWidth() + 22,210,100,24);
        this.jcb_chessColor.addActionListener(this);
        this.jcb_chessColor.setActionCommand("chessColor");
        this.add(this.jcb_chessColor);
        //按钮
        this.jb_new = new JButton("开始游戏");
        this.jb_new.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jb_new.setBounds(this.panelChess.getWidth() + 22,250,100,30);
        this.jb_new.setActionCommand("newGame");
        this.jb_new.addActionListener(this);
        this.add(this.jb_new);
        this.jb_undo = new JButton("我要悔棋");
        this.jb_undo.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jb_undo.setBounds(this.panelChess.getWidth() + 22,295,100,30);
        this.jb_undo.setActionCommand("undo");
        this.jb_undo.addActionListener(this);
        this.jb_undo.setEnabled(false);
        this.add(this.jb_undo);
        this.jb_surrender = new JButton("我认输了");
        this.jb_surrender.setFont(new Font("微软雅黑",Font.PLAIN,12));
        this.jb_surrender.setBounds(this.panelChess.getWidth() + 22,340,100,30);
        this.jb_surrender.setActionCommand("surrender");
        this.jb_surrender.addActionListener(this);
        this.jb_surrender.setEnabled(false);
        this.add(this.jb_surrender);
        //红棋提示
        JPanel groupBoxRed = new JPanel();
        groupBoxRed.setLayout(null);
        groupBoxRed.setBackground(this.getBackground());
        groupBoxRed.setBounds(this.panelChess.getWidth() + 22,380,100,80);
        groupBoxRed.setBorder(BorderFactory.createTitledBorder("红棋"));
        this.add(groupBoxRed);
        JLabel jlb_whiteUndo = new JLabel("悔棋：");
        jlb_whiteUndo.setFont(new Font("微软雅黑",Font.PLAIN,12));
        jlb_whiteUndo.setBounds(10,16,40,30);
        groupBoxRed.add(jlb_whiteUndo);
        this.jlb_redUndoText = new JLabel("剩"+Integer.toString(this.redUndoNum)+"次");
        this.jlb_redUndoText.setFont(new Font("微软雅黑",Font.BOLD,12));
        this.jlb_redUndoText.setForeground(Color.darkGray);
        this.jlb_redUndoText.setBounds(44,16,50,30);
        groupBoxRed.add(this.jlb_redUndoText);
        JLabel jlb_whiteState = new JLabel("状态：");
        jlb_whiteState.setFont(new Font("微软雅黑",Font.PLAIN,12));
        jlb_whiteState.setBounds(10,44,40,30);
        groupBoxRed.add(jlb_whiteState);
        this.jlb_redStateText = new JLabel("未开始");
        this.jlb_redStateText.setFont(new Font("微软雅黑",Font.BOLD,12));
        this.jlb_redStateText.setForeground(Color.darkGray);
        this.jlb_redStateText.setBounds(44,44,50,30);
        groupBoxRed.add(this.jlb_redStateText);
        //黑棋提示
        JPanel groupBoxBlack = new JPanel();
        groupBoxBlack.setLayout(null);
        groupBoxBlack.setBackground(this.getBackground());
        groupBoxBlack.setBounds(this.panelChess.getWidth() + 22,465,100,80);
        groupBoxBlack.setBorder(BorderFactory.createTitledBorder("黑棋"));
        this.add(groupBoxBlack);
        JLabel jlb_blackUndo = new JLabel("悔棋：");
        jlb_blackUndo.setFont(new Font("微软雅黑",Font.PLAIN,12));
        jlb_blackUndo.setBounds(10,16,40,30);
        groupBoxBlack.add(jlb_blackUndo);
        this.jlb_blackUndoText = new JLabel("剩"+Integer.toString(this.blackUndoNum)+"次");
        this.jlb_blackUndoText.setFont(new Font("微软雅黑",Font.BOLD,12));
        this.jlb_blackUndoText.setForeground(Color.darkGray);
        this.jlb_blackUndoText.setBounds(44,16,50,30);
        groupBoxBlack.add(this.jlb_blackUndoText);
        JLabel jlb_blackState = new JLabel("状态：");
        jlb_blackState.setFont(new Font("微软雅黑",Font.PLAIN,12));
        jlb_blackState.setBounds(10,44,40,30);
        groupBoxBlack.add(jlb_blackState);
        this.jlb_blackStateText = new JLabel("未开始");
        this.jlb_blackStateText.setFont(new Font("微软雅黑",Font.BOLD,12));
        this.jlb_blackStateText.setForeground(Color.darkGray);
        this.jlb_blackStateText.setBounds(44,44,50,30);
        groupBoxBlack.add(this.jlb_blackStateText);
    }

    /**
     * 功能：绘图<br>
     */
    @Override
    public void paint(Graphics g)
    {
        //调用父类,让其做一些事前的工作，如刷新屏幕等
        super.paint(g);

        //因为要画一些特殊效果，所以要用Graphics2D
        Graphics2D g2D = (Graphics2D)g;

        //开始画棋盘
        String[] tip = {" 0"," 1"," 2"," 3"," 4"," 5"," 6"," 7"," 8"," 9"};	//行列坐标，有利于编程是查看定位
        g2D.setColor(Color.black);
        for(int row=0;row<this.gridRows;row++)
        {
            //g2D.drawLine(this.gridsLeftX,this.gridsTopY + row * this.gridSize,this.gridsLeftX + this.gridsWidth,this.gridsTopY + row * this.gridSize);
            g2D.drawString(tip[row],this.gridsLeftX - 30,this.gridsTopY + 4 + row * this.gridSize);
        }
        for(int column=0;column<this.gridColumns;column++)
        {
            //g2D.drawLine(this.gridsLeftX + column * this.gridSize,this.gridsTopY,this.gridsLeftX + column * this.gridSize,this.gridsTopY + this.gridsHeight);
            g2D.drawString(tip[column],this.gridsLeftX - 2 + column * this.gridSize,this.gridsTopY - 20);
        }

        //画移动指示器
        if(this.mapPointerMove.get("show") == 1)
        {
            if(this.mapPointerMove.get("color") == this.BLACKCHESS)
            {
                g2D.setColor(Color.BLACK);
            }
            else if(this.mapPointerMove.get("color") == this.REDCHESS)
            {
                g2D.setColor(Color.RED);
            }
            else
            {
                g2D.setColor(Color.GREEN);
            }
            g2D.setStroke(new BasicStroke(3.5f));
            //先以交叉点为中心取到指示器周围的4个角坐标
            //中心点坐标
            int x = this.gridsLeftX + this.mapPointerMove.get("column") * this.gridSize;
            int y = this.gridsTopY + this.mapPointerMove.get("row") * this.gridSize;
            //左上角坐标，并向下向右画线
            int x1 = x - this.chessSize / 2;
            int y1 = y - this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
            //右上角坐标，并向下向左画线
            x1 = x + this.chessSize / 2;
            y1 = y - this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
            //左下角坐标，并向上向右画线
            x1 = x - this.chessSize / 2;
            y1 = y + this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
            //右下角坐标，并向上向左画线
            x1 = x + this.chessSize / 2;
            y1 = y + this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
            //System.out.println("("+this.mapPointerChess.get("x")+","+this.mapPointerChess.get("y")+")");
        }

        //画落子指示器
        if(this.mapPointerChess.get("show") == 1)
        {
            if(this.mapPointerChess.get("color") == this.BLACKCHESS)
            {
                g2D.setColor(Color.BLACK);
            }
            else
            {
                g2D.setColor(Color.RED);
            }
            g2D.setStroke(new BasicStroke(3.5f));
            //先以交叉点为中心取到指示器周围的4个角坐标
            //中心点坐标
            int x = this.gridsLeftX + this.mapPointerChess.get("column") * this.gridSize;
            int y = this.gridsTopY + this.mapPointerChess.get("row") * this.gridSize;
            //左上角坐标，并向下向右画线
            int x1 = x - this.chessSize / 2;
            int y1 = y - this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
            //右上角坐标，并向下向左画线
            x1 = x + this.chessSize / 2;
            y1 = y - this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
            //左下角坐标，并向上向右画线
            x1 = x - this.chessSize / 2;
            y1 = y + this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
            //右下角坐标，并向上向左画线
            x1 = x + this.chessSize / 2;
            y1 = y + this.chessSize / 2;
            g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
            g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
            //System.out.println("("+this.mapPointerChess.get("x")+","+this.mapPointerChess.get("y")+")");
        }

        //画可移动线路图
        if(this.listMove.size() > 0)
        {
            g2D.setColor(Color.BLUE);
            for(int i=0;i<this.listMove.size();i++)
            {
                Map<String,Integer> map = this.listMove.get(i);
                int row = map.get("row");
                int column = map.get("column");
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);	//消除画图锯齿
                g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);		//追求速度或质量
                g2D.fillArc(this.gridsLeftX + column * this.gridSize - 5,this.gridsTopY + row * this.gridSize - 5,10,10,0,360);
            }
        }

    }

    /**
     * 功能：开始新游戏<br>
     */
    public void newGame()
    {
        //初始化游戏
        this.initGame();
        //设置控件状态
        this.setComponentState(true);
        //设置游戏结束标识
        this.isGameOver = false;
        //电脑先手
        if(this.fightType == 0 && this.playFirst == 2)
        {
            this.gameLogic.computerPlay();
        }

    }

    /**
     * 功能：悔棋<br>
     */
    public void undo()
    {
        this.gameLogic.undo();
    }

    /**
     * 功能：投降<br>
     */
    public void surrender()
    {
        if(this.isGameOver){return;}
        JOptionPane.showMessageDialog(null,"啥，认输了，还能再有点出息不！");
        this.isGameOver = true;
        this.setComponentState(false);
        this.jlb_blackStateText.setText("已结束");
        this.jlb_redStateText.setText("已结束");
    }

    /**
     * 功能：功能监听<br>
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        if("newGame".equals(command))
        {
            this.newGame();
        }
        else if("undo".equals(command))
        {
            this.undo();
        }
        else if("surrender".equals(command))
        {
            this.surrender();
        }
        else if("chessColor".equals(command))
        {
            if("玩家执黑".equals(this.jcb_chessColor.getSelectedItem().toString()))
            {
                this.chessColor = this.BLACKCHESS;
            }
            else
            {
                this.chessColor = this.REDCHESS;
            }
            this.initChess();
        }
    }

    /**
     * 功能：鼠标点击事件监听<br>
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        this.gameLogic.mouseClicked(e);
    }

    /**
     * 功能：鼠标移动事件监听<br>
     */
    @Override
    public void mouseMoved(MouseEvent e)
    {
        this.gameLogic.mouseMoved(e);
    }

    @Override
    public void mousePressed(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

    @Override
    public void mouseDragged(MouseEvent e){}

}