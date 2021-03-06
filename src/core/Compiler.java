package core;

import antlr.CmmLexer;
import antlr.CmmParser;
import antlr.CmmToken;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

/**
 * Created by TangJiong on 2015/12/14.
 * The core compiler program based on antlr
 */
public class Compiler {

    private boolean showLexerResult = true;
    private boolean showAST = true;
    private String source;
    private IOInterface lexIo;
    private IOInterface consoleIo;

    public Compiler(String source, IOInterface lexIo, IOInterface consoleIo){
        this.source = source;
        this.lexIo = lexIo;
        this.consoleIo = consoleIo;
    }

    public void run(){

        try{

            consoleIo.output("====== compiler starting... ======");

            CmmLexer lexer = new CmmLexer(new ANTLRInputStream(source));

            // ===================== 词法分析 =======================
            if(showLexerResult){
                lexIo.output("====== lexer analysis result: ======");
                lexIo.output("Token\tLine\tType");
                List<CmmToken> tokenList = (List<CmmToken>) lexer.getAllTokens();
                for(Token token : tokenList){

                    lexIo.output(token.getText() + "\t" + token.getLine()
                            + "\t" + TokenDictionary.getTokenType(token.getType()));

                }
                lexer.reset();
            }

            // =================== 语法分析 =======================
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            CmmParser parser = new CmmParser(tokenStream);
            ParseTree parseTree = parser.program();
            if(showAST){
                consoleIo.output("====== show tree ======");
                Trees.inspect(parseTree, parser);
            }
            ParseTreeWalker walker = new ParseTreeWalker();

            // 定义阶段，语法分析，将变量放入符号表
            DefPhaseListener defPhaseListener = new DefPhaseListener(consoleIo);
            walker.walk(defPhaseListener, parseTree);

            // 引用计算阶段改为visitor的方式
            RefPhaseVisitor refPhaseVisitor = new RefPhaseVisitor(defPhaseListener.globals,
                    defPhaseListener.scopes,
                    consoleIo);
            refPhaseVisitor.visit(parseTree);

        }catch (Exception e){
            consoleIo.output(e.getMessage());
            if(Constant.DEBUG){
                e.printStackTrace();
            }
        }

    }

    public void setShowLexerResult(boolean showLexerResult) {
        this.showLexerResult = showLexerResult;
    }
    public void setShowAST(boolean showAST) {
        this.showAST = showAST;
    }
}
