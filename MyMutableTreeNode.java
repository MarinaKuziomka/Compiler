import javax.swing.tree.DefaultMutableTreeNode;

public class MyMutableTreeNode extends DefaultMutableTreeNode{
    private Integer lexem;

    public MyMutableTreeNode(String str, Integer lexem){
        super(str);
        this.lexem = lexem;
    }

    public MyMutableTreeNode(String str){
        this(str,null);
    }

    public MyMutableTreeNode(char str){
        this(String.valueOf(str),null);
    }

    public MyMutableTreeNode(char str,Integer lexem){
        this(String.valueOf(str),lexem);
    }

    public MyMutableTreeNode(int str,Integer lexem){
        this(String.valueOf(str),lexem);
    }
    
    public MyMutableTreeNode getChildren(int index){
    	return (MyMutableTreeNode)(this.children.get(index));
    }

    public int getLexem(){
        return lexem;
    }

}
