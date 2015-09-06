import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

class Elem{
    String str;
    int num;
    public Elem(String str,int num){
        this.str=str;
        this.num=num;
    }

    @Override
    public String toString(){
         return String.format("%d) %s",num,str);
    }
}

public final class Main {
    private final int sizeWidth = 1350;
    private final int sizeHeight = 580;
    private Form form;
    private JFileChooser fileChooser;
    JEditorPane sourceCode;
    JList<Elem> keyWords;
    JList<Elem> delimiters;
    JList<Elem> identifiers;
    JList<Elem> constants;
    JEditorPane result;
    JTextArea asmCode;
    JTree tree;
    ResultLexAnalize resultLexAnalize;
    ResultSyntaxAnalize resultSyntaxAnalize;
    
    public Main(){
        form = new Form(sizeWidth,sizeHeight);
        Font font = new Font("Tahoma",Font.PLAIN,11);
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String path = f.getAbsolutePath().toLowerCase();
                String extension = "sig";
                if ((path.endsWith(extension) && (path.charAt(path.length()
                        - extension.length() - 1)) == '.')) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });

        JButton buttonOpenFile = new JButton("Відкрити файл...");
        buttonOpenFile.setBounds(50,20,150,30);
        buttonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.exists()){
                        try {
                            StringBuilder sb = new StringBuilder();
                            BufferedReader bf = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                            String s;
                            while((s=bf.readLine())!=null)
                                sb.append(String.format("%s%n",s));
                            sourceCode.setText(sb.toString());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        } catch (IOException e2){
                            e2.printStackTrace();
                        }
                    }
                }
            }
        });
        form.add(buttonOpenFile);

        JButton buttonLexAnalize = new JButton("Лексичний аналіз");
        buttonLexAnalize.setBounds(50,250,160,30);
        buttonLexAnalize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	result.setText(" ");
            	asmCode.setText(" ");
                lexAnalize();
            }
        });
        form.add(buttonLexAnalize);

        JButton buttonSyntaxAnalize = new JButton("Синтаксичний аналіз");
        buttonSyntaxAnalize.setBounds(50,300,160,30);
        
        buttonSyntaxAnalize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	result.setText(" ");
            	asmCode.setText(" ");
                lexAnalize();
                syntaxAnalize();
            }
        });
        form.add(buttonSyntaxAnalize);
        
        
        JButton buttonSemanticAnalize = new JButton("Семантичний аналіз");
        buttonSemanticAnalize.setBounds(50,350,160,30);
        buttonSemanticAnalize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	result.setText(" ");
            	asmCode.setText(" ");
                semanticAnalize();
            }
        });
        form.add(buttonSemanticAnalize);

        
        
        sourceCode = new JEditorPane();
        sourceCode.setBounds(10,60,250,180);
        form.add(sourceCode);

        result = new JEditorPane();
        result.setBounds(30, 400, sizeWidth-70, sizeHeight-450);
        form.add(result);

        
        JLabel label = new JLabel("Код асемблеру");
        label.setBounds(1050, 5, 100, 20);
        form.add(label);
        asmCode = new JTextArea();
        asmCode.setFont(font);
        form.add(asmCode);
        JScrollPane scrollAsmCode = new JScrollPane(asmCode);
        scrollAsmCode.setBounds(1045, 30, 295, 350);
        scrollAsmCode.setPreferredSize(new Dimension(100, 350));
        form.add(scrollAsmCode);
        
        
        label = new JLabel("Ключові слова");
        label.setBounds(300,5,100,20);
        form.add(label);
        keyWords = new JList<Elem>();
        keyWords.setBounds(280,30,200,160);
        loadKeyWords();
        JScrollPane keyWordsScroll = new JScrollPane(keyWords);
        keyWordsScroll.setBounds(280,30,200,160);
        keyWordsScroll.setPreferredSize(new Dimension(100, 100));
        form.add(keyWordsScroll);

        label = new JLabel("Розділювачі");
        label.setBounds(300,195,100,20);
        form.add(label);
        delimiters = new JList<Elem>();
        loadDelimiters();
        JScrollPane delimitersScroll = new JScrollPane(delimiters);
        delimitersScroll.setBounds(280, 220, 200, 160);
        delimitersScroll.setPreferredSize(new Dimension(100, 100));
        form.add(delimitersScroll);

        label = new JLabel("Ідентифікатори");
        label.setBounds(520,5,100,20);
        form.add(label);
        identifiers = new JList<Elem>();
        identifiers.setBounds(500,30,200,160);
        form.add(identifiers);
        JScrollPane identifiersScroll = new JScrollPane(identifiers);
        identifiersScroll.setBounds(500,30,200,160);
        identifiersScroll.setPreferredSize(new Dimension(100, 100));
        form.add(identifiersScroll);

        label = new JLabel("Константи");
        label.setBounds(520,195,100,20);
        form.add(label);
        constants = new JList<Elem>();
        constants.setBounds(500, 220, 200, 160);
        JScrollPane constantsScroll = new JScrollPane(constants);
        constantsScroll.setBounds(500, 220, 200, 160);
        constantsScroll.setPreferredSize(new Dimension(100, 100));
        form.add(constantsScroll);
        
        tree = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("")));
        tree.setBounds(720, 30, 320, 340);
        form.add(tree);
        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setBounds(720, 30, 320, 340);
        treeScroll.setPreferredSize(new Dimension(100, 100));
        form.add(treeScroll);
    }


    private void loadKeyWords() {
        ArrayList<String> str = KeyWordTable.getKeyWords();
        Vector<Elem> elements = new Vector<Elem>();
        int id = KeyWordTable.KEY_WORD_MIN_CODE;
        for (int i=0;i<str.size();i++){
            elements.add(new Elem(str.get(i),id+i));
        }
        keyWords.setListData(elements);
    }

    private void loadIdentifiers(IdentifiersTable idn) {
        ArrayList<String> str = idn.getIdentifiers();
        Vector<Elem> elements = new Vector<Elem>();
        int id = idn.IDENTIFIER_MIN_CODE;
        for (int i=0;i<str.size();i++){
            elements.add(new Elem(str.get(i),id+i));
        }
        identifiers.setListData(elements);
    }

    private void loadConstants(ConstantsTable cons) {
        ArrayList<Long> str = cons.getConstants();
        Vector<Elem> elements = new Vector<Elem>();
        int id = cons.CONSTANT_MIN_CODE;
        for (int i=0;i<str.size();i++){
            elements.add(new Elem(str.get(i).toString(),id+i));
        }
        constants.setListData(elements);
    }

    private void loadDelimiters() {
        ArrayList<String> str = DelimiterTable.getDoubleDelimiters();
        ArrayList<Character> strC = DelimiterTable.getSingleDelimiters();
        Vector<Elem> elements = new Vector<Elem>();
        int id = DelimiterTable.DELIMITER_MIN_CODE;
        for (int i=0;i<strC.size();i++)
            elements.add(new Elem(strC.get(i).toString(),(int)strC.get(i)));
        for (int i=0;i<str.size();i++)
            elements.add(new Elem(str.get(i),id+i));
        delimiters.setListData(elements);
    }
  
    private void loadLexems(ArrayList<Lexem> list){
        StringBuilder sb = new StringBuilder();
        for (Lexem lex: list){
        	if (lex.id != 0){
        		sb.append(String.format("%d ", lex.id));
        	}
        }
        addTextToResult(sb.toString());
    }

    private void addLexErrors(ArrayList<Lexem> errors){
        if (errors.size()>0){
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (Lexem lex:errors){
                sb.append(String.format("Лексична помилка(%d): %s\n",lex.row,ErrorTable.getError(lex.id)));
            }
            addTextToResult(sb.toString());
        }
    }
 
    private void addSemanticErrors(ArrayList<Lexem> errors){
        if (errors.size()>0){
            StringBuilder sb = new StringBuilder();
            for (Lexem lex:errors){
                sb.append(String.format("Семантична помилка: %s\n",ErrorTable.getError(lex.id)));
            }
            addTextToResult(sb.toString());
        }
    }
    
    private void addTextToResult(String text){
        result.setText(result.getText() + text);
    }
    
    private  void lexAnalize(){
        identifiers.removeAll();
        constants.removeAll();
        result.setText("Лексичний аналіз: \n");
        
        LexAnalize lexAnalize = new LexAnalize();
        resultLexAnalize = lexAnalize.lexicalAnalize(sourceCode.getText());
        loadIdentifiers(resultLexAnalize.idnTable);
        loadConstants(resultLexAnalize.constTable);
        loadLexems(resultLexAnalize.lexems);
        addLexErrors(resultLexAnalize.errors);
        tree.setModel(null);
    }

    private void syntaxAnalize(){
    	lexAnalize();
        if (resultLexAnalize != null) {
        	addTextToResult("\n");
            addTextToResult("\nСинтаксичний аналіз: \n");
            SyntaxAnalize syntaxAnalyzer = new SyntaxAnalize();
            resultSyntaxAnalize = syntaxAnalyzer.synataxAnalise(resultLexAnalize);
            if (resultSyntaxAnalize.status){
                DefaultTreeModel model = new DefaultTreeModel(resultSyntaxAnalize.tree);
                tree.setModel(model);
                expandAll(tree);
                for (int i = 0; i < tree.getRowCount(); i++) {
                    tree.collapseRow(i);
                }
                addTextToResult("Успішне завершення аналізу\n");
            }else{
                StringBuilder sb = new StringBuilder();
                for(Lexem error:resultSyntaxAnalize.errors){
                    sb.append(String.format("Помилка(%d): %s\n", error.row, ErrorTable.getSyntaxError(error.id)));
                }
                addTextToResult(sb.toString());
            }

        }
    }

    private void semanticAnalize() {
        syntaxAnalize();
        if (resultSyntaxAnalize != null && resultSyntaxAnalize.errors.size()==0){
            Generator generator = new Generator();
            ResultGenerator resultGenerator = generator.generate(resultSyntaxAnalize);

            StringBuilder builder = new StringBuilder();
            for(String s : resultGenerator.asmListing) {
                builder.append(s).append("\r\n");
            }
            asmCode.setText(builder.toString());

            addTextToResult("\nСемантичний аналіз:\n");
            if (resultGenerator.errors.size()>0)
                addSemanticErrors(resultGenerator.errors);
            else
                addTextToResult("Успішне завершення аналізу\n");

        }
    }
    
    public void expandAll(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root));
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
        //tree.collapsePath(parent);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new  Runnable() {
            public void run() {
                new  Main();
            }
        });
    }

}

