package hku.myothello;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Game_othello extends Activity {

    final static int cmNone  = 0;
    final static int cmBlack = -1;
    final static int cmWhite = 1;
    final static int csPlayer2 = cmWhite;
    final static int csPlayer1 = cmBlack;
    MediaPlayer mediaPlayer;
    ChessBoard mView;
    int Playerflag = 0;
    ImageButton btn_hints, btn_retract, btn_newgame;
    boolean hint_state = false, retract = false;
    ImageView Turn;
    TextView Black_p, White_p;
    String Player1 = "", Player2 = "";

    class ChessTable {
        public int[][] table;
        public int[][] backup;
        public int[][] hints;
        //public int[] last_position;

        public final int Rows = 8;
        public final int Cols = 8;

        public ChessTable() {
            table = new int[8][8];
            backup = new int[8][8];
            hints = new int[8][8];
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++) {
                    table[i][j] = cmNone;
                }
            table[3][3] = cmBlack;
            table[3][4] = cmWhite;
            table[4][3] = cmWhite;
            table[4][4] = cmBlack;
            setZero();
        }
        public int GetGoal(int[] goal) {//0=you, 1=computer, 2=empty
            int i,j;
            goal[0] = 0;
            goal[1] = 0;
            goal[2] = 0;
            for(i = 0; i < Rows; i++)
                for(j = 0; j < Cols; j++) {
                    if (table[i][j] == csPlayer1) (goal[0])++;
                    else if(table[i][j]==csPlayer2) (goal[1])++;
                    else (goal[2])++;
                }
            return 0;
        }
        public void backup(int[][] table_backup) {
            for(int i = 0; i < 8; i++)
//	        	for(int j=0;j<8;j++)
            {
                if (table_backup == null)
                    System.arraycopy(table[i], 0, backup[i], 0, 8);
                else
                    System.arraycopy(table[i], 0, table_backup[i], 0, 8);
//	        		table_backup[i][j]=table[i][j];
            }
        }
        public void restore(int[][] table_backup) {
            for(int i = 0; i < 8; i++)
//	        	for(int j=0;j<8;j++)
            {
                if (table_backup == null)
                    System.arraycopy(backup[i], 0, table[i], 0, 8);
                else
                    System.arraycopy(table_backup[i], 0, table[i], 0, 8);
//	        		table[i][j]=table_backup[i][j];
            }
        }

        public void setZero() {
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                    hints[i][j] = 0;
        }
    }


    ChessTable mTable;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_othello);

        mediaPlayer = MediaPlayer.create(this, R.raw.aaa);
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
        Intent intent = this.getIntent();
        final ArrayList<String> Playername = intent.getStringArrayListExtra("Player_name");
        if (Playername.size() > 0) {
            Player1 = Playername.get(0);
            Player2 = Playername.get(1);
        }

        final Resources r = this.getResources();
        btn_hints = (ImageButton) findViewById(R.id.btn_hints);
        btn_retract = (ImageButton) findViewById(R.id.btn_retract);
        btn_newgame = (ImageButton) findViewById(R.id.btn_newgame);
        Turn = (ImageView) findViewById(R.id.Turn);
        Black_p = (TextView) findViewById(R.id.Black_point);
        White_p = (TextView) findViewById(R.id.White_point);
        mView = (ChessBoard)findViewById(R.id.View01);
     //   mStatusText = (TextView)findViewById(R.id.text);

        mView.resetTiles(5);
        mView.loadTile(0, r.getDrawable(R.drawable.empty));
        mView.loadTile(1, r.getDrawable(R.drawable.black));
        mView.loadTile(2, r.getDrawable(R.drawable.white));
        mView.loadTile(3, r.getDrawable(R.drawable.black_hint));
        mView.loadTile(4, r.getDrawable(R.drawable.white_hint));

        mTable = new ChessTable();
        displayTable(mView, mTable.table, hint_state, mTable.hints);
        mTable.backup(null);
        Black_p.setText(Player1 + " : 2");
        White_p.setText(Player2 + " : 2");

        btn_newgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mTable = new ChessTable();
                Playerflag = 0;
                hint_state = false;
                retract = false;
                displayTable(mView, mTable.table, hint_state, mTable.hints);
                mTable.backup(null);
                Black_p.setText(Player1 + " : 2");
                White_p.setText(Player2 + " : 2");
                Turn.setImageDrawable(Playerflag == 0 ? r.getDrawable(R.drawable.lakers_label) : r.getDrawable(R.drawable.warriors_label));
            }
        });

        btn_retract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (retract) {
                    mTable.restore(null);
                    if (hint_state) {
                        mTable.setZero();
                        int pos = GetPos(mTable, Playerflag == 1 ? csPlayer1 : csPlayer2);
                    }
                    displayTable(mView, mTable.table, hint_state, mTable.hints);
                    Playerflag = (Playerflag == 0 ? 1 : 0);
                    Turn.setImageDrawable(Playerflag == 0 ? r.getDrawable(R.drawable.lakers_label) : r.getDrawable(R.drawable.warriors_label));
                    int[] goal = new int[3];
                    mTable.GetGoal(goal);
                    Black_p.setText(Player1 + " :" + goal[0]);
                    White_p.setText(Player2 + " :" + goal[1]);
                    retract = false;
                }
            }
        });

        btn_hints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hint_state = (hint_state == false ? true : false);
                if (hint_state) {
                    mTable.setZero();
                    int pos = GetPos(mTable, Playerflag == 0 ? csPlayer1 : csPlayer2);
                    displayTable(mView, mTable.table, hint_state, mTable.hints);
                } else {
                    displayTable(mView, mTable.table, hint_state, mTable.hints);
                }
            }
        });

        final Intent intent1 = new Intent(getBaseContext(), Congrat.class);

        mView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int row,col, x, y;
                int[] goal = new int[3];
                x = (int) event.getX();
                y = (int) event.getY();
                col = (int) Math.floor(x / 60);
                row = (int) Math.floor(y / 60);

                if (row < 8 && col < 8) {
                    //
                    if (mTable.hints[row][col] == 1) mTable.backup(null);
                    int res = PutChessman(mTable, row, col, Playerflag == 0 ? csPlayer1 : csPlayer2);
                    int pos = GetPos(mTable, Playerflag == 0 ? csPlayer1 : csPlayer2);
                    if (hint_state) mTable.setZero();
                    GetPos(mTable, Playerflag == 1 ? csPlayer1 : csPlayer2);
                    if (res == 0) {
                        if(pos != 0) {
                            return false;
                        }else {
                            Playerflag = (Playerflag == 0 ? 1 : 0);
                            displayTable((ChessBoard) v, mTable.table, hint_state, mTable.hints);
                        }
                       // Resources resource = mView.getContext().getResources();
                    }else {
                        Playerflag = (Playerflag == 0 ? 1 : 0);
                        displayTable((ChessBoard)v, mTable.table, hint_state, mTable.hints);
                    }
                    retract = true;
                    mTable.GetGoal(goal);
                    Black_p.setText(Player1 + " :" + goal[0]);
                    White_p.setText(Player2 + " :" + goal[1]);
                    /*if (pos == 0) {
                        //Change the Pic of View2 after "Turn:"
                        Turn.setImageDrawable(Playerflag == 0 ? r.getDrawable(R.drawable.black) : r.getDrawable(R.drawable.white));
                    }*/
                    Turn.setImageDrawable(Playerflag == 0 ? r.getDrawable(R.drawable.lakers_label) : r.getDrawable(R.drawable.warriors_label));
                    }
                if (goal[0] == 0) {
                    intent1.putExtra("Winner", Player2);
                    startActivity(intent1);
                } else if (goal[1] == 0) {
                    intent1.putExtra("Winner", Player1);
                    startActivity(intent1);
                } else if (goal[2] == 0) {
                    if (goal[0] > goal[1]) {
                        intent1.putExtra("Winner", Player1);
                        startActivity(intent1);
                    } else {
                        intent1.putExtra("Winner", Player2);
                        startActivity(intent1);
                    }
                }
                return false;
            }


        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "new game");
        menu.add(2, 9, 2, "undo");
        menu.add(3, 10, 2, "exit");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case 0: // new game
                mTable = new ChessTable();
                displayTable(mView, mTable.table, hint_state, mTable.hints);
                return true;
            case 9:
                mTable.restore(null);
                displayTable(mView, mTable.table, hint_state, mTable.hints);
                return true;
            case 10: //exit
                finish();
                return true;
        }
        return false;
    }
    /**
     * Create a simple handler that we can use to cause animation to happen.  We
     * set ourselves as a target and we can use the sleep()
     * function to cause an update/invalidate to occur at a later date.
     */
    /*private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 % 2 == 1)
                displayTable(mView, mTable.backup);
            else
                displayTable(mView, mTable.table);
            int[] goal = new int[3];
            mTable.GetGoal(goal);
            strGoal = goal[0] + ":" + goal[1];
            if(msg.what > 0)
            {
                strHint = "Your turn!";
            }else
            {
                if(GetPos(mTable,csYou,null)<=0)
                {
                    if(goal[0]>goal[1])
                        strHint = "You win!";
                    else if(goal[0]<goal[1])
                        strHint = "You lost!";
                    else
                        strHint = "We are same!";
                }else
                {
                    strHint = "I pass. Your turn!";
                }
            }
            mStatusText.setText(strGoal + " " + strHint);
            if(msg.arg1>0)
                sleep(500, msg.what, --msg.arg1);

        }
        public void start(int res) {
            this.removeMessages(0);
            sendMessage(Message.obtain(mRedrawHandler, res, 5, 0));
        }

        public void sleep(long delayMillis, int res, int count) {
            this.removeMessages(0);
            sendMessageDelayed(Message.obtain(mRedrawHandler, res, count, 0), delayMillis);
        }
    };*/

    void displayTable(ChessBoard view, int[][] table, boolean hint_state, int[][] hint) {
        if (hint_state) {
            for (int col = 0; col < 8; col++)
                for (int row = 0; row < 8; row++) {
                    if (table[row][col] == cmBlack)
                        view.setTile(1, col, row);
                    else if (table[row][col] == cmWhite)
                        view.setTile(2, col, row);
                    else if (hint[row][col] == 1 && Playerflag == 0)
                        view.setTile(3, col, row);
                    else if (hint[row][col] == 1 && Playerflag == 1)
                        view.setTile(4, col, row);
                    else view.setTile(0, col, row);
                }
        } else {
            for (int col = 0; col < 8; col++)
                for (int row = 0; row < 8; row++) {
                    if (table[row][col] == cmBlack)
                        view.setTile(1, col, row);
                    else if (table[row][col] == cmWhite)
                        view.setTile(2, col, row);
                    else
                        view.setTile(0, col, row);
                }
        }
        view.invalidate();
    }

    int GetKills(ChessTable pBoard, int row, int col, int side, boolean isKill)
    {
        int i, j, count = 0, temp, m, n, dirx, diry;
        if (pBoard == null) return 0;
        if (row < 0 || row >= pBoard.Rows || col < 0 || col >= pBoard.Cols) return 0;
        if (pBoard.table[row][col] != cmNone) return 0;

        for (dirx = -1; dirx < 2; dirx++)
            for (diry = -1; diry < 2; diry++) {
                if (dirx == 0 && diry == 0) continue;
                temp = 0;
                if (row + diry >= 0 && row + diry < pBoard.Rows &&
                        col + dirx >= 0 && col + dirx < pBoard.Cols &&
                        pBoard.table[row + diry][col + dirx] == (-1) * side) {
                    temp++;
                    for (i = row + diry * 2, j = col + dirx * 2;
                        i >= 0 && j >= 0 && i < pBoard.Rows && j < pBoard.Cols; i = i + diry, j = j + dirx) {
                        if(pBoard.table[i][j] == (-1)*side) {
                            temp++;
                            continue;
                        }else if (pBoard.table[i][j] == side) {
                            count += temp;
                            if (isKill)
                                for ( m = row + diry, n = col + dirx;
                                    m <= row + temp && m >= row - temp && n <= col + temp && n >= col-temp;
                                    m+=diry,n+=dirx) {
                                    pBoard.table[m][n]=side;
                                }
                            break;
                        }else break;
                    }
                }
            }
        if (isKill && count>0) pBoard.table[row][col] = side;
        return count;
    }

    int GetPos(ChessTable pBoard,int side) {//Point[N]为其相应的坐标；
        int i, j, count = 0;
        for(i = 0; i < pBoard.Rows; i++)
            for(j = 0; j < pBoard.Cols; j++) {
                if (GetKills(pBoard,i,j,side,false) > 0) {
                    pBoard.hints[i][j] = 1;
                    count++;
                }
            }
        return count;
    }

    int PutChessman(ChessTable pBoard,int row,int col,int side)
    {
            return GetKills(pBoard,row,col,side,true);

    }
/*
    int TryPut(ChessTable pBoard,int side,int depth)
    {
        int i,N,count,maxi;
        int[] goal=new int[64];
        int[][] table_backup=new int[8][8];

        int[][] point=new int[64][2];
        if(depth==0)return 0;

        pBoard.backup(table_backup);

        N=GetPos(pBoard,side,point);
        for(i=0;i<N;i++)
        {
            //在Point[I]上落子；
            count=GetKills(pBoard,point[i][0],point[i][1],side,true);//该步吃对方的棋子数；
            goal[i]=count-TryPut(pBoard,(-1)*side,depth-1);
            //if(该步占角)Goal[I]+=10;
            if(point[i][0]==0&&point[i][1]==0||
                    point[i][0]==7&&point[i][1]==7||
                    point[i][0]==7&&point[i][1]==0||
                    point[i][0]==0&&point[i][1]==7)
                goal[i]+=10;
            //if(该步占边)Goal[I]+=5;
            if(point[i][0]==0||point[i][1]==0||
                    point[i][0]==7||point[i][1]==7)
                goal[i]+=5;

            pBoard.restore(table_backup);
        }

        //  找Goal[]中最大值；
        maxi=0;
        for(i=1;i<N;i++)
        {
            if(goal[i]>goal[maxi])maxi=i;
        }
        //在goal[]最大值处落子；
        if(depth==pBoard.level&&N>0)
        {
            GetKills(pBoard,point[maxi][0],point[maxi][1],side,true);
        }
        return (goal[maxi]);
    }*/
}
