/*
象棋
*/
import java.util.Map;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.MouseEvent;


public class GameLogic
{
    /** 游戏面板 */
    GamePanel gamePanel;

    /** 最大搜索深度 */
    int Maxdepth = 2;

    Map<String,String> mapNextChess = new HashMap<String,String>();

    public GameLogic(GamePanel _gamePanel)
    {
        this.gamePanel = _gamePanel;
    }

    /**
     * 功能：得到X像素所对应的列坐标<br>
     */
    private int getColumn(int x)
    {
        //先判断靠哪列近些
        int column = (x - this.gamePanel.gridsLeftX + this.gamePanel.gridSize / 2) / this.gamePanel.gridSize;
        //再判断是否在有效范围内
        int posX = this.gamePanel.gridsLeftX + column * this.gamePanel.gridSize;
        if(x > (posX - this.gamePanel.chessSize / 2) && x < (posX + this.gamePanel.chessSize / 2)){}
        else
        {
            column = -1;
        }

        return column;
    }

    /**
     * 功能：得到Y像素所对应的行坐标<br>
     */
    private int getRow(int y)
    {
        //先判断靠哪行近些
        int row = (y - this.gamePanel.gridsTopY + this.gamePanel.gridSize / 2) / this.gamePanel.gridSize;
        //再判断是否在有效范围内
        int posY = this.gamePanel.gridsTopY + row * this.gamePanel.gridSize;
        if(y > (posY - this.gamePanel.chessSize / 2) && y < (posY + this.gamePanel.chessSize / 2)){}
        else
        {
            row = -1;
        }

        return row;
    }

    /**
     * 功能：判断下一步是红棋下还是黑棋下<br>
     */
    private int getNextChessColor()
    {
        int chessColor = -1;

        //得到上一步信息
        if(this.gamePanel.listChess.size() > 0)
        {
            Map<String,String> mapLast = this.gamePanel.listChess.get(this.gamePanel.listChess.size() - 1);
            if(Integer.parseInt(mapLast.get("color")) == this.gamePanel.BLACKCHESS)
            {
                chessColor = this.gamePanel.REDCHESS;
            }
            else
            {
                chessColor = this.gamePanel.BLACKCHESS;
            }
        }
        else
        {
            if(this.gamePanel.fightType == 0)	//人机对战
            {
                if(this.gamePanel.playFirst == 1)	//玩家先手
                {
                    chessColor = this.gamePanel.chessColor;
                }
                else	//电脑先手（这是不想赢啊）
                {
                    if(this.gamePanel.chessColor == this.gamePanel.BLACKCHESS)
                    {
                        chessColor = this.gamePanel.REDCHESS;
                    }
                    else
                    {
                        chessColor = this.gamePanel.BLACKCHESS;
                    }
                }
            }
            else	//人人对战
            {
                chessColor = this.gamePanel.chessColor;
            }
        }

        return chessColor;
    }

    /**
     * 功能：将军提示<br>
     */
    private void check()
    {
        //全体循环，不知道将哪头的军
        for(int i=0;i<this.gamePanel.mapChess.length;i++)
        {
            this.getMoveRoute(this.gamePanel.mapChess[i]);
            for(int j=0;j<this.gamePanel.listMove.size();j++)
            {
                Map<String,Integer> map = this.gamePanel.listMove.get(j);
                int index = this.gamePanel.chessBoradState[map.get("row")][map.get("column")];
                if(index != -1 && "king".equals(this.gamePanel.mapChess[index].get("type")))
                {
                    JOptionPane.showMessageDialog(null,"将军，十万火急！");
                    break;
                }
            }
        }
        this.gamePanel.listMove.clear();
        this.gamePanel.repaint();

    }

    /**
     * 功能：判断棋子是否可以放到目标位置<br>
     * 参数：_mapChess -> 棋子<br>
     * 参数：_newRow -> 目标行位置<br>
     * 参数：_newColumn -> 目标列位置<br>
     * 备注：点空位或对方棋子上，已方棋子略<br>
     */
    private boolean isAbleToMove(Map<String,String> _mapChess,int _newRow,int _newColumn)
    {
        int oldRow = -1;		//移动前行位置
        int oldColulmn = -1;	//移动前列位置
        int index = -1;			//目标索引
        String type = "";		//棋子类型
        String direction = "";	//棋子方向（T-上方，B-下方）

        //死亡棋子不能移动
        if("T".equals(_mapChess.get("dead"))){return false;}

        oldRow = Integer.parseInt(_mapChess.get("newRow"));
        oldColulmn = Integer.parseInt(_mapChess.get("newColumn"));
        type = _mapChess.get("type");
        direction = _mapChess.get("direction");
        index = this.gamePanel.chessBoradState[_newRow][_newColumn];

        //不能吃自己伙的棋子
        if(index != -1 && Integer.parseInt(this.gamePanel.mapChess[index].get("color")) == Integer.parseInt(_mapChess.get("color"))){return false;}

        //不能吃自身
        if(oldRow == _newRow && oldColulmn == _newColumn) {return false;}

        if("king".equals(type))				//将帅
        {
            //不能出九宫
            if((_newRow > 2 && _newRow < 7) || _newColumn < 3 || _newColumn > 5){return false;}
            //一次只能走一格
            if(Math.abs(_newRow - oldRow) > 1 || Math.abs(_newColumn - oldColulmn) > 1){return false;}
            //不能走斜线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) != 0){return false;}
            //将帅不能露脸
            if(index != -1 && "king".equals(this.gamePanel.mapChess[index].get(type)) && oldColulmn == _newColumn)	//目标棋子是将帅并且在同一列上
            {
                //判断中间是否有棋子
                int count = 0;
                int min = Math.min(oldRow,_newRow);
                int max = Math.max(oldRow,_newRow);
                for(int row=min+1;row<max;row++)
                {
                    if(this.gamePanel.chessBoradState[row][_newColumn] != -1){count++;}
                }
                if(count == 0){return false;}
            }
        }
        else if("guard".equals(type))		//士仕
        {
            //不能出九宫
            if((_newRow > 2 && _newRow < 7) || _newColumn < 3 || _newColumn > 5){return false;}
            //一次只能走一格
            if(Math.abs(_newRow - oldRow) > 1 || Math.abs(_newColumn - oldColulmn) > 1){return false;}
            //不能走横线或竖线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) == 0){return false;}
        }
        else if("elephant".equals(type))	//象相
        {
            //不能越界
            if("T".equals(direction))
            {
                if(_newRow > 4){return false;}
            }
            else
            {
                if(_newRow < 5){return false;}
            }
            //不能走横线或竖线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) == 0){return false;}
            //一次只能走二格
            if(Math.abs(_newRow - oldRow) != 2 || Math.abs(_newColumn - oldColulmn) != 2){return false;}
            //是否堵象眼
            if(this.gamePanel.chessBoradState[Math.min(oldRow,_newRow) + 1][Math.min(oldColulmn,_newColumn) + 1] != -1){return false;}
        }
        else if("horse".equals(type))		//马（8种跳法，4种别腿）
        {
            //必须走日字格
            if( Math.abs((_newRow - oldRow)) * Math.abs((_newColumn - oldColulmn)) != 2){return false;}
            //向上跳
            if(_newRow - oldRow == -2)
            {
                if(this.gamePanel.chessBoradState[oldRow - 1][oldColulmn] != -1){return false;}
            }
            //向下跳
            if(_newRow - oldRow == 2)
            {
                if(this.gamePanel.chessBoradState[oldRow + 1][oldColulmn] != -1){return false;}
            }
            //向左跳
            if(_newColumn - oldColulmn == -2)
            {
                if(this.gamePanel.chessBoradState[oldRow][oldColulmn - 1] != -1){return false;}
            }
            //向右跳
            if(_newColumn - oldColulmn == 2)
            {
                if(this.gamePanel.chessBoradState[oldRow][oldColulmn + 1] != -1){return false;}
            }
        }
        else if("rook".equals(type))		//车
        {
            //不能走斜线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) != 0){return false;}
            //竖走
            if(_newColumn == oldColulmn)
            {
                //判断中间是否有棋子
                int min = Math.min(oldRow,_newRow);
                int max = Math.max(oldRow,_newRow);
                for(int row=min+1;row<max;row++)
                {
                    if(this.gamePanel.chessBoradState[row][_newColumn] != -1){return false;}
                }
            }
            //横走
            if(_newRow == oldRow)
            {
                //判断中间是否有棋子
                int min = Math.min(oldColulmn,_newColumn);
                int max = Math.max(oldColulmn,_newColumn);
                for(int column=min+1;column<max;column++)
                {
                    if(this.gamePanel.chessBoradState[_newRow][column] != -1){return false;}
                }
            }
        }
        else if("cannon".equals(type))		//炮
        {
            int count = 0;
            //不能走斜线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) != 0){return false;}
            //竖走
            if(_newColumn == oldColulmn)
            {
                //判断中间是否有棋子
                int min = Math.min(oldRow,_newRow);
                int max = Math.max(oldRow,_newRow);
                for(int row=min+1;row<max;row++)
                {
                    if(this.gamePanel.chessBoradState[row][_newColumn] != -1){count++;}
                }
            }
            //横走
            if(_newRow == oldRow)
            {
                //判断中间是否有棋子
                int min = Math.min(oldColulmn,_newColumn);
                int max = Math.max(oldColulmn,_newColumn);
                for(int column=min+1;column<max;column++)
                {
                    if(this.gamePanel.chessBoradState[_newRow][column] != -1){count++;}
                }
            }
            //开始判断是否可以移动或吃棋子
            if(count > 1)
            {
                return false;
            }
            else if(count == 1)
            {
                if(this.gamePanel.chessBoradState[_newRow][_newColumn] == -1){return false;}	//打空炮的不要
            }
            else
            {
                if(this.gamePanel.chessBoradState[_newRow][_newColumn] != -1){return false;}
            }
        }
        else if("soldier".equals(type))		//卒兵
        {
            //不能走斜线
            if((_newRow - oldRow) * (_newColumn - oldColulmn) != 0){return false;}
            //一次只能走一格
            if(Math.abs(_newRow - oldRow) > 1 || Math.abs(_newColumn - oldColulmn) > 1){return false;}
            //小卒过河不回头
            if("T".equals(direction))	//上方
            {
                if(oldRow > 4)	//过河了
                {
                    if(_newRow < oldRow){return false;}	//不许向后退
                }
                else
                {
                    if(_newColumn == oldColulmn && _newRow > oldRow){}	//只能往前走
                    else
                    {
                        return false;
                    }
                }
            }
            else	//下方
            {
                if(oldRow < 5)	//过河了
                {
                    if(_newRow > oldRow){return false;}	//不许向后退
                }
                else
                {
                    if(_newColumn == oldColulmn && _newRow < oldRow){}	//只能往前走
                    else
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 功能：将下完的棋子信息复制一份存储到下棋列表中，悔棋用<br>
     * 备注：因为是对象引用，所以必须复制b<br>
     */
    private void addList(Map<String,String> _mapChess)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("index",_mapChess.get("index"));
        map.put("color",_mapChess.get("color"));
        map.put("type",_mapChess.get("type"));
        map.put("name",_mapChess.get("name"));
        map.put("number",_mapChess.get("number"));
        map.put("direction",_mapChess.get("direction"));
        map.put("oldOldRow",_mapChess.get("oldOldRow"));
        map.put("oldOldColumn",_mapChess.get("oldOldColumn"));
        map.put("oldRow",_mapChess.get("oldRow"));
        map.put("oldColumn",_mapChess.get("oldColumn"));
        map.put("newRow",_mapChess.get("newRow"));
        map.put("newColumn",_mapChess.get("newColumn"));
        map.put("dead",_mapChess.get("dead"));
        map.put("oldEatIndex",_mapChess.get("oldEatIndex"));
        map.put("eatIndex",_mapChess.get("eatIndex"));
        this.gamePanel.listChess.add(map);
    }

    /**
     * 功能：悔棋具体步骤<br>
     */
    private void undoStep()
    {
        if(this.gamePanel.isGameOver){return;}
        if(this.gamePanel.listChess.size() < 1){return;}

        //得到最后一步棋信息
        Map<String,String> mapLast = this.gamePanel.listChess.get(this.gamePanel.listChess.size() - 1);
        int index = Integer.parseInt(mapLast.get("index"));
        int oldOldRow = Integer.parseInt(mapLast.get("oldOldRow"));
        int oldOldColumn = Integer.parseInt(mapLast.get("oldOldColumn"));
        int oldRow = Integer.parseInt(mapLast.get("oldRow"));
        int oldColumn = Integer.parseInt(mapLast.get("oldColumn"));
        int newRow = Integer.parseInt(mapLast.get("newRow"));
        int newColumn = Integer.parseInt(mapLast.get("newColumn"));
        int oldEatIndex = Integer.parseInt(mapLast.get("oldEatIndex"));
        int eatIndex = Integer.parseInt(mapLast.get("eatIndex"));

        //开始退回
        this.gamePanel.mapChess[index].put("newRow",Integer.toString(oldRow));
        this.gamePanel.mapChess[index].put("newColumn",Integer.toString(oldColumn));
        this.gamePanel.mapChess[index].put("oldRow",Integer.toString(oldOldRow));
        this.gamePanel.mapChess[index].put("oldColumn",Integer.toString(oldOldColumn));
        this.gamePanel.mapChess[index].put("oldOldRow","-1");
        this.gamePanel.mapChess[index].put("oldOldColumn","-1");
        this.gamePanel.mapChess[index].put("dead","F");
        this.gamePanel.mapChess[index].put("eatIndex",Integer.toString(oldEatIndex));
        this.gamePanel.mapChess[index].put("oldEatIndex","-1");
        this.gamePanel.labelChess[index].setBounds(this.gamePanel.gridsLeftX + oldColumn * this.gamePanel.gridSize - this.gamePanel.chessSize/2,this.gamePanel.gridsTopY + oldRow * this.gamePanel.gridSize  - this.gamePanel.chessSize/2,this.gamePanel.chessSize,this.gamePanel.chessSize);
        this.gamePanel.chessBoradState[oldRow][oldColumn] = index;
        //判断是否吃棋子了
        if(eatIndex == -1)		//未吃棋子
        {
            this.gamePanel.chessBoradState[newRow][newColumn] = -1;
        }
        else	//吃棋子了，给我吐出来
        {
            this.gamePanel.mapChess[eatIndex].put("dead","F");
            this.gamePanel.labelChess[eatIndex].setBounds(this.gamePanel.gridsLeftX + newColumn * this.gamePanel.gridSize - this.gamePanel.chessSize/2,this.gamePanel.gridsTopY + newRow * this.gamePanel.gridSize  - this.gamePanel.chessSize/2,this.gamePanel.chessSize,this.gamePanel.chessSize);
            this.gamePanel.chessBoradState[newRow][newColumn] = eatIndex;
        }
        this.gamePanel.listChess.remove(this.gamePanel.listChess.size() - 1);
    }


    /**
     * 功能：悔棋<br>
     */
    public boolean undo()
    {
        int index,color,oldRow,oldColumn;
        Map<String,String> mapLast = null;

        if(this.gamePanel.isGameOver){return false;}
        if(this.gamePanel.listChess.size() < 1){return false;}

        //得到最后一步棋信息
        mapLast = this.gamePanel.listChess.get(this.gamePanel.listChess.size() - 1);
        index = Integer.parseInt(mapLast.get("index"));
        color = Integer.parseInt(mapLast.get("color"));
        oldRow = Integer.parseInt(mapLast.get("oldRow"));
        oldColumn = Integer.parseInt(mapLast.get("oldColumn"));

        if(this.gamePanel.fightType == 0)	//人机对战（只有玩家才会悔棋，电脑才不会这么耍赖）
        {
            //人机要同时悔2步棋，所以要得到倒数第二步棋信息
            if(this.gamePanel.listChess.size() < 2)
            {
                JOptionPane.showMessageDialog(null,"禁止悔棋！","提示",JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            mapLast = this.gamePanel.listChess.get(this.gamePanel.listChess.size() - 2);
            index = Integer.parseInt(mapLast.get("index"));
            color = Integer.parseInt(mapLast.get("color"));
            oldRow = Integer.parseInt(mapLast.get("oldRow"));
            oldColumn = Integer.parseInt(mapLast.get("oldColumn"));

            //判断玩家是否可以悔棋
            if(this.gamePanel.chessColor == this.gamePanel.BLACKCHESS)		//玩家执黑
            {
                if(this.gamePanel.blackUndoNum == 0)
                {
                    JOptionPane.showMessageDialog(null,"黑棋的悔棋次数已经全部用完了！","提示",JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                this.gamePanel.blackUndoNum--;
            }
            else
            {
                if(this.gamePanel.redUndoNum == 0)
                {
                    JOptionPane.showMessageDialog(null,"红棋的悔棋次数已经全部用完了！","提示",JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                this.gamePanel.redUndoNum--;
            }
            this.undoStep();	//电脑悔一步
            this.undoStep();	//玩家悔一步
        }
        else
        {
            //判断是否可以悔棋
            if(color == this.gamePanel.REDCHESS)
            {
                if(this.gamePanel.redUndoNum == 0)
                {
                    JOptionPane.showMessageDialog(null,"红棋的悔棋次数已经全部用完了！","提示",JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                this.gamePanel.redUndoNum--;
            }
            else
            {
                if(this.gamePanel.blackUndoNum == 0)
                {
                    JOptionPane.showMessageDialog(null,"黑棋的悔棋次数已经全部用完了！","提示",JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                this.gamePanel.blackUndoNum--;
            }
            this.undoStep();	//玩家悔一步
        }

        //重新生成落子指示器
        this.gamePanel.mapPointerChess.put("row",oldRow);
        this.gamePanel.mapPointerChess.put("column",oldColumn);
        this.gamePanel.mapPointerChess.put("color",color);
        this.gamePanel.mapPointerChess.put("show",1);
        this.gamePanel.isFirstClick = false;
        this.gamePanel.firstClickChess = this.gamePanel.mapChess[index];

        //显示移动路线图
        this.getMoveRoute(this.gamePanel.firstClickChess);

        //更新提示
        this.gamePanel.jlb_blackUndoText.setText("剩"+gamePanel.blackUndoNum+"次");
        this.gamePanel.jlb_redUndoText.setText("剩"+gamePanel.redUndoNum+"次");
        if(color == this.gamePanel.REDCHESS)
        {
            this.gamePanel.jlb_redStateText.setText("已下完");
            this.gamePanel.jlb_blackStateText.setText("已选棋");
        }
        else
        {
            this.gamePanel.jlb_redStateText.setText("已选棋");
            this.gamePanel.jlb_blackStateText.setText("已下完");
        }

        //刷新
        this.gamePanel.repaint();

        return true;
    }

    /**
     * 功能：对当前局面进行估分<br>
     * 备注：若电脑下的棋则（电脑分-玩家分），反之（玩家分-电脑分）<br>
     */
    private int evaluation(int[][] _chessBoradMap)
    {
        //基础分
        final int BASE_ROOK = 500;
        final int BASE_HORSE = 350;
        final int BASE_ELEPHANT = 250;
        final int BASE_GUARD = 250;
        final int BASE_KING = 10000;
        final int BASE_CANNON = 350;
        final int BASE_SOLDIER = 100;
        //灵活分（每多一个可走位置的相应加分）
        final int FLEXIBLE_ROOK = 6;
        final int FLEXIBLE_HORSE = 12;
        final int FLEXIBLE_ELEPHANT = 1;
        final int FLEXIBLE_GUARD = 1;
        final int FLEXIBLE_KING = 0;
        final int FLEXIBLE_CANNON = 6;
        final int FLEXIBLE_SOLDIER = 15;
        //其他
        int score = 0;		//总评估分数
        int redScore = 0;	//红旗评估分数
        int blackScore = 0;	//黑棋评估分数

        //判断该谁下棋
        int nextColor = this.getNextChessColor();

        //所有棋子循环
        for(int m=0;m<this.gamePanel.mapChess.length;m++)
        {
            //如果该棋子死亡则略过
            if("T".equals(this.gamePanel.mapChess[m].get("dead"))) {continue;}

            //得到相关参数
            String type = this.gamePanel.mapChess[m].get("type");
            int color = Integer.parseInt(this.gamePanel.mapChess[m].get("color"));
            String direction = this.gamePanel.mapChess[m].get("direction");
            int newRow = Integer.parseInt(this.gamePanel.mapChess[m].get("newRow"));
            int newColumn = Integer.parseInt(this.gamePanel.mapChess[m].get("newColumn"));

            //加基础分
            if("rook".equals(type))				//车
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_ROOK;
                }
                else
                {
                    redScore = redScore + BASE_ROOK;
                }
            }
            else if("horse".equals(type))		//马
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_HORSE;
                }
                else
                {
                    redScore = redScore + BASE_HORSE;
                }
            }
            else if("elephant".equals(type))		//象相
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_ELEPHANT;
                }
                else
                {
                    redScore = redScore + BASE_ELEPHANT;
                }
            }
            else if("guard".equals(type))		//士仕
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_GUARD;
                }
                else
                {
                    redScore = redScore + BASE_GUARD;
                }
            }
            else if("king".equals(type))		//将帅
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_KING;
                }
                else
                {
                    redScore = redScore + BASE_KING;
                }
            }
            else if("cannon".equals(type))		//炮
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_CANNON;
                }
                else
                {
                    redScore = redScore + BASE_CANNON;
                }
            }
            else if("soldier".equals(type))		//卒兵
            {
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + BASE_SOLDIER;
                }
                else
                {
                    redScore = redScore + BASE_SOLDIER;
                }
            }

            //加过河分
            if("soldier".equals(type))		//卒兵
            {
                int riverScore = 0;
                if("T".equals(direction))	//上方
                {
                    if(newRow > 4 && newRow < 9)	//过河了（不要最底框那行）
                    {
                        riverScore = 70;
                        if(newRow >= 6)
                        {
                            if(newColumn >= 2 && newColumn <= 6)
                            {
                                if(newRow >= 7 && newRow <=8 && newColumn >= 3 && newColumn <= 5)
                                {
                                    riverScore = riverScore + 50;
                                }
                                else
                                {
                                    riverScore = riverScore + 40;
                                }
                            }
                        }
                    }
                }
                else	//下方
                {
                    if(newRow > 0 && newRow < 5)	//过河了（不要最顶框那行）
                    {
                        riverScore = 70;
                        if(newRow <= 3)
                        {
                            if(newColumn >= 2 && newColumn <= 6)
                            {
                                if(newRow >= 1 && newRow <=2 && newColumn >= 3 && newColumn <= 5)
                                {
                                    riverScore = riverScore + 50;
                                }
                                else
                                {
                                    riverScore = riverScore + 40;
                                }
                            }
                        }

                    }
                }
                if(color == this.gamePanel.BLACKCHESS)
                {
                    blackScore = blackScore + riverScore;
                }
                else
                {
                    redScore = redScore + riverScore;
                }
            }

            //该棋子可以走的位置
            for(int row=0;row<this.gamePanel.gridRows;row++)
            {
                for(int column=0;column<this.gamePanel.gridColumns;column++)
                {
                    if(this.isAbleToMove(this.gamePanel.mapChess[m],row,column))
                    {
                        //加适应分
                        if("rook".equals(type))				//车
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_ROOK;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_ROOK;
                            }
                        }
                        else if("horse".equals(type))		//马
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_HORSE;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_HORSE;
                            }
                        }
                        else if("elephant".equals(type))		//象相
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_ELEPHANT;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_ELEPHANT;
                            }
                        }
                        else if("guard".equals(type))		//士仕
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_GUARD;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_GUARD;
                            }
                        }
                        else if("king".equals(type))		//将帅
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_KING;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_KING;
                            }
                        }
                        else if("cannon".equals(type))		//炮
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_CANNON;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_CANNON;
                            }
                        }
                        else if("soldier".equals(type))		//卒兵
                        {
                            if(color == this.gamePanel.BLACKCHESS)
                            {
                                blackScore = blackScore + FLEXIBLE_SOLDIER;
                            }
                            else
                            {
                                redScore = redScore + FLEXIBLE_SOLDIER;
                            }
                        }
                        //加威胁分（默认再加一遍基础分）
                        int index = this.gamePanel.chessBoradState[row][column];
                        if(index != -1)
                        {
                            String type1 = this.gamePanel.mapChess[index].get("type");
                            if("rook".equals(type1))				//车
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_ROOK;
                                }
                                else
                                {
                                    redScore = redScore + BASE_ROOK;
                                }
                            }
                            else if("horse".equals(type1))		//马
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_HORSE;
                                }
                                else
                                {
                                    redScore = redScore + BASE_HORSE;
                                }
                            }
                            else if("elephant".equals(type1))		//象相
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_ELEPHANT;
                                }
                                else
                                {
                                    redScore = redScore + BASE_ELEPHANT;
                                }
                            }
                            else if("guard".equals(type1))		//士仕
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_GUARD;
                                }
                                else
                                {
                                    redScore = redScore + BASE_GUARD;
                                }
                            }
                            else if("king".equals(type1))		//将帅
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_KING;
                                }
                                else
                                {
                                    redScore = redScore + BASE_KING;
                                }
                            }
                            else if("cannon".equals(type1))		//炮
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_CANNON;
                                }
                                else
                                {
                                    redScore = redScore + BASE_CANNON;
                                }
                            }
                            else if("soldier".equals(type1))		//卒兵
                            {
                                if(color == this.gamePanel.BLACKCHESS)
                                {
                                    blackScore = blackScore + BASE_SOLDIER;
                                }
                                else
                                {
                                    redScore = redScore + BASE_SOLDIER;
                                }
                            }
                        }
                    }
                }
            }
        }

        //计算总分
        if(nextColor == this.gamePanel.REDCHESS)
        {
            score = blackScore - redScore;
        }
        else
        {
            score = redScore - blackScore;
        }

        return score;
    }

    /**
     * 功能：负极大值算法<br>
     */
    private int negaMax(int[][] _chessBoradMap,int _depth)
    {
        int value;
        int bestValue = -9999999;

        //有事，程序还有好多漏洞，暂时未完善，也没写α与β剪枝，等有空再完善。


        //if(this.gameOver())return evaluation(this.gamePanel.chessBoradState);    //胜负已分，返回估值，有问题

        System.out.println("_depth="+_depth);

        //叶子节点
        if(_depth == 0)
        {
            return this.evaluation(this.gamePanel.chessBoradState);    //调用估值函数，返回估值
        }

        //生成每一步走法
        int nextColor = this.getNextChessColor();

        System.out.println("nextColor="+nextColor);

        for(int i=0;i<this.gamePanel.mapChess.length;i++)
        {
            //判断该谁下棋
            if(Integer.parseInt(this.gamePanel.mapChess[i].get("color")) != nextColor)
            {
                continue;
            }
            //判断是否可以下棋
            for(int row=0;row<this.gamePanel.gridRows;row++)
            {
                for(int column=0;column<this.gamePanel.gridColumns;column++)
                {
                    if(this.isAbleToMove(this.gamePanel.mapChess[i],row,column))
                    {
                        this.moveTo(this.gamePanel.mapChess[i],row,column);
                        //递归搜索子节点
                        value = this.negaMax(this.gamePanel.chessBoradState, _depth - 1);
                        //判断最大值
                        if(value >= bestValue)
                        {
                            bestValue = value;

                            if(_depth == this.Maxdepth)
                            {
                                this.mapNextChess.put("index",""+i);
                                this.mapNextChess.put("newRow",row+"");
                                this.mapNextChess.put("newColumn",column+"");
                            }


                        }
                        //恢复原来位置
                        this.undoStep();
                    }
                }
            }
        }

        return bestValue;   //返回最大值
    }

    /**
     * 功能：轮到电脑下棋了<br>
     */
    public void computerPlay()
    {
        int value;

        value = this.negaMax(this.gamePanel.chessBoradState,Maxdepth);

        int index = Integer.parseInt(this.mapNextChess.get("index"));
        int newRow = Integer.parseInt(this.mapNextChess.get("newRow")) ;
        int newColumn = Integer.parseInt(this.mapNextChess.get("newColumn")) ;


        System.out.println("value="+value);
        System.out.println("index="+index);
        System.out.println("newRow="+newRow);
        System.out.println("newColumn="+newColumn);

        this.moveTo(this.gamePanel.mapChess[index],newRow,newColumn);

        //落子指示器
        this.gamePanel.mapPointerChess.put("row",newRow);
        this.gamePanel.mapPointerChess.put("column",newColumn);
        this.gamePanel.mapPointerChess.put("show",1);
        this.gamePanel.mapPointerChess.put("color",this.gamePanel.computerChess);

        this.gamePanel.repaint();



    }

    /**
     * 功能：得到某棋子的可移动路线图<br>
     */
    private void getMoveRoute(Map<String,String> _mapChess)
    {
        this.gamePanel.listMove.clear();

        //懒得分类挑，反正电脑计算快
        for(int row=0;row<this.gamePanel.gridRows;row++)
        {
            for(int column=0;column<this.gamePanel.gridColumns;column++)
            {
                if(this.isAbleToMove(_mapChess,row,column))
                {
                    Map<String,Integer> map = new HashMap<String,Integer>();
                    map.put("row",row);
                    map.put("column",column);
                    this.gamePanel.listMove.add(map);
                }
            }
        }

    }

    /**
     * 功能：判断游戏是否结束<br>
     */
    private boolean gameOver()
    {
        if(this.gamePanel.fightType == 0)	//人机对战
        {
            if("T".equals(this.gamePanel.mapChess[4].get("dead")))	//黑将被吃
            {
                if(this.gamePanel.computerChess == this.gamePanel.BLACKCHESS)
                {
                    JOptionPane.showMessageDialog(null,"恭喜，你终于赢电脑一把了！");
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"我去，你怎么连电脑都输啊！","提示",JOptionPane.ERROR_MESSAGE);
                }
                return true;
            }
            if("T".equals(this.gamePanel.mapChess[27].get("dead")))	//红帅被吃
            {
                if(this.gamePanel.computerChess == this.gamePanel.BLACKCHESS)
                {
                    JOptionPane.showMessageDialog(null,"我去，你怎么连电脑都输啊！","提示",JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"恭喜，你终于赢电脑一把了！");
                }
                return true;
            }
        }
        else	//人人对战
        {
            if("T".equals(this.gamePanel.mapChess[4].get("dead")))	//黑将被吃
            {
                JOptionPane.showMessageDialog(null,"恭喜，红棋赢了！");
                return true;
            }
            if("T".equals(this.gamePanel.mapChess[27].get("dead")))	//红帅被吃
            {
                JOptionPane.showMessageDialog(null,"恭喜，黑棋赢了！");
                return true;
            }
        }

        return false;
    }

    /**
     * 功能：棋子移动到新位置<br>
     */
    private void moveTo(Map<String,String> _mapChess,int _newRow,int _newColumn)
    {
        //判断是移动还是吃子
        int newIndex = this.gamePanel.chessBoradState[_newRow][_newColumn];
        if(newIndex != -1)	//吃子
        {
            //目标棋子清除
            this.gamePanel.mapChess[newIndex].put("dead","T");
            this.gamePanel.labelChess[newIndex].setBounds(this.gamePanel.gridsLeftX + -2 * this.gamePanel.gridSize - this.gamePanel.chessSize/2,this.gamePanel.gridsTopY + -2 * this.gamePanel.gridSize  - this.gamePanel.chessSize/2,this.gamePanel.chessSize,this.gamePanel.chessSize);
        }
        //新棋子占位
        int index = Integer.parseInt(_mapChess.get("index"));
        _mapChess.put("oldOldRow",_mapChess.get("oldRow"));
        _mapChess.put("oldOldColumn",_mapChess.get("oldColumn"));
        _mapChess.put("oldRow",_mapChess.get("newRow"));
        _mapChess.put("oldColumn",_mapChess.get("newColumn"));
        _mapChess.put("newRow",Integer.toString(_newRow));
        _mapChess.put("newColumn",Integer.toString(_newColumn));
        _mapChess.put("oldEatIndex",_mapChess.get("eatIndex"));
        _mapChess.put("eatIndex",Integer.toString(newIndex));
        this.addList(_mapChess);
        this.gamePanel.labelChess[index].setBounds(this.gamePanel.gridsLeftX + _newColumn * this.gamePanel.gridSize - this.gamePanel.chessSize/2,this.gamePanel.gridsTopY + _newRow * this.gamePanel.gridSize  - this.gamePanel.chessSize/2,this.gamePanel.chessSize,this.gamePanel.chessSize);
        this.gamePanel.chessBoradState[Integer.parseInt(_mapChess.get("oldRow"))][Integer.parseInt(_mapChess.get("oldColumn"))] = -1;
        this.gamePanel.chessBoradState[_newRow][_newColumn] = index;
        this.gamePanel.isFirstClick = true;

    }

    /**
     * 功能：鼠标单击事件<br>
     */
    public void mouseClicked(MouseEvent e)
    {
        if(this.gamePanel.isGameOver){return;}

        if(e.getButton() == MouseEvent.BUTTON1)		//鼠标左键点击
        {
            if(e.getSource() == this.gamePanel.labelChessBorad)		//点击到棋盘上
            {
                //第一次点击无效
                if(this.gamePanel.isFirstClick){return;}

                //判断位置（将X与Y由像素改为相应的行列坐标）
                int row = this.getRow(e.getY());
                int column = this.getColumn(e.getX());
                if(row >= 0 && row < 10 && column >= 0 && column < 9)		//第二次点击
                {
                    //要移动棋子了
                    if(this.isAbleToMove(this.gamePanel.firstClickChess,row,column))
                    {
                        this.moveTo(this.gamePanel.firstClickChess,row,column);
                        //取消移动路线图
                        this.gamePanel.listMove.clear();
                        //落子指示器
                        this.gamePanel.mapPointerChess.put("row",row);
                        this.gamePanel.mapPointerChess.put("column",column);
                        this.gamePanel.mapPointerChess.put("show",1);
                        //更新提示
                        if(Integer.parseInt(gamePanel.firstClickChess.get("color")) == this.gamePanel.BLACKCHESS)
                        {
                            this.gamePanel.jlb_redStateText.setText("思考中");
                            this.gamePanel.jlb_blackStateText.setText("已下完");
                        }
                        else
                        {
                            this.gamePanel.jlb_redStateText.setText("已下完");
                            this.gamePanel.jlb_blackStateText.setText("思考中");
                        }
                        this.gamePanel.repaint();
                        //判断是否将军
                        this.check();

                        //如果是人机对战，机器要回应啊
                        if(this.gamePanel.fightType == 0)	//人机对战
                        {
                            this.computerPlay();
                            if(this.gamePanel.computerChess == this.gamePanel.BLACKCHESS)
                            {
                                this.gamePanel.jlb_blackStateText.setText("已下完");
                                this.gamePanel.jlb_redStateText.setText("思考中");
                            }
                            else
                            {
                                this.gamePanel.jlb_redStateText.setText("已下完");
                                this.gamePanel.jlb_blackStateText.setText("思考中");
                            }
                            //判断游戏是否结束
                            if(this.gameOver())
                            {
                                this.gamePanel.isGameOver = true;
                                this.gamePanel.setComponentState(false);
                                this.gamePanel.jlb_blackStateText.setText("已结束");
                                this.gamePanel.jlb_redStateText.setText("已结束");
                                return;
                            }
                        }
                    }
                }
                else
                {
                    return;
                }
            }
            else	//点到棋子上
            {
                JLabel label = (JLabel)e.getSource();
                int index = Integer.parseInt(label.getName());
                int row = Integer.parseInt(this.gamePanel.mapChess[index].get("newRow"));
                int column = Integer.parseInt(this.gamePanel.mapChess[index].get("newColumn"));
                //判断第几次点击
                if(this.gamePanel.isFirstClick)		//第一次（必须点击到该下棋方的棋子上）
                {
                    if(Integer.parseInt(this.gamePanel.mapChess[index].get("color")) != this.getNextChessColor()){return;}
                    //画个落子指示器并记录下第一次点击对象
                    this.gamePanel.mapPointerChess.put("row",row);
                    this.gamePanel.mapPointerChess.put("column",column);
                    this.gamePanel.mapPointerChess.put("show",1);
                    this.gamePanel.mapPointerChess.put("color",Integer.parseInt(this.gamePanel.mapChess[index].get("color")));
                    this.gamePanel.firstClickChess = this.gamePanel.mapChess[index];
                    this.gamePanel.isFirstClick = false;
                    this.gamePanel.repaint();
                    if(Integer.parseInt(this.gamePanel.mapChess[index].get("color")) == this.gamePanel.BLACKCHESS)
                    {
                        this.gamePanel.jlb_redStateText.setText("等待中");
                        this.gamePanel.jlb_blackStateText.setText("已选棋");
                    }
                    else
                    {
                        this.gamePanel.jlb_redStateText.setText("已选棋");
                        this.gamePanel.jlb_blackStateText.setText("等待中");
                    }
                    //显示移动路线图
                    this.getMoveRoute(this.gamePanel.firstClickChess);
                    this.gamePanel.repaint();
                }
                else	//第二次点击
                {
                    //点击到该下棋方的棋子上则还算是第一次
                    if(Integer.parseInt(this.gamePanel.mapChess[index].get("color")) == this.getNextChessColor())
                    {
                        this.gamePanel.mapPointerChess.put("row",row);
                        this.gamePanel.mapPointerChess.put("column",column);
                        this.gamePanel.mapPointerChess.put("show",1);
                        this.gamePanel.firstClickChess = this.gamePanel.mapChess[index];
                        this.gamePanel.isFirstClick = false;
                        this.getMoveRoute(this.gamePanel.firstClickChess);		//显示移动路线图
                        this.gamePanel.repaint();
                    }
                    else	//要吃棋子了
                    {
                        if(this.isAbleToMove(this.gamePanel.firstClickChess,row,column))	//这个可以吃
                        {
                            this.moveTo(this.gamePanel.firstClickChess,row,column);
                            //取消移动路线图
                            this.gamePanel.listMove.clear();
                            //落子指示器
                            this.gamePanel.mapPointerChess.put("row",row);
                            this.gamePanel.mapPointerChess.put("column",column);
                            this.gamePanel.mapPointerChess.put("show",1);
                            if(Integer.parseInt(gamePanel.firstClickChess.get("color")) == this.gamePanel.BLACKCHESS)
                            {
                                this.gamePanel.jlb_redStateText.setText("思考中");
                                this.gamePanel.jlb_blackStateText.setText("已下完");
                            }
                            else
                            {
                                this.gamePanel.jlb_redStateText.setText("已下完");
                                this.gamePanel.jlb_blackStateText.setText("思考中");
                            }
                            this.gamePanel.repaint();
                            //判断是否将军
                            this.check();
                        }

                        //判断游戏是否结束
                        if(this.gameOver())
                        {
                            this.gamePanel.isGameOver = true;
                            this.gamePanel.setComponentState(false);
                            this.gamePanel.jlb_blackStateText.setText("已结束");
                            this.gamePanel.jlb_redStateText.setText("已结束");
                            return;
                        }

                        //判断双方是否战平（这个不行啊）

                        //如果是人机对战，机器要回应啊
                        if(this.gamePanel.fightType == 0)	//人机对战
                        {
                            this.computerPlay();
                            if(this.gamePanel.computerChess == this.gamePanel.BLACKCHESS)
                            {
                                this.gamePanel.jlb_blackStateText.setText("已下完");
                                this.gamePanel.jlb_redStateText.setText("思考中");
                            }
                            else
                            {
                                this.gamePanel.jlb_redStateText.setText("已下完");
                                this.gamePanel.jlb_blackStateText.setText("思考中");
                            }
                            //判断游戏是否结束
                            if(this.gameOver())
                            {
                                this.gamePanel.isGameOver = true;
                                this.gamePanel.setComponentState(false);
                                this.gamePanel.jlb_blackStateText.setText("已结束");
                                this.gamePanel.jlb_redStateText.setText("已结束");
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 功能：鼠标移动事件<br>
     */
    public void mouseMoved(MouseEvent e)
    {
        int row = -1;
        int column = -1;
        int index = -1;

        if(this.gamePanel.isGameOver){return;}

        //得到行列位置
        if(e.getSource() == this.gamePanel.labelChessBorad)		//在棋盘上移动
        {
            row = this.getRow(e.getY());
            column = this.getColumn(e.getX());
        }
        else	//在棋子上移动
        {
            JLabel label = (JLabel)e.getSource();
            index = Integer.parseInt(label.getName());
            row = Integer.parseInt(this.gamePanel.mapChess[index].get("newRow"));
            column = Integer.parseInt(this.gamePanel.mapChess[index].get("newColumn"));
        }

        //判断是否在棋盘内部移动
        if(row >= 0 && row < 10 && column >= 0 && column < 9)
        {
            //清除落子指示器（先不显示）
            this.gamePanel.mapPointerMove.put("show",0);
            if(this.gamePanel.chessBoradState[row][column] == -1)	//移动到棋盘上
            {
                this.gamePanel.mapPointerMove.put("row",row);
                this.gamePanel.mapPointerMove.put("column",column);
                this.gamePanel.mapPointerMove.put("show",1);
                this.gamePanel.mapPointerMove.put("color",-1);
            }
            else	//移动到棋子上
            {
                //第一次点击处理
                if(this.gamePanel.isFirstClick)
                {
                    //下棋方显示移动显示器，非下棋方不显示移动指示器
                    if(Integer.parseInt(this.gamePanel.mapChess[index].get("color")) == this.getNextChessColor())
                    {
                        this.gamePanel.mapPointerMove.put("row",row);
                        this.gamePanel.mapPointerMove.put("column",column);
                        this.gamePanel.mapPointerMove.put("show",1);
                        this.gamePanel.mapPointerMove.put("color",-1);
                    }
                }
                else		//第二次点击处理
                {
                    this.gamePanel.mapPointerMove.put("row",row);
                    this.gamePanel.mapPointerMove.put("column",column);
                    this.gamePanel.mapPointerMove.put("show",1);
                    this.gamePanel.mapPointerMove.put("color",-1);
                }
            }
            this.gamePanel.repaint();
        }
        else	//点棋盘外边了
        {
            if(this.gamePanel.mapPointerMove.get("show") == 1)
            {
                this.gamePanel.mapPointerMove.put("show",0);
                this.gamePanel.repaint();
            }
        }

    }


}