import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Stack;

public class Calculator extends JFrame{


    private final Stack<Double> operandStack= new Stack<>();
    private final Stack<String> operatorStack = new Stack<>();

    private Calculator(){
        setTitle("科学计算器");
        setSize(313,390);
        setLocation(500,300);
        Container c=getContentPane();
        c.setLayout(null);


        JTextArea jt=new JTextArea(100,100);
        jt.setFont(new Font("Aria",Font.BOLD,18));
        jt.setLineWrap(true);
        JScrollPane sp=new JScrollPane(jt);
        jt.setCaretPosition(jt.getDocument().getLength());
        sp.setBounds(0,0,300,100);
        c.add(sp);

        JPanel p=new JPanel();
        p.setLayout(new GridLayout(6,5,0,0));

        p.setBounds(0,100,300,250);
        String[] num={"sin","cos","tan","lg","㏑","^","(",")","AC","/","√","7","8","9","*","!","4","5","6","-","⅟","1","2","3","+","关于","0",".","Del","="};
        JButton[] jb=new JButton[30];
        for(int i=0;i<30;i++){
            jb[i]=new JButton(num[i]);
            p.add(jb[i]);
        }
        c.add(p);

        for(int i=0;i<28;i++){
            if (i==0){
                jb[i].addActionListener(e-> jt.append("s"));
            }
            if (i==1){
                jb[i].addActionListener(e-> jt.append("c"));
            }
            if (i==2){
                jb[i].addActionListener(e-> jt.append("t"));
            }
            if (i==3){
                jb[i].addActionListener(e-> jt.append("l"));
            }
            if(i!=8&&i!=25&&i!=0&&i!=1&&i!=2&&i!=3/*&&i!=5&&i!=10&&i!=15*/){
                final int j=i;
                jb[i].addActionListener(e-> jt.append(num[j]));
            }
        }

        jb[8].addActionListener(e->{
            //AC键功能
            jt.setText("");
            operandStack.clear();
            operatorStack.clear();
        });
        jb[25].addActionListener(e-> jt.setText("作者:Zezin\n学号:2109020321\nEmail:zezin666@email.cn\n"));//关于
        jb[28].addActionListener(e->{
            //删除键功能
            try{
                jt.setText(jt.getText().substring(0,jt.getText().length()-1));
            }catch(Exception ignored) { }
        });
        jb[29].addActionListener(e->{
            //等于键功能
            try{
                double x= calculate(jt.getText()+"#");
                jt.setText("");
                jt.append(String.valueOf(x));
            }catch(Exception ex){
                if(ex.getMessage()==null)
                    jt.setText("ERROR!");
                else
                    jt.setText(ex.getMessage());
            }
        });

        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        jt.getInputMap().put(enter, "none");

        this.getRootPane().setDefaultButton(jb[29]);

        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void calculate(){
        String b = operatorStack.pop();
        double e;
        double d;
        double c;
        c=operandStack.pop();
        if (b.equals("+")) {//加法
            d=operandStack.pop();
            e = d + c;
            operandStack.push(e);
        }
        if (b.equals("-")) {//减法
            d=operandStack.pop();
            e = c - d;
            operandStack.push(-e);
        }
        if (b.equals("*")) {//乘法
            d=operandStack.pop();
            e = d * c;
            operandStack.push(e);
        }
        if (b.equals("/")) {//除法
            d=operandStack.pop();
            if(c==d)
                throw new ArithmeticException("DivideByZero!");//0不能做除数，抛出异常
            e = c / d;
            operandStack.push(1/e);
        }
        if (b.equals("^")){//次方
            d=operandStack.pop();
            e=Math.pow(d,c);
            operandStack.push(e);
        }
        if (b.equals("√")){//开平方
            e=Math.sqrt(c);
            operandStack.push(e);
        }
        if (b.equals("!")){//阶乘
            e=recursion((int)c);//调用阶乘方法
            operandStack.push(e);
        }
        if (b.equals("⅟")){//倒数
            e=Math.pow(c,-1);
            operandStack.push(e);
        }
        if (b.equals("s")){//sin
            double radians=Math.toRadians(c);
            e=Math.sin(radians);
            e=Math.round(e*1000000000)*0.000000001d;
            operandStack.push(e);
        }
        if (b.equals("c")){//cos
            double radians=Math.toRadians(c);
            e=Math.cos(radians);
            e=Math.round(e*1000000000)*0.000000001d;
            operandStack.push(e);
        }
        if (b.equals("t")){//tan
            double radians=Math.toRadians(c);
            e=Math.tan(radians);
            e=Math.round(e*1000000000)*0.000000001d;
            operandStack.push(e);
        }
        if (b.equals("l")){//以十为底的对数
            e=Math.log10(c);
            operandStack.push(e);
        }
        if (b.equals("㏑")){//以自然常数为底的对数
            e=Math.log(c);
            operandStack.push(e);
        }
    }

    private Double calculate(String text){
        HashMap<String,Integer> precede=new HashMap<>();
        precede.put("(",0);
        precede.put(")",0);
        precede.put("/",2);
        precede.put("*",2);
        precede.put("-",1);
        precede.put("+",1);
        precede.put("#",0);
        precede.put("^",3);
        precede.put("√",3);
        precede.put("!",3);
        precede.put("⅟",3);
        precede.put("s",4);
        precede.put("c",4);
        precede.put("t",4);
        precede.put("l",4);
        precede.put("㏑",4);


        operatorStack.push("#");

        int flag=0;
        for(int i=0;i<text.length();i++){
            String a=String.valueOf(text.charAt(i));
            if(!a.matches("[0-9.]")){
                if(flag!=i)
                    operandStack.push(Double.parseDouble(text.substring(flag,i)));
                flag=i+1;
                while(!(a.equals("#")&&operatorStack.peek().equals("#"))){
                    if(precede.get(a)>precede.get(operatorStack.peek())||a.equals("(")){
                        operatorStack.push(a);
                        break;
                    }else {
                        if(a.equals(")")) {
                            while(!operatorStack.peek().equals("("))
                                calculate();
                            operatorStack.pop();
                            break;
                        }
                        calculate();
                    }
                }

            }
        }

        return(operandStack.pop());
    }

    public static int recursion(int num){

        int sum;

        if(num < 0)

            throw new IllegalArgumentException("必须为正整数!");

        if(num==1){

            return 1;

        }else{

            sum=num * recursion(num-1);

            return sum;

        }

    }

    public static void main(String[] args){
        FlatDarkLaf.setup();//此方法引自一个jar包 用于美化GUI https://github.com/JFormDesigner/FlatLaf
        new Calculator();
    }
}


